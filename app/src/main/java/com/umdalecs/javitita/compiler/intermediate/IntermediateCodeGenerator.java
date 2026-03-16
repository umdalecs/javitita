package com.umdalecs.javitita.compiler.intermediate;

import com.umdalecs.javitita.compiler.SymbolTable;
import com.umdalecs.javitita.compiler.parser.statements.PrintStatement;
import com.umdalecs.javitita.compiler.parser.statements.VarAssignStatement;
import com.umdalecs.javitita.compiler.parser.statements.WhileStatement;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Expression;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Program;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Statement;

public class IntermediateCodeGenerator {
    /**
     * label - instruction  - operands - comment
     * */
    private static final String lineFormat = "%-10s %-14s %-20s\n";
    /**
     * name - size  - initial value - comment
     * */
    private static final String symbolFormat = "%-10s %-14s %-20s\n";
    private final SymbolTable symbolTable;
    private final Program program;
    private final StringBuilder builder;
    private int contadorEtiqueta;

    public IntermediateCodeGenerator(SymbolTable symbolTable, Program program) {
        this.symbolTable = symbolTable;
        this.program = program;
        builder = new StringBuilder();
        contadorEtiqueta = 0;
    }


    public String generate() {
        builder.append(String.format("%-10s %-14s\n", "section",".text"));
        builder.append(String.format("%-10s %-14s\n", "global","_start"));


        generateSymbolTable();

//        builder.append(String.format("%-10s %-14s\n", "", ".CODE"));
        builder.append("_start:\n");

        for (var statement: program.getStatements()) {
            generateStatement(statement);
        }

        return builder.toString();
    }

    public void generateSymbolTable() {
        builder.append(String.format("%-10s %-14s\n", "", ".DATA"));

        for (var symbol: symbolTable.getValues()) {
            var size = switch (symbol.type()) {
                case BOOLEAN -> "DW";
                case INTEGER -> "DD";
            };
            builder.append(String.format(symbolFormat,
                    symbol.token().literal(),
                    size,
                    "?"));
        }
    }

    public void generateStatement(Statement st) {

        switch (st) {
            case WhileStatement ws -> {
                String format = "";

                for (var statement: ws.getStatements()) {
                    generateStatement(statement);
                }

            }
            case PrintStatement ps -> {
                String result = checkExpressionResult(ps.getExpression());

                String format = """
                                   MOV            AX, %s
                                   MOV            BX, 10
                                   XOR            CX, CX
                        E%-8s
                                   XOR            DX, DX
                                   DIV            BX
                                   PUSH           DX
                                   INC            CX
                                   CMP            AX, 0
                                   JNE            E%-8s
                        E%-8s
                                   POP            DX
                                   ADD            DL, '0'
                                   MOV            AH, 02H
                                   INT            21H
                                   LOOP           E%-8s
                        """;
                builder.append(String.format(format,
                        result,
                        ++contadorEtiqueta + ":",
                        contadorEtiqueta,
                        ++contadorEtiqueta + ":",
                        contadorEtiqueta));
            }
            case VarAssignStatement vas -> {
                String var = vas.getIdentifier().literal();
                String result = checkExpressionResult(vas.getExpression());

                String format = "           MOV            %s, %s\n";
                builder.append(String.format(format,
                        var,
                        result));
            }
            default -> {}
        }
    }

    private String checkExpressionResult(Expression expression) {
        return "12396";
    }
}
