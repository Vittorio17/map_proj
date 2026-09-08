package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;

/**
 * Gestisce la sessione di comunicazione con un singolo client.
 * Estende la classe Thread per permettere l'esecuzione concorrente
 * delle richieste provenienti da più client simultaneamente.
 */
public class ServerOneClient extends Thread{
	/** Il socket che mantiene la connessione di rete con il client. */
    private Socket socket;
    /** Stream di input per la ricezione di oggetti serializzati dal client. */
    private ObjectInputStream in;
    /** Stream di output per l'invio di oggetti serializzati al client. */
    private ObjectOutputStream out;

    /**
     * Inizializza il gestore della connessione per un nuovo client.
     * Associa il socket, inizializza gli stream di input e output per la comunicazione 
     * a oggetti e avvia immediatamente il thread.
     * 
     * @param s Il socket stabilito con il client.
     * @throws IOException Se si verifica un errore durante l'apertura degli stream di I/O.
     */
    public ServerOneClient(Socket s) throws IOException{
        socket = s;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.start();
    }

    /**
     * Contiene la logica principale del thread dedicata all'ascolto e all'elaborazione 
     * delle richieste del client. Esegue un ciclo continuo decodificando codici interi 
     * che definiscono l'operazione da svolgere:
     * 
     * - 0 (Acquisizione dati): Estrae i dati dal database per la tabella specificata.
     * - 1 (Costruzione albero): Induce l'albero di regressione e lo salva in un file .dmp.
     * - 2 (Caricamento albero): Ripristina in memoria un albero di regressione salvato in precedenza.
     * - 3 (Predizione): Naviga l'albero richiedendo input all'utente fino al raggiungimento di un nodo foglia.
     * 
     * Gestisce inoltre le eccezioni operative e garantisce la chiusura sicura delle risorse 
     * di rete al termine della connessione.
     */
    public void run(){
        try{
            Data trainingSet = null;
            RegressionTree tree = null;
            String tableName = null;
            
            while(true){
                int request = (Integer) in.readObject();
                switch (request) {
                    case 0: //Acquisizione dati

                        tableName = (String) in.readObject();
                        try{
                            trainingSet = new Data(tableName);
                            out.writeObject("OK");
                        }catch(TrainingDataException e){
                            out.writeObject(e.getMessage());
                        }
                        break;

                    case 1: //Costruzione albero
                        tree = new RegressionTree(trainingSet);
                        tree.salva(tableName + ".dmp");
                        out.writeObject("OK");
                        break;

                    case 2: //Caricamento albero da archivio
                        String tableNameToLoad = (String) in.readObject();
                        try {
                            tree = RegressionTree.carica(tableNameToLoad + ".dmp");
                            out.writeObject("OK");
                        } catch (Exception e) {
                            out.writeObject("Errore durante il caricamento da archivio.");
                        }
                        break;

                    case 3: //Predizione
                        try {
                            RegressionTree current = tree;
                            while (true) {
                                String query = current.getCurrentNodeQuery();
                                if (query == null) {
                                    out.writeObject("OK");
                                    out.writeObject(current.getPredictedValue());
                                    break;
                                }
                                out.writeObject("QUERY");
                                out.writeObject(query);
                                int risp = (Integer) in.readObject();
                                current = current.getChild(risp);
                            }
                        } catch (UnknownValueException e) {
                            out.writeObject(e.getMessage());
                        }
                        break;
                    
                    default:
                        break;
                }
            }
        }catch(Exception e){
            System.out.println("Client disconnected: " + socket.getInetAddress());
        }finally{ // Chiusura di tutti gli stream di I/O e del socket
            try {
                if (in != null)
                    in.close();
                if (out != null) 
                    out.close();
                if (socket != null) 
                    socket.close();
            } catch (IOException e) {
                System.out.println("Errore durante la chiusura delle risorse del client.");
            }
        }
    }
}
