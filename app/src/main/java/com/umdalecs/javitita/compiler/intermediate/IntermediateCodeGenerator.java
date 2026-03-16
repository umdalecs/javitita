package com.umdalecs.javitita.compiler.intermediate;

import com.umdalecs.javitita.compiler.SymbolTable;
import com.umdalecs.javitita.compiler.lexer.TokenType;
import com.umdalecs.javitita.compiler.parser.Type;
import com.umdalecs.javitita.compiler.parser.statements.PrintStatement;
import com.umdalecs.javitita.compiler.parser.statements.VarAssignStatement;
import com.umdalecs.javitita.compiler.parser.statements.WhileStatement;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Expression;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Program;
import com.umdalecs.javitita.compiler.parser.syntaxtree.Statement;

public class IntermediateCodeGenerator {
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
        builder.append(String.format("%-10s %-14s %s\n", "","TITLE", "TEST1"));
        builder.append(String.format("%-10s %-14s %s\n", "",".model", "SMALL"));
        builder.append(String.format("%-10s %-14s %s\n", "",".stack", "100h"));

        generateSymbolTable();

        builder.append(String.format("%-10s %s\n", "", ".code"));
        builder.append("main:\n");

        for (var statement: program.getStatements()) {
            generateStatement(statement);
        }

        builder.append("""
                           MOV            AX, 4C00H
                           INT            21H
                """);
        builder.append("end main\n");

        return builder.toString();
    }

    public void generateSymbolTable() {
        builder.append(String.format("%-10s %s\n", "", ".data"));

        for (var symbol: symbolTable.getValues()) {
            var size = "dw";

            builder.append(String.format("%-10s %-14s %s\n",
                    symbol.token().literal(),
                    size,
                    "?"));
        }
    }

    public void generateStatement(Statement st) {

        switch (st) {
            case WhileStatement ws -> {
                String e1 = "E" + contadorEtiqueta++;
                String e2 = "E" + contadorEtiqueta++;

                builder.append(String.format("%s:\n", e1));

                var result = checkExpressionResult(ws.getCondition());

                builder.append(String.format("""
                                   CMP            %s, 0
                                   JE             %s
                        """, result, e2));

                for (var statement: ws.getStatements()) {
                    generateStatement(statement);
                }

                builder.append(String.format("""
                                   JMP            %s
                        %s:
                        """, e1, e2));

            }
            case PrintStatement ps -> {
                String result = checkExpressionResult(ps.getExpression());

                String e1 = "E" + contadorEtiqueta++;
                String e2 = "E" + contadorEtiqueta++;

                String format = """
                                   MOV            AX, %s
                                   MOV            BX, 10
                                   XOR            CX, CX
                        %s:
                                   XOR            DX, DX
                                   DIV            BX
                                   PUSH           DX
                                   INC            CX
                                   CMP            AX, 0
                                   JNE            %s
                        %s:
                                   POP            DX
                                   ADD            DL, '0'
                                   MOV            AH, 02H
                                   INT            21H
                                   LOOP           %s
                                   MOV            AH, 02h
                                   MOV            DL, 13     ; CR
                                   INT            21h
                        
                                   MOV            AH, 02h
                                   MOV            DL, 10     ; LF
                                   INT            21h
                        """;
                builder.append(String.format(format,
                        result,
                        e1, e1, e2, e2));
            }
            case VarAssignStatement vas -> {
                String result = checkExpressionResult(vas.getExpression());

                var symbol = symbolTable.getSymbol(vas.getIdentifier().literal());

                String format = "           MOV            %s, %s\n";
                builder.append(String.format(format,
                        "word ptr [" + symbol.token().literal() + "]",
                        result));
            }
            default -> {}
        }
    }



    private String checkExpressionResult(Expression expression) {
        if (expression.getType() == Type.BOOLEAN){
            if (expression.getLeft().literal().equals("true"))
                return "1";
            else if (expression.getLeft().literal().equals("false"))
                return "0";
            else {
                var left =  expression.getLeft().type() == TokenType.IDENTIFIER
                        ? "WORD PTR [" + expression.getLeft().literal() + "]"
                        : expression.getLeft().literal();

                var right =  expression.getRight().type() == TokenType.IDENTIFIER
                        ? "WORD PTR [" + expression.getRight().literal() + "]"
                        : expression.getRight().literal();

                String e1 = "E" + contadorEtiqueta++;
                String e2 = "E" + contadorEtiqueta++;

                if (expression.getOperation().type() == TokenType.LOWER_THAN) {
                    builder.append(String.format("""
                                       MOV            AX, %s
                                       MOV            BX, %s
                                       CMP            AX, BX
                                       JL             %s
                                       MOV            AX, 0
                                       JMP            %s
                            %s:
                                       MOV            AX, 1
                            %s:
                            """, left, right, e1, e2, e1, e2));
                }
                return "AX";
            }
        } else {
            if (expression.getOperation() == null){
                if (expression.getLeft().type() == TokenType.IDENTIFIER){
                    return "WORD PTR [" + expression.getLeft().literal() + "]";
                } else {
                    return expression.getLeft().literal();
                }
            }else {
                var left =  expression.getLeft().type() == TokenType.IDENTIFIER
                        ? "WORD PTR [" + expression.getLeft().literal() + "]"
                        : expression.getLeft().literal();

                var right =  expression.getRight().type() == TokenType.IDENTIFIER
                        ? "WORD PTR [" + expression.getRight().literal() + "]"
                        : expression.getRight().literal();

                switch (expression.getOperation().type()) {
                    case PLUS: {
                        builder.append(String.format("""
                                       MOV            AX, %s
                                       MOV            BX, %s
                                       ADD            AX, BX
                            """, left, right));
                    }
                    break;
                    case MINUS: {
                        builder.append(String.format("""
                                       MOV            AX, %s
                                       MOV            BX, %s
                                       SUB            AX, BX
                            """, left, right));
                    }
                    break;
                    case MULTI: {
                        builder.append(String.format("""
                                       MOV            AX, %s
                                       MOV            BX, %s
                                       MUL            AX, BX
                            """, left, right));
                    }
                    break;
                }
                return "AX";
            }

        }
    }
}
