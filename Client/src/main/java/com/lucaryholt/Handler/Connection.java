package com.lucaryholt.Handler;

import java.net.InetAddress;

public interface Connection {

    void initiateConnection(String ip, int port);
    void send(String send, InetAddress ip, int port);
    String receive();

}
