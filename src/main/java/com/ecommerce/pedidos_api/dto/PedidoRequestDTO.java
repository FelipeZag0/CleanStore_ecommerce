package com.ecommerce.pedidos_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class PedidoRequestDTO {
    private Long clienteId;
    private String enderecoEntrega;
    private List<ItemPedidoRequestDTO> itens;
}
