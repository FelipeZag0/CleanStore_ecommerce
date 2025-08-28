package com.ecommerce.pedidos_api.service;

import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;

import java.util.List;

public interface PedidoService {
    PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO);

    List<PedidoResponseDTO> listarTodos();
    PedidoResponseDTO buscarPorId(Long id);
}
