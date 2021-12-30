package de.init.backend.datatable.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class AnnotationService {

	private final Class<? extends Object> typeClass;

	public AnnotationService(@NonNull Class<? extends Object> typeClass) {
		this.typeClass = typeClass;
	}

	public AnnotationService(@NonNull Object object) {
		this.typeClass = object.getClass();
	}

	/**
	 * Get all fields from an Class with the given annotation
	 *
	 * @param annotationClass Field annotation
	 * @return Set of Field
	 */
	public Set<Field> getFieldsWithAnnotation(@NonNull Class<? extends Annotation> annotationClass) {
		return this.getFieldsWithAnnotation(new HashSet<>(Arrays.asList(annotationClass)));
	}

	/**
	 * Get all fields from an Class which contains one of given annotation
	 *
	 * @return Set of Field
	 */
	public Set<Field> getFieldsWithAnnotation(@NonNull Set<Class<? extends Annotation>> annotationClasses) {
		return this.getFieldsUpTo(typeClass, Object.class).stream()
				.filter((Field field) -> this.hasAnnotation(field, annotationClasses)).collect(Collectors.toSet());
	}

	/**
	 * Get all fields from an Class without the given annotation
	 *
	 * @param annotationClass Field annotation
	 * @return Set of Field
	 */
	public Set<Field> getFieldsWithoutAnnotation(@NonNull Class<? extends Annotation> annotationClass) {
		return this.getFieldsWithoutAnnotation(new HashSet<>(Arrays.asList(annotationClass)));
	}

	/**
	 * Get all fields from an Class without any of the given annotation
	 *
	 * @return Set of Field
	 */
	public Set<Field> getFieldsWithoutAnnotation(@NonNull Set<Class<? extends Annotation>> annotationClasses) {
		return this.getFieldsUpTo(typeClass, Object.class).stream()
				.filter((Field field) -> !this.hasAnnotation(field, annotationClasses)).collect(Collectors.toSet());
	}

	/**
	 * Get all fields witch equal to one element in the given name list
	 * 
	 * @param names List of field names
	 * @return Set of Field
	 */
	public Set<Field> getFieldsWithName(@NonNull Set<String> names) {
		Set<Field> fields = this.getFieldsUpTo(typeClass, Object.class);
		return fields.stream().filter(field -> names.contains(field.getName())).collect(Collectors.toSet());
	}

	/**
	 * Get the field witch equal to the given name
	 * 
	 * @param name field name
	 * @return Field
	 */
	public Optional<Field> getFieldWithName(@NonNull String name) {
		Set<Field> fields = this.getFieldsUpTo(typeClass, Object.class);
		return fields.stream() //
				.filter(field -> name.equals(field.getName()))//
				.findFirst();
	}
	

	private Boolean hasAnnotation(Field field, Set<Class<? extends Annotation>> annotationClasses) {
		return annotationClasses.stream().anyMatch(field::isAnnotationPresent);
	}

	private Set<Field> getFieldsUpTo(@NonNull Class<?> startClass, @Nullable Class<?> exclusiveParent) {
		Set<Field> currentClassFields = new HashSet<>(Arrays.asList(startClass.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && !parentClass.equals(exclusiveParent)) {
			Set<Field> parentClassFields = this.getFieldsUpTo(parentClass, exclusiveParent);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}
}
