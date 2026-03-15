package com.umdalecs.javitita.compiler.parser.syntaxtree;

import java.util.ArrayList;
import java.util.List;

public class Program {
    private final List<Statement> statements;


    public Program() {
        statements = new ArrayList<>();
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
