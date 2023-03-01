package com.dxlab.dxlabbackendapi.application.ports.input;

import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;

public interface LaboratoryResultUseCase {
    void uploadLabResult(LaboratoryResult laboratoryResult);

    LaboratoryResultInfo getLabResultFileList(Long orderId);

    void deleteLabResultFile(Long orderId, String fileName);

    void deleteLabResultFolder(Long orderId);

    byte[] downloadLabResultFile(Long orderId, String fileName);

    byte[] downloadZipLabResultFile(Long orderId);
}
