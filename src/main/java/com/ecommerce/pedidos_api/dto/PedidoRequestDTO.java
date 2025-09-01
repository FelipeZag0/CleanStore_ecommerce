package com.ecommerce.pedidos_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO para a criação de um novo pedido.")
public class PedidoRequestDTO {

    @Schema(description = "ID do cliente que está fazendo o pedido.", example = "1")
    private Long clienteId;

    @Schema(description = "Endereço completo para a entrega do pedido.", example = "Rua das Flores, 123, Bairro Centro, Cidade Exemplo - SP, 12345-678")
    private String enderecoEntrega;

    @Schema(description = "Lista de itens que compõem o pedido.")
    private List<ItemPedidoRequestDTO> itens;
}
