package com.lucaryholt.Handler;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Model.Packet;
import com.lucaryholt.Service.MessageService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ConnectionHandler {

    private MessageService mS;

    private DatagramSocket datagramSocket;

    public ConnectionHandler(MessageService mS) {
        this.mS = mS;
    }

    public void initiateConnection(){
        try {
            datagramSocket = new DatagramSocket();

            startThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initiationProtocol(String name, PacketType type, InetAddress ip, int port){
        sendPacket(type, 0L, "", name, ip, port);
    }

    private void startThread(){
        Receiver receiver = new Receiver(datagramSocket, mS);
        Thread thread = new Thread(receiver);
        thread.start();
    }

    public void quitMessage(String name, Long id, InetAddress ip, int port){
        sendPacket(PacketType.QUIT, id, "", name, ip, port);
        System.exit(1);
    }

    public void sendPacket(PacketType type, Long id, String message, String name, InetAddress ip, int port){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type.toString());
            jsonObject.put("id", id);
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

    private MessageService mS;

    private DatagramSocket datagramSocket;

    public Receiver(DatagramSocket datagramSocket, MessageService mS) {
        this.datagramSocket = datagramSocket;
        this.mS = mS;
    }

    private PacketType getType(String type){
        switch (type) {
            case "INIT":
                return PacketType.INIT;
            case "MSG":
                return PacketType.MSG;
            case "QUIT":
                return PacketType.QUIT;
        }
        return null;
    }

    public Packet receiveMessage(){
        try {
            JSONParser parser = new JSONParser();

            byte[] receiveArr = new byte[1000];

            DatagramPacket receivePacket = new DatagramPacket(receiveArr, receiveArr.length);

            datagramSocket.receive(receivePacket);

            String recv = new String(receivePacket.getData(), 0, receivePacket.getLength());

            System.out.println(recv);

            JSONObject jsonObject = (JSONObject) parser.parse(recv);

            Packet recvPacket = generatePacket(jsonObject);

            mS.packetDecision(recvPacket);

            return recvPacket;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Packet generatePacket(JSONObject jsonObject){
        Packet recvPacket = new Packet();

        recvPacket.setType(getType((String) jsonObject.get("type")));
        recvPacket.setId((Long) jsonObject.get("id"));
        recvPacket.setName((String) jsonObject.get("name"));
        recvPacket.setMsg((String) jsonObject.get("msg"));
        recvPacket.setNames((ArrayList<String>) jsonObject.get("names"));

        return recvPacket;
    }

    @Override
    public void run() {
        while(true){
            receiveMessage();
        }
    }
}