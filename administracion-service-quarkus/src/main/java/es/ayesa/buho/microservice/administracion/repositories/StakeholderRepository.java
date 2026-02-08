package es.ayesa.buho.microservice.administracion.repositories;

import java.util.Optional;
import java.util.regex.Pattern;

import es.ayesa.buho.microservice.administracion.model.StakeholderEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StakeholderRepository implements PanacheMongoRepository<StakeholderEntity> {

    private static final String BY_PROYECTO_QUERY = "{ 'proyectoId': ?1 }";
    private static final String SEARCH_BY_PROYECTO_QUERY = "{ 'proyectoId': ?1, $or: [ { 'nombreCompleto': { $regex: ?2, $options: 'i' } }, { 'email': { $regex: ?2, $options: 'i' } }, { 'cargoFuncion': { $regex: ?2, $options: 'i' } }, { 'organismoEmpresa': { $regex: ?2, $options: 'i' } } ] }";

    public PanacheQuery<StakeholderEntity> findByProyectoId(String proyectoId, Sort sort) {
        return find(BY_PROYECTO_QUERY, sort, proyectoId);
    }

    public PanacheQuery<StakeholderEntity> searchByProyectoId(String proyectoId, String search, Sort sort) {
        return find(SEARCH_BY_PROYECTO_QUERY, sort, proyectoId, search);
    }

    public Optional<StakeholderEntity> findByProyectoIdAndEmailIgnoreCase(String proyectoId, String email) {
        if (proyectoId == null || proyectoId.isBlank() || email == null || email.isBlank()) {
            return Optional.empty();
        }
        String anchored = "^" + Pattern.quote(email.trim()) + "$";
        return find("{ 'proyectoId': ?1, 'email': { $regex: ?2, $options: 'i' } }", proyectoId.trim(), anchored)
                .firstResultOptional();
    }
}
