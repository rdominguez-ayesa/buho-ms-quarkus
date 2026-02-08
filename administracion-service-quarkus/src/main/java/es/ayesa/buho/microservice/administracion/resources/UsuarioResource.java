package es.ayesa.buho.microservice.administracion.resources;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.ayesa.buho.microservice.administracion.common.SpringPageResponse;
import es.ayesa.buho.microservice.administracion.services.UsuarioService;
import es.ayesa.buho.microservice.dto.usuario.UsuarioCreateDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioProyectosUpdateDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioUpdateDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioVacacionDTO;
import es.ayesa.buho.microservice.dto.usuario.UsuarioVacacionRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @GET
    public SpringPageResponse<UsuarioDTO> listar(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            @QueryParam("search") String search) {
        int pageVal = page == null ? 0 : page;
        int sizeVal = size == null ? 20 : size;
        String sortVal = sort == null ? "nombre" : sort;
        String directionVal = direction == null ? "asc" : direction;
        return usuarioService.listar(search, pageVal, sizeVal, sortVal, directionVal);
    }

    @GET
    @Path("/{id}")
    public UsuarioDTO obtener(@PathParam("id") String id) {
        return usuarioService.obtener(id).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/proyectos/{proyectoId}")
    public List<UsuarioDTO> listarPorProyecto(@PathParam("proyectoId") String proyectoId) {
        return usuarioService.listarPorProyecto(proyectoId);
    }

    @GET
    @Path("/by-username/{userName}")
    public UsuarioDTO obtenerPorUserName(@PathParam("userName") String userName) {
        Optional<UsuarioDTO> dto = usuarioService.obtenerPorUserName(userName);
        return dto.orElseThrow(NotFoundException::new);
    }

    @POST
    public Response crear(@Valid UsuarioCreateDTO dto) {
        UsuarioDTO creado = usuarioService.crear(dto);
        return Response.status(Response.Status.CREATED).entity(creado).build();
    }

    @PUT
    @Path("/{id}")
    public UsuarioDTO actualizar(@PathParam("id") String id, @Valid UsuarioUpdateDTO dto) {
        dto.setId(id);
        return usuarioService.actualizar(id, dto);
    }

    @PUT
    @Path("/{id}/proyectos")
    public UsuarioDTO actualizarProyectos(@PathParam("id") String id, @Valid UsuarioProyectosUpdateDTO dto) {
        dto.setId(id);
        return usuarioService.actualizarProyectos(id, dto);
    }

    @GET
    @Path("/{id}/vacaciones")
    public List<UsuarioVacacionDTO> listarVacaciones(
            @PathParam("id") String id,
            @QueryParam("from") String from,
            @QueryParam("to") String to) {
        return usuarioService.listarVacaciones(id, parseLocalDate(from), parseLocalDate(to));
    }

    @GET
    @Path("/vacaciones")
    public Map<String, List<UsuarioVacacionDTO>> listarVacacionesPorUsuarios(
            @QueryParam("usuarioIds") List<String> usuarioIds,
            @QueryParam("from") String from,
            @QueryParam("to") String to) {
        return usuarioService.listarVacacionesPorUsuarios(usuarioIds, parseLocalDate(from), parseLocalDate(to));
    }

    @POST
    @Path("/{id}/vacaciones")
    public Response registrarVacacion(@PathParam("id") String id, @Valid UsuarioVacacionRequest request) {
        UsuarioVacacionDTO creada = usuarioService.registrarVacacion(id, request);
        return Response.status(Response.Status.CREATED).entity(creada).build();
    }

    @DELETE
    @Path("/{id}/vacaciones/{vacacionId}")
    public Response eliminarVacacion(@PathParam("id") String id, @PathParam("vacacionId") String vacacionId) {
        usuarioService.eliminarVacacion(id, vacacionId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") String id) {
        usuarioService.eliminar(id);
        return Response.noContent().build();
    }

    private static LocalDate parseLocalDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value.trim());
    }
}
