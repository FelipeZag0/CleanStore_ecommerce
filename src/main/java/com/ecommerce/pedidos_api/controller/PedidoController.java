package com.ecommerce.pedidos_api.controller;

import com.ecommerce.pedidos_api.dto.AtualizacaoStatusDTO;
import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;
import com.ecommerce.pedidos_api.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para Gerenciamento de Pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Cria um novo pedido", description = "Registra um novo pedido no sistema com base nos dados do cliente e itens fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criarNovoPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        return pedidoService.criarPedido(pedidoRequestDTO);
    }

    @Operation(summary = "Lista todos os pedidos", description = "Retorna uma lista de todos os pedidos cadastrados no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    @GetMapping
    public List<PedidoResponseDTO> listarTodosOsPedidos() {
        return pedidoService.listarTodos();
    }

    @Operation(summary = "Busca um pedido por ID", description = "Retorna os detalhes de um pedido específico com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado para o ID fornecido")
    })
    @GetMapping("/{id}")
    public PedidoResponseDTO obterPedidoPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @Operation(summary = "Atualiza o status de um pedido", description = "Altera o status de um pedido existente (ex: de AGUARDANDO_PAGAMENTO para PAGO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PutMapping("/{id}/status")
    public PedidoResponseDTO atualizarStatusPedido(@PathVariable Long id, @RequestBody AtualizacaoStatusDTO atualizacaoStatusDTO) {
        return pedidoService.atualizarStatusPedido(id, atualizacaoStatusDTO);
    }

    @Operation(summary = "Cancela um pedido", description = "Realiza o cancelamento (exclusão lógica) de um pedido, alterando seu status para CANCELADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível cancelar o pedido (ex: já foi enviado)"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
    }
}