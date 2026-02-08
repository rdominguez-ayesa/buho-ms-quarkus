package es.ayesa.buho.microservice.administracion.services;

import java.util.List;

import org.bson.types.ObjectId;

import es.ayesa.buho.microservice.administracion.dto.ActaReunionDTO;
import es.ayesa.buho.microservice.administracion.model.ActaReunionEntity;
import es.ayesa.buho.microservice.administracion.repositories.ActaReunionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ActaReunionService {

    private final ActaReunionRepository actaReunionRepository;

    @Inject
    public ActaReunionService(ActaReunionRepository actaReunionRepository) {
        this.actaReunionRepository = actaReunionRepository;
    }

    public List<ActaReunionDTO> getAll() {
        return actaReunionRepository.listAll().stream().map(ActaReunionService::toDto).toList();
    }

    public List<ActaReunionDTO> getBySeguimientoId(String seguimientoId) {
        if (seguimientoId == null || seguimientoId.isBlank()) {
            return List.of();
        }
        return actaReunionRepository.findBySeguimientoId(seguimientoId.trim()).stream().map(ActaReunionService::toDto).toList();
    }

    public ActaReunionDTO crear(ActaReunionDTO dto) {
        if (dto != null) {
            dto.setId(null);
        }
        ActaReunionEntity entity = toEntity(dto);
        entity.id = null;
        actaReunionRepository.persist(entity);
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
            actaReunionRepository.deleteById(new ObjectId(id));
            return;
        }
        actaReunionRepository.delete("_id", id);
    }

    private java.util.Optional<ActaReunionEntity> findByIdOptional(String id) {
        if (id == null || id.isBlank()) {
            return java.util.Optional.empty();
        }
        if (ObjectId.isValid(id)) {
            ActaReunionEntity byObjectId = actaReunionRepository.findById(new ObjectId(id));
            if (byObjectId != null) {
                return java.util.Optional.of(byObjectId);
            }
        }
        ActaReunionEntity byStringId = actaReunionRepository.find("_id", id).firstResult();
        return java.util.Optional.ofNullable(byStringId);
    }

    private static ActaReunionDTO toDto(ActaReunionEntity entity) {
        if (entity == null) {
            return null;
        }
        ActaReunionDTO dto = new ActaReunionDTO();
        dto.setId(entity.id == null ? null : entity.id.toHexString());
        dto.setSeguimientoId(entity.seguimientoId);
        dto.setTipo(entity.tipo);
        dto.setFechaReunion(entity.fechaReunion);
        dto.setAsistentes(entity.asistentes);
        dto.setOrdenDelDia(entity.ordenDelDia);
        dto.setTemasDiscutidos(entity.temasDiscutidos);
        dto.setLugarReunion(entity.lugarReunion);
        dto.setAcuerdosAlcanzados(entity.acuerdosAlcanzados);
        dto.setAcuerdosPendientes(entity.acuerdosPendientes);
        dto.setHistorialAcuerdos(entity.historialAcuerdos);
        dto.setVideo(entity.video);
        dto.setAudio(entity.audio);
        dto.setTranscripcionTexto(entity.transcripcionTexto);
        return dto;
    }

    private static ActaReunionEntity toEntity(ActaReunionDTO dto) {
        if (dto == null) {
            return null;
        }
        ActaReunionEntity entity = new ActaReunionEntity();
        if (dto.getId() != null && ObjectId.isValid(dto.getId())) {
            entity.id = new ObjectId(dto.getId());
        }
        entity.seguimientoId = dto.getSeguimientoId();
        entity.tipo = dto.getTipo();
        entity.fechaReunion = dto.getFechaReunion();
        entity.asistentes = dto.getAsistentes();
        entity.ordenDelDia = dto.getOrdenDelDia();
        entity.temasDiscutidos = dto.getTemasDiscutidos();
        entity.lugarReunion = dto.getLugarReunion();
        entity.acuerdosAlcanzados = dto.getAcuerdosAlcanzados();
        entity.acuerdosPendientes = dto.getAcuerdosPendientes();
        entity.historialAcuerdos = dto.getHistorialAcuerdos();
        entity.video = dto.getVideo();
        entity.audio = dto.getAudio();
        entity.transcripcionTexto = dto.getTranscripcionTexto();
        return entity;
    }
}
