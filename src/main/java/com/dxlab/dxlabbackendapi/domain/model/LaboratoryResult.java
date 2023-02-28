package com.dxlab.dxlabbackendapi.domain.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaboratoryResult {
    private Long idOrder;
    private List<LaboratoryFile> files;
}
