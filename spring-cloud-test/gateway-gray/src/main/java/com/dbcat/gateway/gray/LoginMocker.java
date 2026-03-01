package com.dbcat.gateway.gray;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoginMocker {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Account> loginUsers;

    @PostConstruct
    public void init() throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("login_user_mock.json");
        List<Account> loginUser = objectMapper.readValue(resourceAsStream, new TypeReference<List<Account>>() {
        });
        loginUsers = loginUser.stream().collect(Collectors.toMap(Account::getToken, k -> k));
    }

    public Account getByToken(String token) {
        return loginUsers.get(token);
    }


    public String buildUnAuthorize() {
        RestResult restResult = RestResult.buildFailure("未登录或登录无效", 401);
        try {
            return objectMapper.writeValueAsString(restResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
