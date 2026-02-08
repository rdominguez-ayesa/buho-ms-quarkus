package es.ayesa.buho.microservice.administracion.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonFormat;

import es.ayesa.buho.microservice.dto.administracion.ConfigProyecto;
import es.ayesa.buho.microservice.dto.administracion.EstadoBuho;
import es.ayesa.buho.microservice.dto.administracion.TipoPeticionBuho;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "proyectos")
public class ProyectoEntity {
    @BsonId
    public ObjectId id;

    public String padreId;
    public String unidadId;

    public Integer redmineId;
    public String redmineProjectName;
    public String anagrama;
    public String nombre;
    public String descripcion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    public LocalDateTime fechaActualizacion;

    public String redmineUrl;
    public String categoria;
    public double presupuesto;
    public String cliente;
    public String estado;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime fechaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime fechaFin;

    @BsonIgnore
    public List<ProyectoEntity> subproyectos = new ArrayList<>();

    public List<TipoPeticionBuho> tiposPeticionBuho = new ArrayList<>();
    public List<EstadoBuho> estadosBuho = new ArrayList<>();

    public ConfigProyecto configProyecto;
}
