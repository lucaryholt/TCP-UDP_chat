package com.lucaryholt.Handler;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Tool.ConsolePrinter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;

public class ConnectionHandler {

    private static InetAddress ip;
    private static int port;
    private static DatagramSocket datagramSocket;
    private static String name;

    public static boolean initiateConnection(String ip, int port, String name){
        try {
            ConnectionHandler.ip = InetAddress.getByName(ip);
            ConnectionHandler.port = port;

            datagramSocket = new DatagramSocket();

            startThread(name);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void initiationProtocol(String name, PacketType type){
        sendMessage(type, "", name);
    }

    private static void startThread(String name){
        Receiver receiver = new Receiver(name, datagramSocket);
        Thread thread = new Thread(receiver);
        thread.start();
    }

    public void quitMessage(){
        sendMessage(PacketType.QUIT, "", "");
    }

    public static void sendMessage(PacketType type, String message, String name){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type.toString());
            jsonObject.put("msg", message);
            jsonObject.put("name", name);

            String data = jsonObject.toJSONString();

            byte[] sendArr = data.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendArr, sendArr.length, ip, port);
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class Receiver implements Runnable {

    private ConsolePrinter conPrint = new ConsolePrinter();
    private String name;
    private DatagramSocket datagramSocket;

    public Receiver(String name, DatagramSocket datagramSocket) {
        this.name = name;
        this.datagramSocket = datagramSocket;
    }

    public void receiveMessage(){
        try {
            JSONParser parser = new JSONParser();

            byte[] receiveArr = new byte[1000];

            DatagramPacket receivePacket = new DatagramPacket(receiveArr, receiveArr.length);

            datagramSocket.receive(receivePacket);

            String recv = new String(receivePacket.getData(), 0, receivePacket.getLength());

            //System.out.println("raw: " + recv);

            JSONObject jsonObject = (JSONObject) parser.parse(recv);

            String name = (String) jsonObject.get("name");
            String message = (String) jsonObject.get("msg");

            if(ChatHandler.ui){
                UIHandler.newChat(name, message);
            } else{
                if(!this.name.equals(name)){
                    conPrint.receivedMessage(name + ": " + message);
                }
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