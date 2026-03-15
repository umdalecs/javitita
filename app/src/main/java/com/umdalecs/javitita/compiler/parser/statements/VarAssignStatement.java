package com.umdalecs.javitita.compiler.parser.statements;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.umdalecs.javitita.compiler.parser.Identifier;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Expression;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Statement;

public class VarAssignStatement extends Statement {
    private Identifier identifier;
    private Expression expression;

    public VarAssignStatement(Identifier identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
}
