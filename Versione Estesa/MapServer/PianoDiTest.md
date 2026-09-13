# Documentazione Test

## 1. Test di Unità

## Package: `data` (continua)

### Classe Testata: `ContinuousAttribute`

#### Test 1 (`testGetName`)

**Significato:** verifica che l'attributo numerico erediti correttamente il comportamento di `getName()` dalla superclasse, restituendo il nome simbolico assegnato.

| Campo | Contenuto |
|---|---|
| Input | `name = "chilometri"` passato al costruttore |
| Azione | `ca.getName()` (metodo ereditato) |
| Atteso | restituisce esattamente `"chilometri"` |

#### Test 2 (`testGetIndex`)

**Significato:** verifica che l'indice identificativo dell'attributo venga correttamente memorizzato e restituito.

| Campo | Contenuto |
|---|---|
| Input | `index = 2` passato al costruttore |
| Azione | `ca.getIndex()` |
| Atteso | restituisce `2` |


### Classe Testata: `DiscreteAttribute`

#### Test 1 (`testNameAndIndex`)

**Significato:** verifica che nome e indice, ereditati dalla superclasse `Attribute`, vengano inizializzati correttamente anche per un attributo discreto.

| Campo | Contenuto |
|---|---|
| Input | `name` e `index` passati al costruttore |
| Azione | `da.getName()` e `da.getIndex()` |
| Atteso | entrambi i metodi restituiscono i valori passati al costruttore |

#### Test 2 (`testNumberOfDistinctValues`)

**Significato:** verifica che il numero di valori distinti gestiti dall'attributo discreto corrisponda esattamente agli elementi inseriti.

| Campo | Contenuto |
|---|---|
| Input | insieme di valori discreti inseriti nel costruttore |
| Azione | `da.getNumberOfDistinctValue()` |
| Atteso | restituisce il numero esatto di elementi inseriti |

#### Test 3 (`testEliminazioneValoriDuplicati`)

**Significato:** verifica che la struttura dati interna (basata su `TreeSet`) elimini automaticamente i valori duplicati, garantendo che la cardinalità riportata rifletta solo i valori realmente distinti.

| Campo | Contenuto |
|---|---|
| Input | array di valori con ripetizioni, es. `["Fiat", "Fiat", "Ford"]` |
| Azione | costruzione dell'attributo con l'array fornito, poi `da.getNumberOfDistinctValue()` |
| Atteso | il conteggio restituito è `2` (i doppioni non vengono considerati) |

#### Test 4 (`testIteratorReturnsValues`)

**Significato:** verifica che l'implementazione dell'interfaccia `Iterable` consenta di attraversare correttamente tutti i valori memorizzati nell'attributo.

| Campo | Contenuto |
|---|---|
| Input | attributo discreto popolato con un insieme noto di valori |
| Azione | ciclo `for-each` su `da` |
| Atteso | l'iteratore attraversa tutti i valori memorizzati, senza omissioni né elementi spuri |


### Classe Testata: `Attribute`

#### Test 1 (`testGetName`)

**Significato:** verifica la corretta memorizzazione e restituzione del nome simbolico dell'attributo base.

| Campo | Contenuto |
|---|---|
| Input | `name = "temperatura"` passato al costruttore |
| Azione | `a.getName()` |
| Atteso | restituisce esattamente `"temperatura"` |

#### Test 2 (`testGetIndex`)

**Significato:** verifica la corretta memorizzazione e restituzione dell'identificativo numerico posizionale dell'attributo.

| Campo | Contenuto |
|---|---|
| Input | `index = 5` passato al costruttore |
| Azione | `a.getIndex()` |
| Atteso | restituisce `5` |


### Classe Testata: `TrainingDataException`

#### Test 1 (`testExceptionMessage`)

**Significato:** verifica che il messaggio diagnostico venga correttamente propagato attraverso la gerarchia delle eccezioni, garantendo la tracciabilità dell'errore.

| Campo | Contenuto |
|---|---|
| Input | stringa diagnostica di prova passata al costruttore |
| Azione | `new TrainingDataException(messaggio)` seguito da `getMessage()` (metodo ereditato) |
| Atteso | `getMessage()` restituisce esattamente il testo fornito al costruttore |


## Package: `database` (continua)

### Classe Testata: `Column`

#### Test 1 (`testGetColumnName`)

**Significato:** verifica che il nome della colonna venga correttamente memorizzato e restituito.

| Campo | Contenuto |
|---|---|
| Input | nome colonna passato al costruttore |
| Azione | `col.getColumnName()` |
| Atteso | restituisce esattamente il valore assegnato tramite costruttore |

#### Test 2 (`testIsNumberTrue` e `testIsNumberFalse`)

**Significato:** verifica che il tipo di dato della colonna (numerico o testuale) venga correttamente riconosciuto dal metodo dedicato.

