package com.tricol.supply.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s avec l'ID %d n'a pas été trouvé", resource, id));
    }
}

