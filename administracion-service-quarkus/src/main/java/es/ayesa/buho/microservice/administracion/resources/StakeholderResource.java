package es.ayesa.buho.microservice.administracion.resources;

import es.ayesa.buho.microservice.administracion.common.SpringPageResponse;
import es.ayesa.buho.microservice.administracion.services.StakeholderService;
import es.ayesa.buho.microservice.dto.stakeholders.StakeholderCreateDTO;
import es.ayesa.buho.microservice.dto.stakeholders.StakeholderDTO;
import es.ayesa.buho.microservice.dto.stakeholders.StakeholderUpdateDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
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

@Path("/stakeholders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StakeholderResource {

    @Inject
    StakeholderService service;

    @GET
    @Path("/lista")
    public SpringPageResponse<StakeholderDTO> listar(
            @QueryParam("proyectoId") String proyectoId,
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            @QueryParam("search") String search) {

        if (proyectoId == null || proyectoId.isBlank()) {
            throw new BadRequestException("proyectoId es obligatorio");
        }

        int pageVal = page == null ? 0 : page;
        int sizeVal = size == null ? 20 : size;
        String sortVal = sort == null ? "nombreCompleto" : sort;
        String directionVal = direction == null ? "asc" : direction;
        return service.listarPorProyecto(proyectoId, search, pageVal, sizeVal, sortVal, directionVal);
    }

    @GET
    @Path("/{id}")
    public StakeholderDTO obtener(@PathParam("id") String id) {
        return service.obtener(id).orElseThrow(NotFoundException::new);
    }

    @POST
    public Response crear(@Valid StakeholderCreateDTO dto) {
        StakeholderDTO creado = service.crear(dto);
        return Response.status(Response.Status.CREATED).entity(creado).build();
    }

    @PUT
    @Path("/{id}")
    public StakeholderDTO actualizar(@PathParam("id") String id, @Valid StakeholderUpdateDTO dto) {
        dto.setId(id);
        return service.actualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") String id) {
        service.eliminar(id);
        return Response.noContent().build();
    }
}
