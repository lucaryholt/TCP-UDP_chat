package Handler;

import java.util.Scanner;

public class ChatHandler {

    //0 = no connection
    //1 = ready to send message
    //2 = ready to receive message
    //3 = connection terminated
    public static int state = 0;

    private SocketHandler socketHandler;
    private Scanner input;

    public ChatHandler() {
        socketHandler = new SocketHandler();
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
                case 2 :    receiveMessage();
                            break;
                case 3 :    System.out.println("Connection terminated...");
                            System.exit(1);
                            break;
            }
        }
    }

    private void initiateConnection(){
        System.out.println("Ready to connect!");

        System.out.println("What IP?");
        String ip = input.nextLine();

        System.out.println("What port?");
        int port = input.nextInt();
        input.nextLine();

        if(socketHandler.initiateConnection(ip, port)){
            state = 1;
        }else {
            System.out.println("No server found.");
            state = 3;
        }
    }

    private void sendMessage(){
        System.out.println("Message:");
        String message = input.nextLine();

        socketHandler.sendMessage(message);

        state = 2;
    }

    private void receiveMessage(){
        String message = socketHandler.receiveMessage();

        System.out.println("Received: " + message);

        state = 1;
    }

}