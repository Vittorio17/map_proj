# Guida Utente - Server MapServer

Francesca Napoletano

---

## Introduzione

MapServer è il componente backend di un sistema per la generazione e l'interrogazione di alberi di regressione. Il server rimane in ascolto sulla rete e gestisce le richieste provenienti da uno o più client: carica i dataset da un database MySQL, costruisce modelli di predizione e permette di navigare gli alberi generati per ottenere stime su nuovi dati.

Una volta avviato, il server resta attivo in attesa di connessioni. Ogni client che si collega viene gestito in modo indipendente, quindi più utenti possono lavorare contemporaneamente senza interferire tra loro.

---

## Requisiti di sistema

Per eseguire il server serve Java 21 o superiore. Il jar è già compilato e include tutte le dipendenze necessarie, quindi non servono librerie esterne.

Il server deve potersi connettere a un database MySQL già configurato e popolato con almeno una tabella di dati. Le tabelle devono avere come ultima colonna un attributo numerico che funge da target per la regressione.

---

## Installazione e configurazione

Nella cartella di lavoro devono essere presenti due file:

- `MapServer.jar` — l'eseguibile del server
- `EseguiServer.bat` — uno script per l'avvio su Windows

Prima di avviare il server, verificare che il database MySQL sia raggiungibile. I parametri di connessione predefiniti sono:

| Parametro | Valore |
|-----------|--------|
| Server | localhost |
| Porta | 3306 |
| Database | MapDB |
| Utente | MapUser |
| Password | map |

Se il database si trova su un altro indirizzo o usa credenziali diverse, il server non riuscirà a connettersi e restituirà un errore al momento della prima operazione sui dati.

**Nota:** I parametri di connessione sono hardcoded nel codice. Per modificarli serve ricompilare il progetto dopo aver cambiato i valori nel file sorgente.

---

## Avvio

Per lanciare il server, posizionarsi nella cartella che contiene il jar ed eseguire:

```
java -jar MapServer.jar
```

Su Windows si può anche fare doppio clic su `EseguiServer.bat`, che esegue lo stesso comando e poi tiene la finestra aperta.

L'output di avvio è questo:

```
[SERVER] Inizializzazione...
[SERVER] Tentativo di avvio sulla porta 8080
Server in ascolto sulla porta 8080
```

Quando compare "Server in ascolto sulla porta 8080", il servizio è pronto ad accettare connessioni. La porta 8080 è quella predefinita e non è configurabile da riga di comando.

---

## Funzionamento e monitoraggio

Il server resta in esecuzione finché non viene terminato manualmente. Durante il normale funzionamento stampa in console i messaggi relativi alle connessioni in ingresso.

Quando un client si collega appare:

```
Nuova connessione da: /192.168.1.50
```

L'indirizzo IP identifica il client che ha avviato la connessione. Da quel momento il server può ricevere richieste da quel client per caricare tabelle, generare alberi o effettuare predizioni.

Se la connessione con un client si interrompe — perché l'applicazione client viene chiusa o per un problema di rete — il server stampa:

```
Client disconnected: /192.168.1.50
```

Questo messaggio indica che la sessione con quel client è terminata. Il server continua a funzionare e può accettare nuove connessioni.

### Messaggi di errore

Se si verifica un errore durante l'avvio del thread per un nuovo client, il server stampa:

```
Errore avvio client
```

In questo caso la connessione viene chiusa immediatamente e il client deve riprovare a collegarsi.

Errori più gravi a livello di socket vengono segnalati con:

```
Errore di connessione sul server: <dettaglio_errore>
```

Se il server non riesce a chiudere correttamente il socket principale, appare:

```
Errore in chiusura di server
```

---

## Risoluzione dei problemi più comuni

**Il server non si avvia e l'output si ferma prima di "Server in ascolto"**

La porta 8080 è probabilmente già in uso da un'altra applicazione. Chiudere eventuali altri programmi che potrebbero occuparla, oppure verificare se c'è un'altra istanza del server ancora attiva.

**Il server si avvia ma i client non riescono a caricare i dati**

Verificare che MySQL sia in esecuzione e che i parametri di connessione corrispondano a quelli configurati nel server. Se le credenziali sono sbagliate o il database non esiste, l'errore viene segnalato al client, non in console sul server.

**Un client si disconnette improvvisamente durante una predizione**

Il messaggio "Client disconnected" indica che la connessione è stata persa. Il server continua a funzionare normalmente e altri client non sono interessati. Il client deve ricollegarsi e ricominciare la sessione.

---

## Arresto

Per fermare il server premere `Ctrl+C` nella finestra della console. Su Windows, se il server è stato avviato tramite il file .bat, la combinazione `Ctrl+C` chiude anche la finestra.

Non esiste un comando di arresto graceful: la pressione di `Ctrl+C` interrompe immediatamente il processo. Le connessioni attive in quel momento vengono chiuse forzatamente e i client riceveranno un errore di disconnessione.
