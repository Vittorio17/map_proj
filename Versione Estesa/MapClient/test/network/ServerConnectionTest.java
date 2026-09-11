package network;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Test unitari per la classe {@link ServerConnection}.
 * Utilizza un ServerSocket locale come mock del server per verificare
 * connessione, disconnessione, invio e ricezione di oggetti.
 */
class ServerConnectionTest {

    private ServerConnection connection;
    private ServerSocket mockServer;
    private int port;

    @BeforeEach
    void setUp() throws IOException {
        connection = new ServerConnection();
        mockServer = new ServerSocket(0);
        port = mockServer.getLocalPort();
    }

    @AfterEach
    void tearDown() {
        connection.close();
        try {
            if (mockServer != null && !mockServer.isClosed()) {
                mockServer.close();
            }
        } catch (IOException ignored) {}
    }

    /**
     * accetta una connessione e inizializza ObjectOutputStream/ObjectInputStream
     * sul lato server, inviando l'header di serializzazione.
     */
    private Socket acceptAndInit() throws IOException {
        Socket accepted = mockServer.accept();
        ObjectOutputStream serverOut = new ObjectOutputStream(accepted.getOutputStream());
        serverOut.flush();
        ObjectInputStream serverIn = new ObjectInputStream(accepted.getInputStream());
        return accepted;
    }

    @Test
    void testInitialNotConnected() {
        assertFalse(connection.isConnected());
    }

    @Test
    void testConnect() throws Exception {
        Thread serverThread = new Thread(() -> {
            try {
                Socket accepted = acceptAndInit();
                accepted.close();
            } catch (IOException ignored) {}
        });
        serverThread.setDaemon(true);
        serverThread.start();

        connection.connect("127.0.0.1", port);
        assertTrue(connection.isConnected());
        serverThread.join(2000);
    }

    @Test
    void testClose() throws Exception {
        Thread serverThread = new Thread(() -> {
            try {
                Socket accepted = acceptAndInit();
                accepted.close();
            } catch (IOException ignored) {}
        });
        serverThread.setDaemon(true);
        serverThread.start();

        connection.connect("127.0.0.1", port);
        assertTrue(connection.isConnected());

        connection.close();
        assertFalse(connection.isConnected());
        serverThread.join(2000);
    }

    @Test
    void testDoubleClose() throws Exception {
        Thread serverThread = new Thread(() -> {
            try {
                Socket accepted = acceptAndInit();
                accepted.close();
            } catch (IOException ignored) {}
        });
        serverThread.setDaemon(true);
        serverThread.start();

        connection.connect("127.0.0.1", port);
        assertDoesNotThrow(() -> {
            connection.close();
            connection.close();
        });
        serverThread.join(2000);
    }

    @Test
    void testSendWithoutConnection() {
        IOException ex = assertThrows(IOException.class,
            () -> connection.send("test"));
        assertEquals("Nessuna connessione attiva con il server.", ex.getMessage());
    }

    @Test
    void testReceiveWithoutConnection() {
        assertThrows(IOException.class, () -> connection.receive());
    }

    @Test
    void testSendAndReceive() throws Exception {
        Thread serverThread = new Thread(() -> {
            try {
                Socket accepted = mockServer.accept();
                ObjectOutputStream serverOut = new ObjectOutputStream(accepted.getOutputStream());
                serverOut.flush();
                ObjectInputStream serverIn = new ObjectInputStream(accepted.getInputStream());

                Object received = serverIn.readObject();
                serverOut.writeObject("Risposta: " + received);
                serverOut.flush();

                serverIn.close();
                serverOut.close();
                accepted.close();
            } catch (Exception ignored) {}
        });
        serverThread.setDaemon(true);
        serverThread.start();

        connection.connect("127.0.0.1", port);
        connection.send("MessaggioTest");

        Object response = connection.receive();
        assertEquals("Risposta: MessaggioTest", response);
        serverThread.join(2000);
    }

    @Test
    void testConnectClosesPrevious() throws Exception {
        // Prima connessione
        ServerSocket mockServer1 = new ServerSocket(0);
        int port1 = mockServer1.getLocalPort();

        Thread serverThread1 = new Thread(() -> {
            try {
                Socket accepted = mockServer1.accept();
                // L'header deve essere inviato per completare la handshake
                new ObjectOutputStream(accepted.getOutputStream()).flush();
                new ObjectInputStream(accepted.getInputStream());
                Thread.sleep(500);
                accepted.close();
            } catch (Exception ignored) {}
        });
        serverThread1.setDaemon(true);
        serverThread1.start();

        connection.connect("127.0.0.1", port1);
        assertTrue(connection.isConnected());

        // Seconda connessione
        Thread serverThread2 = new Thread(() -> {
            try {
                Socket accepted = acceptAndInit();
                accepted.close();
            } catch (IOException ignored) {}
        });
        serverThread2.setDaemon(true);
        serverThread2.start();

        connection.connect("127.0.0.1", port);
        assertTrue(connection.isConnected());

        serverThread1.join(2000);
        serverThread2.join(2000);
        mockServer1.close();
    }
    
}
