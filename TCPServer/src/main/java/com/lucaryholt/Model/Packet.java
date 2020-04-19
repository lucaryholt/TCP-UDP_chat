package com.lucaryholt.Model;

import com.lucaryholt.Enum.PacketType;

import java.io.PrintWriter;

public class Packet {

    private PacketType type;
    private Long id;
    private String name;
    private String msg;
    private String ip;
    private int port;
    private PrintWriter pw;

    public Packet() {
    }

    public Packet(PacketType type, Long id, String name, String msg) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.msg = msg;
    }

    public Packet(PacketType type, Long id, String name, String msg, String ip, int port) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.msg = msg;
        this.ip = ip;
        this.port = port;
    }

    public Packet(PacketType type, Long id, String name, String msg, PrintWriter pw) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.msg = msg;
        this.pw = pw;
    }

    public PacketType getType() {
        return type;
    }

    public void setType(PacketType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PrintWriter getPw() {
        return pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
