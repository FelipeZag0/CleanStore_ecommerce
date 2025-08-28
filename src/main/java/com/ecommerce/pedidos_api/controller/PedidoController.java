package com.ecommerce.pedidos_api.controller;

import com.ecommerce.pedidos_api.dto.AtualizacaoStatusDTO;
import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;
import com.ecommerce.pedidos_api.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // Cria um novo pedido
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criarNovoPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return pedidoService.criarPedido(pedidoRequestDTO);
    }

    // Lista todos os pedidos cadastrados
    @GetMapping
    public List<PedidoResponseDTO> listarTodosOsPedidos() {
        return pedidoService.listarTodos();
    }

    // Busca um pedido específico pelo seu ID
    @GetMapping("/{id}")
    public PedidoResponseDTO obterPedidoPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    // Atualiza o status de um pedido específico
    @PutMapping("/{id}/status")
    public PedidoResponseDTO atualizarStatusPedido(@PathVariable Long id, @RequestBody AtualizacaoStatusDTO atualizacaoStatusDTO) {
        return pedidoService.atualizarStatusPedido(id, atualizacaoStatusDTO);
    }
}