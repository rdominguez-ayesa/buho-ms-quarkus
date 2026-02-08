package es.ayesa.buho.microservice.administracion.resources;

import java.util.List;

import es.ayesa.buho.microservice.administracion.dto.SeguimientoDTO;
import es.ayesa.buho.microservice.administracion.services.SeguimientoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/seguimientos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeguimientoResource {

    @Inject
    SeguimientoService seguimientoService;

    @GET
    public List<SeguimientoDTO> getAllSeguimientos() {
        return seguimientoService.getAll();
    }

    @GET
    @Path("/proyecto/{proyectoId}")
    public List<SeguimientoDTO> getSeguimientosByProyecto(@PathParam("proyectoId") String proyectoId) {
        return seguimientoService.getByProyectoId(proyectoId);
    }

    @POST
    public SeguimientoDTO createSeguimiento(SeguimientoDTO seguimiento) {
        return seguimientoService.crear(seguimiento);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSeguimiento(@PathParam("id") String id) {
        if (seguimientoService.existsById(id)) {
            seguimientoService.deleteById(id);
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
