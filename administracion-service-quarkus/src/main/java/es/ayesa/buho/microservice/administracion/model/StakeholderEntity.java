package es.ayesa.buho.microservice.administracion.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "stakeholders")
public class StakeholderEntity {
    @BsonId
    public ObjectId id;

    public String proyectoId;
    public String nombreCompleto;
    public String email;
    public String cargoFuncion;
    public String organismoEmpresa;
    public boolean interno;
}
