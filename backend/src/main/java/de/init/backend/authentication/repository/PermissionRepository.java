package de.init.backend.authentication.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.init.backend.authentication.model.Permission;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Long> {

	Optional<Permission> findByName(String name);
}
