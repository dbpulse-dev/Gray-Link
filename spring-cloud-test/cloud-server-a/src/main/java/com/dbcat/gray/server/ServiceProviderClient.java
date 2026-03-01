package com.dbcat.gray.server;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("server-b")
public interface ServiceProviderClient {
    @GetMapping("/api/hello")
    String hello();
}