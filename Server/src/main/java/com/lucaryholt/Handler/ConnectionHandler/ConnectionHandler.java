package com.lucaryholt.Handler.ConnectionHandler;

import com.lucaryholt.Enum.PacketType;
import com.lucaryholt.Model.ClientContainer;
import com.lucaryholt.Service.MessageService;
import org.json.simple.JSONObject;

import java.util.List;

public interface ConnectionHandler {

    void start(int port, MessageService mS);

    void sendMessages(PacketType type, String message, String name, List<String> names, List<ClientContainer> clientContainers);

    void sendMessage(JSONObject jsonObject, ClientContainer cC);

}
