package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {
    private int PORT = 8080;

    public MultiServer(int port){
        this.PORT = port;
        run();
    }

    private void run() {
        ServerSocket s = null; 
        
        try {
            s = new ServerSocket(PORT);
            System.out.println("Server in ascolto sulla porta " + PORT);

            while (true) {
                Socket socket = s.accept();
                System.out.println("Nuova connessione da: " + socket.getInetAddress());
                
                try {
                    new ServerOneClient(socket);
                } catch (IOException e) {
                    System.out.println("Errore avvio client");
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        System.out.println("Impossibile chiudere il socket");
                    }
                } 
            }
            
        } catch (IOException e) {
            System.out.println("Errore di connessione sul server: " + e.getMessage());
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (IOException e) {
                System.out.println("Errore in chiusura di server");
            }
        }
    }
}
