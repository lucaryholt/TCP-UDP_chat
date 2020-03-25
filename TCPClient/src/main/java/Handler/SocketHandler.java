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

public class SocketHandler {

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader bufferedReader;

    public boolean initiateConnection(String ip, int port){
        try {
            socket = new Socket(ip, port);

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            pw = new PrintWriter(socket.getOutputStream(), true);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeConnection(){
        try {
            bufferedReader.close();

            pw.close();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);

        pw.println(jsonObject.toJSONString());
    }

    public String receiveMessage(){
        try {
            JSONParser parser = new JSONParser();

            String recv = bufferedReader.readLine();

            //System.out.println("raw: " + recv);

            JSONObject jsonObject = (JSONObject) parser.parse(recv);

            return (String) jsonObject.get("message");
        } catch (SocketException e){
            ChatHandler.state = 3;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
