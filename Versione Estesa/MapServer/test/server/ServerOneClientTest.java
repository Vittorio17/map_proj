package server;

import static org.junit.Assert.assertNotEquals;
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

import database.DbAccess;


class ServerOneClientTest {

    @Test
    void testAcquisizioneDatiOK() throws Exception {
        // Apriamo una porta e accettiamo un client in un thread separato
        ServerSocket serverSocket = new ServerSocket(8100);
        Thread serverAcceptor = new Thread(() -> {
            try {
                Socket serverSideSocket = serverSocket.accept();
                new ServerOneClient(serverSideSocket);
            } catch (Exception e) {}
        });
        serverAcceptor.start();

        Socket clientSocket = new Socket("localhost", 8100);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        out.writeObject(0);
        out.writeObject("provaC");
        out.flush();

        String risposta = (String) in.readObject();
        assertEquals("OK", risposta, "Il server deve rispondere OK se la tabella esiste ed è valida");

        clientSocket.close();
        serverSocket.close();
    }

    @Test
    void testCaricamentoAlberoFallito() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8101);
        Thread serverAcceptor = new Thread(() -> {
            try {
                Socket serverSideSocket = serverSocket.accept();
                new ServerOneClient(serverSideSocket);
            } catch (Exception e) {}
        });
        serverAcceptor.start();

        Socket clientSocket = new Socket("localhost", 8101);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        out.writeObject(2);
        out.writeObject("tabella_inesistente_xyz");
        out.flush();

        String risposta = (String) in.readObject();
        assertEquals("Errore durante il caricamento da archivio.", risposta, 
            "Il server deve restituire il messaggio d'errore previsto per file non trovati");

        clientSocket.close();
        serverSocket.close();
    }
    
    void testRecuperoTabelleOK() throws Exception {
        int testPort = 9091;
        ServerSocket serverSocket = new ServerSocket(testPort);

        Thread srvThread = new Thread(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerOneClient(clientSocket);
            } catch (Exception ignored) {}
        });
        srvThread.start();

        try (Socket socket = new Socket("127.0.0.1", testPort);
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
    
    void testRecuperoTabelleFallito() throws Exception {
        int testPort = 9092;
        ServerSocket serverSocket = new ServerSocket(testPort);

        Thread srvThread = new Thread(() -> {
            try {
                Socket clientSocket = serverSocket.accept();
                new ServerOneClient(clientSocket);
            } catch (Exception ignored) {}
        });
        srvThread.start();

        try {
            Socket socket = new Socket("127.0.0.1", testPort);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            out.writeObject(4);
            out.flush();
            socket.close();

        } finally {
            serverSocket.close();
            srvThread.interrupt();
        }
    }
}