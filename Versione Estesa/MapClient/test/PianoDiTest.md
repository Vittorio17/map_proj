# Piano di Test - MapClient (Regression Tree Dashboard)

## Riepilogo

| Package | Classe | Numero Test |
|---------|--------|-------------|
| model   | NodeDTO | 12 |
| model   | TreeDTO | 5 |
| view    | ControlPanel | 6 |
| view    | SummaryPanel | 6 |
| view    | LogPanel | 5 |
| view    | TreePanel | 3 |
| view    | MainFrame | 2 |
| network | ServerConnection | 8 |
| network | PredictionWorker | 3 |
| controller | MainController | 4 |
| **Totale** | | **54** |

---

## 1. Package `model`

### 1.1 Classe `NodeDTO`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testInternalNodeCreation` | Costruttore(String) | Verifica che un nodo interno creato con una condizione di split abbia `leaf=false`, `splitCondition` impostata, `predictedValue=null` e lista figli vuota |
| `testLeafNodeCreation` | Costruttore(Double) | Verifica che un nodo foglia creato con un valore predetto abbia `leaf=true`, `predictedValue` corretta e `splitCondition=null` |
| `testSetAndGetSplitCondition` | setSplitCondition / getSplitCondition | Verifica che il setter aggiorni correttamente la condizione di split |
| `testSetAndGetPredictedValue` | setPredictedValue / getPredictedValue | Verifica che il setter aggiorni correttamente il valore predetto |
| `testSetLeaf` | setLeaf / isLeaf | Verifica che il setter permetta di cambiare lo stato leaf |
| `testAddChild` | addChild / getChildren | Verifica che addChild aggiunga un nodo figlio alla lista |
| `testMultipleChildren` | addChild / getChildren | Verifica che sia possibile aggiungere multipli figli e che la lista mantenga l'ordine |
| `testChildrenListIsMutable` | getChildren | Verifica che la lista ritornata da getChildren sia la stessa referenza (non una copia difensiva) |
| `testDefaultSelected` | Costruttore | Verifica che il campo `selected` sia `true` di default |
| `testSetSelected` | setSelected / isSelected | Verifica che il setter permetta di cambiare lo stato selected |
| `testToStringInternalNode` | toString | Verifica che un nodo interno restituisca la `splitCondition` come stringa |
| `testToStringLeafNode` | toString | Verifica che un nodo foglia restituisca `"Predizione: <valore>"` |

### 1.2 Classe `TreeDTO`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testConstructor` | Costruttore | Verifica che il costruttore imposti correttamente root e tableName |
| `testSetAndGetRoot` | setRoot / getRoot | Verifica che il setter aggiorni il nodo radice |
| `testSetAndGetTableName` | setTableName / getTableName | Verifica che il setter aggiorni il nome della tabella |
| `testRootCanBeReplaced` | setRoot / getRoot | Verifica che sia possibile sostituire il nodo radice con un altro |
| `testTableNameCanBeChanged` | setTableName / getTableName | Verifica che sia possibile cambiare il nome della tabella |

---

## 2. Package `view`

### 2.1 Classe `ControlPanel`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testDefaultValues` | Costruttore | Verifica che IP sia "127.0.0.1", porta sia 8080, la tabella di default sia "provac" e il bottone predizione sia disabilitato |
| `testGetServerAddress` | getServerAddress | Verifica che restituisca l'indirizzo IP inserito |
| `testGetServerPort` | getServerPort | Verifica che restituisca la porta come intero |
| `testGetServerPortInvalid` | getServerPort | Verifica che lanci NumberFormatException per una porta non numerica |
| `testGetTableName` | getTableName | Verifica che restituisca il nome della tabella selezionata |
| `testIsDatabaseSource` | isDatabaseSource | Verifica che restituisca true quando "Da Database" è selezionato e false per "Da Archivio" |

