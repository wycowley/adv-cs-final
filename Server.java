import java.io.*;
import java.net.*;

public class Server{
    public static void main(String[] args) {
        
        Manager m = new Manager();
        try {
            int portNumber = 3333;
            ServerSocket serverSocket = new ServerSocket(portNumber);
            while(true){
                System.out.println("Waiting for a connection");

                //Wait for a connection.
                Socket clientSocket = serverSocket.accept();

                //Once a connection is made, run the socket in a ServerThread.
                ServerThread serverThread = new ServerThread(m, clientSocket);
                m.add(serverThread);
                Thread thread = new Thread(serverThread);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }
}