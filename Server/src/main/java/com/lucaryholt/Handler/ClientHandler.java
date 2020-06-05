package com.lucaryholt.Handler;

import com.lucaryholt.Model.ClientContainer;
import com.lucaryholt.Model.Packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientHandler {

    private Set<ClientContainer> clientContainers = new HashSet<>();
    private Long clientId = 1L;

    public Long generateId(){
        Long temp = clientId;
        clientId++;
        return temp;
    }

    public Set<ClientContainer> getClientContainers() {
        return clientContainers;
    }

    public void addToClientContainers(ClientContainer clientContainer){
        clientContainers.add(clientContainer);
    }

    public boolean alreadyInList(ClientContainer clientContainer){
        for(ClientContainer cC : clientContainers){
            if(cC.getId().equals(clientContainer.getId()) && cC.getName().equals(clientContainer.getName())){
                return true;
            }
        }
        return false;
    }

    public void removeFromClientContainers(Packet packet){
        ClientContainer clientContainer = new ClientContainer(packet.getName(), packet.getId(), packet.getIp(), packet.getPort());
        clientContainers.remove(clientContainer);
    }

    public String removeFromClientContainers(Long id){
        for(ClientContainer cC : clientContainers){
            if(cC.getId().equals(id)){
                clientContainers.remove(cC);
                return cC.getName();
            }
        }
        return "client";
    }

    public List<String> generateNameList(){
        List<String> names = new ArrayList<>();
        for(ClientContainer cC : clientContainers){
            names.add(cC.getName());
        }
        return names;
    }

}
