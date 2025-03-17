package com.chase.portfolio;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.annotation.PostConstruct;

@Component
public class StaticResourceLogger {

    private final RequestMappingHandlerMapping handlerMapping;

    public StaticResourceLogger(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @PostConstruct
    public void logStaticResources() {
        System.out.println("===== Registered Static Resources =====");
        handlerMapping.getHandlerMethods().forEach((key, value) -> {
            System.out.println(key + " -> " + value);
        });
    }
}
