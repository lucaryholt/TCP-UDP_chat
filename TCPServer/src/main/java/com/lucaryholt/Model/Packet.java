package com.lucaryholt.Model;

import com.lucaryholt.Enum.PacketType;

public class Packet {

    private PacketType type;
    private Long id;
    private String name;
    private String msg;

    public Packet() {
    }

    public Packet(PacketType type, Long id, String name, String msg) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.msg = msg;
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

    @Override
    public String toString() {
        return "Packet{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
