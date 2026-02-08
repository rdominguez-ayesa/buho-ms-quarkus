package es.ayesa.buho.microservice.administracion.services;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

import es.ayesa.buho.microservice.administracion.common.SpringPageResponse;
import es.ayesa.buho.microservice.administracion.model.ProyectoEntity;
import es.ayesa.buho.microservice.administracion.repositories.ProyectoRepository;
import es.ayesa.buho.microservice.dto.administracion.ProjectFilterDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    @Inject
    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    public ProyectoEntity findById(String id) {
        return findByIdOptional(id).orElse(null);
    }

    public java.util.Optional<ProyectoEntity> findByIdOptional(String id) {
        if (id == null || id.isBlank()) {
            return java.util.Optional.empty();
        }
        if (ObjectId.isValid(id)) {
            ProyectoEntity byObjectId = proyectoRepository.findById(new ObjectId(id));
            if (byObjectId != null) {
                return java.util.Optional.of(byObjectId);
            }
        }
        ProyectoEntity byStringId = proyectoRepository.find("_id", id).firstResult();
        return java.util.Optional.ofNullable(byStringId);
    }

    public List<ProyectoEntity> obtenerArbolPorUnidad(String unidadId) {
        List<ProyectoEntity> todos = proyectoRepository.findByUnidadId(unidadId);

        Map<String, ProyectoEntity> mapa = new HashMap<>();
        for (ProyectoEntity p : todos) {
            if (p.id != null) {
                mapa.put(p.id.toHexString(), p);
            }
        }

        for (ProyectoEntity proyecto : todos) {
            if (proyecto.subproyectos == null) {
                proyecto.subproyectos = new ArrayList<>();
            } else {
                proyecto.subproyectos.clear();
            }
        }

        List<ProyectoEntity> raiz = new ArrayList<>();
        for (ProyectoEntity proyecto : todos) {
            if (proyecto.padreId == null) {
                raiz.add(proyecto);
            } else {
                ProyectoEntity padre = mapa.get(proyecto.padreId);
                if (padre != null) {
                    padre.subproyectos.add(proyecto);
                }
            }
        }
        return raiz;
    }

    public SpringPageResponse<ProyectoEntity> findAll(String search, int page, int size, String sort, String direction) {
        // En el controlador Spring actual, el 'search' no cambia la consulta: se lista todo.
        MongoCollection<ProyectoEntity> collection = collection();
        Bson filter = new org.bson.Document();
        Bson sortBson = buildSort(sort, direction);

        long total = collection.countDocuments(filter);
        List<ProyectoEntity> results = collection.find(filter)
                .sort(sortBson)
                .skip(page * size)
                .limit(size)
                .into(new ArrayList<>());

        return SpringPageResponse.of(results, page, size, total, true);
    }

    public SpringPageResponse<ProyectoEntity> findProyectosPermitidos(ProjectFilterDTO filter, int page, int size, String sort, String direction) {
        MongoCollection<ProyectoEntity> collection = collection();

        if (filter == null || filter.getAllowedProjectIds() == null || filter.getAllowedProjectIds().isEmpty()) {
            return SpringPageResponse.of(List.of(), page, size, 0, true);
        }

        List<Bson> andFilters = new ArrayList<>();
        if (filter.getNombre() != null && !filter.getNombre().isBlank()) {
            String q = ".*" + java.util.regex.Pattern.quote(filter.getNombre()) + ".*";
            andFilters.add(Filters.regex("nombre", q, "i"));
        }
        if (filter.getEstado() != null && !filter.getEstado().isBlank()) {
            andFilters.add(Filters.eq("estado", filter.getEstado()));
        }

        List<Object> allowedIds = new ArrayList<>();
        for (String rawId : filter.getAllowedProjectIds()) {
            if (rawId == null || rawId.isBlank()) {
                continue;
            }
            String trimmed = rawId.trim();
            if (ObjectId.isValid(trimmed)) {
                allowedIds.add(new ObjectId(trimmed));
            }
            allowedIds.add(trimmed);
        }
        andFilters.add(Filters.in("_id", allowedIds));

        Bson finalFilter = andFilters.size() == 1 ? andFilters.getFirst() : Filters.and(andFilters);
        Bson sortBson = buildSort(sort, direction);

        long total = collection.countDocuments(finalFilter);
        List<ProyectoEntity> results = collection.find(finalFilter)
                .sort(sortBson)
                .skip(page * size)
                .limit(size)
                .into(new ArrayList<>());

        return SpringPageResponse.of(results, page, size, total, true);
    }

    public ProyectoEntity crearProyecto(ProyectoEntity proyecto) {
        if (proyecto != null) {
            proyecto.id = null;
        }
        proyectoRepository.persist(proyecto);
        return proyecto;
    }

    public ProyectoEntity modificarProyecto(ProyectoEntity proyecto) {
        if (proyecto == null || proyecto.id == null) {
            return proyecto;
        }
        proyectoRepository.update(proyecto);
        return proyecto;
    }

    public void actualizarProyecto(String id, ProyectoEntity proyectoActualizado) {
        ProyectoEntity existente = findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (existente.id == null) {
            throw new RuntimeException("Proyecto no encontrado");
        }

        Bson idFilter = Filters.eq("_id", existente.id);
        List<Bson> updates = new ArrayList<>();
        updates.add(Updates.set("redmineId", proyectoActualizado.redmineId));
        updates.add(Updates.set("redmineProjectName", proyectoActualizado.redmineProjectName));
        updates.add(Updates.set("anagrama", proyectoActualizado.anagrama));
        updates.add(Updates.set("nombre", proyectoActualizado.nombre));
        updates.add(Updates.set("descripcion", proyectoActualizado.descripcion));
        updates.add(Updates.set("categoria", proyectoActualizado.categoria));
        updates.add(Updates.set("presupuesto", proyectoActualizado.presupuesto));
        updates.add(Updates.set("fechaActualizacion", proyectoActualizado.fechaActualizacion));
        updates.add(Updates.set("fechaInicio", proyectoActualizado.fechaInicio));
        updates.add(Updates.set("redmineUrl", proyectoActualizado.redmineUrl));
        updates.add(Updates.set("cliente", proyectoActualizado.cliente));
        updates.add(Updates.set("estado", proyectoActualizado.estado));
        updates.add(Updates.set("fechaFin", proyectoActualizado.fechaFin));

        collection().updateOne(idFilter, Updates.combine(updates));
    }

    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        if (ObjectId.isValid(id)) {
            proyectoRepository.deleteById(new ObjectId(id));
            return;
        }
        proyectoRepository.delete("_id", id);
    }

    public String calcularPeriodo(ProyectoEntity p) {
        if (p == null || p.fechaInicio == null || p.fechaFin == null) {
            return null;
        }
        String startYear = String.valueOf(p.fechaInicio.atZone(ZoneId.systemDefault()).getYear());
        String endYear = String.valueOf(p.fechaFin.atZone(ZoneId.systemDefault()).getYear());
        return startYear + "-" + endYear;
    }

    private MongoCollection<ProyectoEntity> collection() {
        return mongoClient.getDatabase(database).getCollection("proyectos", ProyectoEntity.class);
    }

    private static Bson buildSort(String sort, String direction) {
        String field = (sort == null || sort.isBlank()) ? "id" : sort;
        if (field.equals("id")) {
            field = "_id";
        }
        boolean desc = direction != null && direction.equalsIgnoreCase("desc");
        return desc ? Sorts.descending(field) : Sorts.ascending(field);
    }
}
