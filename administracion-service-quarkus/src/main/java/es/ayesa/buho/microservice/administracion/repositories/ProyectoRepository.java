package es.ayesa.buho.microservice.administracion.repositories;

import java.util.List;

import es.ayesa.buho.microservice.administracion.model.ProyectoEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProyectoRepository implements PanacheMongoRepository<ProyectoEntity> {

    public List<ProyectoEntity> findByUnidadId(String unidadId) {
        return list("unidadId", unidadId);
    }
}
