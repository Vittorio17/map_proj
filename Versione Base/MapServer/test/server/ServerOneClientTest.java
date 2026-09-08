package server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe ServerOneClient.
 * Simula il comportamento di un client reale che invia comandi tramite socket.
 */
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
}