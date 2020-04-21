package com.lucaryholt.Handler.ConnectionHandler;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Handler.UDPConnection;
import com.lucaryholt.Model.ClientContainer;
import com.lucaryholt.Model.Packet;
import com.lucaryholt.Service.MessageService;
import com.lucaryholt.Tool.ProjectVar;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.DatagramPacket;
import java.util.List;

public class UDPConnectionHandler implements ConnectionHandler {

    private JSONParser jsonParser = new JSONParser();

    private MessageService mS;
    private UDPConnection connection;

    public void start(int port, MessageService mS){
        if(ProjectVar.connection.equals("tcp")){
            connection = null;
        }else{
            connection = new UDPConnection();
        }
        this.mS = mS;

        initSendSocket(port);
        startConnection(port);
    }

    private void initSendSocket(int port){
        connection.initiateConnection(port);
    }

    private void startConnection(int port){
        System.out.println("starting server on port: " + port + "...");

        UDPReceiveClient UDPReceiveClient = new UDPReceiveClient(this, connection);
        Thread thread = new Thread(UDPReceiveClient);
        thread.start();

        System.out.println("ready to receive clients...");
    }

    public synchronized void receivePacket(DatagramPacket packet){
        try {
            String data = new String(packet.getData(), 0, packet.getLength());

            JSONObject jsonObject = (JSONObject) jsonParser.parse(data);

            Packet recvPacket = new Packet();
            recvPacket.setId((Long) jsonObject.get("id"));
            recvPacket.setType(getType((String) jsonObject.get("type")));
            recvPacket.setName((String) jsonObject.get("name"));
            recvPacket.setMsg((String) jsonObject.get("msg"));
            recvPacket.setIp(packet.getAddress().getHostName());
            recvPacket.setPort(packet.getPort());

            mS.packetDecision(recvPacket);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public void sendMessages(PacketType type, String message, String name, List<String> names, List<ClientContainer> clientContainers){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.toString());
        jsonObject.put("msg", message);
        jsonObject.put("name", name);
        jsonObject.put("names", names);


        System.out.println("msg: " + message + ", from: " + name);
        for(ClientContainer cC : clientContainers){
            sendMessage(jsonObject, cC);
            System.out.println("sending to: " + cC.getId());
        }
    }

    public void sendMessage(JSONObject jsonObject, ClientContainer cC){
        jsonObject.put("id", cC.getId());

        connection.sendPacket(jsonObject.toJSONString(), cC.getIp(), cC.getPort());
    }

}

class UDPReceiveClient implements Runnable{

    private UDPConnectionHandler UDPConnectionHandler;
    private UDPConnection connection;

    public UDPReceiveClient(UDPConnectionHandler UDPConnectionHandler, UDPConnection connection) {
        this.UDPConnectionHandler = UDPConnectionHandler;
        this.connection = connection;
    }

    @Override
    public void run() {
        while(true){
            UDPConnectionHandler.receivePacket(connection.receive());
        }
    }
}
