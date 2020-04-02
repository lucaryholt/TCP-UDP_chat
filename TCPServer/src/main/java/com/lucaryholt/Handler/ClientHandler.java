package com.lucaryholt.Handler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler {

    private ArrayList<PrintWriter> printWriters = new ArrayList<>();

    public ClientHandler() {

    }

    public void start(){
        Scanner sc = new Scanner(System.in);

        System.out.println("What port?");
        int port = sc.nextInt();

        startConnection(port);
    }

    private void startConnection(int port){
        System.out.println("starting server on port: " + port + "...");

        ReceiveClient receiveClient = new ReceiveClient(port, this);
        Thread thread = new Thread(receiveClient);
        thread.start();

        System.out.println("ready to receive clients...");
    }

    public synchronized void addToPrintWriters(PrintWriter printWriter){
        printWriters.add(printWriter);
    }

    public synchronized void sendMessage(String message, String name){
        for(PrintWriter pw : printWriters){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);
            jsonObject.put("name", name);

            pw.println(jsonObject.toJSONString());
        }
    }

}

class ReceiveClient implements Runnable{

    private ClientHandler clientHandler;
    private ServerSocket serverSocket;

    public ReceiveClient(int port, ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                new Thread(new ClientConnection(serverSocket.accept(), clientHandler)).start();

                System.out.println("client connected and added.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientConnection implements Runnable{

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private ClientHandler clientHandler;
    private boolean quit = false;

    public ClientConnection(Socket socket, ClientHandler clientHandler){
        try {
            this.clientHandler = clientHandler;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            clientHandler.addToPrintWriters(printWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject receiveJSONObject(){
        try {
            JSONParser parser = new JSONParser();

            String recv = bufferedReader.readLine();

            return (JSONObject) parser.parse(recv);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void receiveMessage(){
        JSONObject jsonObject = receiveJSONObject();

        String name = (String) jsonObject.get("name");

        String message =  (String) jsonObject.get("message");

        if(message.equals("quit")){
            System.out.println(name + " has terminated connection.");
            clientHandler.sendMessage((name + " has left the chat..."), "server");
            quit = true;
        }else{
            System.out.println("received from " + name + ":" + message);

            clientHandler.sendMessage(message, name);
        }
    }

    private void initiationProtocol(){
        JSONObject jsonObject = receiveJSONObject();

        String name = (String) jsonObject.get("name");

        System.out.println("client: " + name + " has connected.");

        clientHandler.sendMessage((name + " has joined the chat!"), "server");
    }

    @Override
    public void run() {
        initiationProtocol();
        while(!quit){
            receiveMessage();
        }
    }
}
