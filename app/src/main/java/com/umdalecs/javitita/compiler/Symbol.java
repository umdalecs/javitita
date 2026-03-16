package com.umdalecs.javitita.compiler;

import com.umdalecs.javitita.compiler.lexer.Token;
import com.umdalecs.javitita.compiler.parser.Type;

public record Symbol(
        Token token,
        Type type,
        String value
) {
}
