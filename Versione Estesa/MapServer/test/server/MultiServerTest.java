package server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe MultiServer.
 */
class MultiServerTest {

    @Test
    void testServerAvvioEConnessione() throws Exception {

        // Ottiene una porta effimera libera per evitare conflitti tra esecuzioni parallele
        final int port;
        try (ServerSocket temp = new ServerSocket(0)) {
            port = temp.getLocalPort();
        }

        // Avviamo il server in un thread separato per non bloccare JUnit
        Thread serverThread = new Thread() {
            public void run() {
                new MultiServer(port);
            }
        };
        serverThread.start();
        Thread.sleep(500);

        try (Socket clientSocket = new Socket("localhost", port)) {
            assertTrue(clientSocket.isConnected(), "Il client deve risultare connesso al server in ascolto");
        } finally {
            serverThread.interrupt();
        }
    }
}