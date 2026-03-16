package com.umdalecs.javitita.compiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> symbols;
    public SymbolTable() {
        symbols = new HashMap<>();
    }

    /**
     * @return {@code true} if it adds the entry,
     * {@code false} if the entry actually exists
     */
    public boolean addSymbol(String name, Symbol entry) {
        if (symbols.containsKey(name)) return false;
        symbols.put(name, entry);
        return true;
    }

    public Symbol getSymbol(String key) {
        return symbols.get(key);
    }

    public Collection<Symbol> getValues() {
        return symbols.values();
    }

}
