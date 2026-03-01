package com.dbcat.gray.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;


@RestController
@RequestMapping("/api")
public class ServiceController {
    protected static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private ServiceProviderClient providerClient;

    @Resource(name = "loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @GetMapping("/feign")
    public String hello(HttpServletRequest request) {
        String header = request.getHeader("x_env");
        logger.info("服务接收到的请求头x-env:{}", header);
        logger.info("调用远程服务hello");
        return providerClient.hello();
    }

    @GetMapping("/template")
    public String hello2(HttpServletRequest request) {
        String header = request.getHeader("x-env");
        logger.info("2服务接收到的请求头x-env:{}", header);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        final URI uri = URI.create("http://server-b/api/hello");
        ResponseEntity<String> rs = restTemplate.getForEntity(uri, String.class);
        return rs.getBody();
    }
}