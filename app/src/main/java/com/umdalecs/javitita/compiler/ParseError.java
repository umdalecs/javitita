package com.umdalecs.javitita.compiler;

import com.umdalecs.javitita.compiler.Token;

public class ParseError extends RuntimeException {
    private final Token token;

    public ParseError(String expected, Token token) {
        super(String.format("Error sintáctico: Se esperaba %s pero se encontró %s en %d:%d",
                expected,
                token.type().name(),
                token.line(),
                token.column()));
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
