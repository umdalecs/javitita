package com.umdalecs.javitita.compiler.parser.statements;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.umdalecs.javitita.compiler.parser.Identifier;
import com.umdalecs.javitita.compiler.parser.Type;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Statement;

public class VarDeclarationStatement extends Statement {
    private Type type;
    private Identifier identifier;

    public VarDeclarationStatement(Identifier identifier,  Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
