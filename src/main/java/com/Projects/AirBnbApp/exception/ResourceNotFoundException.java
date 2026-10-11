package com.Projects.AirBnbApp.exception;

import java.util.function.Supplier;

public class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
}
