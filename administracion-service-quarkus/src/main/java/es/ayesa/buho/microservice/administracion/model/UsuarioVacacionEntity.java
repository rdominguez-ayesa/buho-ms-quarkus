package es.ayesa.buho.microservice.administracion.model;

import java.time.LocalDate;

public class UsuarioVacacionEntity {
    public String id;
    public LocalDate startDate;
    public LocalDate endDate;
    public Integer hoursPerDay;
    public String description;
}
