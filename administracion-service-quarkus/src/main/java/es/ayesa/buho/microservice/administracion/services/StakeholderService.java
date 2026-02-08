package es.ayesa.buho.microservice.administracion.services;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.bson.types.ObjectId;

import es.ayesa.buho.microservice.administracion.common.SpringPageResponse;
import es.ayesa.buho.microservice.administracion.model.StakeholderEntity;
import es.ayesa.buho.microservice.administracion.repositories.StakeholderRepository;
import es.ayesa.buho.microservice.dto.stakeholders.StakeholderCreateDTO;
import es.ayesa.buho.microservice.dto.stakeholders.StakeholderDTO;
import es.ayesa.buho.microservice.dto.stakeholders.StakeholderUpdateDTO;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class StakeholderService {
    private final StakeholderRepository repository;

    public SpringPageResponse<StakeholderDTO> listarPorProyecto(String proyectoId, String search, int page, int size, String sort, String direction) {
        Sort sortConfig = buildSort(sort, direction, "nombreCompleto");
        var query = (search != null && !search.isBlank())
                ? repository.searchByProyectoId(proyectoId, search.trim(), sortConfig)
                : repository.findByProyectoId(proyectoId, sortConfig);

        query.page(Page.of(page, size));
        List<StakeholderDTO> content = query.list().stream().map(StakeholderService::toDto).toList();
        long total = query.count();
        return SpringPageResponse.of(content, page, size, total, true);
    }

    public Optional<StakeholderDTO> obtener(String id) {
        ObjectId objectId = parseObjectIdOrNull(id);
        if (objectId == null) {
            return Optional.empty();
        }
        return repository.findByIdOptional(objectId).map(StakeholderService::toDto);
    }

    public StakeholderDTO crear(StakeholderCreateDTO dto) {
        validarEmailUnico(dto.getProyectoId(), dto.getEmail(), null);
        StakeholderEntity entity = new StakeholderEntity();
        entity.id = null;
        entity.proyectoId = trimOrNull(dto.getProyectoId());
        entity.nombreCompleto = trimOrNull(dto.getNombreCompleto());
        entity.email = normalizarEmail(dto.getEmail());
        entity.cargoFuncion = trimOrNull(dto.getCargoFuncion());
        entity.organismoEmpresa = trimOrNull(dto.getOrganismoEmpresa());
        entity.interno = dto.isInterno();
        repository.persist(entity);
        return toDto(entity);
    }

    public StakeholderDTO actualizar(String id, StakeholderUpdateDTO dto) {
        ObjectId objectId = parseObjectIdOrNull(id);
        if (objectId == null) {
            throw new IllegalArgumentException("Stakeholder no encontrado");
        }
        StakeholderEntity existente = repository.findByIdOptional(objectId)
                .orElseThrow(() -> new IllegalArgumentException("Stakeholder no encontrado"));

        String nuevoEmail = normalizarEmail(dto.getEmail());
        if (nuevoEmail != null && (existente.email == null || !nuevoEmail.equalsIgnoreCase(existente.email))) {
            validarEmailUnico(existente.proyectoId, nuevoEmail, id);
        }

        existente.nombreCompleto = trimOrNull(dto.getNombreCompleto());
        existente.email = nuevoEmail;
        existente.cargoFuncion = trimOrNull(dto.getCargoFuncion());
        existente.organismoEmpresa = trimOrNull(dto.getOrganismoEmpresa());
        existente.interno = dto.isInterno();
        repository.update(existente);
        return toDto(existente);
    }

    public void eliminar(String id) {
        ObjectId objectId = parseObjectIdOrNull(id);
        if (objectId != null) {
            repository.deleteById(objectId);
        }
    }

    private void validarEmailUnico(String proyectoId, String email, String actualId) {
        if (proyectoId == null || proyectoId.isBlank() || email == null || email.isBlank()) {
            return;
        }
        repository.findByProyectoIdAndEmailIgnoreCase(proyectoId.trim(), email).ifPresent(existing -> {
            String existingId = existing.id != null ? existing.id.toHexString() : null;
            if (actualId == null || existingId == null || !existingId.equalsIgnoreCase(actualId)) {
                throw new IllegalArgumentException("Ya existe un stakeholder con ese email en el proyecto");
            }
        });
    }

    private static StakeholderDTO toDto(StakeholderEntity entity) {
        StakeholderDTO dto = new StakeholderDTO();
        dto.setId(entity.id != null ? entity.id.toHexString() : null);
        dto.setProyectoId(entity.proyectoId);
        dto.setNombreCompleto(entity.nombreCompleto);
        dto.setEmail(entity.email);
        dto.setCargoFuncion(entity.cargoFuncion);
        dto.setOrganismoEmpresa(entity.organismoEmpresa);
        dto.setInterno(entity.interno);
        return dto;
    }

    private static Sort buildSort(String sort, String direction, String defaultField) {
        String field = (sort == null || sort.isBlank()) ? defaultField : sort;
        boolean desc = direction != null && direction.equalsIgnoreCase("desc");
        return desc ? Sort.descending(field) : Sort.ascending(field);
    }

    private static String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase(Locale.ROOT);
    }

    private static String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static ObjectId parseObjectIdOrNull(String id) {
        if (id == null || id.isBlank() || !ObjectId.isValid(id)) {
            return null;
        }
        return new ObjectId(id);
    }
}
