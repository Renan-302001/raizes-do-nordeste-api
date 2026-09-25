package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CriarPedidoRequest {

    @NotNull(message = "A unidade é obrigatória.")
    private UUID idUnidade;

    @NotNull(message = "O canal do pedido é obrigatório.")
    private CanalPedido canalPedido;

    @Valid
    @NotEmpty(message = "O pedido deve possuir ao menos um item.")
    private List<ItemPedidoRequest> itens = new ArrayList<>();

    public UUID getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(UUID idUnidade) {
        this.idUnidade = idUnidade;
    }

    public CanalPedido getCanalPedido() {
        return canalPedido;
    }

    public void setCanalPedido(CanalPedido canalPedido) {
        this.canalPedido = canalPedido;
    }

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
}
