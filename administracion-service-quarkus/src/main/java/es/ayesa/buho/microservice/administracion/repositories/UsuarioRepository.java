package es.ayesa.buho.microservice.administracion.repositories;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import es.ayesa.buho.microservice.administracion.model.UsuarioEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;

@ApplicationScoped
public class UsuarioRepository implements PanacheMongoRepository<UsuarioEntity> {

	private static final String SEARCH_QUERY = "{ $or: [ { 'userName': { $regex: ?1, $options: 'i' } }, { 'nombre': { $regex: ?1, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } }, { 'redmineUsername': { $regex: ?1, $options: 'i' } } ] }";

	public PanacheQuery<UsuarioEntity> search(String search, Sort sort) {
		return find(SEARCH_QUERY, sort, search);
	}

	public Optional<UsuarioEntity> findByUserNameIgnoreCase(String username) {
		if (username == null || username.isBlank()) {
			return Optional.empty();
		}
		String anchored = "^" + Pattern.quote(username.trim()) + "$";
		return find("{'userName': { $regex: ?1, $options: 'i' }}", anchored).firstResultOptional();
	}

	public Optional<UsuarioEntity> findByEmailIgnoreCase(String email) {
		if (email == null || email.isBlank()) {
			return Optional.empty();
		}
		String anchored = "^" + Pattern.quote(email.trim()) + "$";
		return find("{'email': { $regex: ?1, $options: 'i' }}", anchored).firstResultOptional();
	}

	public List<UsuarioEntity> findByProyectosId(String proyectoId) {
		if (proyectoId == null || proyectoId.isBlank()) {
			return List.of();
		}
		return list("{'proyectosId': ?1}", proyectoId.trim());
	}
}
