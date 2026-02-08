package es.ayesa.buho.microservice.administracion.resources;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/balanceador")
public class BalanceadorResource {

    private static final String SALTOLINEA = "\n";

    @GET
    @Path("/")
    public String get1(HttpServerRequest request) {
        return "En get1 de servidor corriendo en puerto: " + request.localAddress().port() + getRequest(request);
    }

    @GET
    @Path("/dummy")
    public String dummy(HttpServerRequest request) {
        return "En dummy de servidor corriendo en puerto: " + request.localAddress().port() + getRequest(request);
    }

    @GET
    @Path("/dummy/{param1}")
    public String dummyParam(@PathParam("param1") String param1, HttpServerRequest request) {
        return "En dummy con parametro: " + param1 + " de servidor corriendo en puerto: " + request.localAddress().port()
                + getRequest(request);
    }

    private String getRequest(HttpServerRequest request) {
        StringBuilder strLog = new StringBuilder(SALTOLINEA);

        strLog.append("Metodo: ").append(request.method()).append(SALTOLINEA);
        strLog.append("URL: ").append(request.absoluteURI()).append(SALTOLINEA);
        strLog.append("Host Remoto: ")
                .append(request.remoteAddress() != null ? request.remoteAddress().host() : null)
                .append(SALTOLINEA);

        strLog.append("----- PARAMETERS ----").append(SALTOLINEA);
        MultiMap params = request.params();
        for (String key : params.names()) {
            for (String value : params.getAll(key)) {
                strLog.append("Clave:").append(key).append(" Valor: ").append(value).append(SALTOLINEA);
            }
        }

        strLog.append(SALTOLINEA).append("----- Headers ----").append(SALTOLINEA);
        MultiMap headers = request.headers();
        for (String name : headers.names()) {
            for (String value : headers.getAll(name)) {
                strLog.append("Clave:").append(name).append(" Valor: ").append(value).append(SALTOLINEA);
            }
        }
        return strLog.toString();
    }
}
