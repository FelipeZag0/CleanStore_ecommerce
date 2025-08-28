package com.ecommerce.pedidos_api.service;

import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;
import com.ecommerce.pedidos_api.model.ItemPedido;
import com.ecommerce.pedidos_api.model.Pedido;
import com.ecommerce.pedidos_api.model.enums.StatusPedido;
import com.ecommerce.pedidos_api.repository.PedidoRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    @Override
    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO) {
        // 1. Mapear o DTO para entidade Pedido
        Pedido novoPedido = new Pedido();
        novoPedido.setClienteId(requestDTO.getClienteId());
        novoPedido.setEnderecoEntrega(requestDTO.getEnderecoEntrega());
        novoPedido.setItensPedido(requestDTO.getItensPedido());

        // 2. Definir o status inicial como "AGUARDANDO_PAGAMENTO"
        novoPedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        // 3. Mapear os itens, calcular o valor total e associar o pedido
        BigDecimal valorTotalCalculado = new BigDecimal("0.0");

        if (requestDTO.getItens() != null && !requestDTO.getItens().isEmpty()) {
            novoPedido.setItens(requestDTO.getItens().stream().map(itemDTO -> {
                ItemPedido itemPedido = new ItemPedido();
                ItemPedido.setProdutoId(itemDTO.getProdutoId());
                ItemPedido.setDescricaoProduto(itemDTO.getDescricaoProduto());
                ItemPedido.setQunatidade(itemDTO.getQuantidade());
                ItemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());

                // Associar o item ao pedido (essencial para o relacionamento)
                itemPedido.setPedido(novoPedido);

                // Acumular o valor total do pedido
                BigDecimal subtotalItem = itemDTO.getPrecoUnitario().multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));
                // Usamos uma variável externa ao lambda para somar
                return itemPedido;
            }).collect(Collectors.toList()));

            // Recalcula o valor total a partir da lista de itens já associada
            for(ItemPedido item : novoPedido.getItens()) {
                BigDecimal subtotal = item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade()));
                valorTotalCalculado = valorTotalCalculado.add(subtotal);
            }
        }

        novoPedido.setValorTotal(valorTotalCalculado);

        // 4. Salvar o pedido e seus itens no banco de dados
        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        // 5. Mapear a entidade salva para o DTO da resposta
        return mapToPedidoResponseDTO(pedidoSalvo);
    }

    private PedidoResponseDTO mapToPedidoResponseDTO(Pedido pedido) {
        PedidoResponseDTO responseDTO = new PedidoResponseDTO();
        responseDTO.setId(pedido.getId());
        responseDTO.setDataCriacao(pedido.getDataCriacao());
        responseDTO.setClienteId(pedido.getClienteId());
        responseDTO.setValorTotal(pedido.getValorTotal());
        responseDTO.setStatus(pedido.getStatus().name());
        responseDTO.setEnderecoEntrega(pedido.getEnderecoEntrega());
        return responseDTO;
    }
}