### 2.2 Classe `SummaryPanel`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testSetPredictionWithValue` | setPrediction | Verifica che formatti il valore con 4 decimali (es. 3.14 -> "3.1400") |
| `testSetPredictionWithNull` | setPrediction | Verifica che mostri "---" quando il valore è null |
| `testSetSteps` | setSteps | Verifica che aggiorni la label del numero di passi |
| `testSetTable` | setTable | Verifica che aggiorni la label del nome tabella |
| `testReset` | reset | Verifica che resetti passi a 0 e predizione a "---" |
| `testAddHistoryEntry` | addHistoryEntry | Verifica che venga aggiunto un elemento al modello della storia |

### 2.3 Classe `LogPanel`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testLogInfoMessage` | log | Verifica che un messaggio generico venga classificato come INFO |
| `testLogErrorMessage` | log | Verifica che un messaggio contenente "errore" venga classificato come ERROR |
| `testLogSuccessMessage` | log | Verifica che un messaggio contenente "successo" venga classificato come SUCCESS |
| `testLogWarningMessage` | log | Verifica che un messaggio contenente "warning" venga classificato come WARN |
| `testClear` | clear | Verifica che il contenuto venga azzerato dopo clear() |

### 2.4 Classe `TreePanel`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testDefaultState` | Costruttore | Verifica che il pannello sia bianco e senza albero inizialmente |
| `testUpdateTree` | updateTree | Verifica che l'albero venga aggiornato correttamente |
| `testResetTree` | resetTree | Verifica che l'albero venga resettato a null |

### 2.5 Classe `MainFrame`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testTitle` | Costruttore | Verifica che il titolo della finestra sia "Regression Tree Dashboard" |
| `testPanelsExist` | Costruttore / getter | Verifica che tutti e quattro i pannelli (Control, Tree, Summary, Log) siano istanziati e non null |

---

## 3. Package `network`

### 3.1 Classe `ServerConnection`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testInitialNotConnected` | isConnected | Verifica che una nuova ServerConnection non sia connessa |
| `testConnect` | connect / isConnected | Verifica che dopo una connessione a un ServerSocket locale lo stato sia connesso |
| `testClose` | close / isConnected | Verifica che dopo close() lo stato ritorni a non connesso |
| `testDoubleClose` | close | Verifica che chiamare close() due volte non lanci eccezioni |
| `testSendWithoutConnection` | send | Verifica che send() lanci IOException se non connesso |
| `testReceiveWithoutConnection` | receive | Verifica che receive() lanci IOException se non connesso |
| `testSendAndReceive` | send / receive | Verifica lo scambio di oggetti (String) tramite un ServerSocket mock |
| `testConnectClosesPrevious` | connect | Verifica che una seconda connect() chiuda la precedente connessione |

### 3.2 Classe `PredictionWorker` (metodo privato `parseBranchOptions` testato tramite riflessione)

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testParseBranchOptionsWithNumbers` | parseBranchOptions | Verifica che le opzioni con prefisso numerico ("0: Si\n1: No") vengano parsate correttamente rimuovendo il prefisso |
| `testParseBranchOptionsWithoutNumbers` | parseBranchOptions | Verifica che le opzioni senza prefisso numerico vengano restituite così come sono |
| `testParseBranchOptionsWithEmptyLines` | parseBranchOptions | Verifica che le righe vuote vengano ignorate |

### 3.3 Classi `InitTreeWorker` e `TableLoaderWorker`

Non testabili unitariamente senza un server mock complesso. Testati indirettamente tramite `ServerConnection` con un mock server.

---

## 4. Package `controller`

### 4.1 Classe `MainController`

| Test | Metodo | Descrizione |
|------|--------|-------------|
| `testConstructorCreatesConnection` | Costruttore | Verifica che il controller istanzii correttamente i listener e la connessione |
| `testPredictButtonDisabledWithoutConnection` | handlePredict | Verifica che handleMostri un dialogo di avviso quando non c'è connessione attiva |
| `testEmptyTableNameShowsWarning` | handleInitTree | Verifica che un nome tabella vuoto mostri un dialogo di warning |
| `testInvalidPortShowsError` | handleRefreshTables / handleInitTree | Verifica che una porta non valida mostri un messaggio di errore |
