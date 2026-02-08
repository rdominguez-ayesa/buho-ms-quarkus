package es.ayesa.buho.microservice.administracion.repositories;

import java.util.List;

import es.ayesa.buho.microservice.administracion.model.SeguimientoEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SeguimientoRepository implements PanacheMongoRepository<SeguimientoEntity> {
    public List<SeguimientoEntity> findByProyectoId(String proyectoId) {
        return list("proyectoId", proyectoId);
    }
}
