package com.umdalecs.javitita.compiler;

import com.umdalecs.javitita.compiler.lexer.Token;
import com.umdalecs.javitita.compiler.parser.Type;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> symbols;

    public SymbolTable(Token identifier, Type type) {}
}
