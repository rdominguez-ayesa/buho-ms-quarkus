package es.ayesa.buho.microservice.administracion.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "usuarios")
public class UsuarioEntity {

    @BsonId
    public ObjectId id;

    public String userName;
    public String nombre;
    public String email;

    public String redmineUsername;
    public String redmineApiKey;
    public String redminePassword;

    public List<String> proyectosId;
    public List<String> roles;

    public String telefono;
    public String residencia;

    public boolean activo = true;

    public String unidadOrganizativaId;

    public List<UsuarioVacacionEntity> vacaciones = new ArrayList<>();
}
