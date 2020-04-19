package com.lucaryholt.Handler;

import com.lucaryholt.Model.ClientContainer;
import com.lucaryholt.Model.Packet;

import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private List<ClientContainer> clientContainers = new ArrayList<>();
    private Long clientId = 1L;

    public Long generateId(){
        Long temp = clientId;
        clientId++;
        return temp;
    }

    public List<ClientContainer> getClientContainers() {
        return clientContainers;
    }

    public void addToClientContainers(ClientContainer clientContainer){
        clientContainers.add(clientContainer);
    }

    public boolean alreadyInList(ClientContainer clientContainer){
        for(ClientContainer cC : clientContainers){
            if(clientContainer.getName().equals(cC.getName())){
                return true;
            }
        }
        return false;
    }

    public void removeFromClientContainers(Packet packet){
        ClientContainer clientContainer = new ClientContainer(packet.getName(), packet.getId(), packet.getIp(), packet.getPort());
        clientContainers.remove(clientContainer);
    }

    public List<String> generateNameList(){
        List<String> names = new ArrayList<>();
        for(ClientContainer cC : clientContainers){
            names.add(cC.getName());
        }
        return names;
    }

}
