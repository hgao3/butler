package com.butler.exception;

public class ClassInstantiationException extends Exception {

    /**
	 * Thrown when an application tries to create an instance of a class, but the specified class object cannot be instantiated.
	 * The instantiation can fail for a variety of reasons:
	 * 		- ParseException for String convert to Date 
	 */
	private static final long serialVersionUID = 1L;
	private String className;
    private String fieldName;
    private Object fieldValue;

    public ClassInstantiationException(String className, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", className, fieldName, fieldValue));
        this.className = className;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
