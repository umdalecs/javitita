package com.umdalecs.javitita.compiler;

public class SemanticError extends RuntimeException {
    public SemanticError(String message) {
        super(String.format("Error semántico: %s", message));
    }
}
