package com.ecommerce.pedidos_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Pedidos API",
        version = "1.0",
        description = "API de pedidos em Spring Boot" // [cite: 92]
))
public class PedidosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PedidosApiApplication.class, args);
    }

}