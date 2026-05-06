package com.umdalecs.javitita.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ObjectCodeGenerator {
    private final Map<String, IntermediateSymbol> symbols;
    private final Scanner scanner;

    public ObjectCodeGenerator(String input) {
        this.scanner = new Scanner(input);
        this.symbols = new HashMap<>();
    }

    public String generate() throws Exception {
        var output = new StringBuilder();
        scanner.nextLine(); // TITLE
        scanner.nextLine(); // MODEL
        scanner.nextLine(); // STACK
        scanner.nextLine(); // .data

        var offset = 0;

        String line;
        while (!((line = scanner.nextLine()).trim().equals(".code"))) {

            var sc2 = new Scanner(line);

            var name = sc2.next();

            var size = switch (sc2.next()) {
                case "dw" -> 16;
                case "db" -> 8;
                default -> throw new Exception("Invalid size");
            };

            var value = sc2.next();
            var valueI = Integer.parseInt(value);

            var offsetB = String.format("%16s", Integer.toBinaryString(offset)).replace(' ', '0');
            output.append(offsetB);

            output.append(' ');

            var valueB = String.format("%" + size + "s", Integer.toBinaryString(valueI)).replace(' ', '0');
            output.append(valueB);

            symbols.put(name, new IntermediateSymbol(name, offsetB, valueB));

            offset += (size / 8);

            output.append('\n');
        }

        return output.toString();
    }
}
