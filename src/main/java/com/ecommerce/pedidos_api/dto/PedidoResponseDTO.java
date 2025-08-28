package com.ecommerce.pedidos_api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoResponseDTO {
    private Long id;
    private LocalDateTime dataPedido;
    private Long clienteId;
    private BigDecimal valorTotal;
    private String status;
    private String enderecoEntrega;
}
