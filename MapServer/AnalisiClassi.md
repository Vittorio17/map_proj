# Analisi delle Classi - MapServer

## Indice
1. [Package `data`](#package-data)
2. [Package `database`](#package-database)
3. [Package `tree`](#package-tree)
4. [Package `server`](#package-server)
5. [Package default (MainTest)]#packagedefaultmaintest)
6. [Diagramma delle Relazioni](#diagramma-delle-relazioni)

---

## Package `data`

### Classe Astratta: `Attribute`

**Visibilità:** `public abstract`

**Implementa:** `Serializable`

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `String` | `name` | Nome simbolico dell'attributo |
| `private` | `int` | `index` | Indice numerico dell'attributo |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `Attribute` | `(String name, int index)` | Costruttore |
| `public` | `String` | `getName` | `()` | Restituisce il nome |
| `public` | `int` | `getIndex` | `()` | Restituisce l'indice |

---

### Classe: `ContinuousAttribute`

**Visibilità:** `public`

**Eredita da:** `Attribute`

#### Relazioni
- **Ereditarietà:** `extends Attribute`

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `ContinuousAttribute` | `(String name, int index)` | Costruttore |

---

### Classe: `DiscreteAttribute`

**Visibilità:** `public`

**Eredita da:** `Attribute`

**Implementa:** `Iterable<String>`

#### Relazioni
- **Ereditarietà:** `extends Attribute`
- **Implementazione:** `implements Iterable<String>` (interfaccia Java base)

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `Set<String>` | `values` | Set di valori discreti |

#### Aggregazione/Composizione
- **Composizione** con `TreeSet<String>` (classe Java base) - l'oggetto `values` viene creato all'interno della classe

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `DiscreteAttribute` | `(String name, int index, String[] values)` | Costruttore |
| `public` | `int` | `getNumberOfDistinctValue` | `()` | Numero valori distinti |
| `public` | `String` | `getValue` | `(int i)` | Valore all'indice i |
| `public` | `Iterator<String>` | `iterator` | `()` | Iteratore sui valori |

---

### Classe: `Data`

**Visibilità:** `public`

#### Relazioni
- **Aggregazione** con `DbAccess` - utilizzata localmente nel costruttore
- **Aggregazione** con `TableSchema` - utilizzata per leggere lo schema
- **Aggregazione** con `TableData` - utilizzata per leggere i dati

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `List<Example>` | `data` | Lista di esempi |
| `private` | `int` | `numberOfExamples` | Numero di righe |
| `private` | `List<Attribute>` | `explanatorySet` | Attributi indipendenti |
| `private` | `ContinuousAttribute` | `classAttribute` | Attributo target |

#### Aggregazione/Composizione
- **Composizione** con `ArrayList<Example>` (classe Java base) - istanziata direttamente
- **Composizione** con `LinkedList<Attribute>` (classe Java base) - istanziata direttamente
- **Composizione** con `ContinuousAttribute` - istanziata nel costruttore
- **Composizione** con `DiscreteAttribute` - istanziata nel costruttore

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `Data` | `(String tableName)` | Costruttore |
| `public` | `int` | `getNumberOfExamples` | `()` | Numero esempi |
| `public` | `int` | `getNumberOfExplanatoryAttributes` | `()` | Numero attributi esplicativi |
| `public` | `double` | `getClassValue` | `(int exampleIndex)` | Valore classe |
| `public` | `Object` | `getExplanatoryValue` | `(int exampleIndex, int attributeIndex)` | Valore attributo |
| `public` | `Attribute` | `getExplanatoryAttribute` | `(int index)` | Attributo all'indice |
| `package` | `ContinuousAttribute` | `getClassAttribute` | `()` | Attributo di classe |
| `public` | `String` | `toString` | `()` | Rappresentazione testuale |
| `public` | `void` | `sort` | `(Attribute attribute, int beginExampleIndex, int endExampleIndex)` | Ordina dataset |
| `private` | `void` | `swap` | `(int i, int j)` | Scambia elementi |
| `private` | `int` | `partition` | `(DiscreteAttribute attribute, int inf, int sup)` | Partizione discreta |
| `private` | `int` | `partition` | `(ContinuousAttribute attribute, int inf, int sup)` | Partizione continua |
| `private` | `void` | `quicksort` | `(Attribute attribute, int inf, int sup)` | Quicksort |

---

### Classe: `TrainingDataException`

**Visibilità:** `public`

**Eredita da:** `Exception` (classe Java base)

#### Relazioni
- **Ereditarietà:** `extends Exception` (classe Java base)

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `TrainingDataException` | `(String message)` | Costruttore |

---

## Package `database`

### Classe: `Column`

**Visibilità:** `public`

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `String` | `name` | Nome della colonna |
| `private` | `String` | `type` | Tipo di dato |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `Column` | `(String name, String type)` | Costruttore |
| `public` | `String` | `getColumnName` | `()` | Nome colonna |
| `public` | `boolean` | `isNumber` | `()` | Verifica se numerico |
| `public` | `String` | `toString` | `()` | Rappresentazione testuale |

---

### Classe: `DbAccess`

**Visibilità:** `public`

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private final` | `String` | `DRIVER_CLASS_NAME` | Driver JDBC MySQL |
| `private final` | `String` | `DBMS` | Protocollo JDBC |
| `private` | `String` | `SERVER` | Indirizzo server |
| `private` | `String` | `DATABASE` | Nome database |
| `private final` | `int` | `PORT` | Porta MySQL |
| `private` | `String` | `USER_ID` | Utente |
| `private` | `String` | `PASSWORD` | Password |
| `private` | `Connection` | `conn` | Connessione attiva |

#### Aggregazione/Composizione
- **Aggregazione** con `Connection` (interfaccia Java base - `java.sql.Connection`) - ottenuta tramite `DriverManager`

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | `void` | `initConnection` | `()` | Inizializza connessione |
| `package` | `Connection` | `getConnection` | `()` | Restituisce connessione |
| `public` | `void` | `closeConnection` | `()` | Chiude connessione |

---

### Classe: `DatabaseConnectionException`

**Visibilità:** `public`

**Eredita da:** `Exception` (classe Java base)

#### Relazioni
- **Ereditarietà:** `extends Exception` (classe Java base)

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `DatabaseConnectionException` | `(String message)` | Costruttore |

---

### Classe: `EmptySetException`

**Visibilità:** `public`

**Eredita da:** `Exception` (classe Java base)

#### Relazioni
- **Ereditarietà:** `extends Exception` (classe Java base)

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `EmptySetException` | `()` | Costruttore |

---

### Classe: `Example`

**Visibilità:** `public`

**Implementa:** `Comparable<Example>`, `Iterable<Object>` (interfacce Java base)

#### Relazioni
- **Implementazione:** `implements Comparable<Example>` (interfaccia Java base)
- **Implementazione:** `implements Iterable<Object>` (interfaccia Java base)

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `List<Object>` | `example` | Lista valori tupla |

#### Aggregazione/Composizione
- **Composizione** con `ArrayList<Object>` (classe Java base) - istanziata direttamente

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | `void` | `add` | `(Object o)` | Aggiunge elemento |
| `public` | `Object` | `get` | `(int i)` | Elemento all'indice |
| `public` | `int` | `compareTo` | `(Example ex)` | Confronta esempi |
| `public` | `String` | `toString` | `()` | Rappresentazione testuale |
| `public` | `Iterator<Object>` | `iterator` | `()` | Iteratore |

---

### Classe: `TableData`

**Visibilità:** `public`

#### Relazioni
- **Aggregazione** con `DbAccess` - ricevuta come parametro nel costruttore

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `DbAccess` | `db` | Riferimento al database |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `TableData` | `(DbAccess db)` | Costruttore |
| `public` | `List<Example>` | `getTransazioni` | `(String table)` | Recupera transazioni |
| `public` | `Set<Object>` | `getDistinctColumnValues` | `(String table, Column column)` | Valori distinti colonna |

#### Aggregazione/Composizione nei metodi
- **Composizione locale** con `LinkedList<Example>` (classe Java base) - in `getTransazioni()`
- **Composizione locale** con `TreeSet<Object>` (classe Java base) - in `getDistinctColumnValues()`
- **Aggregazione** con `TableSchema` - istanziato localmente nel metodo

---

### Classe: `TableSchema`

**Visibilità:** `public`

**Implementa:** `Iterable<Column>` (interfaccia Java base)

#### Relazioni
- **Implementazione:** `implements Iterable<Column>` (interfaccia Java base)
- **Aggregazione** con `DbAccess` - ricevuta come parametro nel costruttore

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `List<Column>` | `tableSchema` | Lista colonne |

#### Aggregazione/Composizione
- **Composizione** con `ArrayList<Column>` (classe Java base) - istanziata direttamente
- **Composizione** con `Column` - istanziata nel costruttore per ogni colonna
- **Composizione locale** con `HashMap<String,String>` (classe Java base) - per mappatura tipi SQL

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `TableSchema` | `(DbAccess db, String tableName)` | Costruttore |
| `public` | `int` | `getNumberOfAttributes` | `()` | Numero attributi |
| `public` | `Column` | `getColumn` | `(int index)` | Colonna all'indice |
| `public` | `Iterator<Column>` | `iterator` | `()` | Iteratore |

---

## Package `tree`

### Classe Astratta: `Node`

**Visibilità:** `abstract class` (package-private)

**Implementa:** `Serializable` (interfaccia Java base)

#### Relazioni
- **Implementazione:** `implements Serializable` (interfaccia Java base)
- **Aggregazione** con `Data` - ricevuta come parametro nel costruttore

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `static` | `int` | `idNodeCount` | Contatore nodi |
| `private` | `int` | `idNode` | ID nodo |
| `private` | `int` | `beginExampleIndex` | Indice iniziale |
| `private` | `int` | `endExampleIndex` | Indice finale |
| `private` | `double` | `variance` | Varianza SSE |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `Node` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex)` | Costruttore |
| `package` | `int` | `getIdNode` | `()` | ID nodo |
| `package` | `int` | `getBeginExampleIndex` | `()` | Indice iniziale |
| `package` | `int` | `getEndExampleIndex` | `()` | Indice finale |
| `package` | `double` | `getVariance` | `()` | Varianza |
| `abstract` | `int` | `getNumberOfChildren` | `()` | Numero figli |
| `public` | `String` | `toString` | `()` | Rappresentazione testuale |

---

### Classe: `LeafNode`

**Visibilità:** `class` (package-private)

**Eredita da:** `Node`

#### Relazioni
- **Ereditarietà:** `extends Node`

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `double` | `predictedClassValue` | Valore predetto |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `LeafNode` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex)` | Costruttore |
| `package` | `double` | `getPredictedClassValue` | `()` | Valore predetto |
| `package` | `int` | `getNumberOfChildren` | `()` | Restituisce 0 |
| `public` | `String` | `toString` | `()` | Rappresentazione testuale |

---

### Classe Astratta: `SplitNode`

**Visibilità:** `abstract class` (package-private)

**Eredita da:** `Node`

**Implementa:** `Comparable<SplitNode>` (interfaccia Java base)

#### Relazioni
- **Ereditarietà:** `extends Node`
- **Implementazione:** `implements Comparable<SplitNode>` (interfaccia Java base)
- **Aggregazione** con `Attribute` - memorizza riferimento all'attributo di split

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `Attribute` | `attribute` | Attributo di split |
| `protected` | `List<SplitInfo>` | `mapSplit` | Lista split candidati |
| `private` | `double` | `splitVariance` | Varianza dopo split |

#### Aggregazione/Composizione
- **Composizione** con `ArrayList<SplitInfo>` (classe Java base) - istanziata direttamente
- **Composizione** con `SplitInfo` (inner class) - istanziata per ogni ramo

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `abstract` | `void` | `setSplitInfo` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)` | Imposta info split |
| `abstract` | `int` | `testCondition` | `(Object value)` | Test condizione |
| `package` | - | `SplitNode` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)` | Costruttore |
| `package` | `Attribute` | `getAttribute` | `()` | Attributo di split |
| `package` | `double` | `getVariance` | `()` | Varianza split |
| `package` | `int` | `getNumberOfChildren` | `()` | Numero rami |
| `package` | `SplitInfo` | `getSplitInfo` | `(int child)` | Info ramo |
| `package` | `String` | `formulateQuery` | `()` | Formula query |
| `public` | `String` | `toString` | `()` | Rappresentazione testuale |
| `public` | `int` | `compareTo` | `(SplitNode o)` | Confronta nodi |

---

### Inner Class: `SplitNode.SplitInfo`

**Visibilità:** `class` (inner class, package-private)

**Implementa:** `Serializable` (interfaccia Java base)

#### Relazioni
- **Composizione** con `SplitNode` - è una inner class
- **Implementazione:** `implements Serializable` (interfaccia Java base)

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `Object` | `splitValue` | Valore split |
| `private` | `int` | `numberChild` | ID figlio |
| `private` | `int` | `beginIndex` | Indice iniziale |
| `private` | `int` | `endIndex` | Indice finale |
| `private` | `String` | `comparator` | Operatore test |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `SplitInfo` | `(Object splitValue, int beginIndex, int endIndex, int numberChild)` | Costruttore discreto |
| `package` | - | `SplitInfo` | `(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator)` | Costruttore continuo |
| `package` | `int` | `getBeginindex` | `()` | Indice iniziale |
| `package` | `int` | `getEndIndex` | `()` | Indice finale |
| `package` | `Object` | `getSplitValue` | `()` | Valore split |
| `public` | `String` | `toString` | `()` | Rappresentazione |
| `package` | `String` | `getComparator` | `()` | Operatore |

---

### Classe: `DiscreteNode`

**Visibilità:** `class` (package-private)

**Eredita da:** `SplitNode`

#### Relazioni
- **Ereditarietà:** `extends SplitNode`

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `DiscreteNode` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute)` | Costruttore |
| `void` | `setSplitInfo` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)` | Imposta info split |
| `int` | `testCondition` | `(Object value)` | Test condizione |
| `public` | `String` | `toString` | `()` | Rappresentazione |

---

### Classe: `ContinuousNode`

**Visibilità:** `public class`

**Eredita da:** `SplitNode`

#### Relazioni
- **Ereditarietà:** `extends SplitNode`

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `ContinuousNode` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute)` | Costruttore |
| `void` | `setSplitInfo` | `(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute)` | Imposta info split |
| `int` | `testCondition` | `(Object value)` | Test condizione |
| `public` | `String` | `toString` | `()` | Rappresentazione |

---

### Classe: `RegressionTree`

**Visibilità:** `public class`

**Implementa:** `Serializable` (interfaccia Java base)

#### Relazioni
- **Implementazione:** `implements Serializable` (interfaccia Java base)
- **Composizione** con `Node` - la radice è parte integrante dell'albero
- **Composizione** con `RegressionTree[]` - i sotto-alberi sono posseduti dall'albero padre
- **Aggregazione** con `Data` - ricevuta come parametro per costruzione

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `Node` | `root` | Radice dell'albero |
| `private` | `RegressionTree[]` | `childTree` | Array sotto-alberi |

#### Aggregazione/Composizione
- **Composizione** con `Node` (può essere `LeafNode` o `SplitNode`) - istanziato internamente
- **Composizione** con `RegressionTree[]` (array Java base) - array di sotto-alberi
- **Composizione locale** con `TreeSet<SplitNode>` (classe Java base) - in `determineBestSplitNode()`

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `package` | - | `RegressionTree` | `()` | Costruttore vuoto |
| `public` | - | `RegressionTree` | `(Data trainingSet)` | Costruttore con training set |
| `private` | `void` | `learnTree` | `(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf)` | Induzione albero |
| `private` | `boolean` | `isLeaf` | `(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf)` | Verifica foglia |
| `private` | `SplitNode` | `determineBestSplitNode` | `(Data trainingSet, int begin, int end)` | Miglior split |
| `public` | `void` | `printTree` | `()` | Stampa albero |
| `public` | `String` | `toString` | `()` | Rappresentazione |
| `public` | `String` | `getCurrentNodeQuery` | `()` | Query nodo corrente |
| `public` | `RegressionTree` | `getChild` | `(int index)` | Sotto-albero figlio |
| `public` | `Double` | `getPredictedValue` | `()` | Valore predetto |
| `public` | `void` | `printRules` | `()` | Stampa regole |
| `private` | `void` | `printRules` | `(String current)` | Supporto stampa regole |
| `public` | `void` | `salva` | `(String fileName)` | Serializza albero |
| `public static` | `RegressionTree` | `carica` | `(String fileName)` | Deserializza albero |

---

## Package `server`

### Classe: `MultiServer`

**Visibilità:** `public class`

#### Relazioni
- **Composizione** con `ServerSocket` (classe Java base) - gestisce le connessioni
- **Composizione** con `ServerOneClient` - crea un nuovo thread per ogni client

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `int` | `PORT` | Porta di ascolto |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `MultiServer` | `(int port)` | Costruttore |
| `private` | `void` | `run` | `()` | Ciclo principale server |

---

### Classe: `ServerOneClient`

**Visibilità:** `public class`

**Eredita da:** `Thread` (classe Java base)

#### Relazioni
- **Ereditarietà:** `extends Thread` (classe Java base)
- **Composizione** con `Socket` (classe Java base) - connessione client
- **Composizione** con `ObjectInputStream` (classe Java base) - stream input
- **Composizione** con `ObjectOutputStream` (classe Java base) - stream output
- **Aggregazione** con `Data` - utilizzata per il training set
- **Aggregazione** con `RegressionTree` - utilizzata per le predizioni

#### Attributi
| Visibilità | Tipo | Nome | Descrizione |
|------------|------|------|-------------|
| `private` | `Socket` | `socket` | Socket connessione |
| `private` | `ObjectInputStream` | `in` | Stream input |
| `private` | `ObjectOutputStream` | `out` | Stream output |

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `ServerOneClient` | `(Socket s)` | Costruttore |
| `public` | `void` | `run` | `()` | Logica thread |

---

### Classe: `UnknownValueException`

**Visibilità:** `public class`

**Eredita da:** `Exception` (classe Java base)

#### Relazioni
- **Ereditarietà:** `extends Exception` (classe Java base)

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public` | - | `UnknownValueException` | `(String message)` | Costruttore |

---

## Package default (MainTest)

### Classe: `MainTest`

**Visibilità:** `public class`

#### Relazioni
- **Aggregazione** con `MultiServer` - istanzia e avvia il server

#### Metodi
| Visibilità | Tipo Ritorno | Nome | Parametri | Descrizione |
|------------|--------------|------|-----------|-------------|
| `public static` | `void` | `main` | `(String[] args)` | Punto di ingresso |

---

## Diagramma delle Relazioni

### Gerarchia di Ereditarietà

```
Object (Java base)
├── Exception (Java base)
│   ├── TrainingDataException
│   ├── DatabaseConnectionException
│   ├── EmptySetException
│   └── UnknownValueException
│
├── Thread (Java base)
│   └── ServerOneClient
│
└── Attribute (abstract)
    ├── ContinuousAttribute
    └── DiscreteAttribute (implements Iterable<String>)

Node (abstract, implements Serializable)
├── LeafNode
└── SplitNode (abstract, implements Comparable<SplitNode>)
    ├── DiscreteNode
    └── ContinuousNode

RegressionTree (implements Serializable)
```

### Relazioni di Composizione

Una classe A è in **composizione** con una classe B quando:
- A possiede B e B non può esistere senza A
- B viene istanziato all'interno di A

```
RegressionTree ──◆ Node (root)
               ──◆ RegressionTree[] (childTree)
               
SplitNode ──◆ List<SplitInfo> (mapSplit)
           ──◆ SplitInfo (inner class)

DiscreteAttribute ──◆ TreeSet<String> (values)

Data ──◆ List<Example> (data)
     ──◆ List<Attribute> (explanatorySet)
     ──◆ ContinuousAttribute (classAttribute)

Example ──◆ List<Object> (example)

TableSchema ──◆ List<Column> (tableSchema)

DbAccess ──◆ Connection (conn)

ServerOneClient ──◆ Socket (socket)
                 ──◆ ObjectInputStream (in)
                 ──◆ ObjectOutputStream (out)
```

### Relazioni di Aggregazione

Una classe A è in **aggregazione** con una classe B quando:
- A utilizza B ma B può esistere indipendentemente
- B viene passato come parametro o ottenuto da altri oggetti

```
Data ──◇ DbAccess (usato localmente)
    ──◇ TableSchema (usato localmente)
    ──◇ TableData (usato localmente)

TableData ──◇ DbAccess (ricevuto nel costruttore)

TableSchema ──◇ DbAccess (ricevuto nel costruttore)

RegressionTree ──◇ Data (ricevuto nel costruttore)

Node ──◇ Data (ricevuto nel costruttore)

SplitNode ──◇ Attribute (memorizza riferimento)

ServerOneClient ──◇ Data (usato nel thread)
                 ──◇ RegressionTree (usato nel thread)

MainTest ──◇ MultiServer (istanziato nel main)
```

### Interfacce Implementate

| Classe | Interfaccia | Scopo |
|--------|-------------|-------|
| `Attribute` | `Serializable` | Serializzazione |
| `Node` | `Serializable` | Serializzazione |
| `RegressionTree` | `Serializable` | Serializzazione |
| `SplitInfo` | `Serializable` | Serializzazione |
| `DiscreteAttribute` | `Iterable<String>` | Iterazione sui valori |
| `Example` | `Comparable<Example>` | Confronto tra esempi |
| `Example` | `Iterable<Object>` | Iterazione sui valori |
| `TableSchema` | `Iterable<Column>` | Iterazione sulle colonne |
| `SplitNode` | `Comparable<SplitNode>` | Confronto per varianza |

### Dipendenze con Classi Java Base

| Classe Java | Utilizzata in | Tipo Relazione |
|-------------|---------------|----------------|
| `ArrayList<E>` | `Data`, `Example`, `SplitNode`, `TableSchema` | Composizione |
| `LinkedList<E>` | `Data`, `TableData` | Composizione |
| `TreeSet<E>` | `DiscreteAttribute`, `RegressionTree`, `TableData` | Composizione |
| `HashMap<K,V>` | `TableSchema` | Composizione locale |
| `Set<E>` | `DiscreteAttribute`, `TableData` | Tipo attributo |
| `List<E>` | `Data`, `Example`, `SplitNode`, `TableSchema` | Tipo attributo |
| `Iterator<E>` | `DiscreteAttribute`, `Example`, `TableSchema` | Tipo ritorno |
| `Comparable<T>` | `Example`, `SplitNode` | Implementazione |
| `Iterable<T>` | `DiscreteAttribute`, `Example`, `TableSchema` | Implementazione |
| `Serializable` | `Attribute`, `Node`, `RegressionTree`, `SplitInfo` | Implementazione |
| `Exception` | Tutte le eccezioni custom | Ereditarietà |
| `Thread` | `ServerOneClient` | Ereditarietà |
| `Socket`, `ServerSocket` | `MultiServer`, `ServerOneClient` | Composizione |
| `ObjectInputStream`, `ObjectOutputStream` | `ServerOneClient`, `RegressionTree` | Composizione |
| `Connection`, `Statement`, `ResultSet` | `DbAccess`, `TableData`, `TableSchema` | Aggregazione/Composizione locale |

---

## Note

1. **Visibilità package-private:** Le classi `Node`, `LeafNode`, `SplitNode`, `DiscreteNode` hanno visibilità package-private (nessun modificatore), quindi sono accessibili solo all'interno del package `tree`.

2. **Costruttori package-private:** Molti costruttori hanno visibilità package-private per controllare l'istanziazione delle classi.

3. **Pattern Composite:** `RegressionTree` implementa un pattern Composite ricorsivo dove ogni albero può contenere sotto-alberi.

4. **Pattern Template Method:** `SplitNode` definisce metodi astratti (`setSplitInfo`, `testCondition`) che vengono implementati diversamente da `DiscreteNode` e `ContinuousNode`.

5. **Serializzazione:** Le classi principali (`RegressionTree`, `Node`, `Attribute`) implementano `Serializable` per permettere il salvataggio su file.
