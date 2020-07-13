package com.maps.financial.exceptions;

/**
 * Exception será lançada quando um determinado objeto não for encontrado com as informações passadas
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
public class ObjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Long id;
	private final Class<?> clazz;

	/**
	 * ConstructorObjectNotFound Exception
	 * 
	 * @param id
	 * @param clazz
	 */
	public ObjectNotFoundException(Long id, Class<?> clazz) {
		super(ExceptionMessage.MESSAGE_OBJECT_NOT_FOUND.getValue());
		this.id = id;
		this.clazz = clazz;
	}

	/**
	 * Gets id
	 * 
	 * @return Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Gets clazz
	 * 
	 * @return Class<?>
	 */
	public Class<?> getClazz() {
		return clazz;
	}

}