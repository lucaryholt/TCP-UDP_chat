package com.lucaryholt.Handler.ConnectionHandler;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Model.ClientContainer;
import com.lucaryholt.Model.Packet;
import com.lucaryholt.Service.MessageService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class TCPConnectionHandler implements ConnectionHandler {

    private MessageService mS;

    @Override
    public void start(int port, MessageService mS) {
        this.mS = mS;

        TCPReceiveClient receiveClient = new TCPReceiveClient(port, this);
        Thread thread = new Thread(receiveClient);
        thread.start();
    }

    @Override
    public void sendMessages(PacketType type, String message, String name, List<String> names, List<ClientContainer> clientContainers) {
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

    @Override
    public void sendMessage(JSONObject jsonObject, ClientContainer cC) {
        cC.getPw().println(jsonObject.toJSONString());
    }

    public void receivePacket(JSONObject jsonObject, PrintWriter printWriter){
        if(jsonObject != null){
            Packet packet = new Packet(
                    getType((String) jsonObject.get("type")),
                    (Long) jsonObject.get("id"),
                    (String) jsonObject.get("name"),
                    (String) jsonObject.get("msg"),
                    printWriter
            );

            mS.packetDecision(packet);
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
}

class TCPReceiveClient implements Runnable{

    private TCPConnectionHandler conHan;
    private ServerSocket serverSocket;

    public TCPReceiveClient(int port, TCPConnectionHandler conHan) {
        this.conHan = conHan;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("socket initiated on port: " + port + "...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                new Thread(new TCPClientConnection(serverSocket.accept(), conHan)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class TCPClientConnection implements Runnable{

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private TCPConnectionHandler conHan;
    private boolean quit = false;

    public TCPClientConnection(Socket socket, TCPConnectionHandler conHan){
        try {
            this.conHan = conHan;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject receiveJSONObject(){
        try {
            JSONParser parser = new JSONParser();

            String recv = bufferedReader.readLine();

            return (JSONObject) parser.parse(recv);
        } catch (SocketException e){
            System.out.println("lost connection to client...");
            try {
                bufferedReader.close();
                printWriter.close();
                quit = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void receivePacket(){
        conHan.receivePacket(receiveJSONObject(), printWriter);
    }

    @Override
    public void run() {
        while(!quit){
            receivePacket();
        }
    }
}
