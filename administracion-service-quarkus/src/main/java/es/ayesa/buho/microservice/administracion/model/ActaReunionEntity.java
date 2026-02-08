package es.ayesa.buho.microservice.administracion.model;

import java.util.Date;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import es.ayesa.buho.microservice.dto.seguimiento.Acuerdo;
import es.ayesa.buho.microservice.dto.seguimiento.HistorialAcuerdo;
import es.ayesa.buho.microservice.dto.seguimiento.TemaDiscusion;
import es.ayesa.buho.microservice.model.mongo.administracion.TipoActa;
import es.ayesa.buho.microservice.model.mongo.seguimiento.ArchivoBinario;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "actasReuniones")
public class ActaReunionEntity {

    @BsonId
    public ObjectId id;

    public String seguimientoId;
    public TipoActa tipo;
    public Date fechaReunion;
    public List<String> asistentes;
    public String ordenDelDia;
    public List<TemaDiscusion> temasDiscutidos;
    public String lugarReunion;
    public List<Acuerdo> acuerdosAlcanzados;
    public List<Acuerdo> acuerdosPendientes;
    public List<HistorialAcuerdo> historialAcuerdos;
    public ArchivoBinario video;
    public ArchivoBinario audio;
    public Binary transcripcionTexto;
}
