package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper;

import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.OrdenRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.OrdenResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

class OrderRestMapperTest {

    public static final String USER_PEPITO_PEREZ = "Pepito perez";
    public static final String USER_JOSE_JOSE = "Jose jose";
    private final OrderRestMapper mapper = Mappers.getMapper(OrderRestMapper.class);

    @Test
    void shouldMapDomainToDataResponse() {
        List<Order> orderList = List.of(
                Order.builder().id(1L).user(USER_PEPITO_PEREZ).build(),
                Order.builder().id(2L).user(USER_JOSE_JOSE).build()
        );
        List<OrdenResponse> result = mapper.toOrdenResponseList(orderList);

        assertEquals("El mapeo del id de la orden es incorrecto", orderList.get(0).getId(), result.get(0).getId());
        assertEquals("El mapeo del usuario es incorrecto", orderList.get(0).getUser(), result.get(0).getUsuario());
    }

    @Test
    void shouldMapDataRequestToDomain() {
        OrdenRequest ordenRequest = OrdenRequest.builder()
                .id(1L)
                .usuario(USER_PEPITO_PEREZ)
                .build();

        Order result = mapper.toOrder(ordenRequest);

        assertEquals("El mapeo del id de la orden es incorrecto", ordenRequest.getId(), result.getId());
        assertEquals("El mapeo del usuario es incorrecto", ordenRequest.getUsuario(), result.getUser());
    }
}