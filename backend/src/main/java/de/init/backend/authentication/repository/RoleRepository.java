package de.init.backend.authentication.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.init.backend.authentication.model.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

	public Optional<Role> findByName(String string);
}
