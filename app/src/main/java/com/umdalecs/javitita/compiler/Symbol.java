package com.umdalecs.javitita.compiler;

public record Symbol(String identifier, String type, String value,  int line, int column) {
}
