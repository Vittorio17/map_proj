package network;

import org.junit.jupiter.api.Test;

import view.MainFrame;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class TableLoaderWorkerTest {

    @Test
    public void testTableLoaderWorkerExecution() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        List<String> expectedTables = Arrays.asList("tabella1", "tabella2", "tabella3");

        try (ServerSocket server = new ServerSocket(0)) {
            int port = server.getLocalPort();

            Thread serverThread = new Thread(() -> {
                try (Socket socket = server.accept();
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                     ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                    
                    int cmd = (int) in.readObject();
                    if (cmd == 4) {
                        out.writeObject("OK");   
                        out.writeObject(expectedTables); 
                        out.flush();
                    }
                } catch (Exception ignored) {}
            });
            serverThread.start();

            MainFrame view = new MainFrame();
            ServerConnection conn = new ServerConnection();
            conn.connect("127.0.0.1", port);

            TableLoaderWorker worker = new TableLoaderWorker(conn, "127.0.0.1", port, view) {
                @Override
                protected void done() {
                    super.done();
                    latch.countDown();
                }
            };

            worker.execute();
            boolean completed = latch.await(3, TimeUnit.SECONDS);

            assertTrue(completed, "TableLoaderWorker deve completare l'esecuzione prima del timeout");
            
            List<String> result = worker.get();
            assertNotNull(result, "La lista ricevuta non deve essere null");
            assertEquals(3, result.size(), "Devono essere restituite 3 tabelle");
            assertEquals("tabella1", result.get(0));

            conn.close();
            serverThread.join();
        }
    }
}