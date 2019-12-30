package com.udacity.vehicles.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Vehicles REST API",
                "This API returns a several details of vehicles including Manufacturer, " +
                        "Pricing fom a pricing microservice and location from another mock location service" +
                        ".\n It uses the non-blocking webclient from the reactive project to make external requests",
                "1.0",
                "http://www.udacity.com/tos",
                new Contact("Ebuka Anazodo", "www.ebukaanazodo.com", "anazodogodwin@gmail.com"),
                "License of API", "http://www.udacity.com/license", Collections.emptyList());
    }
}
