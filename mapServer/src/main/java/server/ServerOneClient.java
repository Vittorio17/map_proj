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

public class ServerOneClient extends Thread{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ServerOneClient(Socket s) throws IOException{
        socket = s;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.start();
    }

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
