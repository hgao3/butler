package com.butler.exception;

public class ResourceNotFoundException extends RuntimeException {

    /**
	 * Thrown when an application tries to access a non-existing index from Dao.
	 * The instantiation can fail for a variety of reasons:
	 * 		- Account not found
	 * 		- Transaction not found
	 */
	private static final long serialVersionUID = 1L;
	private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
