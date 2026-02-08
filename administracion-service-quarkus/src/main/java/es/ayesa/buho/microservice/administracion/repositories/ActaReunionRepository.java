package es.ayesa.buho.microservice.administracion.repositories;

import java.util.List;

import es.ayesa.buho.microservice.administracion.model.ActaReunionEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActaReunionRepository implements PanacheMongoRepository<ActaReunionEntity> {
    public List<ActaReunionEntity> findBySeguimientoId(String seguimientoId) {
        return list("seguimientoId", seguimientoId);
    }
}
