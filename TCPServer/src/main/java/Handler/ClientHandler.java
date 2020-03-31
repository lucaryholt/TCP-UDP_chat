package Handler;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {

    private int port;
    private ArrayList<PrintWriter> printWriters = new ArrayList<>();

    public ClientHandler(int port) {
        this.port = port;
    }

    public void start(){
        ReceiveClient receiveClient = new ReceiveClient(port, this);
        Thread thread = new Thread(receiveClient);
        thread.start();
    }

    public synchronized void addToPrintWriters(PrintWriter printWriter){
        printWriters.add(printWriter);
    }

    public synchronized void sendMessage(String message, String name){
        for(PrintWriter pw : printWriters){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);
            jsonObject.put("name", name);

            pw.println(jsonObject.toJSONString());
        }
    }

}

class ReceiveClient implements Runnable{

    private ClientHandler clientHandler;
    private ServerSocket serverSocket;

    public ReceiveClient(int port, ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                new Thread(new ClientConnection(serverSocket.accept(), clientHandler)).start();

                System.out.println("client connected and added.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientConnection implements Runnable{

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private ClientHandler clientHandler;
    private boolean quit = false;

    public ClientConnection(Socket socket, ClientHandler clientHandler){
        try {
            this.clientHandler = clientHandler;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            clientHandler.addToPrintWriters(printWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(){
        try {
            JSONParser parser = new JSONParser();

            String recv = bufferedReader.readLine();

            JSONObject jsonObject = (JSONObject) parser.parse(recv);

            String name = (String) jsonObject.get("name");

            String message =  (String) jsonObject.get("message");

            if(message.equals("quit")){
                quit = true;
            }else{
                System.out.println("received from " + name + ":" + message);

                clientHandler.sendMessage(message, name);
            }
        } catch (IOException | ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!quit){
            receiveMessage();
        }
    }
}
