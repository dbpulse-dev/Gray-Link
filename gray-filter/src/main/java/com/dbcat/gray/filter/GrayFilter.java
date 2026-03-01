package com.dbcat.gray.filter;

import com.dbcat.gray.filter.dto.GrayStrategyConfig;
import com.dbcat.gray.filter.dto.GrayValue;
import com.dbcat.gray.filter.parser.CompositeScopeValueParser;
import com.dbcat.gray.filter.parser.CookieValueParser;
import com.dbcat.gray.filter.parser.HeadValueParser;
import com.dbcat.gray.filter.parser.ScopeValueParser;
import com.dbcat.gray.filter.selector.SelectorManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dbcat.gray.filter.RouteLabelUtils.ROUTE_LABEL;

@Component
public class GrayFilter {
    protected static final Logger logger = LoggerFactory.getLogger(GrayFilter.class);

    private SelectorManager selectorManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() throws IOException, InstantiationException, IllegalAccessException {
        List<ScopeValueParser> valueParsers = Arrays.asList(new HeadValueParser(), new CookieValueParser());
        CompositeScopeValueParser valueParser = new CompositeScopeValueParser(valueParsers);
        Map<String, String> grayTypes = new HashMap<>();
        grayTypes.put("user", "用户");
        grayTypes.put("header", "请求头");
        selectorManager = new SelectorManager(valueParser, grayTypes);

        InputStream strategyIS = getClass().getClassLoader().getResourceAsStream("gray_strategy_config.json");
        List<GrayStrategyConfig> strategyConfigs = objectMapper.readValue(strategyIS, new TypeReference<List<GrayStrategyConfig>>() {
        });
        selectorManager.setStrategyConfigs(strategyConfigs);
        //条件值
        InputStream valueIS = getClass().getClassLoader().getResourceAsStream("gray_values.json");
        List<GrayValue> grayValues = objectMapper.readValue(valueIS, new TypeReference<List<GrayValue>>() {
        });
        selectorManager.setConfig(grayValues);
    }

    public void filter(Context context) {
        Boolean env = selectorManager.selectEnv(context);
        if (env != null && env) {
            logger.info("灰度用户");
            RouteLabelUtils.set();
            context.setRequestHeader(ROUTE_LABEL, "1");
            context.setResponseHeader("is-gray", "true");
        } else {
            RouteLabelUtils.remove();
            context.setResponseHeader("is-gray", "false");
        }
    }
}
