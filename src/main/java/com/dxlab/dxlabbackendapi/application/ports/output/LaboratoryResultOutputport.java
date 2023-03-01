package com.dxlab.dxlabbackendapi.application.ports.output;

import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResult;
import com.dxlab.dxlabbackendapi.domain.model.LaboratoryResultInfo;

public interface LaboratoryResultOutputport {
    void uploadFiles(LaboratoryResult laboratoryResult);

    LaboratoryResultInfo getLabResultFileList(Long id);

    void deleteLabResultFile(Long id, String fileName);

    void deleteLabResultFolder(Long id);

    byte[] downloadLabResultFile(Long id, String fileName);

    byte[] downloadZipLabResultFile(Long id);
}
