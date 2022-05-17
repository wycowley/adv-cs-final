import java.io.*;
import java.net.*;

public class ServerThread implements Runnable{
    private Manager manager;
    // private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    
    public ServerThread(Manager manager, Socket clientSocket){
        this.manager = manager;
        // this.clientSocket = clientSocket;
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
                
                String message = in.readLine();
                // System.out.println("READING MESSAGE: "+message);
                manager.update(message, this);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void update(String message){
        out.println(message);
        // send this to the client
        // send the arraylist of messages
        
    }
    
    public String toString(){
        return "";
    }
    
}