| Campo | Contenuto |
|---|---|
| Input | colonna con tipo `"number"` (caso *true*); colonna con tipo `"string"` (caso *false*) |
| Azione | `col.isNumber()` |
| Atteso | restituisce `true` per il tipo `"number"`; restituisce `false` per il tipo `"string"` |

#### Test 3 (`testUnknownTypeIsNotNumber`)

**Significato:** verifica la robustezza del controllo numerico di fronte a tipi non convenzionali, evitando falsi positivi.

| Campo | Contenuto |
|---|---|
| Input | colonna con tipo generico non standard, es. `"date"` |
| Azione | `col.isNumber()` |
| Atteso | restituisce `false` (nessun falso positivo) |

#### Test 4 (`testToString`)

**Significato:** verifica che la rappresentazione testuale della colonna segua il formato standard previsto, utile per il logging e il debug.

| Campo | Contenuto |
|---|---|
| Input | colonna con nome e tipo valorizzati |
| Azione | `col.toString()` |
| Atteso | restituisce la stringa formattata secondo lo schema `nome:tipo` |


### Classe Testata: `Example`

#### Test 1 (`testAddAndGet`)

**Significato:** verifica che l'ordine di inserimento degli elementi della tupla venga rispettato e che ciascun elemento sia recuperabile all'indice corretto, preservandone il tipo originario.

| Campo | Contenuto |
|---|---|
| Input | una stringa e un valore a virgola mobile inseriti in sequenza |
| Azione | `ex.add(...)` per ciascun elemento, poi `ex.get(int)` per ogni indice |
| Atteso | `get(i)` restituisce l'elemento atteso all'indice `i`, con il tipo originario preservato |

#### Test 2 (`testGetOutOfBounds`)

**Significato:** verifica che l'accesso a un indice non presente nella tupla venga correttamente intercettato, rispettando i limiti della lista interna.

| Campo | Contenuto |
|---|---|
| Input | indice non valido (fuori dai limiti della lista interna) |
| Azione | `ex.get(indiceNonValido)` |
| Atteso | viene sollevata `IndexOutOfBoundsException` |

#### Test 3 (`testToString`)

**Significato:** verifica che la serializzazione testuale della tupla produca una rappresentazione leggibile contenente tutti i valori inseriti.

| Campo | Contenuto |
|---|---|
| Input | tupla `ex` popolata con più valori |
| Azione | `ex.toString()` |
| Atteso | restituisce una sequenza di elementi separati da spazio, contenente tutti i valori inseriti |

#### Test 4 (`testCompareToEqual` e `testCompareToDifferent`)

**Significato:** verifica che il comportamento di ordinamento definito dall'interfaccia `Comparable` sia coerente: tuple identiche risultano equivalenti, tuple con almeno un valore differente no.

| Campo | Contenuto |
|---|---|
| Input | due tuple identiche (caso *equal*); due tuple che differiscono in una posizione (caso *different*) |
| Azione | `ex1.compareTo(ex2)` |
| Atteso | restituisce `0` per le tuple identiche; restituisce un valore diverso da zero quando è presente una differenza |

#### Test 5 (`testIterator`)

**Significato:** verifica che l'implementazione di `Iterable` consenta di scorrere correttamente tutti gli elementi registrati nella tupla.

| Campo | Contenuto |
|---|---|
| Input | tupla `ex` popolata con un insieme noto di elementi |
| Azione | ciclo `for-each` su `ex` |
| Atteso | tutti gli elementi registrati vengono scorsi correttamente, senza omissioni |


### Classe Testata: `DatabaseConnectionException`

#### Test 1 (`testMessage`)

**Significato:** verifica che il messaggio diagnostico di errore venga correttamente memorizzato e restituito.

| Campo | Contenuto |
|---|---|
| Input | messaggio descrittivo passato al costruttore |
| Azione | `new DatabaseConnectionException(messaggio)` seguito da `getMessage()` |
| Atteso | `getMessage()` restituisce esattamente la stringa fornita |

#### Test 2 (`testIsException`)

**Significato:** verifica che la classe sia correttamente collocata nella gerarchia standard delle eccezioni Java, qualificandosi come *checked exception*.

| Campo | Contenuto |
|---|---|
| Input | istanza di `DatabaseConnectionException` |
| Azione | asserzione di tipo (`instanceof`) |
| Atteso | l'istanza è riconosciuta come sottoclasse di `java.lang.Exception` |


### Classe Testata: `EmptySetException`

#### Test 1 (`testMessage`)

**Significato:** verifica che l'eccezione, istanziata senza argomenti, esponga un messaggio diagnostico standard e predefinito.

| Campo | Contenuto |
|---|---|
| Input | nessuno (costruttore senza argomenti) |
| Azione | `new EmptySetException()` seguito da `getMessage()` |
| Atteso | `getMessage()` restituisce esattamente la stringa standard `"[!] The result set is empty"` |

#### Test 2 (`testIsException`)

