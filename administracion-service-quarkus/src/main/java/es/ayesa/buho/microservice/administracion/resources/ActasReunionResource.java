package es.ayesa.buho.microservice.administracion.resources;

import java.util.List;

import es.ayesa.buho.microservice.administracion.dto.ActaReunionDTO;
import es.ayesa.buho.microservice.administracion.services.ActaReunionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/actasreunion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActasReunionResource {

    @Inject
    ActaReunionService actaReunionService;

    @GET
    public List<ActaReunionDTO> getAllActas() {
        return actaReunionService.getAll();
    }

    @POST
    @Path("/crearActa")
    public ActaReunionDTO createActa(ActaReunionDTO acta) {
        return actaReunionService.crear(acta);
    }

    @DELETE
    @Path("/actas/{id}")
    public Response borrar(@PathParam("id") String id) {
        if (actaReunionService.existsById(id)) {
            actaReunionService.deleteById(id);
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
