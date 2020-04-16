package com.lucaryholt.Service;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Handler.ConnectionHandler;
import com.lucaryholt.Model.Packet;
import com.lucaryholt.UI.UI;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class MessageService {

    private ConnectionHandler cH;
    private UI ui;

    private InetAddress ip;
    private int port;
    private String name;

    private Long id;

    public MessageService(UI ui) {
        cH = new ConnectionHandler(this);
        this.ui = ui;
    }

    public void initiateConnection(String ipAddress, int port, String name){
        try {
            this.ip = InetAddress.getByName(ipAddress);
            this.port = port;
            this.name = name;

            cH.initiateConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean initiationProtocol(){
        cH.initiationProtocol(name, PacketType.INIT, ip, port);

        //TODO make protocol where it waits for reply from server with ID and if username already exists

        return true;
    }

    private boolean init(Packet initPacket){
        if(initPacket.getMsg().equals("ACK")){
            this.id = initPacket.getId();
            updateNames(initPacket.getNames());
            newChat("server", "Connected!");
            return true;
        }
        //TODO logic to type in connection information again and tell user to try new username
        newChat("server", "Not connected... Try again...");
        return false;
    }

    public void packetDecision(Packet recvPacket){
        switch (recvPacket.getType()){
            case INIT:  System.out.println("init packet...");
                        init(recvPacket);
                        break;
            case MSG:   updateNames(recvPacket.getNames());
                        newChat(recvPacket.getName(), recvPacket.getMsg());
                        break;
            case QUIT:  quitMessage();
                        break;
        }
    }

    public void quitMessage(){
        cH.quitMessage(name, id, ip, port);
    }

    public void sendMessage(String message){
        if(message.equals("quit")){
            quitMessage();
        } else {
            sendPacket(PacketType.MSG, message, name);
        }
    }

    public void sendPacket(PacketType type, String message, String name){
        cH.sendPacket(type, id, message, name, ip, port);
    }

    public void newChat(String name, String message){
        ui.newChat(name, message);
    }

    public void updateNames(List<String> names){
        ui.updateNames(names);
    }

}
