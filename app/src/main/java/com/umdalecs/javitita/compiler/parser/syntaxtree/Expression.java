package com.umdalecs.javitita.compiler.parser.syntaxtree;

import com.umdalecs.javitita.compiler.lexer.Token;
import com.umdalecs.javitita.compiler.parser.Type;

public class Expression {
    private Type type;
    private Token operation;
    private Token left, right;

    public Expression(Type type, Token operation, Token left, Token right) {
        this.type = type;
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    public Expression(Type type, Token left) {
        this.type = type;
        this.left = left;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Token getLeft() {
        return left;
    }

    public Token getRight() {
        return right;
    }

    public Token getOperation() {
        return operation;
    }
}
