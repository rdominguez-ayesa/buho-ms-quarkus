package es.ayesa.buho.microservice.administracion.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bson.types.ObjectId;

import es.ayesa.buho.microservice.administracion.common.SpringPageResponse;
import es.ayesa.buho.microservice.administracion.model.UsuarioEntity;
import es.ayesa.buho.microservice.administracion.model.UsuarioVacacionEntity;
import es.ayesa.buho.microservice.administracion.repositories.UsuarioRepository;
import es.ayesa.buho.microservice.dto.usuario.UsuarioCreateDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioProyectosUpdateDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioUpdateDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioVacacionDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioVacacionRequest;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    private static final int DEFAULT_VACATION_HOURS_PER_DAY = 8;
    private static final String USER_NOT_FOUND = "Usuario no encontrado";

    public SpringPageResponse<UsuarioDTO> listar(String search, int page, int size, String sort, String direction) {
        Sort sortConfig = buildSort(sort, direction, "nombre");
        var query = (search != null && !search.isBlank())
                ? usuarioRepository.search(search.trim(), sortConfig)
                : usuarioRepository.findAll(sortConfig);

        query.page(Page.of(page, size));
        List<UsuarioDTO> content = query.list().stream().map(UsuarioService::toDto).toList();
        long total = query.count();
        return SpringPageResponse.of(content, page, size, total, true);
    }

    public Optional<UsuarioDTO> obtener(String id) {
        return findByIdOptional(id).map(UsuarioService::toDto);
    }

    public Optional<UsuarioDTO> obtenerPorUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.findByUserNameIgnoreCase(userName.trim()).map(UsuarioService::toDto);
    }

    public UsuarioDTO crear(UsuarioCreateDTO dto) {
        String userName = normalizarUserName(dto.getUserName());
        validarUserNameDisponible(userName, null);
        String email = normalizarEmail(dto.getEmail());
        validarEmailDisponible(email, null);

        UsuarioEntity entity = new UsuarioEntity();
        entity.id = null;
        entity.userName = userName;
        entity.nombre = normalizarTexto(dto.getNombre());
        entity.email = email;
        entity.redmineUsername = normalizarTexto(dto.getRedmineUsername());
        entity.redmineApiKey = normalizarTexto(dto.getRedmineApiKey());
        entity.redminePassword = normalizarTexto(dto.getRedminePassword());
        entity.proyectosId = normalizarLista(dto.getProyectosId());
        entity.roles = normalizarLista(dto.getRoles());
        entity.telefono = normalizarTexto(dto.getTelefono());
        entity.unidadOrganizativaId = normalizarTexto(dto.getUnidadOrganizativaId());
        entity.residencia = normalizarResidencia(dto.getResidencia());
        entity.activo = dto.isActivo();

        usuarioRepository.persist(entity);
        return toDto(entity);
    }

    public UsuarioDTO actualizar(String id, UsuarioUpdateDTO dto) {
        UsuarioEntity existente = findByIdOrThrow(id);

        String userName = normalizarUserName(dto.getUserName());
        if (userName != null && (existente.userName == null || !userName.equalsIgnoreCase(existente.userName))) {
            validarUserNameDisponible(userName, id);
        }
        String email = normalizarEmail(dto.getEmail());
        if (email != null && (existente.email == null || !email.equalsIgnoreCase(existente.email))) {
            validarEmailDisponible(email, id);
        }

        existente.userName = userName;
        existente.nombre = normalizarTexto(dto.getNombre());
        existente.email = email;
        existente.redmineUsername = normalizarTexto(dto.getRedmineUsername());
        existente.redmineApiKey = normalizarTexto(dto.getRedmineApiKey());
        existente.redminePassword = normalizarTexto(dto.getRedminePassword());
        existente.proyectosId = normalizarLista(dto.getProyectosId());
        existente.roles = normalizarLista(dto.getRoles());
        existente.telefono = normalizarTexto(dto.getTelefono());
        existente.unidadOrganizativaId = normalizarTexto(dto.getUnidadOrganizativaId());
        existente.residencia = normalizarResidencia(dto.getResidencia());
        existente.activo = dto.isActivo();

        usuarioRepository.update(existente);
        return toDto(existente);
    }

    public UsuarioDTO actualizarProyectos(String id, UsuarioProyectosUpdateDTO dto) {
        UsuarioEntity existente = findByIdOrThrow(id);
        existente.proyectosId = normalizarLista(dto.getProyectosId());
        usuarioRepository.update(existente);
        return toDto(existente);
    }

    public List<UsuarioDTO> listarPorProyecto(String proyectoId) {
        return usuarioRepository.findByProyectosId(proyectoId).stream().map(UsuarioService::toDto).toList();
    }

    public List<UsuarioVacacionDTO> listarVacaciones(String usuarioId, LocalDate from, LocalDate to) {
        UsuarioEntity usuario = findByIdOrThrow(usuarioId);
        return mapVacaciones(filtrarVacaciones(usuario.vacaciones, from, to));
    }

    public Map<String, List<UsuarioVacacionDTO>> listarVacacionesPorUsuarios(List<String> usuarioIds, LocalDate from, LocalDate to) {
        if (usuarioIds == null || usuarioIds.isEmpty()) {
            return Map.of();
        }
        Map<String, List<UsuarioVacacionDTO>> out = new HashMap<>();
        for (String id : usuarioIds) {
            findByIdOptional(id).ifPresent(usuario -> {
                String key = usuario.id != null ? usuario.id.toHexString() : null;
                if (key != null && !out.containsKey(key)) {
                    out.put(key, mapVacaciones(filtrarVacaciones(usuario.vacaciones, from, to)));
                }
            });
        }
        return out;
    }

    public UsuarioVacacionDTO registrarVacacion(String usuarioId, UsuarioVacacionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos de la ausencia son obligatorios");
        }
        UsuarioEntity usuario = findByIdOrThrow(usuarioId);
        validateVacationRange(request.getStartDate(), request.getEndDate());

        UsuarioVacacionEntity nueva = new UsuarioVacacionEntity();
        nueva.id = UUID.randomUUID().toString();
        nueva.startDate = request.getStartDate();
        nueva.endDate = request.getEndDate();
        nueva.hoursPerDay = resolveHoursPerDay(request.getHoursPerDay());
        nueva.description = normalizarTexto(request.getDescription());

        List<UsuarioVacacionEntity> vacaciones = ensureVacaciones(usuario);
        validateVacationOverlap(vacaciones, nueva);
        vacaciones.add(nueva);
        usuario.vacaciones = vacaciones;
        usuarioRepository.update(usuario);
        return toVacacionDto(nueva);
    }

    public void eliminarVacacion(String usuarioId, String vacacionId) {
        if (usuarioId == null || usuarioId.isBlank() || vacacionId == null || vacacionId.isBlank()) {
            return;
        }
        UsuarioEntity usuario = findByIdOrThrow(usuarioId);
        List<UsuarioVacacionEntity> vacaciones = ensureVacaciones(usuario);
        boolean removed = vacaciones.removeIf(v -> vacacionId.equals(v.id));
        if (removed) {
            usuario.vacaciones = vacaciones;
            usuarioRepository.update(usuario);
        }
    }

    public void eliminar(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        if (ObjectId.isValid(id)) {
            usuarioRepository.deleteById(new ObjectId(id));
            return;
        }
        usuarioRepository.delete("_id", id);
    }

    private Optional<UsuarioEntity> findByIdOptional(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        if (ObjectId.isValid(id)) {
            return usuarioRepository.findByIdOptional(new ObjectId(id));
        }
        return Optional.ofNullable(usuarioRepository.find("_id", id).firstResult());
    }

    private UsuarioEntity findByIdOrThrow(String id) {
        return findByIdOptional(id).orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
    }

    private void validarUserNameDisponible(String userName, String actualId) {
        if (userName == null) {
            return;
        }
        usuarioRepository.findByUserNameIgnoreCase(userName).ifPresent(existing -> {
            String existingId = existing.id != null ? existing.id.toHexString() : null;
            if (actualId == null || existingId == null || !existingId.equalsIgnoreCase(actualId)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario");
            }
        });
    }

    private void validarEmailDisponible(String email, String actualId) {
        if (email == null || email.isBlank()) {
            return;
        }
        usuarioRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            String existingId = existing.id != null ? existing.id.toHexString() : null;
            if (actualId == null || existingId == null || !existingId.equalsIgnoreCase(actualId)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese email");
            }
        });
    }

    private static UsuarioDTO toDto(UsuarioEntity entity) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(entity.id != null ? entity.id.toHexString() : null);
        dto.setUserName(entity.userName);
        dto.setNombre(entity.nombre);
        dto.setEmail(entity.email);
        dto.setRedmineUsername(entity.redmineUsername);
        dto.setRedmineApiKey(entity.redmineApiKey);
        dto.setRedminePassword(entity.redminePassword);
        dto.setRoles(entity.roles);
        dto.setTelefono(entity.telefono);
        dto.setResidencia(entity.residencia);
        dto.setActivo(entity.activo);
        dto.setProyectosId(entity.proyectosId);
        dto.setUnidadOrganizativaId(entity.unidadOrganizativaId);
        dto.setVacaciones(mapVacaciones(entity.vacaciones));
        return dto;
    }

    private static List<UsuarioVacacionDTO> mapVacaciones(List<UsuarioVacacionEntity> vacaciones) {
        if (vacaciones == null || vacaciones.isEmpty()) {
            return List.of();
        }
        return vacaciones.stream()
                .sorted(Comparator.comparing(v -> v.startDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(UsuarioService::toVacacionDto)
                .toList();
    }

    private static UsuarioVacacionDTO toVacacionDto(UsuarioVacacionEntity vacacion) {
        return UsuarioVacacionDTO.builder()
                .id(vacacion.id)
                .startDate(vacacion.startDate)
                .endDate(vacacion.endDate)
                .hoursPerDay(vacacion.hoursPerDay)
                .description(vacacion.description)
                .build();
    }

    private static List<UsuarioVacacionDTO> mapVacaciones(List<UsuarioVacacionEntity> vacaciones, LocalDate from, LocalDate to) {
        return mapVacaciones(filtrarVacaciones(vacaciones, from, to));
    }

    private static List<UsuarioVacacionEntity> filtrarVacaciones(List<UsuarioVacacionEntity> vacaciones, LocalDate from, LocalDate to) {
        if (vacaciones == null || vacaciones.isEmpty()) {
            return List.of();
        }
        return vacaciones.stream().filter(v -> overlaps(v, from, to)).toList();
    }

    private static boolean overlaps(UsuarioVacacionEntity vacacion, LocalDate from, LocalDate to) {
        LocalDate start = vacacion.startDate;
        LocalDate end = vacacion.endDate;
        if (start == null || end == null) {
            return false;
        }
        boolean startsBeforeTo = to == null || !start.isAfter(to);
        boolean endsAfterFrom = from == null || !end.isBefore(from);
        return startsBeforeTo && endsAfterFrom;
    }

    private static List<UsuarioVacacionEntity> ensureVacaciones(UsuarioEntity usuario) {
        List<UsuarioVacacionEntity> vacaciones = usuario.vacaciones;
        if (vacaciones == null) {
            vacaciones = new ArrayList<>();
        }
        return vacaciones;
    }

    private static void validateVacationRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Las fechas de la ausencia son obligatorias");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior al inicio");
        }
    }

    private static void validateVacationOverlap(List<UsuarioVacacionEntity> existentes, UsuarioVacacionEntity nueva) {
        for (UsuarioVacacionEntity actual : existentes) {
            if (overlaps(actual, nueva.startDate, nueva.endDate)) {
                throw new IllegalArgumentException("Ya existe una ausencia en ese rango");
            }
        }
    }

    private static int resolveHoursPerDay(Integer hours) {
        if (hours == null) {
            return DEFAULT_VACATION_HOURS_PER_DAY;
        }
        if (hours <= 0) {
            throw new IllegalArgumentException("Las horas deben ser mayores que cero");
        }
        return Math.min(hours, DEFAULT_VACATION_HOURS_PER_DAY);
    }

    private static String normalizarUserName(String userName) {
        return userName == null ? null : userName.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizarTexto(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizarResidencia(String residencia) {
        if (residencia == null || residencia.isBlank()) {
            return null;
        }
        return residencia.trim().replace('_', '-').toUpperCase(Locale.ROOT);
    }

    private static List<String> normalizarLista(List<String> values) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<String> out = new ArrayList<>();
        for (String v : values) {
            if (v == null || v.isBlank()) {
                continue;
            }
            String trimmed = v.trim();
            if (!trimmed.isEmpty() && !out.contains(trimmed)) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private static Sort buildSort(String sort, String direction, String defaultField) {
        String field = (sort == null || sort.isBlank()) ? defaultField : sort;
        boolean desc = direction != null && direction.equalsIgnoreCase("desc");
        return desc ? Sort.descending(field) : Sort.ascending(field);
    }
}
