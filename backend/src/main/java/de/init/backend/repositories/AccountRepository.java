package de.init.backend.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import de.init.backend.authentication.repository.UserRepository;
import de.init.backend.datatable.DataTableRepository;
import de.init.backend.model.entity.Account;

/**
 * This repository is extended from UserRepository to save user instances and
 * DataTableRepository to be used for the table component. Through the extends
 * if UserRepository the Account Entity can be used with the
 * AuthenticatedUserService implementation.
 */
@Repository
public interface AccountRepository extends UserRepository<Account>, DataTableRepository<Account, Long> {

	public Optional<Account> findByFirstName(String firstName);

	public Optional<Account> findByLastName(String lastName);

	public Optional<Account> findByEmail(String email);

	public Optional<Account> findByFirstNameAndLastName(String firstName, String lastName);

	public boolean existsByEmail(String email);
}
