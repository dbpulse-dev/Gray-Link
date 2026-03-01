package com.dbcat.gray.filter.selector;


import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.constant.SelectorType;
import com.dbcat.gray.filter.dto.*;
import com.dbcat.gray.filter.parser.ScopeValueParser;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SelectorManager extends AbstractEnvSelector<List<GrayValue>, Context> {

    private volatile List<Selector> selectors = new ArrayList<>();

    private SimpleWeightSelector weightSelector;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private ScopeValueParser scopeValueParser;

    private List<GrayStrategyConfig> strategyConfigs;

    private static Map<String, Class<? extends Selector>> selectorClassMap = new HashMap<>();

    {
        selectorClassMap.put(SelectorType.CONDITION.name(), ConditionTrafficSelector.class);
        selectorClassMap.put(SelectorType.HASH_WEIGHT.name(), HashWeightSelector.class);
        selectorClassMap.put(SelectorType.SIMPLE_WEIGHT.name(), SimpleWeightSelector.class);
    }


    public SelectorManager(ScopeValueParser scopeValueParser, Map<String, String> grayTypes) {
        this.scopeValueParser = scopeValueParser;
    }


    public void setStrategyConfigs(List<GrayStrategyConfig> strategyConfigs) throws InstantiationException, IllegalAccessException {
        this.strategyConfigs = strategyConfigs;
        for (GrayStrategyConfig config : strategyConfigs) {
            Class<? extends Selector> clazz = selectorClassMap.get(config.getType());
            Selector selector = clazz.newInstance();
            selectors.add(selector);
            if (selector.getType().equals(SelectorType.CONDITION)) {
                continue;
            }
            selector.setConfig(config.getWeight());

        }
    }

    @Override
    public synchronized void setConfig(List<GrayValue> conditions) {
        List<Selector> selectors = findSelectors(SelectorType.CONDITION.name());
        for (Selector s : selectors) {
            s.setConfig(conditions);
        }
    }

    @Override
    public Boolean selectEnv(Context request) {
        return this.selectEnv(request, this.strategyConfigs);
    }

    public Boolean selectEnv(Context request, List<GrayStrategyConfig> configs) {
        totalCount.incrementAndGet();
        for (GrayStrategyConfig config : configs) {
            if (!config.isEnable()) {
                continue;
            }
            Boolean env = selectEnv(request, config);
            if (env == null) {
                continue;
            }
            if (env) {
                grayCount.incrementAndGet();
            }
            return env;
        }
        return null;
    }

    private Boolean selectEnv(Context context, GrayStrategyConfig strategyConfig) {
        List<StrategyCondition> conditions = strategyConfig.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            if (weightSelector != null) {
                return weightSelector.selectEnv(context);
            }
            return null;
        }
        List<Pair<String, String>> conditionValues = parseConditions(context, strategyConfig);
        if (conditionValues.isEmpty()) {
            return null;
        }
        return conditionMatch(context, strategyConfig, conditionValues);
    }

    private List<Pair<String, String>> parseConditions(Context request, GrayStrategyConfig config) {
        List<StrategyCondition> conditions = config.getConditions();
        List<Pair<String, String>> targetConditions = new ArrayList<>();
        for (StrategyCondition c : conditions) {
            List<ScopeConfig> scopeConfigs = c.getScopes();
            for (ScopeConfig scopeConfig : scopeConfigs) {
                String value = scopeValueParser.parse(request, scopeConfig);
                if (StringUtils.hasText(value)) {
                    targetConditions.add(new Pair<>(c.getName(), value));
                    break;
                }
            }
        }
        return targetConditions;
    }


    /**
     * 部分条件比较
     *
     * @param conditionValues
     * @param context
     * @return
     */
    private Boolean conditionMatch(Context context, GrayStrategyConfig strategyConfig, List<Pair<String, String>> conditionValues) {

        context.setAttr("defaultOps", strategyConfig.getOperators());
        context.setAttr("conditionValues", conditionValues);
        List<Selector> matchSelectors = findSelectors(strategyConfig.getType());
        for (Selector selector : matchSelectors) {
            Boolean rs = selector.selectEnv(context);
            if (rs != null) {
                return rs;
            }
        }
        return null;
    }


    private List<Selector> findSelectors(String type) {
        return selectors.stream().filter(s -> s.getType().name().equals(type)).collect(Collectors.toList());
    }


    @Override
    public SelectorType getType() {
        return null;
    }

    @Override
    public List<TrafficCount> getCounts() {
        List<TrafficCount> targetList = new ArrayList<>();
        for (Selector s : selectors) {
            TrafficCount count = TrafficCount.build(s);
            targetList.add(count);
        }
        return targetList;
    }


//    public TrafficCount getCountByValue(GrayValue condition){
//        Map<String, ConditionSelector> typeSelectors = selectorMap.get(condition.getType());
//        if (typeSelectors == null) {
//            return null;
//        }
//        ConditionSelector wrapper = typeSelectors.get(condition.getValue());
//        TrafficCount count = TrafficCount.build(wrapper.getSelector());
//        count.setName(wrapper.getCondition().getValue());
//        count.setType(wrapper.getCondition().getType());
//        return count;
//    }

}