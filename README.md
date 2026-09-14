# MapProj — Regression Tree Client/Server

Progetto universitario per il corso di **Machine Learning and Pattern Recognition** (o similar).
Il sistema implementa un albero di regressione con architettura client/server, consentendo l'addestramento del modello a partire da dati memorizzati in un database MySQL e la predizione interattiva di valori target.

---

## Componenti del gruppo

| Nome | Matricola | Ruolo |
|------|-----------|-------|
| [Nome Cognome 1] | [XXXXXXX] | Server / Algoritmo di addestramento |
| [Nome Cognome 2] | [XXXXXXX] | Client / Interfaccia grafica |
| [Nome Cognome 3] | [XXXXXXX] | Database / Testing |

---

## Struttura del progetto

Il progetto è organizzato in due versioni principali, ciascuna contenente un modulo **Server** e un modulo **Client** indipendenti:

```
map_proj/
├── Versione Base/          # Versione console (client testuale)
│   ├── MapServer/          # Server Java
│   └── MapClient/          # Client testuale (CLI)
│
├── Versione Estesa/        # Versione estesa (client GUI Swing)
│   ├── MapServer/          # Server Java
│   └── MapClient/          # Client grafico (Swing MVC)
│
└── README.md
```

---

## Versione Base

La versione base utilizza un client basato su console per l'interazione con l'utente.

### MapServer

Il server è strutturato nei seguenti pacchetti:

| Pacchetto | Descrizione |
|-----------|-------------|
| `server`  | Gestione delle connessioni di rete (`MultiServer`, `ServerOneClient`) |
| `data`    | Modellazione del dataset di training e degli attributi (`Data`, `Attribute`, `ContinuousAttribute`, `DiscreteAttribute`) |
| `database`| Accesso al database MySQL via JDBC (`DbAccess`, `TableData`, `TableSchema`, `Column`, `Example`) |
| `tree`    | Implementazione dell'albero di regressione (`RegressionTree`, `Node`, `SplitNode`, `ContinuousNode`, `DiscreteNode`, `LeafNode`) |

### MapClient

Il client della versione base è un'applicazione testuale che:
- Si connette al server tramite socket TCP
- Acquisisce i dati dal database o carica un albero pre-addestrato da file (`.dmp`)
- Guida l'utente nella predizione interattiva tramite query successive

---

## Versione Estesa

La versione estesa sostituisce il client console con un'interfaccia grafica **Swing** basata sull'architettura **MVC** (Model-View-Controller).

### Architettura MVC del Client

```
MapClient/src/
├── MapClient/MainTest.java      # Entry point
├── controller/
│   └── MainController.java      # Controller: coordina Vista e rete
├── model/
│   ├── TreeDTO.java             # DTO per la rappresentazione dell'albero
│   └── NodeDTO.java             # DTO per la rappresentazione dei nodi
├── network/
│   ├── ServerConnection.java    # Gestione connessione socket
│   ├── InitTreeWorker.java      # Worker Swing per inizializzazione albero
│   ├── PredictionWorker.java    # Worker Swing per predizione interattiva
│   └── TableLoaderWorker.java   # Worker Swing per caricamento tabelle
└── view/
    ├── MainFrame.java           # Finestra principale ( BorderLayout )
    ├── ControlPanel.java        # Pannello superiore: configurazione e comandi
    ├── TreePanel.java           # Pannello centrale: rendering dell'albero
    ├── SummaryPanel.java        # Pannello laterale: riepilogo e metriche
    └── LogPanel.java            # Pannello inferiore: console di log
```

### Pannelli della GUI

| Pannello | Posizione | Funzione |
|----------|-----------|----------|
| `ControlPanel` | Nord | Configurazione IP/porta, selezione tabella, pulsanti azione |
| `TreePanel` | Centro | Visualizzazione grafica dell'albero di regressione |
| `SummaryPanel` | Est | Riepilogo predizione, conteggio passi, storico |
| `LogPanel` | Sud | Console di log per messaggi di sistema |

### Comunicazione Client-Server

La comunicazione avviene via **TCP socket** con oggetti Java serializzati (`ObjectInputStream` / `ObjectOutputStream`). Il protocollo utilizza codici interi per identificare le operazioni:

| Codice | Operazione |
|--------|------------|
| `0` | Acquisizione dati da tabella MySQL |
| `1` | Costruzione albero di regressione |
| `2` | Caricamento albero da file di archivio (`.dmp`) |
| `3` | Predizione interattiva (navigazione dell'albero) |
| `4` | Elenco tabelle disponibili nel database |

---

## Albero di Regressione

L'algoritmo implementa un albero di regressione con le seguenti caratteristiche:

- **Criterio di split**: riduzione della varianza (SSE — Somma degli Errori Quadratici)
- **Attributi supportati**: discreti (`DiscreteAttribute`) e continui (`ContinuousAttribute`)
- **Nodo di split migliore**: selezionato tra tutti gli attributi indipendenti, scegliendo quello che minimizza la varianza complessiva dei sotto-alberi
- **Condizione di foglia**: il sotto-insieme contiene un numero di esempi inferiore o uguale al 10% del totale
- **Serializzazione**: gli alberi addestrati possono essere salvati/caricati da file `.dmp` (Java serialization)

### Gerarchia delle classi dell'albero

```
Node (astratta)
├── SplitNode (astratta)
│   ├── ContinuousNode    # Split su attributo continuo (soglia)
│   └── DiscreteNode      # Split su attributo discreto (valori distinti)
└── LeafNode              # Nodo foglia (valore predetto = media target)
```

---

## Database

Il server accede a un database **MySQL** tramite JDBC. La configurazione predefinita è:

| Parametro | Valore |
|-----------|--------|
| Driver | `com.mysql.cj.jdbc.Driver` |
| Host | `localhost` |
| Porta | `3306` |
| Database | `MapDB` |
| Utente | `MapUser` |
| Password | `map` |

La libreria JDBC inclusa è `mysql-connector-java-8.0.17.jar` (presente in `MapServer/lib/`).

---

## Test

Il progetto include una suite di test unitari JUnit per ciascun modulo:

```
test/
├── data/           # Test per Attribute, Data, eccezioni
├── database/       # Test per DbAccess, TableData, TableSchema, Column, Example
├── server/         # Test per MultiServer, ServerOneClient
└── tree/           # Test per Node, SplitNode, RegressionTree, LeafNode
```

---

## Come eseguire

### Server

1. Assicurarsi che MySQL sia attivo e il database `MapDB` sia configurato
2. Compilare e eseguire `MainTest.java` dal modulo MapServer
3. Il server si avvia sulla porta **8080** (configurabile)

### Client (Versione Base)

```bash
java -cp MapClient.jar MainTest <indirizzo_IP> <porta>
```

### Client (Versione Estesa)

Compilare ed eseguire `MainTest.java` dal modulo MapClient della Versione Estesa.
La GUI si apre automaticamente; inserire IP, porta e nome tabella, poi avviare l'inizializzazione.

---

## Requisiti

- **Java** 8 o superiore
- **MySQL** 8.x con connector JDBC
- **JUnit** 4 (per l'esecuzione dei test)
- **Swing** (incluso nel JDK, per la Versione Estesa)
