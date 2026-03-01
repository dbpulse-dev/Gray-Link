package com.dbcat.gray.filter.selector;

import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.VersionComparator;
import com.dbcat.gray.filter.constant.Operator;
import com.dbcat.gray.filter.constant.SelectorType;
import com.dbcat.gray.filter.dto.GrayValue;
import com.dbcat.gray.filter.dto.Pair;
import org.springframework.util.AntPathMatcher;

import java.util.*;

import static com.dbcat.gray.filter.constant.GrayConstants.CONDITION_SPIT;
import static com.dbcat.gray.filter.constant.SelectorType.CONDITION;

public class ConditionTrafficSelector extends AbstractEnvSelector<List<GrayValue>, Context> {

    private Map<String/*key*/, ConditionSelector> selectorMap = new HashMap<>();
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public synchronized void setConfig(List<GrayValue> conditions) {
        Map<String, ConditionSelector> tempSelectorMap = buildNews(conditions);
        fillOldValues(tempSelectorMap);
        selectorMap = tempSelectorMap;
    }

    private Map<String, ConditionSelector> buildNews(List<GrayValue> conditions) {
        Map<String, ConditionSelector> tempSelectorMap = new HashMap<>();
        for (GrayValue condition : conditions) {
            //要保留之前的统计
            ConditionSelector selector = selectorMap.get(condition.getValue());
            if (selector == null) {
                selector = buildSelector(condition);

            }
            tempSelectorMap.put(condition.getValue(), selector);
        }
        return tempSelectorMap;
    }

    private ConditionSelector buildSelector(GrayValue condition) {
        SimpleWeightSelector selector = SimpleWeightSelector.build(condition.getWeight());
        ConditionSelector wrapper = new ConditionSelector(selector);
        wrapper.setCondition(condition);
        return wrapper;
    }

    @Override
    public SelectorType getType() {
        return CONDITION;
    }

    @Override
    public Boolean selectEnv(Context context) {
        List<Pair<String, String>> conditionValues = (List) context.getAttr("conditionValues");
        String keyPattern = buildKeyPattern(conditionValues, 0) + "**";
        //key前缀匹配出所有选择器
        List<ConditionSelector> matchSelectors = findSelectors(keyPattern);
        List<Operator> defaultOps = (List<Operator>) context.getAttr("defaultOps");
        for (ConditionSelector selector : matchSelectors) {
            GrayValue condition = selector.getCondition();
            String targetValue = condition.getValue();
            List<Operator> ops = condition.getOperators();
            if (ops == null) {
                ops = defaultOps;
            }
            if (ops == null) {
                continue;
            }
            boolean match = multipleConditionCompare(ops, conditionValues, targetValue);
            if (match) {
                return selector.selectEnv(context);
            }
        }
        return null;
    }


    protected boolean multipleConditionCompare(List<Operator> ops, List<Pair<String, String>> conditionValues, String targetValue) {
        for (Pair<String, String> p : conditionValues) {
            String value = p.getValue();
            boolean match;
            if (VersionComparator.isNumeric(value) && VersionComparator.isNumeric(targetValue)) {
                match = VersionComparator.match(ops, value, targetValue);
            } else {
                match = value.equals(targetValue);
            }
            if (match) {
                return true;
            }
        }
        return false;
    }


    private List<ConditionSelector> findSelectors(String keyPattern) {
        Set<Map.Entry<String, ConditionSelector>> entries = selectorMap.entrySet();
        List<ConditionSelector> matchSelectors = new ArrayList<>();
        for (Map.Entry<String, ConditionSelector> entry : entries) {
            String key = entry.getKey();
            boolean match = PATH_MATCHER.match(keyPattern, key);
            if (match) {
                matchSelectors.add(entry.getValue());
            }
        }
        return matchSelectors;
    }


    private void fillOldValues(Map<String, ConditionSelector> tempSelectorMap) {
        Map<String, ConditionSelector> oldSelectors = selectorMap;
        tempSelectorMap.forEach((key, newWrapper) -> {
            ConditionSelector oldWrapper = oldSelectors.get(key);
            if (oldWrapper == null) {
                return;
            }
            GrayValue condition = newWrapper.getCondition();
            oldWrapper.setCondition(condition);
            AbstractEnvSelector selector = oldWrapper.getSelector();
            selector.setConfig(condition.getWeight());
            newWrapper.setSelector(selector);
        });

    }

    private String buildKeyPattern(List<Pair<String, String>> conditionValues, int offset) {
        StringBuilder sb = new StringBuilder();
        //比较条件必须放在最后
        for (int i = 0; i < conditionValues.size() - offset; i++) {
            Pair<String, String> p = conditionValues.get(i);
            sb.append(p.getValue());
            if (i < (conditionValues.size() - 1 - offset)) {
                sb.append(CONDITION_SPIT);
            }
        }
        return sb.toString();
    }
}
