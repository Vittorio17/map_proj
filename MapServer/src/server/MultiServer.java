package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Gestisce l'infrastruttura di rete principale del sistema.
 * Si occupa di istanziare il servizio in ascolto su una porta specifica e di 
 * delegare ogni nuova connessione in ingresso a un thread indipendente per 
 * consentire l'elaborazione concorrente di più client.
 */
public class MultiServer {
	
	/** Porta di rete su cui il server accetta le connessioni TCP in ingresso. */
	private int PORT = 8080;

	/**
     * Inizializza l'infrastruttura di rete impostando la porta specificata 
     * e avvia immediatamente il servizio di ascolto.
     * 
     * @param port Il numero della porta su cui il server si porrà in attesa.
     */
    public MultiServer(int port){
        this.PORT = port;
        run();
    }

    /**
     * Definisce il ciclo di vita principale del server.
     * Istanzia un {@link ServerSocket} e avvia un ciclo infinito per attendere e accettare 
     * le richieste di connessione. Per ogni nuovo client connesso, genera dinamicamente un 
     * nuovo thread {@link ServerOneClient} per gestirne la comunicazione. 
     * Cattura e gestisce le eventuali eccezioni I/O relative ai socket di rete.
     */
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
