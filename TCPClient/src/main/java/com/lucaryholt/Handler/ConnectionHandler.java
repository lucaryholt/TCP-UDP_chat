package com.lucaryholt.Handler;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Model.Packet;
import com.lucaryholt.Service.MessageService;
import com.lucaryholt.Tool.ProjectVar;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.*;
import java.util.ArrayList;

public class ConnectionHandler {

    private MessageService mS;

    private Connection connection;

    public ConnectionHandler(MessageService mS) {
        this.mS = mS;

        if(ProjectVar.connection.equals("tcp")){
            connection = new TCPConnection();
        }else{
            connection = new UDPConnection();
        }
    }

    public void initiateConnection(String ip, int port){
        connection.initiateConnection(ip, port);

        startThread();
    }

    public void initiationProtocol(String name, PacketType type, InetAddress ip, int port){
        sendPacket(type, 0L, "", name, ip, port);
    }

    private void startThread(){
        Receiver receiver = new Receiver(mS, connection);
        Thread thread = new Thread(receiver);
        thread.start();
    }

    public void quitMessage(String name, Long id, InetAddress ip, int port){
        sendPacket(PacketType.QUIT, id, "", name, ip, port);
        System.exit(1);
    }

    public void sendPacket(PacketType type, Long id, String message, String name, InetAddress ip, int port){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toString());
        jsonObject.put("id", id);
        jsonObject.put("msg", message);
        jsonObject.put("name", name);

        String data = jsonObject.toJSONString();

        connection.send(data, ip, port);
    }

}

class Receiver implements Runnable {

    private MessageService mS;

    private Connection connection;

    public Receiver(MessageService mS, Connection connection) {
        this.mS = mS;
        this.connection = connection;
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

            String recv = connection.receive();

            JSONObject jsonObject = (JSONObject) parser.parse(recv);

            Packet recvPacket = generatePacket(jsonObject);

            mS.packetDecision(recvPacket);

            return recvPacket;
        } catch (ParseException e) {
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