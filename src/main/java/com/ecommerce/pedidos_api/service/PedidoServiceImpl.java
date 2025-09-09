package com.ecommerce.pedidos_api.service;

import com.ecommerce.pedidos_api.dto.AtualizacaoStatusDTO;
import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;
import com.ecommerce.pedidos_api.exception.PedidoNaoEncontradoException;
import com.ecommerce.pedidos_api.model.ItemPedido;
import com.ecommerce.pedidos_api.model.Pedido;
import com.ecommerce.pedidos_api.model.enums.StatusPedido;
import com.ecommerce.pedidos_api.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO requestDTO) {
        // 1. Mapear o DTO para a entidade Pedido
        Pedido novoPedido = new Pedido();
        novoPedido.setClienteId(requestDTO.getClienteId());
        novoPedido.setEnderecoEntrega(requestDTO.getEnderecoEntrega());
        novoPedido.setDataPedido(LocalDateTime.now());

        // 2. Definir o status inicial como "AGUARDANDO_PAGAMENTO" [cite: 52]
        novoPedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        // 3. Mapear os itens, calcular o valor total e associar ao pedido
        BigDecimal valorTotalCalculado = new BigDecimal("0.0");

        if (requestDTO.getItens() != null && !requestDTO.getItens().isEmpty()) {
            novoPedido.setItens(requestDTO.getItens().stream().map(itemDTO -> {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setProdutoId(itemDTO.getProdutoId());
                itemPedido.setDescricaoProduto(itemDTO.getDescricaoProduto());
                itemPedido.setQuantidade(itemDTO.getQuantidade());
                itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());

                // Associar o item ao pedido (essencial para o relacionamento)
                itemPedido.setPedido(novoPedido);

                // Acumular o valor total [cite: 52]
                BigDecimal subtotalItem = itemDTO.getPrecoUnitario().multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));
                // Usamos uma variável externa ao lambda para somar
                // (Note: há formas mais funcionais, mas esta é clara)
                return itemPedido;
            }).collect(Collectors.toList()));

            // Recalcula o valor total a partir da lista de itens já associada
            for(ItemPedido item : novoPedido.getItens()){
                BigDecimal subtotal = item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade()));
                valorTotalCalculado = valorTotalCalculado.add(subtotal);
            }
        }

        novoPedido.setValorTotal(valorTotalCalculado);

        // 4. Salvar o pedido e seus itens no banco de dados [cite: 19]
        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        // 5. Mapear a entidade salva para o DTO de resposta
        return mapToPedidoResponseDTO(pedidoSalvo);
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        // Busca todos os pedidos do repositório
        return pedidoRepository.findAll()
                .stream()
                // Mapeia cada entidade Pedido para um PedidoResponseDTO
                .map(this::mapToPedidoResponseDTO)
                // Coleta os resultados em uma lista
                .collect(Collectors.toList());
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        // Busca o pedido por ID ou lança a exceção customizada se não encontrar
        return pedidoRepository.findById(id)
                .map(this::mapToPedidoResponseDTO)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido com ID " + id + " não encontrado."));
    }

    private PedidoResponseDTO mapToPedidoResponseDTO(Pedido pedido) {
        PedidoResponseDTO responseDTO = new PedidoResponseDTO();
        responseDTO.setId(pedido.getId());
        responseDTO.setDataPedido(pedido.getDataPedido());
        responseDTO.setClienteId(pedido.getClienteId());
        responseDTO.setValorTotal(pedido.getValorTotal());
        responseDTO.setStatus(pedido.getStatus().name());
        responseDTO.setEnderecoEntrega(pedido.getEnderecoEntrega());
        return responseDTO;
    }

    @Override
    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, AtualizacaoStatusDTO atualizacaoStatusDTO) {
        // 1. Busca o pedido pelo ID. Se não encontrar, lança PedidoNaoEncontradoException (404)
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido com ID " + id + " não encontrado."));

        StatusPedido novoStatus = atualizacaoStatusDTO.getNovoStatus();

        // 2. Valida se a transição de status é permitida (regra de negócio)
        validarTransicaoStatus(pedido.getStatus(), novoStatus);

        // 3. Atualiza o status do pedido
        pedido.setStatus(novoStatus);

        // 4. Salva a alteração no banco de dados
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        // 5. Retorno o DTO com os dados atualizados
        return mapToPedidoResponseDTO(pedidoAtualizado);
    }

    /**
     * Metodo auxiliar para validar a transição de status do pedido
     */
    private void validarTransicaoStatus(StatusPedido statusAtual, StatusPedido novoStatus){
        // Regra de negócio: não se pode alterar um pedido que já foi entregue ou cancelado.
        if (statusAtual == StatusPedido.CANCELADO || statusAtual == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException("Não é possível alterar o status de um pedido que já foi " + statusAtual + ".");
        }
    }

    @Override
    @Transactional
    public void cancelarPedido(Long id) {
        // 1. Busca o pedido pelo ID. Se não encontrar, lança exceção PedidoNaoEncontradoException (404)
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido com ID " + id + " não encontrado."));

        // 2. Verifica se o pedido pode ainda pode ser cancelado (regra de negócio)
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException("Não é possível cancelar um pedido que já foi entregue.");
        }

        // 3. Altera o status do pedido para CANCELADO
        pedido.setStatus(StatusPedido.CANCELADO);

        // 4. Salva o pedido atualizado no banco de dados
        pedidoRepository.save(pedido);
    }
}