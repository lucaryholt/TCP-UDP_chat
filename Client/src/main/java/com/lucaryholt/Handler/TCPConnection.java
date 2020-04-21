package com.lucaryholt.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection implements Connection {

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader bufferedReader;

    @Override
    public void initiateConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String send, InetAddress ip, int port) {
        pw.println(send);
    }

    @Override
    public String receive() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
