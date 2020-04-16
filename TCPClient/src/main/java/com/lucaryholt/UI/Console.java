package com.lucaryholt.UI;

import com.lucaryholt.Service.MessageService;
import com.lucaryholt.Tool.TextPrinter;

import java.util.List;
import java.util.Scanner;

public class Console implements UI {

    private static int state = 0;

    private Scanner input;
    private MessageService mS;
    private TextPrinter tP;

    private List<String> names;

    public Console(){
        input = new Scanner(System.in);
        mS = new MessageService(this);
        tP = new TextPrinter();

        chatLoop();
    }

    private void chatLoop(){
        System.out.println("Welcome to the chat!");

        while(true){
            switch (state){
                case 0 :    initiateConnection();
                    break;
                case 1 :    messageInput();
                    break;
                case 2 :    System.out.println("Connection terminated...");
                            System.exit(1);
                            break;
            }
        }
    }

    private void initiateConnection(){
        System.out.println("Ready to connect!");

        System.out.println("Your name?");
        String name = input.nextLine();

        System.out.println("What IP?");
        String ip = input.nextLine();

        System.out.println("What port?");
        int port = input.nextInt();
        input.nextLine();

        mS.initiateConnection(ip, port, name);

        if(mS.initiationProtocol()){
            state = 1;
        }else {
            System.out.println("No server found.");
            state = 2;
        }
    }

    private void messageInput(){
        String message = input.nextLine();

        if(message.equals("!online")){
            listNames();
        }else{
            sendMessage(message);
        }
    }

    private void listNames(){
        StringBuilder sb = new StringBuilder("Online now: ");
        for(String s: names){
            sb.append(s).append(" | ");
        }
        sb.append(".");
        System.out.println(sb.toString());
    }

    private void sendMessage(String message){
        mS.sendMessage(message);
    }

    @Override
    public void newChat(String name, String message) {
        System.out.println(tP.receivedMessage(name + ": " + message));
    }

    @Override
    public void updateNames(List<String> names) {
        this.names = names;
    }
}