**Significato:** verifica che la classe erediti correttamente da `java.lang.Exception`, garantendone il comportamento come *checked exception*.

| Campo | Contenuto |
|---|---|
| Input | istanza di `EmptySetException` |
| Azione | asserzione di tipo (`instanceof`) |
| Atteso | l'istanza è riconosciuta come sottoclasse di `java.lang.Exception` |

## Package: `server` (continua)

### Classe Testata: `UnknownValueException`

#### Test 1 (`testMessage`)

**Significato:** verifica la corretta inizializzazione e conservazione del messaggio di errore diagnostico.

| Campo | Contenuto |
|---|---|
| Input | stringa specifica passata al costruttore |
| Azione | `new UnknownValueException(messaggio)` seguito da `getMessage()` |
| Atteso | `getMessage()` restituisce esattamente il testo fornito originariamente |

#### Test 2 (`testIsException`)

**Significato:** verifica che la classe estenda direttamente `java.lang.Exception`, qualificandosi come *checked exception* che obbliga i chiamanti a gestirne esplicitamente la cattura.

| Campo | Contenuto |
|---|---|
| Input | istanza di `UnknownValueException` |
| Azione | asserzione di tipo (`instanceof`) |
| Atteso | l'istanza è riconosciuta come sottoclasse diretta di `java.lang.Exception` |

## 2. Test di Integrazione

Test che verificano l'interazione tra i moduli interni o con sistemi esterni (es. database MySQL).


## Package: `tree`

### Classe Testata: `DiscreteNode`

#### Test 1 (`testGenerazioneRami`)

**Significato:** verifica che il nodo generi automaticamente un ramo per ciascun valore distinto assunto dall'attributo discreto su cui è basato lo split.

| Campo | Contenuto |
|---|---|
| Input | dati della colonna discreta `X`, con valori distinti `"A"` e `"B"` |
| Azione | costruzione di `DiscreteNode` sui dati forniti |
| Atteso | vengono generati esattamente `2` rami distinti (uno per ciascun valore) |

#### Test 2 (`testCondizioneValoreEsistente`)

**Significato:** verifica che, dato un valore di input noto, il nodo sappia individuare correttamente il ramo di split corrispondente.

| Campo | Contenuto |
|---|---|
| Input | valore esistente `"A"` |
| Azione | `node.testCondition("A")` |
| Atteso | restituisce un indice valido (`0` o `1`) corrispondente al ramo generato per quel valore |

#### Test 3 (`testCondizioneValoreInesistente`)

**Significato:** verifica la gestione di valori non previsti nel training set originale, evitando corrispondenze errate o crash.

| Campo | Contenuto |
|---|---|
| Input | valore testuale non mappato, es. `"Z"` |
| Azione | `node.testCondition("Z")` |
| Atteso | restituisce rigorosamente `-1` |

### Classe Testata: `ContinuousNode`

#### Test 1 (`testGenerazioneRami`)

**Significato:** verifica che l'algoritmo di minimizzazione della varianza identifichi correttamente una soglia di split valida per un attributo continuo, generando i rami figli conseguenti.

| Campo | Contenuto |
|---|---|
| Input | dati numerici della colonna `Y` |
| Azione | costruzione di `ContinuousNode` sui dati forniti |
| Atteso | viene identificata una soglia di split valida; vengono generati almeno un ramo figlio (tipicamente due: `<=` soglia e `>` soglia) |

#### Test 2 (`testValutazioneSoglia`)

**Significato:** verifica che un valore continuo in input venga instradato correttamente attraverso gli operatori relazionali definiti dalla soglia di split del nodo.

| Campo | Contenuto |
|---|---|
| Input | valore numerico di input, es. `1.0` |
| Azione | `node.testCondition(1.0)` |
| Atteso | l'indice restituito identifica coerentemente il ramo di partizione corretto, oppure `-1` in assenza di corrispondenza |


### Classe Testata: `LeafNode`

#### Test 1 (`testCalcoloValorePredetto`)

**Significato:** verifica la correttezza aritmetica del calcolo della media dei valori target assegnati alla foglia, base della predizione del modello.

