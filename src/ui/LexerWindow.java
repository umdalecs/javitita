package ui;

import javax.swing.*;

import compiler.Lexer;
import compiler.Token;

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
        setSize(1600, 900);
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

        SwingUtilities.invokeLater(() -> {
            errorArea.reset();
            lexemArea.setRowCount(0);
            symbolArea.setRowCount(0);
        });

        while(true) {
            Token token = lexer.nextToken();

            switch (token.getType()) {
                case ASSIGN, LOWERT,
                     PLUS, MINUS, LPAREN,
                     RPAREN, LBRACE, RBRACE, INTEGERLITERAL,
                     WHILE, BOOLEAN, INTEGER, SEMICOLON,
                     TRUE, FALSE, PRINTSTATEMENT, CLASS, VOID -> {
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
                case ILLEGAL -> {
                    SwingUtilities.invokeLater(() -> {
                        this.errorArea.addText(token.getLiteral());
                    });

                }
                case EOF -> {
                    return;
                }
            }
        }
    }
}