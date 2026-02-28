package com.umdalecs.javitita.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.umdalecs.javitita.compiler.Scanner;
import com.umdalecs.javitita.compiler.Parser;
import com.umdalecs.javitita.compiler.Symbol;
import com.umdalecs.javitita.compiler.Token;
import com.umdalecs.javitita.compiler.TokenType;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Window extends JFrame implements ComponentListener {
    private final CodeArea codeArea;
    private final JButton lexerButton, parserButton, semButton;
    private final LexemArea lexemArea;
    private final ErrorArea errorArea;

    private List<Token> tokens;
    private Map<String, Symbol> symbols;

    List<String> parserErrors;

    public Window() {
        setTitle("Compilador javitita");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        add(codeArea = new CodeArea());
        add(lexerButton = new JButton("Análisis léxico"));
        add(lexemArea = new LexemArea());
        add(errorArea = new ErrorArea());
        add(parserButton =  new JButton("Análisis sintáctico"));
        add(semButton =  new JButton("Análisis semántico"));

        lexerButton.addActionListener((e -> {
            symbols = new LinkedHashMap<>();
            tokens = new ArrayList<>();

            var lexer = new Scanner(codeArea.getText());

            while (true) {
                Token token = lexer.nextToken();

                switch (token.type()) {
                    case IDENTIFIER -> {
                        symbols.put(token.literal(), new Symbol(token.literal(), "", "", token.line(), token.column()));
                    }
                    case EOF -> {
                        updateInterface();
                        tokens.add(token);
                        return;
                    }
                    default -> {
                    }
                }
                tokens.add(token);
            }
        }));
        parserButton.addActionListener(e -> {
            var symbols = new HashMap<String, Symbol>();
            var parser = new Parser(tokens, symbols);
            parser.parse();

            var errors = parser.getErrors();
        });
        semButton.addActionListener(e -> {});

        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addComponentListener(this);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        codeArea.setBounds(
                10,
                10,
                (int) (getWidth() * .5),
                (int) (getHeight() * .5));

        lexerButton.setBounds(
                (int) (getWidth() * .5) + 10,
                25,
                (int) (getWidth() * .20),
                (int) (getHeight() * .05));

        lexemArea.setBounds(
                (getWidth() / 2) + 10,
                (int) (getHeight() * .05 + 25),
                (int) (getWidth() * .20),
                (int) ((getHeight() * .5) - ((getHeight() * .05) + 25)));

        parserButton.setBounds(
                (int) (getWidth() * .5) + 10 + (int) (getWidth() * .20),
                25,
                getWidth() - ((getWidth() / 2) + 10 + (int)(getWidth() * .20)) - 10,
                (int) (getHeight() * .05));

        errorArea.setBounds(
                (int) (getWidth() * .5) + 10 + (int) (getWidth() * .20),
                (int) (getHeight() * .05 + 25),
                getWidth() - ((getWidth() / 2) + 10 + (int)(getWidth() * .20)) - 10,
                (int) ((getHeight() * .5) - ((getHeight() * .05) + 25)));

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

    void updateInterface() {
        SwingUtilities.invokeLater(() -> {
            lexemArea.reset();

            for (Token token : tokens) {
                if (token.type() == TokenType.ILLEGAL) {
                    codeArea.underlineText(token.absolutePos(), token.literal().length());
                }
                Object[] row = { token.literal(), token.type().tokenName() };
                lexemArea.addRow(row);
            }

        });
    }
}