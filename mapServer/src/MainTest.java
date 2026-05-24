import server.MultiServer;

/**
 * Punto di ingresso per l'applicazione Server.
 * Si occupa di avviare l'ascolto sulle porte di rete.
 */
public class MainTest {

    public static void main(String[] args){
        int port = 8080;
        System.out.println("[SERVER] Inizializzazione...");
        System.out.println("[SERVER] Tentativo di avvio sulla porta " + port);
        
        
        MultiServer server = new MultiServer(port);
    }
}