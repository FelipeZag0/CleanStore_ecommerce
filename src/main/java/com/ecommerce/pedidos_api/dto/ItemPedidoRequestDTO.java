package com.ecommerce.pedidos_api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemPedidoRequestDTO {
    private Long produtoId;
    private String descricaoProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}
