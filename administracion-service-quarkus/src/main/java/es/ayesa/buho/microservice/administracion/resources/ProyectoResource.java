package es.ayesa.buho.microservice.administracion.resources;

import java.util.List;

import es.ayesa.buho.microservice.administracion.common.SpringPageResponse;
import es.ayesa.buho.microservice.administracion.model.ProyectoEntity;
import es.ayesa.buho.microservice.administracion.services.ProyectoService;
import es.ayesa.buho.microservice.dto.administracion.ConfiguracionProyectoDTO;
import es.ayesa.buho.microservice.dto.administracion.EstadoBuho;
import es.ayesa.buho.microservice.dto.administracion.ProjectFilterDTO;
import es.ayesa.buho.microservice.dto.administracion.TipoPeticionBuho;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/proyectos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProyectoResource {

    @Inject
    ProyectoService proyectoService;

    @GET
    @Path("/{unidadId}/arbol-proyectos")
    public List<ProyectoEntity> obtenerArbol(@PathParam("unidadId") String unidadId) {
        return proyectoService.obtenerArbolPorUnidad(unidadId);
    }

    @GET
    @Path("/{idProyecto}/configProyecto")
    public ConfiguracionProyectoDTO obtenerConfiguracionPorProyectoRedmine(@PathParam("idProyecto") String idProyecto) {
        ProyectoEntity p = proyectoService.findById(idProyecto);
        if (p == null) {
            throw new NotFoundException();
        }

        List<String> estadosAbiertos = p.estadosBuho == null ? List.of() : p.estadosBuho.stream().filter(EstadoBuho::isAbierta)
                .map(EstadoBuho::getEstado).toList();

        List<String> estadosCerrados = p.estadosBuho == null ? List.of() : p.estadosBuho.stream().filter(EstadoBuho::isCerrada)
                .map(EstadoBuho::getEstado).toList();

        List<Integer> defectos = p.tiposPeticionBuho == null ? List.of() : p.tiposPeticionBuho.stream().filter(TipoPeticionBuho::isDefecto)
                .map(TipoPeticionBuho::getIdRedmine).toList();

        List<Integer> historiasUsuario = p.tiposPeticionBuho == null ? List.of()
                : p.tiposPeticionBuho.stream().filter(TipoPeticionBuho::isHistoriaDeUsuario)
                        .map(TipoPeticionBuho::getIdRedmine).toList();

        List<Integer> tareas = p.tiposPeticionBuho == null ? List.of() : p.tiposPeticionBuho.stream().filter(TipoPeticionBuho::isTarea)
                .map(TipoPeticionBuho::getIdRedmine).toList();

        String periodo = proyectoService.calcularPeriodo(p);

        return new ConfiguracionProyectoDTO(estadosAbiertos, estadosCerrados, historiasUsuario, tareas, defectos, periodo);
    }

    @GET
    @Path("/listaProyectos")
    public SpringPageResponse<ProyectoEntity> getAllProyectos(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            @QueryParam("search") String search) {

        int pageVal = page == null ? 0 : page;
        int sizeVal = size == null ? 10 : size;
        String sortVal = sort == null ? "id" : sort;
        String directionVal = direction == null ? "desc" : direction;
        return proyectoService.findAll(search, pageVal, sizeVal, sortVal, directionVal);
    }

    @POST
    @Path("/listaProyectosPermitidos")
    public SpringPageResponse<ProyectoEntity> getProyectosPermitidos(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sort") String sort,
            @QueryParam("direction") String direction,
            ProjectFilterDTO filter) {

        int pageVal = page == null ? 0 : page;
        int sizeVal = size == null ? 10 : size;
        String sortVal = sort == null ? "id" : sort;
        String directionVal = direction == null ? "desc" : direction;
        return proyectoService.findProyectosPermitidos(filter, pageVal, sizeVal, sortVal, directionVal);
    }

    @GET
    @Path("/obtenerProyecto/{id}")
    public ProyectoEntity getProyectoById(@PathParam("id") String id) {
        ProyectoEntity proyecto = proyectoService.findById(id);
        if (proyecto == null) {
            throw new NotFoundException();
        }
        return proyecto;
    }

    @POST
    @Path("/crear")
    public Response createProyecto(ProyectoEntity proyecto) {
        ProyectoEntity created = proyectoService.crearProyecto(proyecto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/update")
    public ProyectoEntity updateProyectoCompleto(ProyectoEntity proyecto) {
        return proyectoService.modificarProyecto(proyecto);
    }

    @PATCH
    @Path("/subproyectos/{id}")
    public Response agregarSubproyecto(@PathParam("id") String id, ProyectoEntity subproyecto) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).entity("Método no implementado").build();
    }

    @PATCH
    @Path("/proyectos/{id}")
    public Response actualizarFichaProyecto(@PathParam("id") String id, ProyectoEntity proyecto) {
        proyectoService.actualizarProyecto(id, proyecto);
        return Response.ok("Subproyecto agregado correctamente").build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response deleteProyecto(@PathParam("id") String id) {
        proyectoService.deleteById(id);
        return Response.ok().build();
    }
}
