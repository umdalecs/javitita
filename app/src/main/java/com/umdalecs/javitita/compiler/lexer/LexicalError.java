package com.umdalecs.javitita.compiler.lexer;

public class LexicalError extends RuntimeException {
    public LexicalError(Token token) {
        super(String.format("Error léxico: Token desconocido %s en %d:%d",
                token.literal(), token.line(), token.column()));
    }
}
