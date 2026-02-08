package es.ayesa.buho.microservice.administracion.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SeguimientoDTO {
    private String id;
    private String proyectoId;
    private String descripcion;
    private Date fecha;
    private List<String> actasIds;
}
