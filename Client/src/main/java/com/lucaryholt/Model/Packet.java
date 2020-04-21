package com.lucaryholt.Model;

import com.lucaryholt.Enum.PacketType;

import java.util.List;

public class Packet {

    private PacketType type;
    private Long id;
    private String name;
    private String msg;
    private List<String> names;

    public Packet() {
    }

    public Packet(PacketType type, Long id, String name, String msg, List<String> names) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.msg = msg;
        this.names = names;
    }

    public PacketType getType() {
        return type;
    }

    public void setType(PacketType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "type=" + type +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                ", names=" + names +
                '}';
    }
}
