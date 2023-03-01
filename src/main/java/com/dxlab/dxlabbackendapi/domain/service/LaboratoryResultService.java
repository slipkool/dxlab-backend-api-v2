package com.dxlab.dxlabbackendapi.domain.service;

import com.dxlab.dxlabbackendapi.application.ports.input.LaboratoryResultUseCase;
import com.dxlab.dxlabbackendapi.application.ports.output.LaboratoryResultOutputport;
import com.dxlab.dxlabbackendapi.application.ports.output.OrderOutputPort;
import com.dxlab.dxlabbackendapi.domain.exception.OrderNotFound;
import com.dxlab.dxlabbackendapi.domain.exception.OrderResultNotUpdate;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;
import com.dxlab.dxlabbackendapi.domain.model.Order;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LaboratoryResultService implements LaboratoryResultUseCase {
    private final LaboratoryResultOutputport laboratoryResultOutputport;
    private final OrderOutputPort orderOutputPort;

    @Override
    public void uploadLabResult(LaboratoryResult laboratoryResult) {
        Order order = getOrder(laboratoryResult.getOrderId());
        laboratoryResultOutputport.uploadFiles(laboratoryResult);

        order.setReadyResult(true);
        if(!orderOutputPort.updateDateResultOrder(order)) {
            throw new OrderResultNotUpdate("No se pudo actualizar el resultado de laboratorio de la orden: " + laboratoryResult.getOrderId());
        }
    }

    @Override
    public LaboratoryResultInfo getLabResultFileList(Long orderId) {
        Order order = getOrder(orderId);
        return laboratoryResultOutputport.getLabResultFileList(order.getId());
    }

    @Override
    public void deleteLabResultFile(Long orderId, String fileName) {
        Order order = getOrder(orderId);
        laboratoryResultOutputport.deleteLabResultFile(order.getId(), fileName);
    }

    @Override
    public void deleteLabResultFolder(Long orderId) {
        Order order = getOrder(orderId);
        laboratoryResultOutputport.deleteLabResultFolder(order.getId());
    }

    @Override
    public byte[] downloadLabResultFile(Long orderId, String fileName) {
        Order order = getOrder(orderId);
        return laboratoryResultOutputport.downloadLabResultFile(order.getId(), fileName);
    }

    @Override
    public byte[] downloadZipLabResultFile(Long orderId) {
        Order order = getOrder(orderId);
        return laboratoryResultOutputport.downloadZipLabResultFile(order.getId());
    }

    private Order getOrder(Long orderId) {
        return orderOutputPort.getOrderById(orderId)
                .orElseThrow(() -> new OrderNotFound("Orden no encontrada con el id: " + orderId));
    }
}
