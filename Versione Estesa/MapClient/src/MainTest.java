import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import utility.Keyboard;

/**
 * Gestisce l'interazione con l'utente tramite console e si connette 
 * al server remoto tramite Socket. Consente di inviare richieste per addestrare
 * un albero di regressione da database, caricarlo da un file archivio sul server 
 * o navigare l'albero per effettuare predizioni.
 */
public class MainTest {

	/**
     * Inizializza la connessione di rete ed esegue il ciclo 
     * principale di interazione basato sui comandi utente.
     * 
	 * Il protocollo di comunicazione prevede l'invio di codici operativi interi al server:
     * 		- 0: Avvia l'acquisizione dello schema e dei dati di una tabella dal DB.
     *      - 1: Avvia la fase di apprendimento dell'albero di regressione.
     *      - 2: Richiede il caricamento di un albero pre-addestrato da un file di archivio sul server.
     *   	- 3: Avvia la fase di predizione guidata.
     * 
     * @param args array di argomenti passati da linea di comando:
	 *             	  - args[0]: indirizzo IP o l'hostname del server (es. "127.0.0.1").
     *                - args[1]: la porta del servizio sulla quale il server è in ascolto (es. "8080").
     */
	public static void main(String[] args){
		
		InetAddress addr;
		try {
			addr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println(e.toString());
			return;
		}
		Socket socket=null;
		ObjectOutputStream out=null;
		ObjectInputStream in=null;
		try {
			socket = new Socket(args[0], new Integer(args[1]).intValue());
			System.out.println(socket);		
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());	; // stream con richieste del client
			
		}  catch (IOException e) {
			System.out.println(e.toString());
			return;
		}

		

		
		String answer="";
		
		int decision=0;
		do{
		
			System.out.println("Learn Regression Tree from data [1]");
			System.out.println("Load Regression Tree from archive [2]");
			decision=Keyboard.readInt();
		}while(!(decision==1) && !(decision ==2));
		
		String tableName="";
		System.out.println("File name:");
		tableName=Keyboard.readString();
		try{
		
		if(decision==1)
		{
			System.out.println("Starting data acquisition phase!");
			
			
			
			out.writeObject(0);
			out.writeObject(tableName);
			answer=in.readObject().toString();
			if(!answer.equals("OK")){
				System.out.println(answer);
				return;
			}
				
			
			
		
			System.out.println("Starting learning phase!");
			out.writeObject(1);
			
		
		}
		else
		{
			out.writeObject(2);
			out.writeObject(tableName);
			
		}
		
		answer=in.readObject().toString();
		if(!answer.equals("OK")){
			System.out.println(answer);
			return;
		}
			
		
		
		// .........
		
		char risp='y';
		
		do{
			out.writeObject(3);
			
			System.out.println("Starting prediction phase!");
			answer=in.readObject().toString();
		
			
			while(answer.equals("QUERY")){
				// Formualting query, reading answer
				answer=in.readObject().toString();
				System.out.println(answer);
				int path=Keyboard.readInt();
				out.writeObject(path);
				answer=in.readObject().toString();
			}
		
			if(answer.equals("OK"))
			{ // Reading prediction
				answer=in.readObject().toString();
				System.out.println("Predicted class:"+answer);
				
			}
			else //Printing error message
				System.out.println(answer);
			
		
			System.out.println("Would you repeat ? (y/n)");
			risp=Keyboard.readChar();
				
		}while (Character.toUpperCase(risp)=='Y');
		
		}
		catch(IOException | ClassNotFoundException e){
			System.out.println(e.toString());
			
		}
	}

}
