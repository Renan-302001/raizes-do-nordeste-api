package br.com.raizesdonordeste.api.controller.dto;

import br.com.raizesdonordeste.api.domain.model.Cardapio;
import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import br.com.raizesdonordeste.api.domain.model.StatusCardapio;

import java.util.List;
import java.util.UUID;

public class CardapioResponse {

    private final UUID idCardapio;
    private final UUID idUnidade;
    private final String nome;
    private final StatusCardapio statusCardapio;
    private final List<ItemCardapioResponse> itens;

    public CardapioResponse(Cardapio cardapio, List<ItemCardapio> itens) {
        this.idCardapio = cardapio.getIdCardapio();
        this.idUnidade = cardapio.getUnidade().getIdUnidade();
        this.nome = cardapio.getNome();
        this.statusCardapio = cardapio.getStatusCardapio();
        this.itens = itens.stream().map(ItemCardapioResponse::new).toList();
    }

    public UUID getIdCardapio() {
        return idCardapio;
    }

    public UUID getIdUnidade() {
        return idUnidade;
    }

    public String getNome() {
        return nome;
    }

    public StatusCardapio getStatusCardapio() {
        return statusCardapio;
    }

    public List<ItemCardapioResponse> getItens() {
        return itens;
    }
}
