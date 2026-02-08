package es.ayesa.buho.microservice.administracion.model;

import java.util.Date;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "actasReuniones")
public class SeguimientoEntity {

    @BsonId
    public ObjectId id;

    public String proyectoId;
    public String descripcion;
    public Date fecha;
    public List<String> actasIds;
}
