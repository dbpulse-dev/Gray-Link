package com.dbcat.gray;

import com.dbcat.gray.filter.Context;
import com.dbcat.gray.filter.dto.GrayStrategyConfig;
import com.dbcat.gray.filter.dto.GrayValue;
import com.dbcat.gray.filter.dto.TrafficCount;
import com.dbcat.gray.filter.parser.CompositeScopeValueParser;
import com.dbcat.gray.filter.parser.CookieValueParser;
import com.dbcat.gray.filter.parser.HeadValueParser;
import com.dbcat.gray.filter.parser.ScopeValueParser;
import com.dbcat.gray.filter.selector.SelectorManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvSelectorManagerTest {

    private SelectorManager selectorManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
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


    @Test
    public void selectEnv() {
        Context context = mockContext("1234567");
        Boolean env = selectorManager.selectEnv(context);
        assert env != null;
    }

    @Test
    public void selectEnv2() {
        Context context = mockContext("12345678");
        Boolean env = selectorManager.selectEnv(context);
        assert env == null;
    }


    @Test
    public void selectEnv3() throws JsonProcessingException {
        GrayValue value = new GrayValue();
        value.setValue("1234567");
        value.setType("user");
        Context context = mockContext(value.getValue());
        for (int i = 0; i < 970; i++) {
            selectorManager.selectEnv(context);
        }
        List<TrafficCount> countByValue = selectorManager.getCounts();
        System.out.println("结果:" + objectMapper.writeValueAsString(countByValue));
    }

    @Test
    public void hashWeight() throws JsonProcessingException {
        for (int i = 0; i < 100000; i++) {
            Context context = mockContext("" + i);
            selectorManager.selectEnv(context);
        }
        List<TrafficCount> countByValue = selectorManager.getCounts();
        System.out.println("结果:" + objectMapper.writeValueAsString(countByValue));
    }


    private Context mockContext(String userId) {
        Context context = new Context() {
            private Map<String, String> headers = new HashMap<>();

            private Map<String, Object> attr = new HashMap<>();

            @Override
            public String getRequestHeader(String key) {
                return headers.get(key);
            }

            @Override
            public void setRequestHeader(String key, String value) {
                this.headers.put(key, value);
            }

            @Override
            public String getCookie(String name) {
                return null;
            }

            @Override
            public void setResponseHeader(String name, String value) {

            }

            @Override
            public Object getAttr(String key) {
                return attr.get(key);
            }

            @Override
            public void setAttr(String key, Object value) {
                attr.put(key, value);
            }
        };
        context.setRequestHeader("user-id", userId);
        return context;
    }
}
