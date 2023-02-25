package com.dxlab.dxlabbackendapi.domain.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaboratoryResult {
    private Long idOrder;
    private MultipartFile[] files;
}
