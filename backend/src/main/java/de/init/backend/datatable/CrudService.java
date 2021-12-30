package de.init.backend.datatable;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import org.springframework.data.repository.CrudRepository;

import de.init.backend.datatable.annotation.AnnotationService;
import de.init.backend.datatable.annotation.Editable;

/**
 * This class could not be annotated created to a bean, because the repository
 * has to be chosen by runtime.
 * 
 * @param <T> The class from the object 
 * @param <I> The Id type of the object
 */
public class CrudService<T, I> {

	private final Class<T> typeClass;
	private final CrudRepository<T, I> repository;

	/**
	 * Create CrudService instance.
	 *
	 * @param repository The repository to work on
	 */
	public CrudService(CrudRepository<T, I> repository, Class<T> typeClass) {
		this.repository = repository;
		this.typeClass = typeClass;
	}

	/**
	 * Return all elements of this entity
	 * 
	 * @return List<T>
	 */
	public Iterable<T> getAll() {
		return this.repository.findAll();
	}

	/**
	 * Return one specifiy entity by id
	 * 
	 * @param id Entity id
	 * @return T
	 */
	public T get(I id) {
		Optional<T> entityOptional = this.repository.findById(id);
		return entityOptional.orElse(null);
	}

	public T create(T entity) {
		T result = this.repository.save(entity);
		return result;
	}


	public T update(I id, T updatedEntity) {
		return this.update(id, updatedEntity, null);
	}
	
	public T update(I id, T updatedEntity, @Nullable Set<Field> editableFields) {
		T oldEntity = this.get(id);
		if (oldEntity != null) {
			// merge editable fields from updatedEntity to oldEntity
			T newEntity = this.merge(oldEntity, updatedEntity, editableFields);
			return this.repository.save(newEntity);
		}
		return null;
	}

	public void delete(I id) {
		T entity = this.get(id);
		if (entity != null) {
			this.repository.delete(entity);
		}
	}

	/**
	 * Merge all properties which are annotated with Editable from newEntity to
	 * oldEntity
	 * 
	 * @param oldEntity      Object to merge to
	 * @param newEntity      Object to merge from
	 * @param editableFields Set of fields which will be overwritten by newEntity.
	 *                       Can be <tt>null<tt> for default fields (the fields
	 *                       annotated with {@link Editable})
	 * @return oldEntity object
	 */
	private T merge(T oldEntity, T newEntity, @Nullable Set<Field> editableFields) {
		if (editableFields == null) {
			AnnotationService annotationService = new AnnotationService(this.typeClass);
			editableFields = annotationService.getFieldsWithAnnotation(Editable.class);
		}
		for (Field field : editableFields) {
			field.setAccessible(true);
			try {
				field.set(oldEntity, field.get(newEntity));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("Field " + field + " not found on class " + this.typeClass, e);
			}
		}
		return oldEntity;
	}

}
