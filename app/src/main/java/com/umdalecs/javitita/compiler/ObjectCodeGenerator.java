package com.umdalecs.javitita.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ObjectCodeGenerator {

    private final Map<String, IntermediateSymbol> symbols;
    private final Map<String, Integer> labels;
    private final String input;
    private Scanner scanner;
    private StringBuilder globalOutput;
    private int offset;
    private List<LineMetadata> lines;
    private List<JumpMetadata> jumps;

    private static record LineMetadata(StringBuilder output, String offset) {

    }

    private static record JumpMetadata(StringBuilder output, String label) {

    }

    public ObjectCodeGenerator(String input) {
        this.input = input;
        symbols = new HashMap<>();
        labels = new HashMap<>();
    }

    public String generate() throws Exception {
        globalOutput = new StringBuilder();
        scanner = new Scanner(input);

        scanner.nextLine(); // TITLE
        scanner.nextLine(); // MODEL
        scanner.nextLine(); // STACK
        scanner.nextLine(); // .data

        generateData();

        generateCode();

        scanner.close();

        return globalOutput.toString();
    }

    private void generateData() throws Exception {
        offset = 0;

        String line;
        while (!((line = scanner.nextLine()).trim().equals(".code"))) {
            try (var sc2 = new Scanner(line)) {
                var name = sc2.next();

                var size = switch (sc2.next()) {
                    case "dw" ->
                        16;
                    case "db" ->
                        8;
                    default -> {
                        throw new Exception("Invalid size");
                    }
                };

                var value = sc2.next();

                var offsetB = parseNumToBin(offset + "", 16);
                globalOutput.append(offsetB);

                globalOutput.append(' ');

                var valueB = parseNumToBin(value, size);

                var valArr = IntStream.range(0, valueB.length() / 8)
                        .mapToObj(i -> valueB.substring(i * 8, (i + 1) * 8))
                        .toArray(String[]::new);

                var valRev = new String[valArr.length];

                for (int i = 0; i < valArr.length; i++) {
                    valRev[i] = valArr[valArr.length - i - 1];
                }

                var valB = String.join("", valRev);

                globalOutput.append(valB);

                symbols.put(name, new IntermediateSymbol(name, offsetB, valB));

                offset += (size / 8);

                globalOutput.append('\n');
            }
        }
    }

    public String getRegCode(String name) {
        name = name.toUpperCase();

        return switch (name) {
            case "AX", "AL" ->
                "000";
            case "CX", "CL" ->
                "001";
            case "DX", "DL" ->
                "010";
            case "BX", "BL" ->
                "011";
            case "SP", "AH" ->
                "100";
            case "BP", "CH" ->
                "101";
            case "SI", "DH" ->
                "110";
            case "DI", "BH" ->
                "111";
            default ->
                throw new IllegalArgumentException("Invalid register: " + name);
        };
    }

    private String wordValue(String name) {
        name = name.toUpperCase();
        return switch (name) {
            case "AX", "BX", "CX", "DX", "SI", "DI", "SP", "BP" ->
                "1";
            case "AL", "BL", "CL", "DL", "AH", "BH", "CH", "DH" ->
                "0";
            default ->
                throw new IllegalArgumentException("Invalid register: " + name);
        };
    }

    private String parseNumToBin(String value, int size) {
        int valueI;
        if (value.startsWith("'") || value.endsWith("'")) {
            value = value.substring(1, value.length() - 1);
            valueI = value.charAt(0);
        } else if (value.toLowerCase().endsWith("h")) {
            valueI = Integer.parseInt(value.substring(0, value.length() - 1), 16);
        } else {
            valueI = Integer.parseInt(value);
        }

        return String.format("%" + size + "s", Integer.toBinaryString(valueI)).replace(' ', '0');
    }

    private void generateCode() {
        lines = new ArrayList<>();
        jumps = new ArrayList<>();

        Pattern labelPattern = Pattern.compile("^[a-zA-Z._][a-zA-Z0-9._$]*:");

        offset = 0;

        String line;
        while (!((line = scanner.nextLine()).trim().equals("end main"))) {
            try (var sc2 = new Scanner(line)) {

                var first = sc2.next();
                first = first.toUpperCase();

                var labelMatcher = labelPattern.matcher(first);

                if (labelMatcher.matches()) {
                    labels.put(first, offset);
                } else {
                    System.out.println(line);
                    var operand1 = sc2.next();

                    String operand2 = "";
                    if (sc2.hasNext()) {
                        operand1 = operand1.substring(0, operand1.length() - 1);
                        operand2 = sc2.next();
                    }

                    var currentOffset = parseNumToBin(offset + "", 16);

                    var codeLine = switch (first) {
                        case "MOV" ->
                            generateMOV(operand1, operand2);
                        case "CMP" ->
                            generateCMP(operand1, operand2);
                        case "XOR" ->
                            generateXOR(operand1, operand2);
                        case "DIV" ->
                            generateDIV(operand1);
                        case "INC" ->
                            generateINC(operand1);
                        case "ADD" ->
                            generateADD(operand1, operand2);
                        case "MUL" ->
                            generateMUL(operand1);
                        case "SUB" ->
                            generateSUB(operand1, operand2);
                        case "PUSH" ->
                            generatePUSH(operand1);
                        case "POP" ->
                            generatePOP(operand1);
                        case "INT" ->
                            generateINT(operand1);
                        case "JL" ->
                            generateJL(operand1);
                        case "JNE" ->
                            generateJNE(operand1);
                        case "JE" ->
                            generateJE(operand1);
                        case "JMP" ->
                            generateJMP(operand1);
                        case "LOOP" ->
                            generateLOOP(operand1);
                        default ->
                            null;
                    };

                    lines.add(new LineMetadata(codeLine, currentOffset));
                }

            }
        }
        for (var j : jumps) {
            j.output.append(parseNumToBin(labels.get(j.label + ":").toString(), 16));
        }
        for (var l : lines) {
            globalOutput.append(l.offset);
            globalOutput.append(" ");
            globalOutput.append(l.output.toString());
            globalOutput.append("\n");
        }

    }

    private StringBuilder generateMOV(String op1, String op2) {
        var builder = new StringBuilder();

        boolean op1IsReg = true;
        String reg1Code = "";
        try {
            reg1Code = getRegCode(op1);
        } catch (IllegalArgumentException e) {
            op1IsReg = false;
        }

        boolean op2IsReg = true;
        String reg2Code = "";
        try {
            reg2Code = getRegCode(op2);
        } catch (IllegalArgumentException e) {
            op2IsReg = false;
        }

        boolean op1IsMem = symbols.containsKey(op1);
        boolean op2IsMem = symbols.containsKey(op2);

        if (op1IsReg && op2IsReg) {
            String w = wordValue(op1);
            builder.append("1000100").append(w).append("11").append(reg2Code).append(reg1Code);
        } else if (op1IsMem && op2IsReg) {
            String w = wordValue(op2);
            builder.append("1000100").append(w).append("00").append(reg2Code).append("110");
            builder.append(symbols.get(op1).offset());
        } else if (op1IsReg && op2IsMem) {
            String w = wordValue(op1);
            builder.append("1000101").append(w).append("00").append(reg1Code).append("110");
            builder.append(symbols.get(op2).offset());
        } else if (op1IsMem && !op2IsReg && !op2IsMem) {
            String w = symbols.get(op1).value().length() == 16 ? "1" : "0";
            int size = w.equals("1") ? 16 : 8;
            builder.append("1100011").append(w).append("00000110");
            builder.append(symbols.get(op1).offset());
            builder.append(parseNumToBin(op2, size));
        } else if (op1IsReg && !op2IsReg && !op2IsMem) {
            String w = wordValue(op1);
            int size = w.equals("1") ? 16 : 8;
            builder.append("1011").append(w).append(reg1Code);
            builder.append(parseNumToBin(op2, size));
        }

        offset += builder.length() / 8;

        return builder;
    }

    private StringBuilder generateSUB(String op1, String op2) {
        var output = new StringBuilder();

        var reg1 = getRegCode(op1);
        var reg2 = getRegCode(op2);

        output.append("0000010");
        output.append(wordValue(op1));
        output.append("11");
        output.append(reg2);
        output.append(reg1);

        offset += 4;
        return output;
    }

    private StringBuilder generateMUL(String op1) {
        var output = new StringBuilder();

        var reg1 = getRegCode(op1);

        output.append("1111011");
        output.append(wordValue(op1));
        output.append("11100");
        output.append(reg1);

        offset += 4;
        offset += 4;
        return output;
    }

    private StringBuilder generateCMP(String op1, String op2) {
        var output = new StringBuilder();
        boolean isReg = true;

        String reg1 = getRegCode(op1);
        String reg2 = "";

        try {
            reg2 = getRegCode(op2);
        } catch (IllegalArgumentException e) {
            isReg = false;
        }

        if (isReg) {
            output.append("0011100");
            output.append(wordValue(op1));
            output.append("11");
            output.append(reg2);
            offset += 4;
        } else {
            output.append("1000000");
            output.append(wordValue(op1));
            output.append("11111");
            offset += 6;
        }
        output.append(reg1);

        if (!isReg) {
            output.append(parseNumToBin(op2, 16));
        }

        return output;
    }

    private StringBuilder generateXOR(String op1, String op2) {
        var output = new StringBuilder();
        output.append("0001100");
        output.append(wordValue(op1));
        output.append("11");
        output.append(getRegCode(op2));
        output.append(getRegCode(op1));
        offset += 4;
        return output;
    }

    private StringBuilder generateJL(String label) {
        var output = new StringBuilder();
        output.append("000011111000");
        output.append("1100");
        jumps.add(new JumpMetadata(output, label));
        offset += 6;
        return output;
    }

    private StringBuilder generateJNE(String label) {
        var output = new StringBuilder();
        output.append("000011111000");
        output.append("0101");
        jumps.add(new JumpMetadata(output, label));
        offset += 6;
        return output;
    }

    private StringBuilder generateJE(String label) {
        var output = new StringBuilder();
        output.append("000011111000");
        output.append("0100");
        jumps.add(new JumpMetadata(output, label));
        offset += 6;
        return output;
    }

    private StringBuilder generateJMP(String label) {
        var output = new StringBuilder();
        output.append("11101011");
        jumps.add(new JumpMetadata(output, label));
        offset += 4;
        return output;
    }

    private StringBuilder generateLOOP(String label) {
        var output = new StringBuilder();
        output.append("11100010");
        jumps.add(new JumpMetadata(output, label));
        offset += 4;
        return output;
    }

    private StringBuilder generateDIV(String reg) {
        var output = new StringBuilder();
        output.append("1111011");
        output.append(wordValue(reg));
        output.append("11110");
        output.append(getRegCode(reg));
        offset += 4;
        return output;
    }

    private StringBuilder generateINC(String reg) {
        var output = new StringBuilder();
        output.append("1111111");
        output.append(wordValue(reg));
        output.append("11110");
        output.append(getRegCode(reg));
        offset += 4;
        return output;
    }

    private StringBuilder generateADD(String op1, String op2) {
        var output = new StringBuilder();
        boolean isReg = true;

        String reg1 = getRegCode(op1);
        String reg2 = "";

        try {
            reg2 = getRegCode(op2);
        } catch (IllegalArgumentException e) {
            isReg = false;
        }
        output.append(isReg ? "0" : "1");
        output.append("000000");
        output.append(wordValue(op1));
        output.append("11");

        if (isReg) {
            output.append(reg2);
            offset += 4;
        } else {
            output.append("000");
            offset += 6;
        }
        output.append(reg1);

        if (!isReg) {
            output.append(parseNumToBin(op2, 16));
        }
        return output;
    }

    private StringBuilder generatePUSH(String reg) {
        var output = new StringBuilder();
        output.append("01010");
        output.append(getRegCode(reg));
        offset += 2;
        return output;
    }

    private StringBuilder generatePOP(String reg) {
        var output = new StringBuilder();
        output.append("01011");
        output.append(getRegCode(reg));
        offset += 2;
        return output;
    }

    private StringBuilder generateINT(String type) {
        var output = new StringBuilder();
        var typeI = parseNumToBin(type, 8);
        output.append("11001101");
        output.append(typeI);
        offset += 4;
        return output;
    }
}
