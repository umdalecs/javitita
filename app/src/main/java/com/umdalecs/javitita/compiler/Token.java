package com.umdalecs.javitita.compiler;

public record Token(
        TokenType type,
        String literal,
        int line, int column,
        int absolutePos) {
    public Token(TokenType type, char c, int position, int column, int absolutePos) {
        this(type, c + "", position, column, absolutePos);
    }
}
