package com.umdalecs.javitita.compiler;

public class VarDeclarationStatement extends Statement {
    private final Type type;
    private final Token identifier;

    public VarDeclarationStatement(Token identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Token getIdentifier() {
        return identifier;
    }
}
