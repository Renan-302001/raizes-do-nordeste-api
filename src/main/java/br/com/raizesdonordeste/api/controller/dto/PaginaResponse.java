package br.com.raizesdonordeste.api.controller.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PaginaResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PaginaResponse(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public static <S, T> PaginaResponse<T> from(
            Page<S> pagina,
            Function<S, T> mapper
    ) {
        return new PaginaResponse<>(
                pagina.getContent().stream().map(mapper).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
