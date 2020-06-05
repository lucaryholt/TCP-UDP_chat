package com.lucaryholt.Service;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Handler.ClientHandler;
import com.lucaryholt.Handler.ConnectionHandler.ConnectionHandler;
import com.lucaryholt.Handler.ConnectionHandler.TCPConnectionHandler;
import com.lucaryholt.Handler.ConnectionHandler.UDPConnectionHandler;
import com.lucaryholt.Model.ClientContainer;
import com.lucaryholt.Model.Packet;
import com.lucaryholt.Tool.ProjectVar;
import org.json.simple.JSONObject;

public class MessageService {

    private ClientHandler cliHan = new ClientHandler();
    private ConnectionHandler conHan;

    public MessageService() {
        if(ProjectVar.connection.equals("tcp")){
            conHan = new TCPConnectionHandler();
        }else{
            conHan = new UDPConnectionHandler();
        }
    }

    public void start(int port){
        conHan.start(port, this);
    }

    public void packetDecision(Packet recvPacket){
        if(cliHan.alreadyInList(new ClientContainer(recvPacket.getName(), recvPacket.getId())) || recvPacket.getType() == PacketType.INIT){
            switch(recvPacket.getType()){
                case INIT:      initiationProtocol(recvPacket);
                                break;
                case MSG:       conHan.sendMessages(PacketType.MSG, recvPacket.getMsg(), recvPacket.getName(), cliHan.generateNameList(), cliHan.getClientContainers());
                                break;
                case QUIT:      removeFromClientContainers(recvPacket);
                                break;
                case UPDATE:    System.out.println("received update packet from " + recvPacket.getName() + "...");
                                break;
            }
        }
    }

    private void initiationProtocol(Packet packet){
        ClientContainer clientContainer = new ClientContainer(packet.getName(), packet.getId());

        if(ProjectVar.connection.equals("tcp")){
            clientContainer.setPw(packet.getPw());
        }else{
            clientContainer.setIp(packet.getIp());
            clientContainer.setPort(packet.getPort());
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", PacketType.INIT.toString());
        jsonObject.put("name", clientContainer.getName());

        if(!cliHan.alreadyInList(clientContainer)){
            clientContainer.setId(cliHan.generateId());

            jsonObject.put("id", clientContainer.getId());
            jsonObject.put("names", cliHan.generateNameList());
            jsonObject.put("msg", "ACK");

            conHan.sendMessage(jsonObject, clientContainer);

            addToClientContainers(clientContainer);
        }
    }

    private void addToClientContainers(ClientContainer clientContainer){
        cliHan.addToClientContainers(clientContainer);
        conHan.sendMessages(PacketType.MSG, (clientContainer.getName() + " has joined the chat!"), "server", cliHan.generateNameList(), cliHan.getClientContainers());
        System.out.println("added client to list...");
    }

    private void removeFromClientContainers(Packet packet){
        cliHan.removeFromClientContainers(packet);
        conHan.sendMessages(PacketType.MSG, (packet.getName() + " has left the chat."), "server", cliHan.generateNameList(), cliHan.getClientContainers());
    }

    public void removeFromClientContainers(Long id){
        cliHan.removeFromClientContainers(id);
    }

}
