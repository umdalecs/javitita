package ui;

import javax.swing.*;

import compiler.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class Window extends JFrame {
    private final CodeArea codeArea;
    private final ErrorArea errorArea;
    private final LexemArea lexemArea;
    private final SymbolArea symbolArea;
    private final JButton parseButton;
    private List<Token> tokens;
    private Map<String, Symbol> symbols;
    List<String> parserErrors;

    public Window() {
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
        var lexerButton = new JButton("Análisis Léxico");
        parseButton = new JButton("Análisis Sintáctico");

        lexerButton.addActionListener(new PerformLexerAction());
        parseButton.addActionListener(new PerformParseAction());
        parseButton.setEnabled(false);

        surPanel.add(lexerButton);
        surPanel.add(parseButton);

        add(surPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    void updateInterface() {
        boolean flag = true;

        SwingUtilities.invokeLater(() -> {
            errorArea.reset();
            lexemArea.reset();
            symbolArea.reset();
        });

        for (Token token : tokens) {
            if (token.type() == TokenType.ILLEGAL) {
                flag = false;
                SwingUtilities.invokeLater(() -> {
                    errorArea.addText(String.format("illegal token ( %s ) on line %d", token.literal(), token.position()));
                });
            } else {
                Object[] row = {token.literal(), token.type().name()};
                SwingUtilities.invokeLater(() -> {
                    lexemArea.addRow(row);
                });
            }
        }

        for (var entry : symbols.entrySet()) {
            Object[] row = {
                entry.getValue().identifier(),
                entry.getValue().type(),
                entry.getValue().value(),
                entry.getValue().position()
            };
            SwingUtilities.invokeLater(() -> {
                symbolArea.addRow(row);
            });
        }

        if (parserErrors != null) {
            for (var error : parserErrors) {
                SwingUtilities.invokeLater(() -> {
                    errorArea.addText(error);
                });
            }
            parserErrors = null;
        }

        parseButton.setEnabled(flag);
    }

    class PerformLexerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            symbols = new LinkedHashMap<>();
            tokens = new ArrayList<>();

            var lexer = new Lexer(codeArea.getText());

            while(true) {
                Token token = lexer.nextToken();

                switch(token.type()) {
                    case IDENTIFIER -> {
                        symbols.put(token.literal(), new Symbol(token.literal(), "", "",token.position()));
                    }
                    case EOF -> {
                        updateInterface();
                        tokens.add(token);
                        return;
                    }
                }
                tokens.add(token);

            }
        }
    }

    class PerformParseAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            var parser = new Parser(tokens, symbols);
            parser.parse();

            parserErrors = parser.getErrors();

            updateInterface();
        }
    }

}