package com.umdalecs.javitita.compiler;

public record Symbol(
        Token token,
        Type type,
        String value
) {
}
