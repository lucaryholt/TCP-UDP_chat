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

    //TODO refactoring
    //Split up in ClientHandler and ConnectionHandler
    //Maybe MessageService as Client

    private List<ClientContainer> clientContainers = new ArrayList<>(); //Lav om til map
    private DatagramSocket datagramSocket;
    private JSONParser jsonParser = new JSONParser();

    private Long clientId = 1L;

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
            recvPacket.setId((Long) jsonObject.get("id"));
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
        if(clientContainers.contains(new ClientContainer(recvPacket.getId())) || recvPacket.getType() == PacketType.INIT){
            switch(recvPacket.getType()){
                case INIT:  initiationProtocol(recvPacket, packet);
                            break;
                case MSG:   sendMessages(PacketType.MSG, recvPacket.getMsg(), recvPacket.getName());
                            break;
                case QUIT:  removeFromClientContainers(recvPacket, packet);
                            break;
            }
        }
    }

    private Long generateId(){
        Long temp = clientId;
        clientId++;
        return temp;
    }

    private void initiationProtocol(Packet packet, DatagramPacket dGPacket){
        ClientContainer clientContainer = new ClientContainer(packet.getName(), packet.getId(), dGPacket.getAddress(), dGPacket.getPort());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PacketType.INIT.toString());
        jsonObject.put("name", clientContainer.getName());

        if(!alreadyInList(clientContainer)){
            clientContainer.setId(generateId());

            jsonObject.put("id", clientContainer.getId());
            jsonObject.put("names", generateNameList());
            jsonObject.put("msg", "ACK");

            sendMessage(jsonObject, clientContainer);

            addToClientContainers(clientContainer);
        }
    }

    private void addToClientContainers(ClientContainer clientContainer){
        clientContainers.add(clientContainer);
        sendMessages(PacketType.MSG, (clientContainer.getName() + " has joined the chat!"), "server");
        System.out.println("added client to list...");
    }

    //TODO Does not work at the moment, always returns false, will be easier with ID's
    private boolean alreadyInList(ClientContainer clientContainer){
        for(ClientContainer cC : clientContainers){
            if(clientContainer.getName().equals(cC.getName())){
                return true;
            }
        }
        return false;
    }

    private void removeFromClientContainers(Packet packet, DatagramPacket dGPacket){
        ClientContainer clientContainer = new ClientContainer(packet.getName(), packet.getId(), dGPacket.getAddress(), dGPacket.getPort());
        clientContainers.remove(clientContainer);
        sendMessages(PacketType.MSG, (clientContainer.getName() + " has left the chat."), "server");
    }

    private List<String> generateNameList(){
        List<String> names = new ArrayList<>();
        for(ClientContainer cC : clientContainers){
            names.add(cC.getName());
        }
        return names;
    }

    private void sendMessages(PacketType type, String message, String name){
        List<String> names = generateNameList();

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

    private void sendMessage(JSONObject jsonObject, ClientContainer cC){
        try {
            jsonObject.put("id", cC.getId());

            byte[] sendArr = jsonObject.toJSONString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendArr, sendArr.length, cC.getIp(), cC.getPort());
            datagramSocket.send(sendPacket);
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
