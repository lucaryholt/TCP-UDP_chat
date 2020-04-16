package com.lucaryholt.Model;

import java.net.InetAddress;
import java.util.Objects;

public class ClientContainer {

    private String name;
    private Long id;
    private InetAddress ip;
    private int port;

    public ClientContainer() {
    }

    public ClientContainer(Long id) {
        this.id = id;
    }

    public ClientContainer(String name, Long id, InetAddress ip, int port) {
        this.name = name;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        ClientContainer that = (ClientContainer) o;
        if(id == null || that.id == null){
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
