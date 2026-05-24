package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestisce la connessione al database MySQL per l'applicazione.
 * Fornisce metodi per inizializzare, ottenere e chiudere la connessione.
 */
public class DbAccess {
    /**Nome del driver JDBC per MySQL.*/
    private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    /**Protocollo della tecnologia DBMS utilizzato nella stringa di connessione (JDBC). */
    private final String DBMS = "jdbc:mysql";
    /**Indirizzo IP o nome del Server su cui risiede il database */
    private String SERVER = "localhost";
    /**Nome della base di dati a cui ci si vuole collegare*/
    private String DATABASE = "MapDB";
    /**Porta su cui il DBMS MySQL accetta le connessioni */
    private final int PORT = 3306;
    /**Nome utente utilizzato per l'accesso al database */
    private String USER_ID = "MapUser";
    /**Password associata all'utente identificato da {@link #USER_ID} */
    private String PASSWORD = "map";
    /**Oggetto che mantiene e gestisce la sessione di connessione attiva con il database. */
    private Connection conn;

    /**
     * Impartisce al ClassLoader di caricare il driver JDBC di MySQL e inizializza 
     * la connessione verso il database utilizzando la connessione riferita
     * dall'attributo {@link #conn}.
     *
     * @throws DatabaseConnectionException Se il caricamento del driver fallisce, 
     *                                     o se i parametri di connessione sono errati, 
     *                                     causando il fallimento del collegamento al database.
     */
    public void initConnection() throws DatabaseConnectionException{
        try{
            Class.forName(DRIVER_CLASS_NAME).newInstance();
        }catch(ClassNotFoundException e){
            throw new DatabaseConnectionException("[!] Driver not found: " + e.getMessage());
        }catch(InstantiationException e){
            throw new DatabaseConnectionException("[!] Error during the instantiation: " + e.getMessage());
        }catch(IllegalAccessException e){
            throw new DatabaseConnectionException("[!] Cannot access the drver: " + e.getMessage());
        }
        String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE + "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";  
        
        try{
            conn = DriverManager.getConnection(connectionString);
        }catch(SQLException e){
            System.out.println("[!] SQLException: " + e.getMessage());
            System.out.println("[!] SQLState: " + e.getSQLState());
            System.out.println("[!] VendorError: " + e.getErrorCode());
            throw new DatabaseConnectionException("Fallimento nella connessione al database.");
        }
    } 

    /**
     * Restituisce l'oggetto connessione.

     * @return L'oggetto {@link Connection} attivo, oppure {@code null} se la connessione 
     *         non è ancora stata inizializzata o è stata chiusa.
     */
    Connection getConnection(){
        return conn;
    }

    /**
     * Chiude la connessione attiva riferita dall'attributo {@link #conn} e rilascia 
     * in modo appropriato le risorse di rete e di database allocate.
     */
    public void closeConnection(){
        if(conn != null){
            try{
                conn.close();
                System.out.println("Connessione chiusa con successo");
            }catch(SQLException e){
                System.out.println("[!] Errore durante la chiusura della connessione: " + e.getMessage());
                System.out.println("[!] SQLState: " + e.getSQLState());
                System.out.println("[!] VendorError: " + e.getErrorCode());
            }
        }
    }
}
