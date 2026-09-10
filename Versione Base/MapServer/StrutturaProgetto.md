# Struttura del Progetto MapServer

Documentazione completa dei package, classi, attributi e metodi con relativa visibilità.

---

## Indice

1. [Package: default (root)](#package-default)
2. [Package: server](#package-server)
3. [Package: tree](#package-tree)
4. [Package: data](#package-data)
5. [Package: database](#package-database)

---

## Package: default

### `MainTest`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `public static` | `main(String[] args)` | metodo | Punto di ingresso dell'applicazione. Istanzia `MultiServer` sulla porta 8080. |

---

## Package: server

### `MultiServer`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `PORT` | int | Porta di rete TCP (default 8080) |
| `public` | `MultiServer(int port)` | costruttore | Inizializza la porta e avvia `run()` |
| `private` | `run()` | metodo | Ciclo principale del server: accetta connessioni e genera thread `ServerOneClient` |

---

### `ServerOneClient` extends `Thread`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `socket` | Socket | Socket di connessione con il client |
| `private` | `in` | ObjectInputStream | Stream di input per oggetti serializzati |
| `private` | `out` | ObjectOutputStream | Stream di output per oggetti serializzati |
| `public` | `ServerOneClient(Socket s)` | costruttore | Associa socket, inizializza stream, avvia il thread |
| `public` | `run()` | metodo | Gestisce le richieste del client (codici 0-3) |

---

### `UnknownValueException` extends `Exception`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `public` | `UnknownValueException(String message)` | costruttore | Eccezione per input utente non validi |

---

## Package: tree

### `Node` *(abstract)* implements `Serializable`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `static` (package) | `idNodeCount` | int | Contatore globale dei nodi generati |
| `private` | `idNode` | int | Identificativo numerico del nodo |
| `private` | `beginExampleIndex` | int | Indice iniziale del sotto-insieme nel training set |
| `private` | `endExampleIndex` | int | Indice finale del sotto-insieme nel training set |
| `private` | `variance` | double | Valore SSE (Errore Quadratico Medio) |
| package | `Node(Data trainingSet, int beginExampleIndex, int endExampleIndex)` | costruttore | Inizializza attributi e calcola la varianza |
| package | `getIdNode()` | metodo → int | Restituisce l'ID del nodo |
| package | `getBeginExampleIndex()` | metodo → int | Restituisce l'indice iniziale |
| package | `getEndExampleIndex()` | metodo → int | Restituisce l'indice finale |
| package | `getVariance()` | metodo → double | Restituisce la varianza SSE |
| package | `getNumberOfChildren()` | metodo astratto → int | Numero di figli del nodo |
| `public` | `toString()` | metodo → String | Rappresentazione testuale del nodo |

---

### `SplitNode` *(abstract)* extends `Node` implements `Comparable<SplitNode>`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `attribute` | Attribute | Attributo indipendente per lo split |
| `protected` | `mapSplit` | List\<SplitInfo\> | Lista degli split candidati (figli) |
| `private` | `splitVariance` | double | Varianza risultante dal partizionamento |
| package | `SplitNode(Data, int, int, Attribute)` | costruttore | Invoca `Node`, ordina il training set e calcola split |
| package | `getAttribute()` | metodo → Attribute | Restituisce l'attributo di split |
| package | `getVariance()` | metodo → double | Restituisce la varianza dello split |
| package | `getNumberOfChildren()` | metodo → int | Numero di rami figli |
| package | `getSplitInfo(int child)` | metodo → SplitInfo | Informazioni del ramo specificato |
| package | `formulateQuery()` | metodo → String | Genera la query di test per la predizione |
| `public` | `toString()` | metodo → String | Rappresentazione testuale del nodo di split |
| `public` | `compareTo(SplitNode o)` | metodo → int | Confronto basato sulla varianza |
| package | `setSplitInfo(Data, int, int, Attribute)` | metodo astratto | Imposta le informazioni di split |
| package | `testCondition(Object value)` | metodo astratto → int | Valuta la condizione di test |

#### Inner Class: `SplitNode.SplitInfo` implements `Serializable`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `splitValue` | Object | Valore dell'attributo per lo split |
| `private` | `numberChild` | int | Identificativo del ramo |
| `private` | `beginIndex` | int | Indice iniziale del sotto-insieme |
| `private` | `endIndex` | int | Indice finale del sotto-insieme |
| `private` | `comparator` | String | Operatore di confronto (default `"="`) |
| package | `SplitInfo(Object, int, int, int)` | costruttore | Split discreto |
| package | `SplitInfo(Object, int, int, int, String)` | costruttore | Split continuo (con comparatore) |
| package | `getBeginindex()` | metodo → int | Indice iniziale |
| package | `getEndIndex()` | metodo → int | Indice finale |
| package | `getSplitValue()` | metodo → Object | Valore dello split |
| package | `getComparator()` | metodo → String | Operatore matematico del test |
| `public` | `toString()` | metodo → String | Rappresentazione testuale |

---

### `LeafNode` extends `Node`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `predictedClassValue` | double | Valore predetto dalla foglia |
| package | `LeafNode(Data, int, int)` | costruttore | Calcola la media dei valori target |
| package | `getPredictedClassValue()` | metodo → double | Restituisce il valore predetto |
| package | `getNumberOfChildren()` | metodo → int | Restituisce sempre 0 |
| `public` | `toString()` | metodo → String | Rappresentazione testuale della foglia |

---

### `ContinuousNode` extends `SplitNode`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| package | `ContinuousNode(Data, int, int, ContinuousAttribute)` | costruttore | Invoca il costruttore di `SplitNode` |
| package | `setSplitInfo(Data, int, int, Attribute)` | metodo | Determina gli split per attributi continui (soglia ottimale) |
| package | `testCondition(Object value)` | metodo → int | Confronta valore continuo con le soglie (`<=`, `>`) |
| `public` | `toString()` | metodo → String | Rappresentazione testuale del nodo continuo |

---

### `DiscreteNode` extends `SplitNode`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| package | `DiscreteNode(Data, int, int, DiscreteAttribute)` | costruttore | Invoca il costruttore di `SplitNode` |
| package | `setSplitInfo(Data, int, int, Attribute)` | metodo | Determina gli split per attributi discreti (una partizione per valore) |
| package | `testCondition(Object value)` | metodo → int | Confronta valore discreto con gli split |
| `public` | `toString()` | metodo → String | Rappresentazione testuale del nodo discreto |

---

### `RegressionTree` implements `Serializable`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `root` | Node | Radice dell'albero o sotto-albero corrente |
| `private` | `childTree` | RegressionTree[] | Array di sotto-alberi figli |
| package | `RegressionTree()` | costruttore | Istanzia un sotto-albero vuoto |
| `public` | `RegressionTree(Data trainingSet)` | costruttore | Avvia l'induzione dell'albero dai dati |
| `private` | `learnTree(Data, int, int, int)` | metodo | Genera ricorsivamente la struttura dell'albero |
| `private` | `isLeaf(Data, int, int, int)` | metodo → boolean | Verifica se il sotto-insieme è una foglia |
| `private` | `determineBestSplitNode(Data, int, int)` | metodo → SplitNode | Seleziona l'attributo migliore per lo split |
| `public` | `printTree()` | metodo | Stampa le informazioni dell'intero albero |
| `public` | `toString()` | metodo → String | Concatena informazioni di root e childTree |
| `public` | `getCurrentNodeQuery()` | metodo → String | Restituisce la query del nodo corrente, oppure `null` se foglia |
| `public` | `getChild(int index)` | metodo → RegressionTree | Restituisce il sotto-albero figlio; lancia `UnknownValueException` |
| `public` | `getPredictedValue()` | metodo → Double | Restituisce il valore predetto, oppure `null` se non foglia |
| `public` | `printRules()` | metodo | Stampa le regole dall radice alle foglie |
| `private` | `printRules(String current)` | metodo | Ricorsivo: concatena le condizioni lungo i percorsi |
| `public` | `salva(String fileName)` | metodo | Serializza l'albero su file (`.dmp`) |
| `public static` | `carica(String fileName)` | metodo → RegressionTree | Deserializza un albero da file |

---

## Package: data

### `Attribute` *(abstract)* implements `Serializable`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `name` | String | Nome simbolico dell'attributo |
| `private` | `index` | int | Indice numerico dell'attributo |
| package | `Attribute(String name, int index)` | costruttore | Inizializza nome e indice |
| `public` | `getName()` | metodo → String | Restituisce il nome |
| `public` | `getIndex()` | metodo → int | Restituisce l'indice |

---

### `ContinuousAttribute` extends `Attribute`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| package | `ContinuousAttribute(String name, int index)` | costruttore | Invoca il costruttore di `Attribute` |

---

### `DiscreteAttribute` extends `Attribute` implements `Iterable<String>`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `values` | Set\<String\> | Insieme ordinato dei valori distinti |
| package | `DiscreteAttribute(String name, int index, String[] values)` | costruttore | Invoca `Attribute` e popola il set dei valori |
| `public` | `getNumberOfDistinctValue()` | metodo → int | Cardinalità del set dei valori |
| `public` | `getValue(int i)` | metodo → String | Restituisce il valore all'indice i; lancia `IndexOutOfBoundsException` |
| `public` | `iterator()` | metodo → Iterator\<String\> | Iteratore sui valori distinti |

---

### `Data`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `data` | List\<Example\> | Lista di esempi (tuple) |
| `private` | `numberOfExamples` | int | Numero di righe del dataset |
| `private` | `explanatorySet` | List\<Attribute\> | Lista degli attributi indipendenti |
| `private` | `classAttribute` | ContinuousAttribute | Attributo target da predire |
| `public` | `Data(String tableName)` | costruttore | Carica lo schema e le tuple dalla tabella del DB; lancia `TrainingDataException` |
| `public` | `getNumberOfExamples()` | metodo → int | Numero di esempi |
| `public` | `getNumberOfExplanatoryAttributes()` | metodo → int | Numero di attributi indipendenti |
| `public` | `getClassValue(int exampleIndex)` | metodo → double | Valore dell'attributo di classe per un esempio |
| `public` | `getExplanatoryValue(int exampleIndex, int attributeIndex)` | metodo → Object | Valore di un attributo indipendente per un esempio |
| `public` | `getExplanatoryAttribute(int index)` | metodo → Attribute | Attributo indipendente per indice |
| package | `getClassAttribute()` | metodo → ContinuousAttribute | Restituisce l'attributo di classe |
| `public` | `toString()` | metodo → String | Rappresentazione testuale del dataset |
| `public` | `sort(Attribute, int begin, int end)` | metodo | Ordina il sottoinsieme di esempi (Quicksort) |
| `private` | `swap(int i, int j)` | metodo | Scambia due esempi |
| `private` | `partition(DiscreteAttribute, int inf, int sup)` | metodo → int | Partizionamento per attributo discreto |
| `private` | `partition(ContinuousAttribute, int inf, int sup)` | metodo → int | Partizionamento per attributo continuo |
| `private` | `quicksort(Attribute, int inf, int sup)` | metodo | Ordinamento Quicksort ricorsivo |

---

### `TrainingDataException` extends `Exception`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `public` | `TrainingDataException(String message)` | costruttore | Eccezione per errori di acquisizione del training set |

---

## Package: database

### `Column`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `name` | String | Nome della colonna |
| `private` | `type` | String | Tipo di dato della colonna |
| package | `Column(String name, String type)` | costruttore | Inizializza nome e tipo |
| `public` | `getColumnName()` | metodo → String | Restituisce il nome |
| `public` | `isNumber()` | metodo → boolean | Verifica se la colonna è numerica |
| `public` | `toString()` | metodo → String | Rappresentazione `"name:type"` |

---

### `DbAccess`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private final` | `DRIVER_CLASS_NAME` | String | `"com.mysql.cj.jdbc.Driver"` |
| `private final` | `DBMS` | String | `"jdbc:mysql"` |
| `private` | `SERVER` | String | `"localhost"` |
| `private` | `DATABASE` | String | `"MapDB"` |
| `private final` | `PORT` | int | `3306` |
| `private` | `USER_ID` | String | `"MapUser"` |
| `private` | `PASSWORD` | String | `"map"` |
| `private` | `conn` | Connection | Sessione di connessione attiva |
| `public` | `initConnection()` | metodo | Carica il driver JDBC e inizializza la connessione; lancia `DatabaseConnectionException` |
| package | `getConnection()` | metodo → Connection | Restituisce l'oggetto connessione |
| `public` | `closeConnection()` | metodo | Chiude la connessione attiva |

---

### `TableSchema` implements `Iterable<Column>`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `tableSchema` | List\<Column\> | Lista delle colonne della tabella |
| `public` | `TableSchema(DbAccess db, String tableName)` | costruttore | Estrae lo schema della tabella dai metadati del DB; lancia `SQLException` |
| `public` | `getNumberOfAttributes()` | metodo → int | Numero di colonne nello schema |
| `public` | `getColumn(int index)` | metodo → Column | Colonna in posizione specificata |
| `public` | `iterator()` | metodo → Iterator\<Column\> | Iteratore sulle colonne |

---

### `TableData`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `db` | DbAccess | Riferimento all'accesso al database |
| `public` | `TableData(DbAccess db)` | costruttore | Collega l'oggetto all'istanza di `DbAccess` |
| `public` | `getTransazioni(String table)` | metodo → List\<Example\> | Recupera tutte le tuple dalla tabella; lancia `SQLException`, `EmptySetException` |
| `public` | `getDistinctColumnValues(String table, Column column)` | metodo → Set\<Object\> | Recupera i valori distinti di una colonna; lancia `SQLException` |

---

### `Example` implements `Comparable<Example>`, `Iterable<Object>`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `private` | `example` | List\<Object\> | Lista interna dei valori della tupla |
| package | `add(Object o)` | metodo | Aggiunge un elemento in coda |
| `public` | `get(int i)` | metodo → Object | Restituisce l'elemento alla posizione i |
| `public` | `compareTo(Example ex)` | metodo → int | Confronto elemento per elemento |
| `public` | `toString()` | metodo → String | Rappresentazione testuale degli elementi |
| `public` | `iterator()` | metodo → Iterator\<Object\> | Iteratore sugli elementi |

---

### `DatabaseConnectionException` extends `Exception`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `public` | `DatabaseConnectionException(String message)` | costruttore | Eccezione per errori di connessione al database |

---

### `EmptySetException` extends `Exception`

| Visibilità | Membro | Tipo | Descrizione |
|:---:|---|---|---|
| `public` | `EmptySetException()` | costruttore | Eccezione per insieme di risultati vuoto |

---

## Legenda Visibilità

| Simbolo | Significato |
|:---:|---|
| `public` | Accessibile da qualsiasi classe |
| `protected` | Accessibile dallo stesso package e dalle sottoclassi |
| package | *(nessun modificatore)* Accessibile solo dallo stesso package |
| `private` | Accessibile solo all'interno della classe stessa |
| `static` | Appartenente alla classe, non alle istanze |
| `final` | Valore costante, non modificabile dopo l'inizializzazione |
| `abstract` | Metodo/classe astratta, richiede implementazione nelle sottoclassi |

---

## Gerarchia delle Classi

```
Serializable
├── Node (abstract)
│   ├── LeafNode
│   └── SplitNode (abstract)
│       ├── ContinuousNode
│       └── DiscreteNode
│           └── SplitInfo (inner class)
├── RegressionTree
├── Attribute (abstract)
│   ├── ContinuousAttribute
│   └── DiscreteAttribute
└── Exception
    ├── UnknownValueException
    ├── TrainingDataException
    ├── DatabaseConnectionException
    └── EmptySetException

Thread
└── ServerOneClient

Comparable<Example>, Iterable<Object>
└── Example

Iterable<Column>
└── TableSchema

Comparable<SplitNode>
└── SplitNode
```
