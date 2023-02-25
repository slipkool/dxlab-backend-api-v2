package com.dxlab.dxlabbackendapi.domain.service;

import com.dxlab.dxlabbackendapi.application.ports.input.LaboratoryResultUseCase;
import com.dxlab.dxlabbackendapi.application.ports.output.LaboratoryResultOutputport;
import com.dxlab.dxlabbackendapi.application.ports.output.OrderOutputPort;
import com.dxlab.dxlabbackendapi.domain.exception.OrderNotFound;
import com.dxlab.dxlabbackendapi.domain.exception.OrderResultNotUpdate;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.Order;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LaboratoryResultService implements LaboratoryResultUseCase {
    private final LaboratoryResultOutputport laboratoryResultOutputport;
    private final OrderOutputPort orderOutputPort;

    @Override
    public void uploadLabResult(LaboratoryResult laboratoryResult) {
        Order order = orderOutputPort.getOrderById(laboratoryResult.getIdOrder())
                .orElseThrow(() -> new OrderNotFound("Orden no encontrada con el id: " + laboratoryResult.getIdOrder()));

        laboratoryResultOutputport.uploadFiles(laboratoryResult);

        order.setReadyResult(true);
        if(!orderOutputPort.updateDateResultOrder(order)) {
            throw new OrderResultNotUpdate("No se pudo actualizar el resultado de laboratorio de la orden: " + laboratoryResult.getIdOrder());
        }
    }
}
