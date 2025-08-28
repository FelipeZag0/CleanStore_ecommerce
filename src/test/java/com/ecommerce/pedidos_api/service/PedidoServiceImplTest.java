package com.ecommerce.pedidos_api.service;

import com.ecommerce.pedidos_api.dto.AtualizacaoStatusDTO;
import com.ecommerce.pedidos_api.dto.ItemPedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoRequestDTO;
import com.ecommerce.pedidos_api.dto.PedidoResponseDTO;
import com.ecommerce.pedidos_api.exception.PedidoNaoEncontradoException;
import com.ecommerce.pedidos_api.model.Pedido;
import com.ecommerce.pedidos_api.model.enums.StatusPedido;
import com.ecommerce.pedidos_api.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Test
    void deveCriarPedidoECalcularValorTotalCorretamente() {
        // Arrange (Preparação)
        ItemPedidoRequestDTO item1 = new ItemPedidoRequestDTO();
        item1.setPrecoUnitario(new BigDecimal("50.00"));
        item1.setQuantidade(2); // 2 unidades de R$ 50,00 = R$ 100,00

        ItemPedidoRequestDTO item2 = new ItemPedidoRequestDTO();
        item2.setPrecoUnitario(new BigDecimal("35.00"));
        item2.setQuantidade(3); // 3 unidades de R$ 35,00 = R$ 105,00

        PedidoRequestDTO pedidoRequestDTO = new PedidoRequestDTO();
        pedidoRequestDTO.setItens(java.util.Arrays.asList(item1, item2));

        // Mock do save para retornar o pedido com um ID
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedidoSalvo = invocation.getArgument(0);
            pedidoSalvo.setId(1L);
            return pedidoSalvo;
        });

        // Act (Ação)
        PedidoResponseDTO pedidoResponseDTO = pedidoService.criarPedido(pedidoRequestDTO);

        // Assert (Verificação)
        assertNotNull(pedidoResponseDTO);
        assertEquals(new BigDecimal("170.00"), pedidoResponseDTO.getValorTotal()); // Verifica se o valor total do pedido é calculado corretamente
        verify(pedidoRepository, times(1)).save(any(Pedido.class)); // Verifica se o metodo save foi chamado uma vez
    }

    @Test
    void deveLancarPedidoNaoEncontradoExceptionQuandoIdNaoExiste() {
        // Arrange (Preparação)
        Long idInexistente = 99L;
        when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert (Ação & Verificação)
        assertThrows(PedidoNaoEncontradoException.class, () -> {
            pedidoService.buscarPorId(idInexistente);
        });
    }

    @Test
    void deveLancarExceptionAoTentarAtualizarStatusDePedidoJaEnviado() {
        // Arrange (Preparação)
        Long pedidoId = 1L;
        Pedido pedidoEnviado = new Pedido();
        pedidoEnviado.setId(pedidoId);
        pedidoEnviado.setStatus(StatusPedido.ENVIADO);

        AtualizacaoStatusDTO atualizacaoStatusDTO = new AtualizacaoStatusDTO();
        atualizacaoStatusDTO.setNovoStatus(StatusPedido.ENTREGUE);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoEnviado));

        // Simular a transição para um estado inválido a partir do ENVIADO
        atualizacaoStatusDTO.setNovoStatus(StatusPedido.ENTREGUE);

        // Act & Assert (Ação & Verificação)
        assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.atualizarStatusPedido(pedidoId, atualizacaoStatusDTO);
        });
    }
}
