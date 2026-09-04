# Analisi Approfondita del Progetto "Map Project"

## 📋 Indice
1. [Panoramica Generale](#panoramica-generale)
2. [Architettura del Sistema](#architettura-del-sistema)
3. [Struttura dei Package](#struttura-dei-package)
4. [Analisi Dettagliata dei Moduli](#analisi-dettagliata-dei-moduli)
5. [Protocollo di Comunicazione Client-Server](#protocollo-di-comunicazione-client-server)
6. [Algoritmo di Regression Tree](#algoritmo-di-regression-tree)
7. [Database Layer](#database-layer)
8. [Test Suite](#test-suite)
9. [Punti di Forza](#punti-di-forza)
10. [Aree di Miglioramento e Debito Tecnico](#aree-di-miglioramento-e-debito-tecnico)
11. [Piano di Miglioramento Suggerito](#piano-di-miglioramento-suggerito)

---

## 1. Panoramica Generale

### Descrizione
Il progetto **"Map Project"** è un sistema **Client-Server** sviluppato in **Java** per l'**apprendimento e la predizione mediante Alberi di Regressione (Regression Trees)** su dati provenienti da un database **MySQL**.

### Componenti Principali
| Componente | Directory | Descrizione |
|------------|-----------|-------------|
| **mapClient** | `mapClient/` | Client console-based che interagisce con l'utente e comunica col server via Socket |
| **mapServer** | `mapServer/` | Server multi-threaded che gestisce: accesso DB, training alberi, serializzazione, predizione |

### Tecnologie Utilizzate
- **Java** (versione compatibile con JDK 8+)
- **MySQL** (JDBC Driver 8.0.17) - Database relazionale
- **JUnit 5** (JUnit Platform Console Standalone 1.10.2) - Testing
- **Java Serialization** - Persistenza alberi (file `.dmp`)
- **Socket TCP/IP** - Comunicazione Client-Server
- **Java Collections Framework** + **TreeSet** - Strutture dati ordinate

### Dipendenze Esterne (`mapServer/lib/`)
- `mysql-connector-java-8.0.17.jar`
- `junit-platform-console-standalone-1.10.2.jar`

---

## 2. Architettura del Sistema

```
┌─────────────────────┐         TCP Socket (Object Streams)         ┌─────────────────────┐
│      mapClient      │ ◄─────────────────────────────────────────► │      mapServer      │
│  (MainTest.java)    │                                             │  (MultiServer)      │
│                     │  Protocollo:                                │                     │
│  - Keyboard I/O     │   0 = Acquisizione schema/dati tabella DB   │  - Thread per client│
│  - Menu console     │   1 = Training Regression Tree              │  - ServerOneClient  │
│  - Object I/O       │   2 = Caricamento albero da file .dmp       │  - RegressionTree   │
└─────────────────────┘   3 = Predizione guidata (query interattiva) │  - Data (DB access) │
                           Risposte: "OK", "QUERY", "ERROR", valore  │  - Serializzazione  │
                                                         predetto    └─────────────────────┘
                                                                        ▲
                                                                        │ JDBC
                                                                        ▼
                                                                 ┌───────────────┐
                                                                 │    MySQL      │
                                                                 │   (MapDB)     │
                                                                 └───────────────┘
```

### Flusso Principale
1. **Client** si connette al Server (IP:porta da command line)
2. **Fase Acquisizione**: Client invia `0` + `tableName` → Server carica schema+dati da MySQL → `Data` object
3. **Fase Training**: Client invia `1` → Server costruisce `RegressionTree` → salva su file `.dmp`
4. **Fase Caricamento (opzionale)**: Client invia `2` + `tableName` → Server carica albero da `.dmp`
5. **Fase Predizione**: Client invia `3` → Server naviga albero interattivamente:
   - Invia query (`QUERY` + stringa condizione)
   - Client risponde con indice ramo (int)
   - Fino a foglia → Server invia `OK` + valore predetto

---

## 3. Struttura dei Package

```
mapServer/src/
├── data/                    # Package dati (training set, attributi)
│   ├── Attribute.java       # Classe astratta base per attributi
│   ├── ContinuousAttribute.java
│   ├── DiscreteAttribute.java
│   ├── Data.java            # Training set completo (caricamento DB, sorting, accesso)
│   └── TrainingDataException.java
│
├── database/                # Package accesso database
│   ├── Column.java          # Metadati colonna (nome, tipo)
│   ├── DbAccess.java        # Gestione connessione JDBC MySQL
│   ├── DatabaseConnectionException.java
│   ├── EmptySetException.java
│   ├── Example.java         # Singola tupla/record (List<Object>)
│   ├── TableData.java       # Query dati (transazioni, valori distinti)
│   └── TableSchema.java     # Estrazione metadati da DatabaseMetaData
│
├── server/                  # Package server networking
│   ├── MultiServer.java     # ServerSocket multi-thread (main entry)
│   ├── ServerOneClient.java # Thread per singolo client (protocollo)
│   └── UnknownValueException.java
│
└── tree/                    # Package Regression Tree
    ├── RegressionTree.java  # Albero completo (training, predizione, I/O, regole)
    ├── Node.java            # Nodo base astratto (variance, indici)
    ├── LeafNode.java        # Nodo foglia (valore predetto = media)
    ├── SplitNode.java       # Nodo split astratto (Comparable, SplitInfo)
    ├── ContinuousNode.java  # Split su attributo continuo (threshold)
    ├── DiscreteNode.java    # Split su attributo discreto (valori distinti)
    └── SplitNode.SplitInfo  # Inner class: metadati singolo branch
```

```
mapClient/src/
├── MainTest.java            # Entry point client + protocollo + menu console
└── utility/
    └── Keyboard.java        # Utility input parsing (Lewis & Loftus)
```

---

## 4. Analisi Dettagliata dei Moduli

### 4.1 Package `data` — Training Set & Attributi

#### `Attribute.java` (Classe Astratta Base)
```java
abstract class Attribute implements Serializable {
    private String name;
    private int index;
    // getter: getName(), getIndex()
}
```
- **Scopo**: Rappresenta un attributo (feature) del dataset
- **Implementa `Serializable`** per permettere serializzazione alberi

#### `ContinuousAttribute.java` / `DiscreteAttribute.java`
```java
// Continuo: solo nome + index (valori numerici illimitati)
class ContinuousAttribute extends Attribute { ... }

// Discreto: nome + index + Set<String> values (TreeSet ordinato)
class DiscreteAttribute extends Attribute implements Iterable<String> {
    private Set<String> values = new TreeSet<>();
    // getNumberOfDistinctValue(), iterator()
}
```
- **Design Pattern**: Inheritance + Polymorfismo per gestione uniforme in `Data`
- **DiscreteAttribute** implementa `Iterable` per iterazione sui valori

#### `Data.java` — **Cuore del Training Set**
```java
public class Data {
    private List<Example> data = new ArrayList<>();
    private List<Attribute> explanatorySet = new LinkedList<>();
    private ContinuousAttribute classAttribute;  // Target (ultima colonna, numerica)
    private int numberOfExamples;
    
    public Data(String tableName) throws TrainingDataException { ... }
}
```

**Responsabilità**:
1. **Caricamento da DB** (costruttore):
   - Usa `DbAccess` → `TableSchema` → `TableData`
   - Valida: tabella esiste, ≥2 colonne, ultima colonna numerica
   - Crea attributi: `ContinuousAttribute` per colonne numeric, `DiscreteAttribute` per string (con valori distinti da DB)
2. **Accesso dati**:
   - `getClassValue(index)`, `getExplanatoryValue(row, attrIndex)`
   - `getExplanatoryAttribute(index)`, `getClassAttribute()`
3. **Ordinamento (Quicksort custom)**:
   - `sort(Attribute, begin, end)` → delega a `partition()` per tipo attributo
   - `quicksort()` ricorsivo con ottimizzazione tail-recursion (ricorsione prima sulla partizione minore)
   - **Nota**: `attribute` è field instance ma dovrebbe essere parametro (bug potenziale concorrenza)

**Eccezioni**: `TrainingDataException` (wrapper per DB errors, empty set, schema errors)

---

### 4.2 Package `database` — Data Access Layer

#### `DbAccess.java` — Connection Manager
```java
public class DbAccess {
    // Hardcoded credentials (security concern)
    private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private String SERVER = "localhost", DATABASE = "MapDB";
    private String USER_ID = "MapUser", PASSWORD = "map";
    private final int PORT = 3306;
    private Connection conn;
    
    void initConnection() throws DatabaseConnectionException { ... }
    Connection getConnection() { return conn; }
    void closeConnection() { ... }
}
```
- **Pattern**: Singleton-like per connessione (un'istanza per `Data`)
- **Hardcoded credentials** → **Security Issue** (vedi sezione miglioramenti)

#### `TableSchema.java` — Metadata Extraction
```java
public class TableSchema implements Iterable<Column> {
    private List<Column> tableSchema = new ArrayList<>();
    private static final Map<String,String> mapSQL_JAVATypes = Map.of(
        "CHAR","string", "VARCHAR","string", "INT","number", "DOUBLE","number", ...
    );
    
    public TableSchema(DbAccess db, String tableName) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        ResultSet res = meta.getColumns(null, null, tableName, null);
        // Mappa TYPE_NAME SQL → "string"/"number"
    }
}
```
- Usa `DatabaseMetaData.getColumns()` per introspezione schema
- Mapping tipi SQL → categorie astratte (`string`/`number`)

#### `TableData.java` — Data Retrieval
```java
public class TableData {
    public List<Example> getTransazioni(String table) throws SQLException, EmptySetException {
        // SELECT * FROM table
        // Per ogni riga: Example con Double per number, String per string
    }
    
    public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException {
        // SELECT DISTINCT column FROM table ORDER BY column
        // TreeSet per ordinamento naturale
    }
}
```

#### `Example.java` — Tuple Representation
```java
public class Example implements Comparable<Example>, Iterable<Object> {
    private List<Object> example = new ArrayList<>();
    // add(), get(), compareTo(), toString(), iterator()
}
```
- **Comparable**: confronto lessicografico elemento per elemento
- **Iterable**: supporta for-each

---

### 4.3 Package `server` — Networking Layer

#### `MultiServer.java` — Acceptor Loop
```java
public class MultiServer {
    private int PORT = 8080;
    
    public MultiServer(int port) { this.PORT = port; run(); }
    
    private void run() {
        ServerSocket s = new ServerSocket(PORT);
        while (true) {
            Socket socket = s.accept();
            new ServerOneClient(socket); // Thread per client
        }
    }
}
```
- **Architettura**: Thread-per-client (classic blocking I/O)
- **Limite**: Nessun thread pool → rischio resource exhaustion sotto carico

#### `ServerOneClient.java` — Protocol Handler (Thread)
```java
public class ServerOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public void run() {
        Data trainingSet = null;
        RegressionTree tree = null;
        String tableName = null;
        
        while (true) {
            int request = (Integer) in.readObject();
            switch (request) {
                case 0: // Acquisizione dati
                    tableName = (String) in.readObject();
                    trainingSet = new Data(tableName);
                    out.writeObject("OK");
                    break;
                case 1: // Training
                    tree = new RegressionTree(trainingSet);
                    tree.salva(tableName + ".dmp");
                    out.writeObject("OK");
                    break;
                case 2: // Caricamento da file
                    tree = RegressionTree.carica(tableName + ".dmp");
                    out.writeObject("OK");
                    break;
                case 3: // Predizione
                    RegressionTree current = tree;
                    while (true) {
                        String query = current.getCurrentNodeQuery();
                        if (query == null) { // Leaf
                            out.writeObject("OK");
                            out.writeObject(current.getPredictedValue());
                            break;
                        }
                        out.writeObject("QUERY");
                        out.writeObject(query);
                        int risp = (Integer) in.readObject();
                        current = current.getChild(risp);
                    }
                    break;
            }
        }
    }
}
```
- **Protocollo Object Stream**: Serializzazione Java nativa
- **Stateful per sessione**: Mantiene `trainingSet` e `tree` in memoria per il client
- **Gestione errori**: Try-catch generico, chiude risorse in `finally`

#### `UnknownValueException.java`
```java
public class UnknownValueException extends Exception {
    public UnknownValueException(String message) { super(message); }
}
```
- Lanciata da `RegressionTree.getChild()` per indice ramo non valido

---

### 4.4 Package `tree` — Regression Tree Implementation

#### `RegressionTree.java` — Classe Principale (Serializable)
```java
public class RegressionTree implements Serializable {
    private Node root;
    private RegressionTree[] childTree;
    
    public RegressionTree(Data trainingSet) {
        learnTree(trainingSet, 0, trainingSet.getNumberOfExamples()-1, 
                  trainingSet.getNumberOfExamples()*10/100); // 10% min examples per leaf
    }
    
    private void learnTree(Data trainingSet, int begin, int end, int minExamplesPerLeaf) {
        if (isLeaf(trainingSet, begin, end, minExamplesPerLeaf)) {
            root = new LeafNode(trainingSet, begin, end);
        } else {
            root = determineBestSplitNode(trainingSet, begin, end);
            if (root.getNumberOfChildren() > 1) {
                childTree = new RegressionTree[root.getNumberOfChildren()];
                for (int i=0; i<childTree.length; i++) {
                    childTree[i] = new RegressionTree();
                    childTree[i].learnTree(trainingSet, 
                        ((SplitNode)root).getSplitInfo(i).getBeginindex(),
                        ((SplitNode)root).getSplitInfo(i).getEndIndex(),
                        minExamplesPerLeaf);
                }
            } else {
                root = new LeafNode(trainingSet, begin, end);
            }
        }
    }
    
    private boolean isLeaf(...) { return (end-begin+1) <= minExamplesPerLeaf; }
    
    private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
        TreeSet<SplitNode> sortedSplits = new TreeSet<>(); // Ordinato per varianza (Comparable)
        for (int i=0; i<trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            Attribute attr = trainingSet.getExplanatoryAttribute(i);
            if (attr instanceof DiscreteAttribute)
                sortedSplits.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute)attr));
            else
                sortedSplits.add(new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute)attr));
        }
        SplitNode best = sortedSplits.first(); // Min varianza
        if (best != null) trainingSet.sort(best.getAttribute(), begin, end);
        return best;
    }
    
    // Predizione interattiva (server-side)
    public String getCurrentNodeQuery() { ... }
    public RegressionTree getChild(int index) throws UnknownValueException { ... }
    public Double getPredictedValue() { ... }
    
    // Serializzazione
    public void salva(String fileName) throws IOException { ... }
    public static RegressionTree carica(String fileName) throws IOException, ClassNotFoundException { ... }
    
    // Estrazione regole (debug)
    public void printRules() { ... }
}
```

**Algoritmo**: **Regression Tree (MSE minimization)**
- **Splitting criterion**: Minimizzazione varianza (SSE - Sum of Squared Errors)
- **Stopping criterion**: Min 10% esempi per foglia (configurabile hardcoded)
- **Gestione attributi**:
  - **Discreti**: Un branch per ogni valore distinto presente nel subset
  - **Continui**: Binary split (≤ threshold / > threshold) con ricerca best threshold

#### `Node.java` — Base Class
```java
abstract class Node implements Serializable {
    static int idNodeCount = 0; // Contatore globale (non thread-safe!)
    private int idNode, beginExampleIndex, endExampleIndex;
    private double variance; // SSE del nodo
    
    Node(Data trainingSet, int begin, int end) {
        // Calcola media target nel subset
        // Calcola variance = Σ(target_i - mean)²
    }
    // getters + abstract getNumberOfChildren() + toString()
}
```
- **Variance (SSE)**: Calcolata su valori target del subset coperto dal nodo
- **idNodeCount**: Static → conflitti se alberi multipli concorrenti

#### `LeafNode.java`
```java
class LeafNode extends Node {
    private double predictedClassValue; // Media target nel subset
    
    LeafNode(Data trainingSet, int begin, int end) {
        super(trainingSet, begin, end);
        predictedClassValue = mean(target values);
    }
    double getPredictedClassValue() { return predictedClassValue; }
    int getNumberOfChildren() { return 0; }
}
```

#### `SplitNode.java` (Abstract) + `SplitInfo` (Inner Class)
```java
abstract class SplitNode extends Node implements Comparable<SplitNode> {
    private Attribute attribute;
    protected List<SplitInfo> mapSplit = new ArrayList<>();
    private double splitVariance; // Somma varianze figli
    
    SplitNode(Data trainingSet, int begin, int end, Attribute attribute) {
        super(trainingSet, begin, end);
        this.attribute = attribute;
        trainingSet.sort(attribute, begin, end); // Ordina per valutare split
        setSplitInfo(trainingSet, begin, end, attribute); // Astratto
        
        // Calcola splitVariance = Σ LeafNode(subset_i).variance
        splitVariance = 0;
        for (SplitInfo info : mapSplit) {
            splitVariance += new LeafNode(trainingSet, info.getBeginindex(), info.getEndIndex()).getVariance();
        }
    }
    
    // Comparable: ordina per splitVariance ASC (minore varianza = split migliore)
    public int compareTo(SplitNode o) { ... }
    
    abstract void setSplitInfo(Data, int, int, Attribute);
    abstract int testCondition(Object value); // Restituisce branch index
    
    String formulateQuery() { ... } // Es: "0:X<=5.0\n1:X>5.0"
}
```

**SplitInfo** — Metadati per singolo branch:
```java
class SplitInfo implements Serializable {
    Object splitValue;      // Valore soglia (Double per continuo, String per discreto)
    int numberChild;        // Indice branch (0, 1, ...)
    int beginIndex, endIndex; // Range indici nel training set ordinato
    String comparator;      // "=" (discreto), "<=" o ">" (continuo)
}
```

#### `DiscreteNode.java` — Split su Attributo Discreto
```java
class DiscreteNode extends SplitNode {
    DiscreteNode(Data trainingSet, int begin, int end, DiscreteAttribute attribute) {
        super(trainingSet, begin, end, attribute);
    }
    
    @Override
    void setSplitInfo(Data trainingSet, int begin, int end, Attribute attribute) {
        mapSplit = new ArrayList<>();
        int currentBegin = begin;
        Object lastValue = trainingSet.getExplanatoryValue(begin, attribute.getIndex());
        
        for (int i = begin+1; i <= end; i++) {
            Object currentValue = trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!currentValue.equals(lastValue)) {
                // Fine partizione per lastValue
                mapSplit.add(new SplitInfo(lastValue, currentBegin, i-1, mapSplit.size()));
                currentBegin = i;
                lastValue = currentValue;
            }
        }
        // Ultima partizione
        mapSplit.add(new SplitInfo(lastValue, currentBegin, end, mapSplit.size()));
    }
    
    @Override
    int testCondition(Object value) {
        for (SplitInfo info : mapSplit)
            if (info.getSplitValue().equals(value))
                return mapSplit.indexOf(info);
        return -1;
    }
}
```
- **Un branch per valore distinto** presente nel subset ordinato
- **Ordinamento prerequisito**: `Data.sort()` chiamato in `SplitNode` constructor

#### `ContinuousNode.java` — Split su Attributo Continuo (Binary Split Optimization)
```java
class ContinuousNode extends SplitNode {
    ContinuousNode(Data trainingSet, int begin, int end, ContinuousAttribute attribute) {
        super(trainingSet, begin, end, attribute);
    }
    
    @Override
    void setSplitInfo(Data trainingSet, int begin, int end, Attribute attribute) {
        Double currentSplitValue = (Double)trainingSet.getExplanatoryValue(begin, attribute.getIndex());
        double bestInfoVariance = 0;
        List<SplitInfo> bestMapSplit = null;
        
        for (int i = begin+1; i <= end; i++) {
            Double value = (Double)trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (value != currentSplitValue) {
                // Valuta split tra currentSplitValue e value
                double leftVar = new LeafNode(trainingSet, begin, i-1).getVariance();
                double rightVar = new LeafNode(trainingSet, i, end).getVariance();
                double candidateVariance = leftVar + rightVar;
                
                if (bestMapSplit == null || candidateVariance < bestInfoVariance) {
                    bestInfoVariance = candidateVariance;
                    bestMapSplit = new ArrayList<>();
                    bestMapSplit.add(new SplitInfo(currentSplitValue, begin, i-1, 0, "<="));
                    bestMapSplit.add(new SplitInfo(currentSplitValue, i, end, 1, ">"));
                }
                currentSplitValue = value;
            }
        }
        mapSplit = bestMapSplit;
        
        // Rimuovi split degeneri (una partizione vuota o singleton problematica)
        if (mapSplit.get(1).getBeginindex() == mapSplit.get(1).getEndIndex()) {
            mapSplit.remove(1);
        }
    }
    
    @Override
    int testCondition(Object value) {
        double v = (Double) value;
        for (SplitInfo info : mapSplit) {
            double splitVal = (Double) info.getSplitValue();
            if ("<=".equals(info.getComparator()) && v <= splitVal) return mapSplit.indexOf(info);
            if (">".equals(info.getComparator()) && v > splitVal) return mapSplit.indexOf(info);
        }
        return -1;
    }
}
```
- **Algoritmo**: Scansiona tutti i punti di split candidati (valori adiacenti distinti nel subset ordinato)
- **Criterio**: Minimizza `variance(left) + variance(right)` = SSE totale post-split
- **Output**: Al massimo 2 branch (`<= threshold` e `> threshold`)
- **Ottimizzazione**: Rimuove secondo branch se degenere

---

### 4.5 Client Side — `mapClient/src/MainTest.java`

```java
public class MainTest {
    public static void main(String[] args) {
        // args[0] = host, args[1] = port
        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        
        // Menu iniziale
        int decision;
        do {
            System.out.println("Learn Regression Tree from data [1]");
            System.out.println("Load Regression Tree from archive [2]");
            decision = Keyboard.readInt();
        } while (decision != 1 && decision != 2);
        
        String tableName = Keyboard.readString(); // "File name" (nome tabella)
        
        if (decision == 1) {
            out.writeObject(0); // Acquisizione dati
            out.writeObject(tableName);
            if (!"OK".equals(in.readObject())) return;
            
            out.writeObject(1); // Training
        } else {
            out.writeObject(2); // Carica da file
            out.writeObject(tableName);
        }
        
        if (!"OK".equals(in.readObject())) return;
        
        // Loop predizione
        char risp = 'y';
        do {
            out.writeObject(3); // Predizione
            String answer = (String) in.readObject();
            
            while ("QUERY".equals(answer)) {
                String query = (String) in.readObject(); // Es: "0:X<=5.0\n1:X>5.0"
                System.out.println(query);
                int path = Keyboard.readInt(); // Utente sceglie branch
                out.writeObject(path);
                answer = (String) in.readObject();
            }
            
            if ("OK".equals(answer)) {
                String predicted = in.readObject().toString();
                System.out.println("Predicted class: " + predicted);
            } else {
                System.out.println(answer); // Errore
            }
            
            System.out.println("Would you repeat? (y/n)");
            risp = Keyboard.readChar();
        } while (Character.toUpperCase(risp) == 'Y');
    }
}
```

**Keyboard.java** — Utility class (Lewis & Loftus) per parsing input console robusto (tokenizer, error handling).

---

## 5. Protocollo di Comunicazione Client-Server

### Formato: Java Object Serialization over TCP Socket

| Codice | Direzione | Payload | Descrizione |
|--------|-----------|---------|-------------|
| `0` | C→S | `String tableName` | Richiesta acquisizione schema+dati tabella |
| `1` | C→S | — | Avvia training albero su dati caricati |
| `2` | C→S | `String tableName` | Carica albero pre-addestrato da `tableName.dmp` |
| `3` | C→S | — | Avvia predizione interattiva |

### Risposte Server → Client
| Tipo | Payload | Significato |
|------|---------|-------------|
| `String "OK"` | — | Operazione completata con successo |
| `String "QUERY"` | `String queryText` | Richiesta input utente per navigazione albero |
| `String "ERROR"` / msg | `String errorMsg` | Errore (eccezione, valore non valido, ecc.) |
| `Double` | `Double predictedValue` | Valore predetto (dopo "OK" in fase predizione) |

### Flusso Predizione (Request 3)
```
Client                    Server
  │                         │
  ├─ 3 ──────────────────► │
  │                         ├─ current = root
  │                         ├─ query = current.getCurrentNodeQuery()
  │                         │  (null se leaf)
  │ ◄── "QUERY" ───────────┤
  │ ◄── "0:X<=5.0\n1:X>5.0" ┤
  │                         │
  ├─ 0 (scelta utente) ───► │
  │                         ├─ current = current.getChild(0)
  │                         ├─ loop fino a leaf
  │ ◄── "OK" ──────────────┤
  │ ◄── 42.5 (Double) ─────┤
  │                         │
```

---

## 6. Algoritmo di Regression Tree — Analisi Tecnica

### 6.1 Tipo di Algoritmo
**Regression Tree (Decision Tree per regressione)** basato su **minimizzazione SSE (Sum of Squared Errors)**.

### 6.2 Criterio di Splitting
Per ogni attributo candidato, valuta la **riduzione di varianza** (≈ Information Gain per regressione):

```
SplitVariance = Σᵢ Variance(Partitionᵢ)
Variance(Partition) = Σⱼ (targetⱼ - mean(Partition))²  [SSE]
```

Il **best split** = argmin SplitVariance tra tutti gli attributi.

### 6.3 Gestione Attributi

| Tipo Attributo | Strategia Split | Numero Branch |
|----------------|-----------------|---------------|
| **Discreto** (DiscreteAttribute) | Multi-way: un branch per ogni valore distinto presente nel subset | 2..N |
| **Continuo** (ContinuousAttribute) | Binary: threshold ottimale tra valori adiacenti ordinati | 2 (≤ / >) |

### 6.4 Criterio di Stopping (Pre-pruning)
```java
// In RegressionTree.learnTree()
int minExamplesPerLeaf = trainingSet.getNumberOfExamples() * 10 / 100; // 10%
boolean isLeaf = (end - begin + 1) <= minExamplesPerLeaf;
```
- **Hardcoded**: 10% del training set totale
- **Nessun altro criterio**: No max depth, no min impurity decrease, no statistical significance test

### 6.5 Predizione
- **Valore foglia**: Media dei target nel subset (`LeafNode.predictedClassValue`)
- **Navigazione**: Interattiva (utente sceglie branch) — *non automatica basata su feature vector*

### 6.6 Complessità Computazionale
- **Training**: O(n × m × log n) circa (quicksort per attributo + scansione split)
  - n = esempi, m = attributi
  - TreeSet per ordinamento split nodes: O(m log m) per nodo
- **Spazio**: O(n × m) per training set + O(nodi albero)
- **Serializzazione**: Java native serialization (intero object graph)

---

## 7. Database Layer — Analisi

### Schema Richiesto (Implicito)
- Database: **MySQL** (MapDB)
- Tabella: Qualsiasi, ma **ultima colonna = target numerico**
- Colonne precedenti = feature (numeriche o testuali)
- Utente: `MapUser` / `map` (hardcoded)

### Mapping Tipi SQL → Java
| SQL Type | Java Category | Attribute Class |
|----------|---------------|-----------------|
| CHAR, VARCHAR, LONGVARCHAR, BIT | `string` | `DiscreteAttribute` |
| SHORT, INT, LONG, FLOAT, DOUBLE | `number` | `ContinuousAttribute` |
| Altri (DATE, etc.) | `string` (fallback) | `DiscreteAttribute` |

### Query Eseguite
1. **Schema**: `DatabaseMetaData.getColumns()` → metadati
2. **Dati**: `SELECT col1, col2, ... FROM table` → tutte le righe
3. **Valori distinti**: `SELECT DISTINCT col FROM table ORDER BY col` → per attributi discreti

### Gestione Connessioni
- **Una connessione per `Data` instance** (aperto in costruttore, chiuso in `finally`)
- **Nessun connection pooling**
- **Auto-commit default** (read-only queries)

---

## 8. Test Suite

### Struttura Test (`mapServer/Test/`)
```
Test/
├── data/
│   ├── ContinuousAttributeTest.java
│   ├── DataTest.java              # Integration test con DB reale
│   ├── DiscreteAttributeTest.java
│   └── TrainingDataExceptionTest.java
├── database/
│   ├── ColumnTest.java
│   ├── DatabaseConnectionExceptionTest.java
│   ├── EmptySetExceptionTest.java
│   └── ExampleTest.java
└── tree/
    ├── DiscreteNodeTest.java
    └── leafNodeTest.java
```

### Esecuzione Test
```bat
:: mapServer/RunTest.bat
java -jar lib/junit-platform-console-standalone-1.10.2.jar 
     --class-path "bin;lib/mysql-connector-java-8.0.17.jar" 
     --select-package=data
```

### Caratteristiche
- **JUnit 5** (Jupiter API)
- **Test di integrazione reali** (`DataTest` richiede MySQL attivo con tabella `provaC`)
- **Test unitari** per classi data/tree/database (mock-free, testano logica pura)
- **Coverage**: Base classi, eccezioni, attributi, nodi albero

### Prerequisiti Test
- MySQL running su `localhost:3306`
- Database `MapDB` con utente `MapUser`/`map`
- Tabella `provaC` (almeno 3 colonne: X varchar, Y double, C double target)
- Tabelle di test: `provaC_singola` (1 colonna), `provaC_errata` (target non numerico)

---

## 9. Punti di Forza

| Area | Punti di Forza |
|------|----------------|
| **Architettura** | Separazione chiara: Client/Server, Data/Tree/Database/Network layers |
| **Algoritmo** | Implementazione completa Regression Tree con splitting ottimizzato per continui |
| **Serializzazione** | Java Native Serialization → alberi persistenti `.dmp` ricaricabili |
| **Database** | Metadata-driven (TableSchema via DatabaseMetaData), supporto tipi misti |
| **Testing** | Test suite JUnit 5 strutturata, integration test con DB reale |
| **Codice** | Java idiomatico, javadoc esteso, naming convention italiana/inglese mista ma consistente |
| **Pattern** | Template Method (`SplitNode.setSplitInfo`), Strategy (Discrete/Continuous Node), Comparable per ordinamento split |
| **Estensibilità** | Package `tree` isolato, facile swap algoritmo splitting |

---

## 10. Aree di Miglioramento e Debito Tecnico

### 🔴 Critici (Security & Robustness)

| Issue | Descrizione | Impatto | File |
|-------|-------------|---------|------|
| **Hardcoded DB Credentials** | `MapUser`/`map` in `DbAccess.java` | **Security**: Credenziali in plaintext nel codice | `DbAccess.java:23-25` |
| **SQL Injection Risk** | `tableName` concatenato in query (`"FROM "+tableName`) | **Security**: Table name non parametrizzabile ma validato solo via metadata | `TableData.java:58`, `TableSchema.java:50` |
| **Java Deserialization Vulnerability** | `ObjectInputStream.readObject()` su dati non trusted (client→server) | **Critical RCE**: Deserializzazione arbitraria | `ServerOneClient.java:35`, `RegressionTree.carica()` |
| **No Input Validation** | `tableName` da client usato direttamente in query | **Security/Stability** | `ServerOneClient.java:39` |
| **Thread-per-client senza pool** | `new ServerOneClient(socket)` per ogni connessione | **DoS**: Resource exhaustion sotto carico | `MultiServer.java:27` |

### 🟠 Alta Priorità (Architettura & Maintainability)

| Issue | Descrizione | Impatto | File |
|-------|-------------|---------|------|
| **Static `idNodeCount` non thread-safe** | Contatore nodi condiviso tra tutti gli alberi | Race condition, ID duplicati | `Node.java:15` |
| **Field `attribute` in `Data.quicksort`** | Variabile d'istanza usata come parametro implicito | Bug concorrenza, codice confuso | `Data.java:164-284` |
| **Eccezioni ingoiate/gestite male** | `catch(Exception e)` generico, solo `System.out.println` | Debugging difficile, errori silenziosi | `ServerOneClient.java:88`, `MultiServer.java` |
| **Nessun Connection Pooling** | Nuova connessione per ogni `Data` | Performance, scalabilità | `DbAccess.java` |
| **Protocollo fragile** | Object serialization + switch su int magic numbers | Versioning impossibile, coupling forte | `ServerOneClient.java`, `MainTest.java` |
| **Hardcoded 10% min leaf size** | Non configurabile | Flessibilità limitata | `RegressionTree.java:39` |
| **Predizione solo interattiva** | Richiede input utente per ogni split | Non usabile per batch/automazione | `RegressionTree.getChild()`, `ServerOneClient.java:64-81` |

### 🟡 Media Priorità (Code Quality)

| Issue | Descrizione | File |
|-------|-------------|------|
| **Package `server` classi package-private** | `ServerOneClient`, `MultiServer`, `UnknownValueException` senza `public` | `server/*.java` |
| **`Keyboard.java` copiato da textbook** | Non standard, encoding issues potenziali | `mapClient/src/utility/Keyboard.java` |
| **Javadoc misto IT/EN** | Inconsistenza documentazione | Tutto il progetto |
| **Nessun build system** (Maven/Gradle) | Compilazione manuale, dipendenze in `lib/` | Root project |
| **Test richiedono DB reale** | Nessun mock, CI difficile | `DataTest.java` |
| **`SplitNode.mapSplit` package-private** | Accesso diretto da `ContinuousNode`/`DiscreteNode` | `SplitNode.java:96` |
| **`LeafNode` ricalcola variance nel costruttore** | Duplicazione logica `Node` constructor | `LeafNode.java:21-28` |

### 🟢 Bassa Priorità (Enhancement)

- Logging framework (SLF4J/Log4j2) al posto di `System.out.println`
- Configurazione esterna (properties/YAML) per DB, porta, parametri albero
- Predizione batch (feature vector → valore) senza interazione utente
- Pruning post-training (cost-complexity, reduced error pruning)
- Supporto attributi categorici ordinali / one-hot encoding automatico
- Metriche valutazione (RMSE, MAE, R²) su test set
- Thread pool per server (ExecutorService)

---

## 11. Piano di Miglioramento Suggerito

### Fase 1: Sicurezza e Stabilità (Critico) — **Settimana 1-2**

| Task | Descrizione | File da Modificare |
|------|-------------|---------------------|
| 1.1 | Rimuovi hardcoded credentials → config file/env vars | `DbAccess.java`, nuovo `config.properties` |
| 1.2 | Valida/sanitizza `tableName` (whitelist o regex `^[a-zA-Z_][a-zA-Z0-9_]*$`) | `ServerOneClient.java`, `TableData.java`, `TableSchema.java` |
| 1.3 | Sostituisci Java Serialization con **JSON (Jackson/Gson)** o **Protobuf** per protocollo network | `ServerOneClient.java`, `MainTest.java`, `RegressionTree.salva/carica` |
| 1.4 | Aggiungi Thread Pool (`Executors.newFixedThreadPool`) in `MultiServer` | `MultiServer.java` |
| 1.5 | Rendi `idNodeCount` thread-safe (`AtomicInteger` o `ThreadLocal`) | `Node.java` |

### Fase 2: Architettura e Qualità Codice — **Settimana 3-4**

| Task | Descrizione | File da Modificare |
|------|-------------|---------------------|
| 2.1 | Introduci **Maven/Gradle** per build e dependency management | `pom.xml` / `build.gradle` |
| 2.2 | Refactor `Data.quicksort`: rimuovi field `attribute`, passa come parametro | `Data.java` |
| 2.3 | Estrai interfaccia `TreeTrainer` / `TreePredictor` per disaccoppiare algoritmo | Nuovi file in `tree/` |
| 2.4 | Aggiungi `PredictionInput` (feature vector) → predizione batch non-interattiva | `RegressionTree.java`, `ServerOneClient.java` |
| 2.5 | Rendi parametri albero configurabili (minLeafSize, maxDepth, minImpurityDecrease) | `RegressionTree.java`, config file |
| 2.6 | Logging strutturato (SLF4J + Logback) | Tutti i file, `logback.xml` |

### Fase 3: Testing e CI/CD — **Settimana 5**

| Task | Descrizione |
|------|-------------|
| 3.1 | Aggiungi test con **Testcontainers** (MySQL in Docker) per CI isolato |
| 3.2 | Mock `DbAccess`/`TableData` per unit test puri (`Mockito`) |
| 3.3 | Pipeline CI (GitHub Actions/GitLab CI): compile, test, spotbugs, checkstyle |
| 3.4 | Code coverage (JaCoCo) target >80% |

### Fase 4: Feature Enhancement — **Settimana 6+**

| Feature | Descrizione |
|---------|-------------|
| 4.1 | **Pruning** (Reduced Error Pruning, Cost-Complexity) |
| 4.2 | **Model Evaluation**: Train/Test split, k-fold CV, metriche (RMSE, MAE, R²) |
| 4.3 | **Feature Importance** basata su riduzione varianza aggregata |
| 4.4 | **REST API** wrapper sul protocollo socket (Spring Boot / JAX-RS) |
| 4.5 | **Model Registry**: Versioning alberi, metadata (training date, params, metrics) |
| 4.6 | **Client Library** Java/Python per predizione programmatica |

---

## 12. Metriche Progetto (Stima)

| Metrica | Valore |
|---------|--------|
| **Lines of Code (Java)** | ~2.500 LOC (src/main) + ~500 LOC (test) |
| **Classi Principali** | 18 (data: 5, database: 7, server: 3, tree: 6) |
| **Test Cases** | ~25 test methods |
| **Dipendenze Esterne** | 2 JAR (MySQL, JUnit) |
| **Complessità Ciclomatica Media** | Media bassa (~3-5), picchi in `Data.quicksort` e `ContinuousNode.setSplitInfo` |
| **Debito Tecnico Stimato** | **Alto** (security, threading, serialization, build system) |

---

## 13. Conclusione

Il **Map Project** è un **progetto accademico/educativo ben strutturato** che dimostra una solida comprensione di:
- Architettura Client-Server con Socket Java
- Accesso database metadata-driven (JDBC DatabaseMetaData)
- Implementazione completa di **Regression Tree** (algoritmo non banale)
- Serializzazione modelli ML
- Testing con JUnit 5

**Tuttavia**, per uso in produzione o anche solo per evoluzione sicura, richiede **interventi critici su sicurezza (deserializzazione, credenziali, SQL injection), concorrenza (thread pool, statici non safe), e modernizzazione build/dependency management**.

Il piano in 4 fasi sopra delineato porta il progetto da "esercizio universitario" a "codebase manutenibile e sicura" in ~6 settimane di lavoro part-time.

---

*Report generato automaticamente — Analisi statica del codice sorgente in `E:\map_proj`*
*Data: 2026-07-28*