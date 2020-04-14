package com.lucaryholt.Handler;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Model.ClientContainer;
import com.lucaryholt.Model.Packet;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientHandler {

    private List<ClientContainer> clientContainers = new ArrayList<>(); //Lav om til map
    private DatagramSocket datagramSocket;
    private JSONParser jsonParser = new JSONParser();

    public ClientHandler() {

    }

    public void start(){
        Scanner sc = new Scanner(System.in);

        System.out.println("What port?");
        int port = sc.nextInt();

        initSendSocket(port);
        startConnection(port);
    }

    private void initSendSocket(int port){
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void startConnection(int port){
        System.out.println("starting server on port: " + port + "...");

        ReceiveClient receiveClient = new ReceiveClient(datagramSocket, this);
        Thread thread = new Thread(receiveClient);
        thread.start();

        System.out.println("ready to receive clients...");
    }

    public synchronized void receivePacket(DatagramPacket packet){
        try {
            String data = new String(packet.getData(), 0, packet.getLength());

            JSONObject jsonObject = (JSONObject) jsonParser.parse(data);

            Packet recvPacket = new Packet();
            recvPacket.setType(getType((String) jsonObject.get("type")));
            recvPacket.setName((String) jsonObject.get("name"));
            recvPacket.setMsg((String) jsonObject.get("msg"));

            packetDecision(recvPacket, packet);
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

    private void packetDecision(Packet recvPacket, DatagramPacket packet){
        switch(recvPacket.getType()){
            case INIT:  addToClientContainers(packet);
                        break;
            case MSG:   sendMessage(recvPacket.getMsg(), recvPacket.getName());
                        break;
            case QUIT:  removeFromClientContainers(packet);
                        break;
        }
    }

    private void addToClientContainers(DatagramPacket packet){
        ClientContainer clientContainer = new ClientContainer(packet.getAddress(), packet.getPort());
        if(!alreadyInList(clientContainer)){
            clientContainers.add(clientContainer);
            System.out.println("added client to list...");
        }else{
            System.out.println("client already added...");
        }
    }

    //Does not work at the moment, always returns false
    private boolean alreadyInList(ClientContainer clientContainer){
        for(ClientContainer cC : clientContainers){
            if(clientContainer.getIp().getCanonicalHostName().equals(cC.getIp().getCanonicalHostName()) && clientContainer.getPort() == cC.getPort()){
                return true;
            }
        }
        return false;
    }

    private void removeFromClientContainers(DatagramPacket packet){
        ClientContainer clientContainer = new ClientContainer(packet.getAddress(), packet.getPort());
        clientContainers.remove(clientContainer);
    }

    private void sendMessage(String message, String name){
        try {
            for(ClientContainer cC : clientContainers){
                System.out.println("msg: " + message + ", from: " + name);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", message);
                jsonObject.put("name", name);

                byte[] sendArr = jsonObject.toJSONString().getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendArr, sendArr.length, cC.getIp(), cC.getPort());
                datagramSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ReceiveClient implements Runnable{

    private ClientHandler clientHandler;
    private DatagramSocket datagramSocket;

    public ReceiveClient(DatagramSocket datagramSocket, ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        while(true){
            try {
                while(true){
                    byte[] receiveArr = new byte[1000];

                    DatagramPacket receivePacket = new DatagramPacket(receiveArr, receiveArr.length);

                    datagramSocket.receive(receivePacket);

                    clientHandler.receivePacket(receivePacket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
