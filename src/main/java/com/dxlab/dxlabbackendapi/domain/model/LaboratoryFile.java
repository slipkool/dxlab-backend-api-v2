package com.dxlab.dxlabbackendapi.domain.model;

import lombok.*;

import java.io.InputStream;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LaboratoryFile {
    private String fileName;
    private String contentType;
    private Long size;
    private InputStream inputStreamFile;
}
