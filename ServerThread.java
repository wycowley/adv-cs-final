import java.io.*;
import java.net.*;

public class ServerThread implements Runnable{
    private Manager manager;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int uid;
    private boolean closed = false;

    
    public ServerThread(Manager manager, Socket clientSocket){
        this.manager = manager;
        uid = (int)(Math.random()*10000000);
        this.clientSocket = clientSocket;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            System.out.println(e);
        }
       
    }
    public void run(){
        //listen for messages
        //update when new message
        while(true){

            try {
                if(closed){
                    break;
                }
                String message = in.readLine();
                // System.out.println("READING MESSAGE: "+message);
                manager.update(message, this);
            } catch (Exception e) {
                System.out.println(e);
                manager.remove(this);
                break;
            }
        }
    }

    public void update(String message){
        if(message.contains("win")){
            System.out.println("Serverthread sending over a win");
        }
        out.println(message);
        out.flush();
        // send this to the client
        // send the arraylist of messages
        
    }
    public void close(){
        try {
            out.close();
            in.close();
            clientSocket.close();
            closed = true;
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public String toString(){
        return "";
    }
    @Override
    public boolean equals(Object o){
        ServerThread t = (ServerThread) o;
        if(t.uid == this.uid){
            return true;
        }
        return false;
    }
    
}
