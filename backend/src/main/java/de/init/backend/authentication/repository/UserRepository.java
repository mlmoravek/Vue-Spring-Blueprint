package de.init.backend.authentication.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.init.backend.authentication.model.User;

@Repository
public interface UserRepository<T extends User> extends CrudRepository<T, Long> {

	public Optional<T> findByUsername(String username);

	public boolean existsByUsername(String username);

}
