package com.ecommerce.pedidos_api.dto;

import com.ecommerce.pedidos_api.model.enums.StatusPedido;
import lombok.Data;

@Data
public class AtualizacaoStatusDTO {
    private StatusPedido novoStatus;
}
