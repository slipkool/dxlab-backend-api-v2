package com.dxlab.dxlabbackendapi.application.ports.input;

import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;

public interface LaboratoryResultUseCase {
    void uploadLabResult(LaboratoryResult laboratoryResult);
}
