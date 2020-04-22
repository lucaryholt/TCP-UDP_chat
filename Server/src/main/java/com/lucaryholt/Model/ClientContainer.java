package com.lucaryholt.Model;

import java.io.PrintWriter;
import java.util.Objects;

public class ClientContainer {

    private String name;
    private Long id;
    private String ip;
    private int port;
    private PrintWriter pw;

    public ClientContainer() {
    }

    public ClientContainer(Long id) {
        this.id = id;
    }

    public ClientContainer(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public ClientContainer(String name, Long id, String ip, int port) {
        this.name = name;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public ClientContainer(String name, Long id, String ip, int port, PrintWriter pw) {
        this.name = name;
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.pw = pw;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
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

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public boolean equals(Object o) {
        if(this.getClass() != o.getClass()){
            return false;
        }
        ClientContainer that = (ClientContainer) o;
        if(id == null || that.id == null){
            return false;
        }
        if(name.equals(that.getName())){
            return true;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
