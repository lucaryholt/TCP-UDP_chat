package Handler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler {

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader bufferedReader;

    public boolean initiateConnection(String ip, int port){
        try {
            socket = new Socket(ip, port);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw = new PrintWriter(socket.getOutputStream(), true);

            startThread();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startThread(){
        Receiver receiver = new Receiver(bufferedReader);
        Thread thread = new Thread(receiver);
        thread.start();
    }

    public void sendMessage(String message, String name){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        jsonObject.put("name", name);

        pw.println(jsonObject.toJSONString());
    }

}

class Receiver implements Runnable {

    private BufferedReader bufferedReader;

    public Receiver(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public void receiveMessage(){
        try {
            JSONParser parser = new JSONParser();

            String recv = bufferedReader.readLine();

            //System.out.println("raw: " + recv);

            JSONObject jsonObject = (JSONObject) parser.parse(recv);

            String name = (String) jsonObject.get("name");
            String message = (String) jsonObject.get("message");

            System.out.println(name + ": " + message);
        } catch (SocketException e){
            ChatHandler.state = 3;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            receiveMessage();
        }
    }
}