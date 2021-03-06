package com.lucaryholt.UI;

import com.lucaryholt.Service.MessageService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame implements UI {

    private JTextField jTextField;
    private JTextArea jTextArea;
    private JButton jButton;
    private JList<String> jList;
    private boolean show = true;

    private MessageService mS;

    public static boolean connectionInitiated = false;

    public GUI(){
        super("Chat");

        mS = new MessageService(this);

        connection();

        setSize(400,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        setLayout(new BorderLayout());

        setupComponents();

        while(!connectionInitiated){
            System.out.println("waiting...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        new TrayIconHandler(this).instantiateSystemTray();

        setVisible(true);
    }

    private void connection(){
        new ConnectionWindow(mS);
    }

    private void setupComponents(){
        setupTextfield();
        setupTextarea();
        setupButton();
        setupJList();
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
                mS.sendMessage(jTextField.getText());
                jTextField.setText("");
            }
        });
        jButton.setVisible(true);
        add(jButton, BorderLayout.LINE_END);

        getRootPane().setDefaultButton(jButton);
    }

    private void setupJList(){
        jList = new JList<>();
        List<String> temp = new ArrayList<>();
        temp.add("Names:");
        jList.setBackground(Color.GRAY);
        jList.setModel(new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return temp.size();
            }

            @Override
            public String getElementAt(int i) {
                return temp.get(i);
            }
        });

        //Error when updating list...
//        jList.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent listSelectionEvent) {
//                jTextField.setText("@" + jList.getSelectedValue() + " ");
//            }
//        });

        add(jList, BorderLayout.LINE_START);
    }

    public void newChat(String name, String text){
        String newLine = name + ": " + text + "\n";
        jTextArea.append(newLine);
        if(!show){
            TrayIconHandler.displayNotification("New Message!", name + ": " + text, TrayIcon.MessageType.INFO);
        }
    }

    @Override
    public void updateNames(List<String> names) {
        names.add(0, "Names:");
        jList.setModel(new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return names.size();
            }

            @Override
            public String getElementAt(int i) {
                return names.get(i);
            }
        });
    }

    public void hideShow(){
        show = !show;
        setVisible(show);
    }

}

class ConnectionWindow extends JFrame{

    private JTextField nameField;
    private JTextField ipField;
    private JTextField portField;

    private JButton jButton;

    private MessageService mS;

    public ConnectionWindow(MessageService mS) {
        super("Connection info");

        this.mS = mS;

        setSize(300,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        setLayout(new BorderLayout());

        setupComponents();

        setVisible(true);
    }

    public void setupComponents(){
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

        JPanel inputPanel = new JPanel();

        inputPanel.add(namePanel);
        inputPanel.add(ipPanel);
        inputPanel.add(portPanel);

        add(inputPanel, BorderLayout.CENTER);

        setupButton();
    }

    private void setupButton(){
        jButton = new JButton("OK");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String name = nameField.getText();
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());

                mS.initiateConnection(ip, port, name);

                if(mS.initiationProtocol()){
                    GUI.connectionInitiated = true;
                }

                removeAll();
                setVisible(false);
            }
        });
        add(jButton, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(jButton);
    }

}

class TrayIconHandler{

    private GUI gui;
    private static TrayIcon trayIcon = null;

    public TrayIconHandler(GUI gui) {
        this.gui = gui;
    }

    public void instantiateSystemTray(){
        try{
            if(SystemTray.isSupported()){
                SystemTray tray = SystemTray.getSystemTray();
                Image image = new ImageIcon("icon0.png").getImage(); //need to add imagefile

                ActionListener hideShowListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        gui.hideShow();
                    }
                };

                ActionListener quitListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        System.exit(1);
                    }
                };

                PopupMenu popup = new PopupMenu();

                MenuItem hideShowItem = new MenuItem("Hide/Show");
                hideShowItem.addActionListener(hideShowListener);
                MenuItem quitItem = new MenuItem("Quit");
                quitItem.addActionListener(quitListener);

                popup.add(hideShowItem);
                popup.add(quitItem);

                trayIcon = new TrayIcon(image, "Chat", popup);

                trayIcon.addActionListener(hideShowListener);

                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("System tray not available. Running program without.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void displayNotification(String title, String notif, TrayIcon.MessageType type){
        trayIcon.displayMessage(title, notif, type);
    }

}
