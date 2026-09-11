package network;

import org.junit.jupiter.api.Test;

import model.NodeDTO;
import view.MainFrame;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class InitTreeWorkerTest {

	@Test
    public void testInitTreeWorkerExecution() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        try (ServerSocket server = new ServerSocket(0)) {
            int port = server.getLocalPort();

            Thread serverThread = new Thread(() -> {
                try (Socket socket = server.accept();
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    
                    // 1. Risposta al Comando 0 (acquisizione DB)
                    int cmd0 = (int) in.readObject();
                    String tableName = (String) in.readObject();
                    if (cmd0 == 0 && "provac".equals(tableName)) {
                        out.writeObject("OK");
                        out.flush();
                    }

                    // 2. Risposta al Comando 1 (apprendimento albero)
                    int cmd1 = (int) in.readObject();
                    if (cmd1 == 1) {
                        out.writeObject("OK");
                        out.flush();
                    }
                } catch (Exception ignored) {}
            });
            serverThread.start();

            MainFrame view = new MainFrame();
            ServerConnection conn = new ServerConnection();
            conn.connect("127.0.0.1", port);

            // fromDB = true
            InitTreeWorker worker = new InitTreeWorker(conn, "127.0.0.1", port, "provac", true, view) {
                @Override
                protected void done() {
                    // Evita l'apertura del pop-up JOptionPane durante i test automatici
                    latch.countDown();
                }
            };

            worker.execute();
            boolean completed = latch.await(3, TimeUnit.SECONDS);

            assertTrue(completed, "InitTreeWorker deve terminare senza deadlock");
            assertEquals("OK", worker.get(), "L'esito finale dell'inizializzazione deve essere 'OK'");

            conn.close();
            serverThread.join();
        }
    }
}