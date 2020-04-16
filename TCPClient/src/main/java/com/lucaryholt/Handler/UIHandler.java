package com.lucaryholt.Handler;

import com.lucaryholt.Enum.PacketType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIHandler extends JFrame {

    private JTextField jTextField;
    private static JTextArea jTextArea;
    private JButton jButton;

    public static ChatHandler chatHandler;
    public static ConnectionHandler connectionHandler;

    public static boolean connectionInitiated = false;

    public UIHandler(){
        super("Chat");

        new ConnectionWindow(this).setupComponents();

        setSize(400,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        setVisible(true);

        while(!connectionInitiated){
            System.out.println("waiting...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setLayout(new BorderLayout());

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
        jTextArea.setText("Welcome to the chat!\n");
        add(jTextArea, BorderLayout.CENTER);
    }

    private void setupButton(){
        jButton = new JButton("Send");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ChatHandler.sendMessage(jTextField.getText());
                jTextField.setText("");
            }
        });
        jButton.setVisible(true);
        add(jButton, BorderLayout.LINE_END);

        getRootPane().setDefaultButton(jButton);
    }

    public static void newChat(String name, String text){
        String newLine = name + ": " + text + "\n";
        jTextArea.append(newLine);
    }

}

class ConnectionWindow{

    private JFrame jFrame;

    private JTextField nameField;
    private JTextField ipField;
    private JTextField portField;
    private JButton jButton;

    private String name;
    private String ip;
    private int port;

    public ConnectionWindow(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    public void setupComponents(){
        jFrame.setLayout(new FlowLayout());

        nameField = new JTextField(15);
        ipField = new JTextField(15);
        portField = new JTextField(15);

        JPanel namePanel = new JPanel();
        JPanel ipPanel = new JPanel();
        JPanel portPanel = new JPanel();

        namePanel.add(new JLabel("Name:"));
        namePanel.add(nameField);
        ipPanel.add(new JLabel("IP:"));
        ipPanel.add(ipField);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);

        jFrame.add(namePanel);
        jFrame.add(ipPanel);
        jFrame.add(portPanel);

        setupButton();
    }

    private void setupButton(){
        jButton = new JButton("OK");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ChatHandler.name = nameField.getText();
                name = nameField.getText();
                ip = ipField.getText();
                port = Integer.parseInt(portField.getText());

                if(ConnectionHandler.initiateConnection(ip, port, name)){
                    ConnectionHandler.initiationProtocol(name, PacketType.INIT);
                    UIHandler.connectionInitiated = true;
                }

                jFrame.remove(nameField);
                jFrame.remove(ipField);
                jFrame.remove(portField);
                jFrame.remove(jButton);
            }
        });
        jFrame.add(jButton);

        jFrame.getRootPane().setDefaultButton(jButton);
    }

}
