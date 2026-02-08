package es.ayesa.buho.microservice.administracion.services;

import java.util.List;

import org.bson.types.ObjectId;

import es.ayesa.buho.microservice.administracion.dto.SeguimientoDTO;
import es.ayesa.buho.microservice.administracion.model.SeguimientoEntity;
import es.ayesa.buho.microservice.administracion.repositories.SeguimientoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SeguimientoService {

    private final SeguimientoRepository seguimientoRepository;

    @Inject
    public SeguimientoService(SeguimientoRepository seguimientoRepository) {
        this.seguimientoRepository = seguimientoRepository;
    }

    public List<SeguimientoDTO> getAll() {
        return seguimientoRepository.listAll().stream().map(SeguimientoService::toDto).toList();
    }

    public List<SeguimientoDTO> getByProyectoId(String proyectoId) {
        if (proyectoId == null || proyectoId.isBlank()) {
            return List.of();
        }
        return seguimientoRepository.findByProyectoId(proyectoId.trim()).stream().map(SeguimientoService::toDto).toList();
    }

    public SeguimientoDTO crear(SeguimientoDTO dto) {
        if (dto != null) {
            dto.setId(null);
        }
        SeguimientoEntity entity = toEntity(dto);
        entity.id = null;
        seguimientoRepository.persist(entity);
        return toDto(entity);
    }

    public boolean existsById(String id) {
        return findByIdOptional(id).isPresent();
    }

    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        if (ObjectId.isValid(id)) {
            seguimientoRepository.deleteById(new ObjectId(id));
            return;
        }
        seguimientoRepository.delete("_id", id);
    }

    private java.util.Optional<SeguimientoEntity> findByIdOptional(String id) {
        if (id == null || id.isBlank()) {
            return java.util.Optional.empty();
        }
        if (ObjectId.isValid(id)) {
            SeguimientoEntity byObjectId = seguimientoRepository.findById(new ObjectId(id));
            if (byObjectId != null) {
                return java.util.Optional.of(byObjectId);
            }
        }
        SeguimientoEntity byStringId = seguimientoRepository.find("_id", id).firstResult();
        return java.util.Optional.ofNullable(byStringId);
    }

    private static SeguimientoDTO toDto(SeguimientoEntity entity) {
        if (entity == null) {
            return null;
        }
        SeguimientoDTO dto = new SeguimientoDTO();
        dto.setId(entity.id == null ? null : entity.id.toHexString());
        dto.setProyectoId(entity.proyectoId);
        dto.setDescripcion(entity.descripcion);
        dto.setFecha(entity.fecha);
        dto.setActasIds(entity.actasIds);
        return dto;
    }

    private static SeguimientoEntity toEntity(SeguimientoDTO dto) {
        if (dto == null) {
            return null;
        }
        SeguimientoEntity entity = new SeguimientoEntity();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.id = new ObjectId(dto.getId());
        }
        entity.proyectoId = dto.getProyectoId();
        entity.descripcion = dto.getDescripcion();
        entity.fecha = dto.getFecha();
        entity.actasIds = dto.getActasIds();
        return entity;
    }
}