| Campo | Contenuto |
|---|---|
| Input | insieme di esempi (`Example`) assegnati alla foglia, con valori target numerici (indici `[beginExampleIndex, endExampleIndex]`) |
| Azione | costruzione di `LeafNode` sugli esempi forniti, poi `leaf.getPredictedClassValue()` (nome dedotto dall'uso in `RegressionTree.getPredictedValue()` e `printRules()`) |
| Atteso | il valore restituito è un numero in virgola mobile formalmente valido (`!Double.isNaN(valore)`) |

#### Test 2 (`testNumeroRamiFigli`)

**Significato:** verifica l'invariante strutturale primaria dei nodi terminali: una foglia non può possedere rami discendenti.

| Campo | Contenuto |
|---|---|
| Input | istanza di `LeafNode` |
| Azione | `leaf.getNumberOfChildren()` |
| Atteso | restituisce `0` |

#### Test 3 (`testToString`)

**Significato:** verifica che la rappresentazione testuale del nodo terminale contenga l'indicazione esplicita che si tratta di una foglia, fondamentale per la corretta visualizzazione delle regole dell'albero.

| Campo | Contenuto |
|---|---|
| Input | istanza di `LeafNode` |
| Azione | `leaf.toString()` |
| Atteso | la stringa generata contiene esplicitamente la parola chiave `"LEAF"` |

### Classe Testata: `Node` (testata tramite l'estensione concreta `LeafNode`)

#### Test 1 (`testInizializzazioneIndici`)

**Significato:** verifica che gli estremi dell'intervallo di esempi coperti dal nodo vengano correttamente assegnati e recuperabili.

| Campo | Contenuto |
|---|---|
| Input | `trainingSet`, `beginExampleIndex`, `endExampleIndex` passati al costruttore di `Node` (tramite `LeafNode`) |
| Azione | `node.getBeginExampleIndex()` e `node.getEndExampleIndex()` |
| Atteso | i due metodi restituiscono esattamente `beginExampleIndex` ed `endExampleIndex` passati al costruttore |

#### Test 2 (`testGenerazioneIdProgressivo`)

**Significato:** verifica che l'identificativo univoco del nodo venga generato tramite un contatore statico progressivo, garantendo unicità crescente tra nodi creati in sequenza.

| Campo | Contenuto |
|---|---|
| Input | un primo nodo creato nel setup (es. `LeafNode`); un secondo nodo creato successivamente |
| Azione | confronto tra `secondoNodo.getIdNode()` e `primoNodo.getIdNode()` (il contatore statico `idNodeCount` viene incrementato ad ogni costruzione) |
| Atteso | `secondoNodo.getIdNode() > primoNodo.getIdNode()` |

#### Test 3 (`testCalcoloVarianza`)

**Significato:** verifica la coerenza matematica del calcolo della varianza (somma dei quadrati degli scarti, SSE) effettuato nel costruttore del nodo.

| Campo | Contenuto |
|---|---|
| Input | `trainingSet` con esempi coperti dal nodo (intervallo `[beginExampleIndex, endExampleIndex]`) |
| Azione | costruzione del nodo (calcolo interno dell'SSE rispetto alla media dei `getClassValue()`), poi `node.getVariance()` |
| Atteso | `node.getVariance() >= 0.0` |

#### Test 4 (`testToString`)

**Significato:** verifica che la stringa descrittiva del nodo riporti i riferimenti chiave necessari alla diagnostica: gli indici coperti e il valore della varianza.

| Campo | Contenuto |
|---|---|
| Input | nodo con `beginExampleIndex`, `endExampleIndex` e `variance` già calcolati |
| Azione | `node.toString()` |
| Atteso | la stringa restituita è nel formato esatto `"Nodo: [Examples:<begin>-<end>] variance:<variance>"` |

### Classe Testata: `SplitNode` (testata mediante l'implementazione concreta `DiscreteNode`)

#### Test 1 (`testGetAttribute`)

**Significato:** verifica l'incapsulamento dell'attributo indipendente su cui è generato lo split, assicurando che sia recuperabile fedelmente.

| Campo | Contenuto |
|---|---|
| Input | istanza di `Attribute` (es. `DiscreteAttribute`) passata al costruttore di `DiscreteNode`/`ContinuousNode` |
| Azione | `splitNode.getAttribute()` |
| Atteso | restituisce esattamente l'istanza di `Attribute` passata alla costruzione (campo privato `attribute`) |

#### Test 2 (`testCalcoloSplitVariance`)

**Significato:** verifica la validità matematica della varianza complessiva di split, calcolata come somma delle varianze dei rami figli.

| Campo | Contenuto |
|---|---|
| Input | sotto-insieme di dati di training `[beginExampleIndex, endExampleIndex]` usato per generare i rami figli |
| Azione | costruzione del nodo (il costruttore di `SplitNode` ordina il training set, invoca `setSplitInfo()` e somma, per ogni elemento di `mapSplit`, la varianza di un `LeafNode` temporaneo costruito su quel ramo), poi `splitNode.getVariance()` |
| Atteso | `splitNode.getVariance()` (la `splitVariance`) restituisce un numero reale `>= 0.0` |

#### Test 3 (`testGestioneFigliESplitInfo`)

**Significato:** verifica il corretto popolamento della struttura dati dei rami figli (`mapSplit`) e l'accessibilità dei relativi oggetti aggregati `SplitInfo`.

| Campo | Contenuto |
|---|---|
| Input | nodo di split con rami figli già generati (popolati nella lista protetta `mapSplit`) |
| Azione | `splitNode.getNumberOfChildren()` (restituisce `mapSplit.size()`) e `splitNode.getSplitInfo(0)` (restituisce `mapSplit.get(0)`) |
| Atteso | `getNumberOfChildren()` è `> 0` e conferma la presenza dei rami; `getSplitInfo(0)` restituisce un'istanza non nulla di `SplitNode.SplitInfo` |

#### Test 4 (`testFormulateQuery`)

**Significato:** verifica che il nodo sappia esportare in forma testuale le condizioni di test (regole di split) associate ai propri rami, per la generazione delle query verso il server.

| Campo | Contenuto |
|---|---|
| Input | nodo di split con rami figli configurati in `mapSplit` |
| Azione | `splitNode.formulateQuery()` |
| Atteso | la stringa risultante concatena, per ciascun ramo `i`, una riga nel formato `"<i>:<nomeAttributo><comparator><splitValue>\n"`, includendo quindi sia il nome dell'attributo di split sia il comparatore e il valore di ciascun ramo |

#### Test 5 (`testCompareTo`)

**Significato:** verifica l'implementazione dell'interfaccia `Comparable`, assicurando che il confronto tra nodi con varianze equivalenti sia coerente.

| Campo | Contenuto |
|---|---|
| Input | un secondo nodo con caratteristiche e dati analoghi al primo, tali da produrre `getVariance()` equivalente |
| Azione | `nodo1.compareTo(nodo2)` |
| Atteso | restituisce `0` quando `nodo1.getVariance() == nodo2.getVariance()`; restituisce `-1` se `nodo1.getVariance() < nodo2.getVariance()`; restituisce `1` se `nodo1.getVariance() > nodo2.getVariance()` |

### Classe Testata: `RegressionTree`

#### Test 1 (`testInduzioneAlbero`)

**Significato:** verifica la corretta costruzione strutturale dell'intero albero di regressione a partire dal dataset di training.

| Campo | Contenuto |
|---|---|
| Input | `Data trainingSet` caricato dalla tabella `provaC` |
| Azione | `new RegressionTree(trainingSet)` (invoca internamente `learnTree()`, che ricorsivamente crea `LeafNode` o `SplitNode` in base alla soglia `numberOfExamplesPerLeaf = 10%` degli esempi) |
| Atteso | il costruttore istanzia correttamente la gerarchia dei nodi (`root` e, se non foglia, `childTree[]`); `tree.toString()` non è `null` e contiene la stringa `"SPLIT"` (da `SplitNode.toString()`) e/o `"LEAF"` (da `LeafNode.toString()`) |

#### Test 2 (`testComportamentoNodoRadice`)

**Significato:** verifica il comportamento dei metodi di estrazione dati quando invocati sul nodo radice, distinguendo il caso di nodo decisionale da quello di nodo foglia.

| Campo | Contenuto |
|---|---|
| Input | albero generato su `provaC`, la cui radice è un nodo decisionale (non terminale) |
| Azione | `tree.getCurrentNodeQuery()` e `tree.getPredictedValue()` |
| Atteso | `getCurrentNodeQuery()` restituisce una stringa valida (la condizione di split); `getPredictedValue()` restituisce `null` (comportamento atteso per un nodo non foglia) |

#### Test 3 (`testNavigazioneEccezione`)

**Significato:** verifica la corretta gestione degli errori quando si richiede l'accesso a un ramo figlio inesistente durante la navigazione dell'albero.

| Campo | Contenuto |
|---|---|
| Input | indice non valido, es. `-1` |
| Azione | `tree.getChild(-1)` |
| Atteso | viene sollevata l'eccezione personalizzata `UnknownValueException` |

#### Test 4 (`testSerializzazione`)

**Significato:** verifica che l'albero possa essere salvato su file e successivamente ricaricato mantenendo intatta la propria struttura, tramite l'interfaccia `Serializable`.

| Campo | Contenuto |
|---|---|
| Input | istanza di `RegressionTree` già costruita; percorso di un file temporaneo locale (`"test_tree.ser"`) |
| Azione | `tree.salva("test_tree.ser")` (serializza l'oggetto con `ObjectOutputStream`), seguito da `RegressionTree.carica("test_tree.ser")` (metodo statico, deserializza con `ObjectInputStream`) |
| Atteso | il file `"test_tree.ser"` viene effettivamente creato; l'albero restituito da `carica()` produce lo stesso output di `toString()` dell'albero originale |


## Package: `data`

### Classe Testata: `Data`

#### Test 1 (`tabellaInesistente_throwsTrainingDataException`)

**Significato:** verifica che l'accesso a una tabella non esistente venga gestito in modo controllato, sollevando un'eccezione applicativa invece di generare un errore di sistema non gestito.

| Campo | Contenuto |
|---|---|
| Input | nome di una tabella non esistente nel database |
| Azione | `new Data(nomeTabellaInesistente)` |
| Atteso | viene sollevata `TrainingDataException` |

#### Test 2 (`tabellaConSingolaColonna_throwsTrainingDataException`)

**Significato:** verifica la validazione strutturale dello schema della tabella, assicurando che sia presente almeno un attributo esplicativo e uno target.

| Campo | Contenuto |
|---|---|
| Input | tabella con meno di due colonne |
| Azione | `new Data(nomeTabella)` |
| Atteso | viene sollevata `TrainingDataException` |

#### Test 3 (`targetNonNumerico_throwsTrainingDataException`)

**Significato:** verifica il vincolo di dominio sull'attributo di classe, richiesto obbligatoriamente per gli alberi di regressione.

| Campo | Contenuto |
|---|---|
| Input | tabella la cui ultima colonna (target) non è di tipo numerico continuo |
| Azione | `new Data(nomeTabella)` |
| Atteso | viene sollevata `TrainingDataException` |

#### Test 4 (`identificaTipoColonnaCorrettamente`)

**Significato:** verifica il meccanismo dinamico di mappatura dei tipi sulle colonne della tabella caricata, distinguendo correttamente attributi discreti e continui.

| Campo | Contenuto |
|---|---|
| Input | dataset caricato dalla tabella `provaC` (colonna testuale `X` all'indice 0, colonna numerica `Y` all'indice 1) |
| Azione | `data.getExplanatoryAttribute(0)` e `data.getExplanatoryAttribute(1)` (gli attributi esplicativi sono popolati nel costruttore in base a `Column.isNumber()`) |
| Atteso | `getExplanatoryAttribute(0)` restituisce un'istanza di `DiscreteAttribute` (colonna `X`); `getExplanatoryAttribute(1)` restituisce un'istanza di `ContinuousAttribute` (colonna `Y`) |

#### Test 5 (`getExplanatoryValue_ritornaValoreNonNullo` e `_throwsIndexOutOfBoundsException`)

**Significato:** verifica le garanzie di affidabilità e i limiti di accesso alla matrice degli esempi caricati.

| Campo | Contenuto |
|---|---|
| Input | indici (riga, colonna) validi (caso *non nullo*); indici fuori range (caso *eccezione*) |
| Azione | `data.getExplanatoryValue(riga, colonna)` |
| Atteso | con indici validi restituisce un oggetto non nullo; con indici fuori range viene sollevata `IndexOutOfBoundsException` |

#### Test 6 (`getClassAttribute_attributoTargetCorretto` e `getClassValue_ritornaIndiceValido`)

**Significato:** verifica il corretto binding e la corretta lettura della variabile target (colonna di classe) del dataset.

| Campo | Contenuto |
|---|---|
| Input | dataset caricato con colonna target `C` come ultima colonna |
| Azione | `data.getClassAttribute()` (metodo *package-private*, quindi il test deve trovarsi nel package `data`) e `data.getClassValue(riga)` |
| Atteso | `getClassAttribute()` restituisce l'oggetto `ContinuousAttribute` corrispondente all'ultima colonna (nome `"C"` e indice `numAttributes - 1`); `getClassValue(riga)` restituisce il valore `double` primitivo del target per quella riga, senza sollevare errori |

#### Test 7 (`ordinamentoCorretto_discreteAttribute`)

**Significato:** verifica l'implementazione dell'algoritmo di ordinamento interno (Quicksort) applicato ai valori testuali di un attributo discreto.

| Campo | Contenuto |
|---|---|
| Input | dataset con colonna discreta `X`; intervallo completo `[0, data.getNumberOfExamples() - 1]` |
| Azione | `data.sort(attributoX, 0, data.getNumberOfExamples() - 1)` (internamente invoca `quicksort()`, che per gli attributi discreti usa `String.compareTo()` come criterio di partizionamento) |
| Atteso | dopo l'ordinamento, iterando sulle righe tramite `getExplanatoryValue(i, indiceX)`, i valori della colonna `X` risultano in ordine alfabetico crescente |

#### Test 8 (`ordinamentoCorretto_continuousAttribute`)

**Significato:** verifica l'implementazione dell'algoritmo di ordinamento interno (Quicksort) applicato ai valori reali di un attributo continuo.

| Campo | Contenuto |
|---|---|
| Input | dataset con colonna continua `Y`; intervallo completo `[0, data.getNumberOfExamples() - 1]` |
| Azione | `data.sort(attributoY, 0, data.getNumberOfExamples() - 1)` (internamente invoca `quicksort()`, che per gli attributi continui usa `Double.compareTo()` come criterio di partizionamento) |
| Atteso | dopo l'ordinamento, iterando con `getExplanatoryValue(i, indiceY)`, tutti gli elementi della colonna `Y` risultano in ordine numerico crescente |


## Package: `database`

### Classe Testata: `TableData`

#### Test 1 (`testGetTransazioniValide`)

**Significato:** verifica che l'esecuzione della query `SELECT` produca correttamente una lista ordinata di tuple `Example` a partire dai risultati del database.

| Campo | Contenuto |
|---|---|
| Input | tabella `"provaC"` |
| Azione | `tableData.getTransazioni("provaC")` |
| Atteso | la `List<Example>` restituita non è `null` né vuota |

#### Test 2 (`testGetTransazioniTabellaInesistente`)

**Significato:** verifica la corretta gestione degli errori SQL quando viene richiesta l'estrazione da una tabella non esistente.

| Campo | Contenuto |
|---|---|
| Input | nome di una tabella inesistente |
| Azione | `tableData.getTransazioni(nomeTabellaInesistente)` |
| Atteso | viene sollevata `SQLException`; nel codice questo avviene perché `TableSchema` per una tabella inesistente produce `getNumberOfAttributes() == 0`, condizione per cui `getTransazioni()` solleva esplicitamente `new SQLException()` prima ancora di eseguire la query |

#### Test 3 (`testGetDistinctColumnValuesString`)

**Significato:** verifica l'estrazione e l'ordinamento (tramite `TreeSet`) dei valori unici di tipo stringa presenti in una colonna.

| Campo | Contenuto |
|---|---|
| Input | tabella `"provaC"`; oggetto `Column` per la colonna testuale `X` |
| Azione | `tableData.getDistinctColumnValues("provaC", columnX)` (esegue `SELECT DISTINCT X FROM provaC ORDER BY X ASC`) |
| Atteso | il `Set<Object>` (basato su `TreeSet`) restituito non è vuoto e contiene valori di tipo `String` |

#### Test 4 (`testGetDistinctColumnValuesNumber`)

**Significato:** verifica l'estrazione e l'ordinamento (tramite `TreeSet`) dei valori unici di tipo numerico (`Double`) presenti in una colonna.

| Campo | Contenuto |
|---|---|
| Input | tabella `"provaC"`; oggetto `Column` per la colonna numerica `Y` |
| Azione | `tableData.getDistinctColumnValues("provaC", columnY)` (poiché `columnY.isNumber() == true`, i valori vengono letti con `rs.getDouble(1)`) |
| Atteso | il `Set<Object>` restituito non è vuoto e contiene valori di tipo `Double` |

### Classe Testata: `TableSchema`

#### Test 1 (`testGetNumberOfAttributes`)

**Significato:** verifica l'estrazione e il conteggio corretto delle colonne dal dizionario dei dati del database.

| Campo | Contenuto |
|---|---|
| Input | `db` connesso; tabella esistente nel database, es. `"provaC"` |
| Azione | `new TableSchema(db, "provaC")` seguito da `schema.getNumberOfAttributes()` |
| Atteso | restituisce un valore strettamente maggiore di zero |

#### Test 2 (`testGetColumn` e `testGetColumnOutOfBounds`)

**Significato:** verifica l'accesso posizionale alla definizione dei tipi della singola colonna, e la corretta gestione di richieste con indici non validi.

| Campo | Contenuto |
|---|---|
| Input | indice valido (caso *getColumn*); indice non presente nello schema (caso *out of bounds*) |
| Azione | `schema.getColumn(indice)` |
| Atteso | con indice valido restituisce l'oggetto `Column` corrispondente; con indice non valido viene sollevata `IndexOutOfBoundsException` |

#### Test 3 (`testIterator`)

**Significato:** verifica che l'implementazione dell'interfaccia `Iterable` consenta di scorrere sequenzialmente tutte le colonne dello schema.

| Campo | Contenuto |
|---|---|
| Input | schema popolato con un insieme noto di colonne |
| Azione | ciclo `for-each` su `schema` |
| Atteso | il numero di cicli eseguiti corrisponde esattamente alla cardinalità degli attributi |

#### Test 4 (`testTabellaInesistente`)

**Significato:** verifica che il costruttore gestisca in modo robusto la scansione dei metadati per una tabella non definita nel database, senza generare crash.

| Campo | Contenuto |
|---|---|
| Input | nome di una tabella fittizia (non esistente) |
| Azione | `new TableSchema(nomeTabellaFittizia)` |
| Atteso | il sistema non va in crash; il `ResultSet` viene semplicemente esaurito; la lista interna delle colonne resta vuota (cardinalità pari a `0`) |


### Classe Testata: `DbAccess`

#### Test 1 (`testInitAndGetConnection`)

**Significato:** verifica l'inizializzazione della connessione al database MySQL e il corretto recupero della sessione attiva.

| Campo | Contenuto |
|---|---|
| Input | parametri di rete predefiniti hard-coded nella classe (`SERVER = "localhost"`, `PORT = 3306`, `DATABASE = "MapDB"`, `USER_ID = "MapUser"`) |
| Azione | `dbAccess.initConnection()` (carica il driver `com.mysql.cj.jdbc.Driver` e invoca `DriverManager.getConnection()`) seguito da `dbAccess.getConnection()` (metodo *package-private*, quindi il test deve trovarsi nel package `database`) |
| Atteso | l'oggetto `Connection` restituito non è `null`; `connection.isClosed() == false`; non viene sollevata `DatabaseConnectionException` |

#### Test 2 (`testCloseConnection`)

**Significato:** verifica la corretta chiusura e il rilascio delle risorse di rete allocate per la sessione, evitando memory leak o connessioni pendenti.

| Campo | Contenuto |
|---|---|
| Input | connessione al database già avviata |
| Azione | `dbAccess.closeConnection()` |
| Atteso | l'oggetto `Connection` sottostante risulta formalmente chiuso (`connection.isClosed() == true`) |

#### Test 3 (`testGetAvailableTablesSuccess`)

**Significato:** verifica l'estrazione dell'elenco delle tabelle utente tramite i metadati del database relazionale, escludendo viste e tabelle di sistema.

| Campo | Contenuto |
|---|---|
| Input | connessione al database attiva |
| Azione | `dbAccess.getAvailableTables()` |
| Atteso | la lista restituita non è `null`, contiene almeno un elemento, include la tabella di prova `provac`, e contiene esclusivamente entità di tipo `TABLE` (viste e tabelle di sistema escluse) |

#### Test 4 (`testGetAvailableTablesConnectionClosed`)

**Significato:** verifica la corretta propagazione degli errori SQL quando si tenta di interrogare i metadati con una sessione non più attiva.

| Campo | Contenuto |
|---|---|
| Input | connessione precedentemente chiusa tramite `closeConnection()` (`conn == null` oppure `conn.isClosed() == true`) |
| Azione | `dbAccess.getAvailableTables()` |
| Atteso | viene sollevata `SQLException` con messaggio esatto `"Connessione al database chiusa o non inizializzata."`, senza ritorni incoerenti o crash non gestiti |

## Package: `server`

### Classe Testata: `MultiServer`

#### Test 1 (`testServerAvvioEConnessione`)

**Significato:** verifica che il server si avvii correttamente su un thread dedicato e sia in grado di accettare richieste di connessione in ingresso.

| Campo | Contenuto |
|---|---|
| Input | server avviato su una porta di test, in un thread secondario |
| Azione | creazione di un `Socket` client e tentativo di connessione verso il server |
| Atteso | `assertTrue` conferma che il collegamento viene stabilito correttamente (il server è regolarmente in ascolto) |

### Classe Testata: `ServerOneClient`

#### Test 1 (`testAcquisizioneDatiOK`)

**Significato:** verifica l'elaborazione del comando `0` (acquisizione dati) e la corretta istanziazione di `ServerOneClient` come thread dedicato al client.

| Campo | Contenuto |
|---|---|
| Input | `ServerSocket` temporaneo; client che invia l'intero `0` e la stringa `"provaC"` tramite `ObjectOutputStream` |
| Azione | `new ServerOneClient(socketConnesso)` (avvio del thread), seguito dall'invio dei dati sopra descritti |
| Atteso | la stringa ricevuta in risposta corrisponde esattamente a `"OK"` |

#### Test 2 (`testCaricamentoAlberoFallito`)

**Significato:** verifica la gestione delle eccezioni quando si richiede il caricamento (comando `2`) di un file `.dmp` che non esiste, assicurando che l'errore venga comunicato correttamente al client.

| Campo | Contenuto |
|---|---|
| Input | comando intero `2` seguito dal nome di una tabella non serializzata in precedenza |
| Azione | invio del comando e del nome tabella al `ServerOneClient` |
| Atteso | il blocco `catch` interno viene innescato; viene restituita la stringa di errore cablata `"Errore durante il caricamento da archivio."` |

#### Test 3 (`testRecuperoTabelleOK`)

**Significato:** verifica l'elaborazione del comando `4` per il recupero asincrono dell'elenco delle tabelle disponibili, incluso il corretto protocollo di sincronizzazione e serializzazione della risposta.

| Campo | Contenuto |
|---|---|
| Input | comando intero `4` inviato tramite stream di output su socket di test |
| Azione | invio del comando a `ServerOneClient`, ricezione della risposta |
| Atteso | la prima risposta ricevuta è la stringa di sincronizzazione `"OK"`; l'oggetto successivo è un'istanza di `List<String>`, non vuota, contenente la tabella di test `provac` |

> **Nota sul percorso di errore del comando `4`:** il ramo `catch (Exception e)` che scrive in sequenza `"ERROR"` e `"Errore recupero tabelle dal DB: " + e.getMessage()` è implementato in `ServerOneClient.run()` ma non è verificato da un test automatico deterministico: per innescarlo il DBMS deve risultare non raggiungibile durante l'esecuzione del test, condizione non riproducibile in modo stabile. Il piano copre pertanto, per il comando `4`, unicamente il percorso di successo (`testRecuperoTabelleOK`).

