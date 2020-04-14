package com.lucaryholt.Handler;

import com.lucaryholt.Enum.PacketType;

import java.util.Scanner;

public class ChatHandler {

    public static int state = 0;

    private ConnectionHandler connectionHandler;
    private Scanner input;
    private String name;

    public ChatHandler() {
        connectionHandler = new ConnectionHandler();
        input = new Scanner(System.in);
    }

    public void chatLoop(){
        System.out.println("Welcome to the chat!");

        while(true){
            switch (state){
                case 0 :    initiateConnection();
                            break;
                case 1 :    sendMessage();
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
        name = input.nextLine();

        System.out.println("What IP?");
        String ip = input.nextLine();

        System.out.println("What port?");
        int port = input.nextInt();
        input.nextLine();

        if(connectionHandler.initiateConnection(ip, port, name)){
            connectionHandler.initiationProtocol(name, PacketType.INIT);
            state = 1;
        }else {
            System.out.println("No server found.");
            state = 2;
        }
    }

    private void sendMessage(){
        String message = input.nextLine();

        if(message.equals("quit")){
            connectionHandler.quitMessage();
            System.out.println("closing connection...");
            state = 2;
        }else{
            connectionHandler.sendMessage(PacketType.MSG, message, name);
        }
    }

}