package com.lucaryholt.Handler;

import com.lucaryholt.Tool.ConsolePrinter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler {

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader bufferedReader;

    public boolean initiateConnection(String ip, int port, String name){
        try {
            socket = new Socket(ip, port);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw = new PrintWriter(socket.getOutputStream(), true);

            startThread(name);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initiationProtocol(String name){
        sendMessage("", name);
    }

    private void startThread(String name){
        Receiver receiver = new Receiver(bufferedReader, name);
        Thread thread = new Thread(receiver);
        thread.start();
    }

    public void sendMessage(String message, String name){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        jsonObject.put("name", name);

        pw.println(jsonObject.toJSONString());
    }

}

class Receiver implements Runnable {

    private ConsolePrinter conPrint = new ConsolePrinter();
    private BufferedReader bufferedReader;
    private String name;

    public Receiver(BufferedReader bufferedReader, String name) {
        this.bufferedReader = bufferedReader;
        this.name = name;
    }

    public void receiveMessage(){
        try {
            JSONParser parser = new JSONParser();

            String recv = bufferedReader.readLine();

            //System.out.println("raw: " + recv);

            JSONObject jsonObject = (JSONObject) parser.parse(recv);

            String name = (String) jsonObject.get("name");
            String message = (String) jsonObject.get("message");

            if(!this.name.equals(name)){
                conPrint.receivedMessage(name + ": " + message);
            }
        } catch (SocketException e){
            ChatHandler.state = 3;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            receiveMessage();
        }
    }
}