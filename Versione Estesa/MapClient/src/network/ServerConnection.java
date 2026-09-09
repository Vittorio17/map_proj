package network;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection {
	private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public void connect(String ip, int port) throws IOException {
        // Se c'è già una connessione aperta la chiudiamo
        close();

        socket = new Socket();
        // Timeout di 5 secondi per il tentativo di connessione
        socket.connect(new InetSocketAddress(ip, port), 5000);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    public void send(Object obj) throws IOException {
        if (!isConnected()) {
            throw new IOException("Nessuna connessione attiva con il server.");
        }
        out.writeObject(obj);
        out.flush();
    }
    
    public Object receive() throws IOException, ClassNotFoundException {
        if (!isConnected()) {
            throw new IOException("Nessuna connessione attiva con il server.");
        }
        return in.readObject();
    }
    
    public void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        	
        } finally {
            out = null;
            in = null;
            socket = null;
        }
    }
}
