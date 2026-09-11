package network;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Gestisce la connessione di rete via socket verso il server remoto.
 * Fornisce metodi per stabilire la connessione, verificare lo stato del socket,
 * inviare o ricevere oggetti serializzati e chiudere in sicurezza le risorse.
 */
public class ServerConnection {
	/** Socket TCP utilizzato per comunicare con il server remoto. */
	private Socket socket;
	/** Stream per la serializzazione e l'invio di oggetti al server. */
    private ObjectOutputStream out;
    /** Stream per la ricezione e la deserializzazione di oggetti provenienti dal server. */
    private ObjectInputStream in;
    
    /**
     * Stabilisce una connessione socket verso il server all'indirizzo IP e alla porta specificati.
     * Se è già presente una sessione aperta, la chiude prima di tentare il nuovo collegamento.
     * Applica un timeout di connessione di 5 secondi.
     *
     * @param ip indirizzo IP del server
     * @param port numero di porta del server
     * @throws IOException se si verificano errori durante la connessione o l'inizializzazione degli stream
     */
    public void connect(String ip, int port) throws IOException {
        close();

        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), 5000);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    /**
     * Verifica lo stato attuale del socket.
     *
     * @return true se il socket è istanziato, connesso e non chiuso; false altrimenti
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    /**
     * Invia un oggetto al server tramite lo stream di output serializzandolo.
     *
     * @param obj oggetto da trasmettere
     * @throws IOException se non vi è alcuna connessione attiva o si verifica un errore durante l'invio
     */
    public void send(Object obj) throws IOException {
        if (!isConnected()) {
            throw new IOException("Nessuna connessione attiva con il server.");
        }
        out.writeObject(obj);
        out.flush();
    }
    
    /**
     * Riceve e deserializza un oggetto inviato dal server tramite lo stream di input.
     *
     * @return l'oggetto ricevuto dal server
     * @throws IOException se non vi è alcuna connessione attiva o si verifica un errore di lettura
     * @throws ClassNotFoundException se la classe dell'oggetto serializzato non viene trovata
     */
    public Object receive() throws IOException, ClassNotFoundException {
        if (!isConnected()) {
            throw new IOException("Nessuna connessione attiva con il server.");
        }
        return in.readObject();
    }
    
    /**
     * Chiude gli stream di input, output e il socket di rete,
     * rilasciando le risorse associate e reimpostando i riferimenti a null.
     */
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
