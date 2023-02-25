package com.dxlab.dxlabbackendapi.infrastructure.adapters.input.rest.data.request;

import com.dxlab.dxlabbackendapi.domain.exception.LaboratoryResultException;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "archivos")
public class ResultadoLaboratorioRequest {
    @NotNull(message = "El id de la orden no debe ser vacio")
    private Long idOrden;
    @NotNull(message = "Debe cargar al menos un archivo")
    private MultipartFile[] archivos;

    public void validateSelf(int maxLengthFiles, int maxFileSizeMb) {
        if (archivos.length > maxLengthFiles)
            throw new LaboratoryResultException("No se pueden cargar más de 3 archivos");

        validateFiles(archivos, maxFileSizeMb);
    }

    private void validateFiles(MultipartFile[] files, int maxFileSizeMb) {
        for(MultipartFile file: files){
            if(file.getSize() > (long) maxFileSizeMb * 1024 * 1024) {
                throw new LaboratoryResultException("Los archivos deben ser menores a 3MB");
            }

            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!isSupportedExtension(extension))
                throw new LaboratoryResultException("La extensión del documento no es válido(png, jpg, jpeg, pdf)");

            String contentType = file.getContentType();
            if (contentType == null || !isSupportedContentType(contentType)) {
                throw new LaboratoryResultException("El tipo de contenido del documento no es válido(png, jpg, jpeg, pdf)");
            }
        }
    }

    private boolean isSupportedExtension(String extension) {
        return extension != null && (
                extension.equals("png")
                        || extension.equals("jpg")
                        || extension.equals("jpeg")
                        || extension.equals("pdf"));
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("application/pdf")
                || contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
