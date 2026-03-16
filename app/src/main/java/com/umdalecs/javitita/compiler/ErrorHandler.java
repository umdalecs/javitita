package com.umdalecs.javitita.compiler;

import com.umdalecs.javitita.compiler.lexer.LexicalError;
import com.umdalecs.javitita.compiler.lexer.Token;
import com.umdalecs.javitita.compiler.lexer.TokenType;
import com.umdalecs.javitita.compiler.parser.ParseError;
import com.umdalecs.javitita.compiler.semantic.SemanticError;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private final List<Exception> errors;

    public ErrorHandler() {
        errors = new ArrayList<>();
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public void unknownToken(Token token) {
        errors.add(new LexicalError(token));
    }

    public boolean expectToken(Token token, TokenType expectedTokenType) {
        var result = token.type() == expectedTokenType;

        if (!result) {
            errors.add(new ParseError(expectedTokenType.name(), token));
        }

        return result;
    }
    public boolean expectTokens(Token token, List<TokenType> expectedTokens) {
        boolean flag = false;
        StringBuilder expectedTokenName = new StringBuilder();

        for (TokenType expectedTokenType : expectedTokens) {
            expectedTokenName.append(expectedTokenType.name());
            expectedTokenName.append(" or ");
            var result = token.type() == expectedTokenType;

            if (result) {
                flag = true;
            }
        }

        expectedTokenName.delete(expectedTokenName.length() - " or ".length(), expectedTokenName.length());

        if (!flag) {
            errors.add(new ParseError(expectedTokenName.toString(), token));
        }
        return flag;
    }

    public void addSemanticError(String msg) {
        errors.add(new SemanticError(msg));
    }
}
