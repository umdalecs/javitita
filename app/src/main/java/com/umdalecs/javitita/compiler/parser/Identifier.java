package com.umdalecs.javitita.compiler.parser;

import com.umdalecs.javitita.compiler.lexer.Token;

public class Identifier {
    private Token token;

    public Identifier(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
