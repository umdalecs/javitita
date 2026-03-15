package com.umdalecs.javitita.compiler.lexer;

import java.util.Map;

public record Token(
        TokenType type,
        String literal,
        int line, int column,
        int absolutePos) {
    public Token(TokenType type, char c, int position, int column, int absolutePos) {
        this(type, c + "", position, column, absolutePos);
    }

    private static final Map<String, TokenType> keywords = Map.of(
            "while", TokenType.WHILE,
            "println", TokenType.PRINT_STATEMENT,
            "fn", TokenType.FN,
            "int", TokenType.INTEGER_TYPE,
            "bool", TokenType.BOOLEAN_TYPE,
            "true", TokenType.TRUE_LITERAL,
            "false", TokenType.FALSE_LITERAL);


    public static TokenType lookupKw(String identifier) {
        var kw = keywords.get(identifier);

        if (kw != null)
            return kw;

        return TokenType.IDENTIFIER;
    }
}
