package com.hackathon.churninsight.exception;

/**
 * Excepción personalizada para errores relacionados con servicios externos.
 * Se lanza cuando hay problemas al comunicarse con el servicio de ML o cuando
 * el servicio externo retorna errores.
 */
public class ExternalServiceException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo del error
     */
    public ExternalServiceException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa raíz.
     *
     * @param message Mensaje descriptivo del error
     * @param cause   Excepción original que causó el error
     */
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
