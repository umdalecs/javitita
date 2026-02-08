package com.umdalecs.javitita.uiv2;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.umdalecs.javitita.compiler.Lexer;
import com.umdalecs.javitita.compiler.Symbol;
import com.umdalecs.javitita.compiler.Token;
import com.umdalecs.javitita.compiler.TokenType;
import com.umdalecs.javitita.uiv1.CodeArea;
import com.umdalecs.javitita.uiv1.LexemArea;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Window extends JFrame implements ComponentListener {
    private final CodeArea codeArea;
    private final LexemArea lexemArea;
    private final JButton lexerButton;

    private List<Token> tokens;
    private Map<String, Symbol> symbols;

    List<String> parserErrors;

    public Window() {
        setTitle("Compilador javita");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        add(codeArea = new CodeArea());
        add(lexerButton = new JButton("Análisis léxico"));
        add(lexemArea = new LexemArea());

        lexerButton.addActionListener(new LexerActionPerformer());

        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addComponentListener(this);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        codeArea.setBounds(
                10,
                10,
                (int) (this.getWidth() * .5),
                (int) (this.getHeight() * .5));

        lexerButton.setBounds(
                (int) (this.getWidth() * .5) + 10,
                25,
                (int) (this.getWidth() * .20),
                (int) (this.getHeight() * .05));

        lexemArea.setBounds(
                (this.getWidth() / 2) + 10,
                (int) (this.getHeight() * .05 + 25),
                (int) (this.getWidth() * .20),
                (int) ((this.getHeight() * .5) - ((this.getHeight() * .05) + 25)));

        // repaint();
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
            // boolean flag = true;
            lexemArea.reset();

            for (Token token : tokens) {
                if (token.type() == TokenType.ILLEGAL) {
                    // flag = false;
                    // errorArea.addText(
                    // String.format("illegal token ( %s ) on line %d", token.literal(),
                    // token.position()));
                } else {
                    Object[] row = { token.literal(), token.type().name() };
                    lexemArea.addRow(row);
                }
            }

            // for (var entry : symbols.entrySet()) {
            // Object[] row = {
            // entry.getValue().identifier(),
            // entry.getValue().type(),
            // entry.getValue().value(),
            // entry.getValue().position()
            // };
            // symbolArea.addRow(row);
            // }

            // if (parserErrors != null) {
            // for (var error : parserErrors) {
            // SwingUtilities.invokeLater(() -> {
            // errorArea.addText(error);
            // });
            // }
            // parserErrors = null;
            // }

            // parseButton.setEnabled(flag);
        });
    }

    class LexerActionPerformer implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            symbols = new LinkedHashMap<>();
            tokens = new ArrayList<>();

            var lexer = new Lexer(codeArea.getText());

            while (true) {
                Token token = lexer.nextToken();

                switch (token.type()) {
                    case IDENTIFIER -> {
                        symbols.put(token.literal(), new Symbol(token.literal(), "", "", token.position()));
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
        }
    }
}