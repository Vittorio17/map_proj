# Guida Utente

## 1. Introduzione

Questo progetto implementa un'applicazione **client-server** per la costruzione e l'utilizzo di un **albero di regressione**. Il programma permette di:

- Connettersi a un database MySQL per acquisire i dati di training
- Costruire un albero di regressione partendo dai dati di una tabella
- Salvare l'albero addestrato su file (archivio `.dmp`)
- Caricare un albero precedentemente salvato
- Effettuare predizioni navigando l'albero in modo interattivo

Il server gestisce la logica di apprendimento e predizione, mentre il client fornisce l'interfaccia a riga di comando per l'utente.

---

## 2. Requisiti

Per eseguire il programma servono:

- **Java 8 o superiore** (JDK/JRE)
- **MySQL Server** in esecuzione e accessibile
- **Driver MySQL Connector/J** (incluso in `mapServer/lib/mysql-connector-java-8.0.17.jar`)
- Database `MapDB` già creato su MySQL con almeno una tabella contenente dati numerici (l'ultima colonna deve essere numerica e rappresenta il target da predire)
- Utente MySQL `MapUser` con password `map` e permessi sul database `MapDB` (valori di default, modificabili in `DbAccess.java`)

> **Nota:** Se il database ha configurazione diversa, modificare i parametri in `mapServer/src/database/DbAccess.java` prima di compilare.

---

## 3. Struttura generale dell'applicazione

L'applicazione è composta da due programmi separati che comunicano via **socket TCP**:

| Componente | Ruolo |
|------------|-------|
| **Server** (`MultiServer` + `ServerOneClient`) | Resta in ascolto sulla porta 8080. Gestisce: connessione al database, caricamento dati, costruzione albero, salvataggio/caricamento archivio, predizione. Un thread per ogni client connesso. |
| **Client** (`MainTest` in mapClient) | Interfaccia utente a console. Si connette al server, invia comandi, mostra query e riceve risultati. |

**Comunicazione:** Il client invia codici operativi interi al server:
- `0` → Acquisizione dati da tabella database
- `1` → Costruzione albero di regressione
- `2` → Caricamento albero da file `.dmp`
- `3` → Avvio fase di predizione guidata

---

## 4. Avvio del programma

### 4.1 Compilazione (se necessario)

I file `.class` sono già presenti nelle cartelle `bin/`. Se serve ricompilare:

```bash
# Da mapServer
javac -cp "lib/mysql-connector-java-8.0.17.jar" -d bin src/**/*.java

# Da mapClient
javac -d bin src/**/*.java
```

### 4.2 Avvio del Server

Il server deve essere avviato **per primo**.

```bash
cd mapServer
java -cp "bin;lib/mysql-connector-java-8.0.17.jar" MainTest
```

Output atteso:
```
[SERVER] Inizializzazione...
[SERVER] Tentativo di avvio sulla porta 8080
Server in ascolto sulla porta 8080
```

Il server rimane in esecuzione in attesa di connessioni.

### 4.3 Avvio del Client

In un **nuovo terminale**:

```bash
cd mapClient
java -cp bin MainTest <IP_SERVER> <PORTA>
```

Esempio (server in locale):
```bash
java -cp bin MainTest 127.0.0.1 8080
```

Output atteso:
```
Socket[addr=/127.0.0.1,port=8080,localport=xxxxx]
Learn Regression Tree from data [1]
Load Regression Tree from archive [2]
```

> **Importante:** Il client richiede **due argomenti**: indirizzo IP (o hostname) del server e porta (default 8080). Senza argomenti il programma termina con errore.

### 4.4 Verifica connessione

Se vedi il menu `Learn Regression Tree from data [1] / Load Regression Tree from archive [2]`, la connessione è riuscita. Se il server non è raggiungibile, il client stampa l'eccezione (es. `Connection refused`) e termina.

---

## 5. Utilizzo del client

Una volta connesso, il client guida l'utente attraverso questi passaggi:

### Passo 1: Scelta modalità

```
Learn Regression Tree from data [1]
Load Regression Tree from archive [2]
```

Digita `1` per **costruire un nuovo albero** dai dati del database, oppure `2` per **caricare un albero salvato** in precedenza.

### Passo 2: Nome file/tabella

```
File name:
```

- Se hai scelto **1**: inserisci il **nome della tabella** nel database `MapDB` (es. `provac`).
- Se hai scelto **2**: inserisci il **nome base del file archivio** (senza estensione `.dmp`, es. `provac` per caricare `provac.dmp` dal server).

### Passo 3: Acquisizione dati (solo modalità 1)

```
Starting data acquisition phase!
```

Il client invia la richiesta al server. Se la tabella non esiste, è vuota, o l'ultima colonna non è numerica, il server restituisce un messaggio di errore e il client termina.

Se tutto va bene:
```
Starting learning phase!
```

Il server costruisce l'albero e lo salva automaticamente come `<nomeTabella>.dmp` (es. `provac.dmp`).

### Passo 4: Fase di predizione

```
Starting prediction phase!
```

Il server inizia a inviare **query** per navigare l'albero. Ogni query ha questo formato:

```
0:Attributo=Valore
1:Attributo=Valore
...
```

Per attributi continui la query usa operatori `<=` o `>`:
```
0:Temperatura<=23.5
1:Temperatura>23.5
```

L'utente deve digitare l'**indice numerico** del ramo da seguire (0, 1, 2...).

Esempio:
```
0:Outlook=Sunny
1:Outlook=Overcast
2:Outlook=Rain
```
Input utente: `0`

### Passo 5: Risultato

Quando si raggiunge una foglia, il server invia `OK` seguito dal valore predetto:

```
Predicted class:24.6
```

### Passo 6: Nuova predizione

```
Would you repeat ? (y/n)
```

Digita `y` per fare un'altra predizione con lo stesso albero, `n` per uscire.

---

## 6. Utilizzo dei dati

I dati vengono letti **direttamente dal database MySQL**.

### Requisiti sulla tabella

- La tabella deve esistere nel database `MapDB`
- Deve avere **almeno 2 colonne**
- **L'ultima colonna** deve essere di tipo **numerico** (INT, DOUBLE, FLOAT, ecc.) → è il **target** (valore da predire)
- Le colonne precedenti sono gli **attributi esplicativi** (possono essere numerici o testuali)

### Esempio tabella valida (`provac`)

| Outlook | Temperature | Humidity | Windy | PlayHours |
|---------|-------------|----------|-------|-----------|
| Sunny   | 85          | 85       | false | 2.0       |
| Sunny   | 80          | 90       | true  | 1.5       |
| Overcast| 83          | 86       | false | 3.0       |
| Rain    | 70          | 96       | false | 2.5       |

- Attributi esplicativi: `Outlook` (discreto), `Temperature` (continuo), `Humidity` (continuo), `Windy` (discreto)
- Target: `PlayHours` (continuo, ultima colonna)

### Come vengono gestiti gli attributi

- **Discreti** (stringhe): il server estrae i valori distinti dalla colonna e crea un ramo per ogni valore
- **Continui** (numerici): il server trova la soglia migliore che minimizza la varianza e crea due rami (`<= soglia` e `> soglia`)

---

## 7. Costruzione dell'albero di regressione

Quando si sceglie l'opzione **1** (Learn), il server:

1. **Carica i dati** dalla tabella specificata (connessione al database, lettura schema e tuple)
2. **Costruisce l'albero** ricorsivamente:
   - Se il sotto-insieme ha ≤ 10% degli esempi totali → crea una **foglia** (valore predetto = media dei target in quel sotto-insieme)
   - Altrimenti cerca lo **split migliore** (attributo + soglia/valore) che **minimizza la varianza** ponderata dei figli
   - Ripete ricorsivamente per ogni ramo
3. **Salva l'albero** su file `<nomeTabella>.dmp` nella cartella di lavoro del server (es. `provac.dmp`)

> Il parametro "10% esempi per foglia" è fisso nel codice (`trainingSet.getNumberOfExamples()*10/100`).

---

## 8. Utilizzo dell'albero e predizione

Dopo il training (opzione 1) o il caricamento (opzione 2), si entra nella fase di predizione (opzione 3).

### Cosa fornire

L'utente risponde alle **query del server** digitando l'indice del ramo (intero).

### Cosa succede

Il server naviga l'albero partendo dalla radice:
- Nodo di split → invia la query al client → attende risposta → scende nel sotto-albero scelto
- Nodo foglia → invia `OK` + valore predetto (media dei target di quella foglia)

### Come interpretare il risultato

```
Predicted class: 24.6
```

Significa che, per la combinazione di valori scelti percorrendo l'albero, il valore predetto per l'attributo target è **24.6** (unità dipendenti dal dataset, es. ore, euro, gradi...).

---

## 9. Esempio completo

Supponiamo di avere nel database `MapDB` una tabella `provac` con dati meteo (target = ore di gioco).

### 1. Avvio server
```bash
cd mapServer
java -cp "bin;lib/mysql-connector-java-8.0.17.jar" MainTest
```
```
[SERVER] Inizializzazione...
[SERVER] Tentativo di avvio sulla porta 8080
Server in ascolto sulla porta 8080
Nuova connessione da: /127.0.0.1
```

### 2. Avvio client
```bash
cd mapClient
java -cp bin MainTest 127.0.0.1 8080
```
```
Socket[addr=/127.0.0.1,port=8080,localport=54321]
Learn Regression Tree from data [1]
Load Regression Tree from archive [2]
```

### 3. Scelta modalità
```
1
```

### 4. Nome tabella
```
File name:
provac
```
```
Starting data acquisition phase!
Starting learning phase!
```

### 5. Predizione
```
Starting prediction phase!
0:Outlook=Sunny
1:Outlook=Overcast
2:Outlook=Rain
```
Input: `0`

```
0:Temperature<=83.0
1:Temperature>83.0
```
Input: `0`

```
0:Humidity<=87.0
1:Humidity>87.0
```
Input: `1`

```
Predicted class:1.75
```

### 6. Nuova predizione
```
Would you repeat ? (y/n)
y
```
(Ripete da step 5 con nuove query)

```
Would you repeat ? (y/n)
n
```
(Client termina)

---

## 10. Errori comuni

| Errore | Causa | Soluzione |
|--------|-------|-----------|
| `Connection refused` / `ConnectException` | Server non avviato o porta sbagliata | Avviare prima il server, verificare IP e porta (default 8080) |
| `UnknownHostException` | IP/hostname server non valido | Controllare l'argomento passato al client (es. `127.0.0.1`) |
| `Database connection failed` | MySQL non raggiungibile, credenziali errate, DB inesistente | Verificare MySQL attivo, database `MapDB` esiste, utente `MapUser`/password `map` corretti |
| `Table '...' does not exist or contains no valid attributes` | Tabella inesistente o vuota | Controllare nome tabella (case-sensitive), verificare che esista in `MapDB` |
| `Table contains less than two columns` | Tabella con una sola colonna | La tabella deve avere almeno 2 colonne (1+ esplicative + 1 target) |
| `The target attribute ... is non-numeric` | Ultima colonna non numerica | Il target deve essere numerico (INT, DOUBLE, FLOAT...). Cambiare tabella o schema |
| `The selected table has zero tuples` | Tabella vuota | Inserire dati nella tabella |
| `Errore durante il caricamento da archivio` | File `.dmp` non trovato o corrotto | Verificare che il file esista nella cartella del server (es. `provac.dmp`), ricrearlo con opzione 1 |
| `The answer should be an integer between 0 and N!` | Input utente non valido durante predizione | Digitare solo un numero intero tra 0 e N-1 (N = numero rami mostrati) |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Driver MySQL non nel classpath | Avviare server con `-cp "bin;lib/mysql-connector-java-8.0.17.jar"` |

---

## 11. Chiusura del programma

### Client
- Digita `n` alla domanda `Would you repeat ? (y/n)` → il client chiude la connessione e termina

### Server
- Il server **non ha un comando di spegnimento** dal client
- Per terminarlo: `Ctrl+C` nel terminale dove gira il server
- Il server chiude automaticamente socket e connessioni database alla terminazione

---

## Note finali

- Il file archivio `.dmp` viene creato nella **cartella di lavoro del server** (dove hai lanciato `java MainTest`)
- Per usare un albero salvato in un'altra sessione: avvia server, avvia client, scegli opzione `2`, inserisci lo stesso nome base (es. `provac`)
- Il server supporta **più client contemporanei** (un thread per client)
- Per modificare porta server, database, utente o password: editare `DbAccess.java` e `MultiServer.java` prima di compilare