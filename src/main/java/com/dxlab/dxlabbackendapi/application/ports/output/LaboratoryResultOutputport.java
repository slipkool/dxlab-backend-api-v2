package com.dxlab.dxlabbackendapi.application.ports.output;

import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;

public interface LaboratoryResultOutputport {
    void uploadFiles(LaboratoryResult laboratoryResult);
}
