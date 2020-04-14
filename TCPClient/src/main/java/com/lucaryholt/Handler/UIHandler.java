package com.lucaryholt.Handler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIHandler extends JFrame {

    private JTextField jTextField;
    private JTextArea jTextArea;
    private JButton jButton;

    public UIHandler(){
        super("Chat");
        setSize(400,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        setVisible(true);

        setupComponents();
    }

    private void setupComponents(){
        setupTextfield();
        setupTextarea();
        setupButton();
    }

    private void setupTextfield(){
        jTextField = new JTextField(20);
        add(jTextField, BorderLayout.PAGE_END);
    }

    private void setupTextarea(){
        jTextArea = new JTextArea(20, 20);
        jTextArea.setText("Welcome to the chat!");
        add(jTextArea, BorderLayout.CENTER);
    }

    private void setupButton(){
        jButton = new JButton("Send");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                jTextField.setText("");
            }
        });
        jButton.setVisible(true);
        add(jButton, BorderLayout.LINE_END);

        getRootPane().setDefaultButton(jButton);
    }

}
