package com.ecommerce.pedidos_api.service;

import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;

public interface PedidoService {
    PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO);
}
