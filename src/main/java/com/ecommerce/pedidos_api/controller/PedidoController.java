package com.ecommerce.pedidos_api.controller;

import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;
import com.ecommerce.pedidos_api.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criarNovoPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return pedidoService.criarPedido(pedidoRequestDTO);
    }
}