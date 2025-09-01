package com.ecommerce.pedidos_api.controller;

import com.ecommerce.pedidos_api.dto.ItemPedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class PedidoControllerIntegrationTest {

    // Definição contêiner do PostgreSQL
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // Configurar dinamicamente as propriedades [cite: 77]
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername); // [cite: 78]
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarUmPedidoEDepoisBuscaLoComSucesso() throws Exception {
        // Arrange: Preparar o DTO para a requisição de criação
        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO();
        itemDTO.setProdutoId(10L);
        itemDTO.setDescricaoProduto("Produto Teste");
        itemDTO.setPrecoUnitario(new BigDecimal("150.00"));
        itemDTO.setQuantidade(1);

        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setEnderecoEntrega("Rua Teste, 123");
        requestDTO.setItens(Collections.singletonList(itemDTO));

        // -- Parte 1: Testar o POST /pedidos --
        // Act & Assert (Criar Pedido)
        MvcResult postResult = mockMvc.perform(post("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.valorTotal").value(150.00))
                .andReturn();

        // Extrair o ID do pedido criado a partir da resposta
        String responseBody = postResult.getResponse().getContentAsString();
        Integer idCriado = com.jayway.jsonpath.JsonPath.read(responseBody, "$.id");

        // -- Parte 2: Testar o GET /pedidos/{id} --
        // Act & Assert (Buscar Pedido)
        mockMvc.perform(get("/pedidos/" + idCriado))
                .andExpect(status().isOk()) // Verifica se o status é 200 OK
                .andExpect(jsonPath("$.id").value(idCriado))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }
}
