package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.api.application.CardapioService;
import br.com.raizesdonordeste.api.controller.dto.CadastroCardapioRequest;
import br.com.raizesdonordeste.api.controller.dto.CadastroItemCardapioRequest;
import br.com.raizesdonordeste.api.controller.dto.CardapioResponse;
import br.com.raizesdonordeste.api.controller.dto.ItemCardapioResponse;
import br.com.raizesdonordeste.api.domain.model.Cardapio;
import br.com.raizesdonordeste.api.domain.model.ItemCardapio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CardapioController {

    private final CardapioService cardapioService;

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    @PostMapping("/unidades/{idUnidade}/cardapios")
    public ResponseEntity<CardapioResponse> cadastrar(
            @PathVariable UUID idUnidade,
            @Valid @RequestBody CadastroCardapioRequest request
    ) {
        Cardapio cardapio = cardapioService.cadastrar(
                idUnidade,
                request.getNome(),
                request.isAtivo()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CardapioResponse(cardapio, List.of()));
    }

    @PostMapping("/cardapios/{idCardapio}/itens")
    public ResponseEntity<ItemCardapioResponse> adicionarItem(
            @PathVariable UUID idCardapio,
            @Valid @RequestBody CadastroItemCardapioRequest request
    ) {
        ItemCardapio item = cardapioService.adicionarItem(
                idCardapio,
                request.getIdProduto(),
                request.getPreco(),
                request.getInicioVigencia(),
                request.getFimVigencia()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ItemCardapioResponse(item));
    }

    @GetMapping("/unidades/{idUnidade}/cardapios/ativo")
    public CardapioResponse buscarAtivo(@PathVariable UUID idUnidade) {
        Cardapio cardapio = cardapioService.buscarAtivo(idUnidade);

        return new CardapioResponse(
                cardapio,
                cardapioService.listarItens(cardapio.getIdCardapio())
        );
    }
}
