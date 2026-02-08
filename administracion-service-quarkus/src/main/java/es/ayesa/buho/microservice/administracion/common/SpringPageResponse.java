package es.ayesa.buho.microservice.administracion.common;

import java.util.List;

/**
 * DTO de respuesta que replica la forma JSON típica de Spring Data Page.
 *
 * No depende de Spring; sólo mantiene el contrato de serialización.
 */
public class SpringPageResponse<T> {

    public List<T> content;
    public Pageable pageable;
    public long totalElements;
    public int totalPages;
    public boolean last;
    public int size;
    public int number;
    public Sort sort;
    public boolean first;
    public int numberOfElements;
    public boolean empty;

    public static <T> SpringPageResponse<T> of(List<T> content, int page, int size, long totalElements, boolean sorted) {
        SpringPageResponse<T> response = new SpringPageResponse<>();
        response.content = content;
        response.number = page;
        response.size = size;
        response.totalElements = totalElements;
        response.numberOfElements = content == null ? 0 : content.size();
        response.empty = response.numberOfElements == 0;
        response.totalPages = size > 0 ? (int) Math.ceil(totalElements / (double) size) : 1;
        response.first = page <= 0;
        response.last = response.totalPages == 0 || page >= (response.totalPages - 1);

        Sort sortObj = new Sort();
        sortObj.sorted = sorted;
        sortObj.unsorted = !sorted;
        sortObj.empty = !sorted;
        response.sort = sortObj;

        Pageable pageableObj = new Pageable();
        pageableObj.pageNumber = page;
        pageableObj.pageSize = size;
        pageableObj.offset = (long) page * (long) size;
        pageableObj.paged = true;
        pageableObj.unpaged = false;
        pageableObj.sort = sortObj;
        response.pageable = pageableObj;

        return response;
    }

    public static class Pageable {
        public Sort sort;
        public int pageNumber;
        public int pageSize;
        public long offset;
        public boolean paged;
        public boolean unpaged;
    }

    public static class Sort {
        public boolean empty;
        public boolean sorted;
        public boolean unsorted;
    }
}
