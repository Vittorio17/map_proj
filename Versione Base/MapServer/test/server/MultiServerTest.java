package server;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.Socket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe MultiServer.
 */
class MultiServerTest {

    @Test
    void testServerAvvioEConnessione() throws Exception {
        
        // Avviamo il server in un thread separato per non bloccare JUnit
        Thread serverThread = new Thread() {
            public void run() {
                new MultiServer(8099);
            }
        };
        serverThread.start();
        Thread.sleep(500);

        Socket clientSocket = new Socket("localhost", 8099);

        assertTrue(clientSocket.isConnected(), "Il client deve risultare connesso al server in ascolto");

        clientSocket.close();
        serverThread.interrupt();
    }
}