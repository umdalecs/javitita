package com.umdalecs.javitita.ui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.umdalecs.javitita.compiler.ErrorHandler;
import com.umdalecs.javitita.compiler.IntermediateCodeGenerator;
import com.umdalecs.javitita.compiler.Lexer;
import com.umdalecs.javitita.compiler.ObjectCodeGenerator;
import com.umdalecs.javitita.compiler.ParseError;
import com.umdalecs.javitita.compiler.Parser;
import com.umdalecs.javitita.compiler.Semantic;
import com.umdalecs.javitita.compiler.SymbolTable;
import com.umdalecs.javitita.compiler.Token;
import com.umdalecs.javitita.compiler.TokenType;

public class Window extends JFrame {
    private final CodeArea codeArea;
    private final JButton lexerButton, parserButton, semButton, interButton, objButton;
    private final LexemArea lexemArea;
    private final ErrorArea errorArea;
    private final IntermediateCodeArea intermediateCodeArea;
    private final ObjectCodeArea objectCodeArea;

    private List<Token> tokens;

    public Window() {
        setTitle("Compilador javitita");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        add(codeArea = new CodeArea());
        add(lexemArea = new LexemArea());
        add(errorArea = new ErrorArea());
        add(intermediateCodeArea = new IntermediateCodeArea());
        add(objectCodeArea = new ObjectCodeArea());

        add(lexerButton = new JButton("Análisis léxico"));
        add(parserButton = new JButton("Análisis sintáctico"));
        parserButton.setEnabled(false);
        add(semButton = new JButton("Análisis semántico"));
        semButton.setEnabled(false);
        add(interButton = new JButton("Código intermedio"));
        interButton.setEnabled(false);
        add(objButton = new JButton("Código Objeto"));
        objButton.setEnabled(false);

        JMenuBar menuBar = new JMenuBar();

        JMenu archiveMenu = new JMenu("\uD83D\uDCC2 Archivo");

        JMenuItem load = new JMenuItem("\uD83D\uDCC4 Abrir");
        JMenuItem save = new JMenuItem("\uD83D\uDCC4 Guardar");

        archiveMenu.add(load);
        archiveMenu.add(save);
        menuBar.add(archiveMenu);

        setJMenuBar(menuBar);

        load.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File archive = fileChooser.getSelectedFile();
                try {
                    var fileContent = Files.readString(archive.toPath());
                    codeArea.setText(fileContent);
                } catch (Exception ignored) {
                }
            }
        });

        save.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File archive = fileChooser.getSelectedFile();
                try {
                    try (FileWriter writer = new FileWriter(archive)) {
                        writer.write(codeArea.getText());
                    }
                } catch (Exception ignored) {
                }
            }
        });

        lexerButton.addActionListener((e -> {
            var errorHandler = new ErrorHandler();
            tokens = new ArrayList<>();

            var lexer = new Lexer(codeArea.getText(), errorHandler);

            Token token;
            do {
                token = lexer.nextToken();
                tokens.add(token);
            } while (token.type() != TokenType.EOF);

            SwingUtilities.invokeLater(() -> {
                lexemArea.reset();

                for (Token t : tokens) {
                    switch (t.type()) {
                        case ILLEGAL -> codeArea.markError(t);
                        case WHILE, BOOLEAN_TYPE, INTEGER_TYPE,
                             TRUE_LITERAL, FALSE_LITERAL, FN,
                             PRINT_STATEMENT -> codeArea.markKeyword(t);
                        case INTEGER_LITERAL -> codeArea.markInteger(t);
                        case IDENTIFIER -> codeArea.markIdent(t);
                        default -> {
                        }
                    }
                    Object[] row = {t.literal(), t.type().tokenName()};
                    lexemArea.addRow(row);
                }

                parserButton.setEnabled(errorHandler.getErrors().isEmpty());

                updateErrors(errorHandler);
            });
        }));
        parserButton.addActionListener(e -> {
            var errorHandler = new ErrorHandler();
            var lexer = new Lexer(codeArea.getText(), errorHandler);
            var parser = new Parser(lexer, errorHandler);

            var ast = parser.parse();

            SwingUtilities.invokeLater(() -> {
                semButton.setEnabled(errorHandler.getErrors().isEmpty());

                updateErrors(errorHandler);
            });
        });
        semButton.addActionListener(e -> {
            var errorHandler = new ErrorHandler();
            var lexer = new Lexer(codeArea.getText(), errorHandler);
            var parser = new Parser(lexer, errorHandler);

            var ast = parser.parse();

            var symbolTable = new SymbolTable();

            var sem = new Semantic(ast, errorHandler, symbolTable);

            sem.CheckSemantics();

            SwingUtilities.invokeLater(() -> {
                interButton.setEnabled(errorHandler.getErrors().isEmpty());

                updateErrors(errorHandler);
            });
        });

        interButton.addActionListener(e -> {
            var errorHandler = new ErrorHandler();
            var lexer = new Lexer(codeArea.getText(), errorHandler);
            var parser = new Parser(lexer, errorHandler);

            var ast = parser.parse();

            var symbolTable = new SymbolTable();

            var sem = new Semantic(ast, errorHandler, symbolTable);

            sem.CheckSemantics();

            var intermediateCodeGenerator = new IntermediateCodeGenerator(symbolTable, ast);

            var intermediateCode = intermediateCodeGenerator.generate();

            SwingUtilities.invokeLater(() -> {
                // Here will be the object code button
                objButton.setEnabled(errorHandler.getErrors().isEmpty());

                intermediateCodeArea.setText(intermediateCode);

                updateErrors(errorHandler);
            });
        });

        objButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    var objectCode = new ObjectCodeGenerator(intermediateCodeArea.getText()).generate();

                    objectCodeArea.setText(objectCode);
                } catch (Exception ex) {
                    errorArea.setText("Invalid intermediate code: " + ex.getMessage());
                }
            });
        });

        setSize(1024, 768);
        setMinimumSize(new java.awt.Dimension(1280, 720));

        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int x, y, w, h;

                x = 10;
                y = 10;
                w = (int) (getWidth() * .5);
                h = (int) (getHeight() * .5);
                codeArea.setBounds(x, y, w, h);

                x = x + w;
                y = y + 15;
                w = (int) (getWidth() * .2);
                h = (int) (getHeight() * .05);
                lexerButton.setBounds(x, y, w, h);

                x = x + w;
                w = getWidth() - (int) (getWidth() * .7) - 30;
                parserButton.setBounds(x, y, w, h);

                y = y + h;
                semButton.setBounds(x, y, w, h);

                x = (getWidth() / 2) + 10;
                y = (int) (getHeight() * .05 + 25);
                w = (int) (getWidth() * .20);
                h = (int) ((getHeight() * .5) - ((getHeight() * .05) + 15));
                lexemArea.setBounds(x, y, w, h);

                x = (int) (getWidth() * .5) + 10 + (int) (getWidth() * .20);
                y = y + (int) (getHeight() * .05);
                w = getWidth() - (int) (getWidth() * .7) - 30;
                h = h - (int) (getHeight() * .05);
                errorArea.setBounds(x, y, w, h);

                x = 10;
                y = (int) (getHeight() * .5) + 10;
                w = (int) (getWidth() * .5);
                h = (int) (getHeight() * .05);
                interButton.setBounds(x, y, w, h);

                y = y + h;
                h = (int) (getHeight() * .5) - 60 - h;
                intermediateCodeArea.setBounds(x, y, w, h);

                x = x + w;
                y = (int) (getHeight() * .5) + 10;
                w = (int) (getWidth() * .5) - 30;
                h = (int) (getHeight() * .05);
                objButton.setBounds(x, y, w, h);

                y = y + h;
                h = (int) (getHeight() * .5) - 60 - h;
                objectCodeArea.setBounds(x, y, w, h);

                validate();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    private void updateErrors(ErrorHandler errorHandler) {
        if (!errorHandler.getErrors().isEmpty()) {
            var sb = new StringBuilder();
            for (var error : errorHandler.getErrors()) {
                if (error instanceof ParseError pe) {
                    codeArea.markMissing(pe.getToken().absolutePos() - 1);
                }
                sb.append(error.getMessage()).append("\n");
            }

            errorArea.setText(sb.toString());
        } else
            errorArea.setText("No se detectaron errores");
    }
}