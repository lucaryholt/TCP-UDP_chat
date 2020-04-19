package com.lucaryholt.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPConnection implements Connection {

    private DatagramSocket datagramSocket;

    @Override
    public void initiateConnection(String ip, int port) {
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String send, InetAddress ip, int port) {
        try {
            byte[] sendArr = send.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendArr, sendArr.length, ip, port);
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receive() {
        try {
            byte[] receiveArr = new byte[1000];

            DatagramPacket receivePacket = new DatagramPacket(receiveArr, receiveArr.length);

            datagramSocket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
