package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Classe di test per il protocollo di comunicazione di {@link ServerOneClient}.
 * Ogni test apre un socket di test su una porta effimera (ServerSocket(0)) per
 * evitare conflitti tra esecuzioni parallele, avvia il thread dedicato al client
 * e ne verifica le risposte.
 */
class ServerOneClientTest {

    @Test
    void testAcquisizioneDatiOK() throws Exception {
        // Apriamo una porta effimera e accettiamo un client in un thread separato
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        Thread serverAcceptor = new Thread(() -> {
            try {
                Socket serverSideSocket = serverSocket.accept();
                new ServerOneClient(serverSideSocket);
            } catch (Exception e) {}
        });
        serverAcceptor.start();

        try (Socket clientSocket = new Socket("localhost", port);
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            out.writeObject(0);
            out.writeObject("provaC");
            out.flush();

            String risposta = (String) in.readObject();
            assertEquals("OK", risposta, "Il server deve rispondere OK se la tabella esiste ed è valida");
        } finally {
            serverSocket.close();
            serverAcceptor.interrupt();
        }
    }

    @Test
    void testCaricamentoAlberoFallito() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        Thread serverAcceptor = new Thread(() -> {
            try {
                Socket serverSideSocket = serverSocket.accept();
                new ServerOneClient(serverSideSocket);
            } catch (Exception e) {}
        });
        serverAcceptor.start();

        try (Socket clientSocket = new Socket("localhost", port);
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            out.writeObject(2);
            out.writeObject("tabella_inesistente_xyz");
            out.flush();

            String risposta = (String) in.readObject();
            assertEquals("Errore durante il caricamento da archivio.", risposta,
                "Il server deve restituire il messaggio d'errore previsto per file non trovati");
        } finally {
            serverSocket.close();
            serverAcceptor.interrupt();
        }
    }

    @Test
    void testRecuperoTabelleOK() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();

        Thread srvThread = new Thread(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerOneClient(clientSocket);
            } catch (Exception ignored) {}
        });
        srvThread.start();

        try (Socket socket = new Socket("127.0.0.1", port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.flush();

            out.writeObject(4);
            out.flush();

            Object status = in.readObject();
            assertNotNull(status, "Lo stato restituito non deve essere nullo");
            assertInstanceOf(String.class, status, "Lo stato deve essere una String");
            assertEquals("OK", status, "Il server deve restituire 'OK' come prima risposta");

            Object data = in.readObject();
            assertNotNull(data, "La seconda risposta non deve essere nulla");
            assertInstanceOf(List.class, data, "La risposta deve essere un'istanza di List");

            @SuppressWarnings("unchecked")
            List<String> tables = (List<String>) data;
            assertFalse(tables.isEmpty(), "La lista delle tabelle non deve essere vuota");
            assertTrue(tables.contains("provac"), "La lista deve contenere la tabella di test 'provac'");
        } finally {
            serverSocket.close();
            srvThread.interrupt();
        }
    }
}