package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.mapper;

import com.dxlab.dxlabbackendapi.domain.model.Order;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request.OrdenRequest;
import com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.response.OrdenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderRestMapper {
    OrderRestMapper INSTANCE = Mappers.getMapper(OrderRestMapper.class);

    @Mapping(target="usuario", source="user")
    @Mapping(target="fecha", source="date", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target="examen", source="examination")
    @Mapping(target="resultadoListo", source="readyResult")
    @Mapping(target="muestraPendiente", source="samplePending")
    @Mapping(target ="galeriaDocumentos", ignore = true)
    @Mapping(target ="fechaResultado", ignore = true)
    OrdenResponse toOrdenResponse(Order order);
    List<OrdenResponse> toOrdenResponseList(List<Order> order);

    @Mapping(target="user", source="usuario")
    @Mapping(target="date", source="fecha", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target="examination", source="examen")
    @Mapping(target="readyResult", source="resultadoListo")
    @Mapping(target="samplePending", source="muestraPendiente")
    @Mapping(target ="dateResult", source="fechaResultado", dateFormat = "dd-MM-yyyy HH:mm:ss")
    Order toOrder(OrdenRequest ordenRequest);
}
