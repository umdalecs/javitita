package ui;

import javax.swing.*;

import compiler.Lexer;
import compiler.LexicalException;
import compiler.Token;
import compiler.TokenType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class LexerWindow extends JFrame implements ActionListener {
    private final CodeArea codeArea;
    private final ErrorArea errorArea;
    private final LexemArea lexemArea;
    private final SymbolArea symbolArea;

    public LexerWindow() {
        setTitle("Análisis Léxico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1920, 1080);
        setLayout(new BorderLayout());

        var centroPanel = new JPanel();
        centroPanel.setLayout(new GridLayout(2, 2, 5, 5));

        centroPanel.add(this.codeArea = new CodeArea());
        centroPanel.add(this.errorArea = new ErrorArea());
        centroPanel.add(this.lexemArea = new LexemArea());
        centroPanel.add(this.symbolArea = new SymbolArea());

        add(centroPanel, BorderLayout.CENTER);

        var surPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var analisisButton = new JButton("Análisis Léxico");

        analisisButton.addActionListener(this);

        surPanel.add(analisisButton);

        add(surPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Set<String> identifiers = new HashSet<>();

        var input = codeArea.getText();

        var lexer = new Lexer(input);
        Token token;

        SwingUtilities.invokeLater(() -> {
            errorArea.setText("");
            lexemArea.setRowCount(0);
            symbolArea.setRowCount(0);
        });

        do {
            try {
                token = lexer.nextToken();

                switch (token.getType()) {
                    case ASSIGN, LOWERT,
                         PLUS, MINUS, LPAREN,
                         RPAREN, LBRACE, RBRACE, INTEGERLITERAL,
                         WHILE, BOOLEAN, INTEGER, SEMICOLON,
                         TRUE, FALSE, PRINTSTATEMENT, CLASS -> {
                        Object[] row = {token.getLiteral(), token.getType().name()};

                        SwingUtilities.invokeLater(() -> {
                            lexemArea.addRow(row);
                        });
                    }
                    case IDENTIFIER -> {
                        Object[] lexemRow = {token.getLiteral(), token.getType().name()};

                        SwingUtilities.invokeLater(() -> {
                            lexemArea.addRow(lexemRow);
                        });

                        if (identifiers.add(token.getLiteral())) {
                            Object[] symbolRow = {token.getLiteral()};

                            SwingUtilities.invokeLater(() -> {
                                symbolArea.addRow(symbolRow);
                            });
                        }

                    }
                    default -> {
                    }
                }
            } catch (LexicalException ex) {
                String error = String.format("%s: %s `%s`",
                        ex.getMessage(),
                        ex.getToken().getType().name(),
                        ex.getToken().getLiteral()
                );
                this.errorArea.setText(errorArea.getText() + "\n" + error);
                break;
            }
        } while (token.getType() != TokenType.EOF);
    }
}