package es.ayesa.buho.microservice.administracion.dto;

import java.util.Date;
import java.util.List;

import org.bson.types.Binary;

import es.ayesa.buho.microservice.dto.seguimiento.Acuerdo;
import es.ayesa.buho.microservice.dto.seguimiento.HistorialAcuerdo;
import es.ayesa.buho.microservice.dto.seguimiento.TemaDiscusion;
import es.ayesa.buho.microservice.model.mongo.administracion.TipoActa;
import es.ayesa.buho.microservice.model.mongo.seguimiento.ArchivoBinario;
import lombok.Data;

@Data
public class ActaReunionDTO {
    private String id;
    private String seguimientoId;
    private TipoActa tipo;
    private Date fechaReunion;
    private List<String> asistentes;
    private String ordenDelDia;
    private List<TemaDiscusion> temasDiscutidos;
    private String lugarReunion;
    private List<Acuerdo> acuerdosAlcanzados;
    private List<Acuerdo> acuerdosPendientes;
    private List<HistorialAcuerdo> historialAcuerdos;
    private ArchivoBinario video;
    private ArchivoBinario audio;
    private Binary transcripcionTexto;
}
