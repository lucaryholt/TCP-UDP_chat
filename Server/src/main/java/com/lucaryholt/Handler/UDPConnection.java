package com.lucaryholt.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPConnection {

    private DatagramSocket datagramSocket;

    public void initiateConnection(int port) {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(String send, String ip, int port) {
        try {
            byte[] sendArr = send.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendArr, sendArr.length, InetAddress.getByName(ip), port);
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramPacket receive() {
        try {
            byte[] receiveArr = new byte[1000];

            DatagramPacket receivePacket = new DatagramPacket(receiveArr, receiveArr.length);

            datagramSocket.receive(receivePacket);

            return receivePacket;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
