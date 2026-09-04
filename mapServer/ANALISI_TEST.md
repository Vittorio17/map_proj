# Analisi dei Test - MapServer Project

**Data analisi:** 2026-07-28  
**Progetto:** MapServer - Regression Tree Learning System  
**Framework di test:** JUnit 5 (Jupiter)  
**Build/Run:** JUnit Platform Console Standalone 1.10.2 + MySQL Connector/J 8.0.17  

---

## 📋 Panoramica Generale

Il progetto contiene **12 file di test** organizzati in 4 package:
- `database` (5 test) - Test per layer database
- `data` (4 test) - Test per layer dati/attributi
- `tree` (2 test + 1 vuoto) - Test per alberi di regressione
- `integration` (1 test - DbIntegrationTest) - Test di integrazione con DB reale

**Totale test case:** ~50+ metodi `@Test`

---

## ✅ TEST ESISTENTI - Analisi Dettagliata

### 1. Package `data` - Attributi e Dati di Training

#### **ContinuousAttributeTest.java** (2 test)
| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `continuousAttribute_getName()` | Costruttore assegna nome corretto | `new ContinuousAttribute("chilometri", 0)` → `getName()` | Costruttore, getter nome |
| `continuousAttribute_getIndex()` | Costruttore assegna indice corretto | `new ContinuousAttribute("prezzo", 3)` → `getIndex()` | Costruttore, getter indice |

**MANCA:** Test per `toString()`, `equals()`, `hashCode()`, iteratore, confronto valori

---

#### **DiscreteAttributeTest.java** (4 test)
| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `discreteAttribute_nameAndIndex()` | Costruttore assegna nome e indice | `new DiscreteAttribute("colore", 1, [...])` | Costruttore, getter base |
| `discreteAttribute_numberOfDistinctValues()` | Conta valori distinti correttamente | 3 valori → `getNumberOfDistinctValue() == 3` | Conteggio valori |
| `costruttore_eliminaValoriDuplicati()` | Deduplicazione valori in input | `["Fiat", "Fiat", "Ford"]` → 2 valori distinti | Deduplicazione costruttore |
| `discreteAttribute_iteratorReturnsValues()` | Iteratore scorre tutti i valori | `for (String v : attr)` count == 3 | `Iterable` implementation |

**MANCA:** Test per valori nulli, array vuoto, `getIndex()` out of bounds, `toString()`

---

#### **TrainingDataExceptionTest.java** (1 test)
| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `trainingDataException_message()` | Messaggio eccezione preservato | `new TrainingDataException("errore test")` → `getMessage()` | Costruttore messaggio |

**MANCA:** Test ereditarietà `Exception`, cause chaining

---

#### **DataTest.java** (12 test) - **TEST DI INTEGRAZIONE CON DB REALE**
> ⚠️ **Prerequisiti:** MySQL attivo su 127.0.0.1:3306, DB `MapDB`, utente `MapUser/map`, tabelle `provaC`, `provaC_singola`, `provaC_errata`, `empty_test`

| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `tabellaInesistente_throwsTrainingDataException()` | Costruttore lancia eccezione tabella inesistente | `new Data("tabella_inesistente")` → `TrainingDataException` | Validazione input costruttore |
| `tabellaConSingolaColonna_throwsTrainingDataException()` | Tabella con 1 colonna (manca target) | `new Data("provaC_singola")` → `TrainingDataException` | Validazione schema minimo |
| `targetNonNumerico_throwsTrainingDataException()` | Target non numerico lancia eccezione | `new Data("provaC_errata")` → `TrainingDataException` | Validazione tipo target |
| `identificaTipoColonnaCorrettamente()` | Riconosce attributi Discrete/Continuous | `getExplanatoryAttribute(0)` → `DiscreteAttribute`, `(1)` → `ContinuousAttribute` | Type inference da DB |
| `getExplanatoryValue_ritornaValoreNonNullo()` | Lettura valore esplicativo valido | `getExplanatoryValue(0, 0)` ≠ null | Lettura dati base |
| `getExplanatoryValue_throwsIndexOutOfBoundsException()` | Riga inesistente lancia eccezione | `getExplanatoryValue(20, 0)` → `IndexOutOfBoundsException` | Bounds checking |
| `getClassAttribute_attributoTargetCorretto()` | Identifica attributo classe target | `getClassAttribute()` → nome="C", index=2 | Target attribute metadata |
| `getClassValue_ritornaIndiceValido()` | Lettura valore target senza errori | `getClassValue(0)` no throw | Lettura target value |
| `ordinamentoCorretto_discreteAttribute()` | **Quicksort su attributo discreto** | `sort(attrX, 0, n-1)` → verifica ordine alfabetico | **Algoritmo Quicksort (discreto)** |
| `ordinamentoCorretto_continuousAttribute()` | **Quicksort su attributo continuo** | `sort(attrY, 0, n-1)` → verifica ordine numerico | **Algoritmo Quicksort (continuo)** |

**PUNTI DI FORZA:** Test dell'algoritmo di ordinamento Quicksort su dati reali (entrambi i tipi attributo)

**MANCA:** Test `getNumberOfExamples()`, `getNumberOfExplanatoryAttributes()`, `toString()`, partizioni vuote, sort su range parziali

---

### 2. Package `database` - Layer Database

#### **ColumnTest.java** (5 test)
| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `column_getName()` | Getter nome colonna | `new Column("eta", "number")` → `getName()` | Costruttore, getter |
| `column_isNumber_true()` | `isNumber()` true per type "number" | `new Column("eta", "number")` → `isNumber()` | Type mapping number |
| `column_isNumber_false()` | `isNumber()` false per type "string" | `new Column("nome", "string")` → `!isNumber()` | Type mapping string |
| `column_toString()` | Formato toString "nome:tipo" | `new Column("altezza", "number")` → `"altezza:number"` | `toString()` format |
| `column_unknownTypeIsNotNumber()` | Tipo sconosciuto → non numerico | `new Column("x", "date")` → `!isNumber()` | Unknown type handling |

**COPERTURA:** Buona - tutti i metodi pubblici testati

---

#### **DatabaseConnectionExceptionTest.java** (2 test)
| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `databaseConnectionException_message()` | Messaggio preservato | `new DatabaseConnectionException("conn failed")` | Costruttore messaggio |
| `databaseConnectionException_isException()` | Estende `Exception` | `assertInstanceOf(Exception.class, ...)` | Ereditarietà |

---

#### **EmptySetExceptionTest.java** (2 test) 
| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `emptySetException_message()` | Messaggio contiene "empty" | `new EmptySetException()` → `getMessage().contains("empty")` | Messaggio default |
| `emptySetException_isException()` | Estende `Exception` | `assertInstanceOf(Exception.class, ...)` | Ereditarietà |

---

#### **ExampleTest.java** (5 test)
| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `example_addAndGet()` | Add/get mantengono ordine | `add("sunny")`, `add(25.0)` → `get(0)`, `get(1)` | Add/get base |
| `example_toString()` | toString contiene valori | `add("hot")`, `add(30.0)` → `toString()` contains both | `toString()` |
| `example_compareTo_equal()` | `compareTo()` = 0 per esempi identici | Due `Example` identici → `compareTo() == 0` | `Comparable` equal |
| `example_compareTo_different()` | `compareTo()` ≠ 0 per esempi diversi | Valori diversi → `compareTo() != 0` | `Comparable` diff |
| `example_iterator()` | Iteratore scorre tutti elementi | `add("X","Y","Z")` → for-each count == 3 | `Iterable` |

**COPERTURA:** Buona - `List`-like behavior, `Comparable`, `Iterable`

---

#### **DbIntegrationTest.java** (12 test) - **INTEGRATION TESTS** `@Tag("integration")`
> ⚠️ **Prerequisiti:** MySQL localhost:3306, DB `MapDB`, user `MapUser/map`, tabella `playtennis` popolata

| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `dbAccess_connectionNotNull()` | `DbAccess.initConnection()` → connection ≠ null | `db.getConnection()` | Connessione DB |
| `dbAccess_connectionIsValid()` | Connessione valida (`isValid(2)`) | `getConnection().isValid(2)` | Validità connessione |
| `tableSchema_hasAttributes()` | Tabella esistente ha attributi | `new TableSchema(db, "playtennis")` → `getNumberOfAttributes() > 0` | Schema loading |
| `tableSchema_firstColumnNotNull()` | Prima colonna non null | `ts.getColumn(0)` ≠ null | Column access |
| `tableSchema_iteratorCoversAllColumns()` | Iteratore copre tutte colonne | `for (Column c : ts) count++` == `getNumberOfAttributes()` | `Iterable<Column>` |
| `tableSchema_nonExistentTableEmpty()` | Tabella inesistente → schema vuoto | `new TableSchema(db, "tabella_inesistente_xyz")` → 0 attributi | Error handling |
| `tableData_getTransazioni_notEmpty()` | `getTransazioni()` lista non vuota | `td.getTransazioni("playtennis")` → `!isEmpty()` | Data loading |
| `tableData_exampleSizeMatchesSchema()` | Ogni Example ha n elementi = colonne | `ex.get(ncols-1)` no throw per tutti | Schema-data consistency |
| `tableData_distinctValues_notEmpty()` | `getDistinctColumnValues()` non vuoto per colonna discreta | Trova colonna non-numerica → valori distinti ≠ ∅ | Distinct values |
| `tableData_emptyTable_throwsEmptySetException()` | Tabella vuota lancia `EmptySetException` | `td.getTransazioni("empty_test")` → throws | Empty table handling |

**PUNTI DI FORZA:** Test di integrazione reali con DB, coprono `DbAccess`, `TableSchema`, `TableData`

---

### 3. Package `tree` - Alberi di Regressione

#### **LeafNodeTest.java** (1 test) - **INTEGRATION TEST**
> ⚠️ **Prerequisiti:** Stessi di `DataTest` (DB reale `provaC`)

| Test | Cosa Testa | Come | Copertura |
|------|------------|------|-----------|
| `verificaCorrettezzaMatematica()` | **Media e Varianza foglia corrette** | Calcola manualmente media/varianza prime 3 righe → confronta con `LeafNode.getPredictedClassValue()` e `getVariance()` | **Calcolo statistico foglia (SSE)** |

**MOLTO IMPORTANTE:** Unico test che valida la **matematica core** dell'algoritmo (media/varianza/SSE)

---

#### **DiscreteNodeTest.java** (0 test) - **FILE VUOTO!** ⚠️
> **CLASSE SOTTO TEST:** `DiscreteNode` (estende `SplitNode`) - gestisce split su attributi discreti

**MANCA COMPLETAMENTE:** Nessun test per:
- Costruttore e partizionamento valori discreti
- Calcolo `splitVariance` (somma varianze figli)
- `getNumberOfChildren()` = numero valori distinti
- `getSplitInfo(i)` per ogni ramo
- `formulateQuery()` generazione query SQL-like
- `compareTo()` ordinamento per varianza
- Gestione valori non visti / unknown

---

#### **ContinuousNodeTest.java** - **MANCANTE** ⚠️
> **CLASSE SOTTO TEST:** `ContinuousNode` (estende `SplitNode`) - gestisce split su attributi continui (threshold)

**MANCA COMPLETAMENTE:** Nessun file di test per:
- Ricerca best split point (threshold) su attributo continuo
- Partizionamento ≤ threshold / > threshold
- Calcolo varianza per split continui
- Gestione edge cases (tutti valori uguali, single value)

---

#### **RegressionTreeTest.java** - **MANCANTE** ⚠️
> **CLASSE PRINCIPALE:** `RegressionTree` - algoritmo completo di induzione albero

**MANCA COMPLETAMENTE:** Nessun test per:
- Costruttore `RegressionTree(Data)` → `learnTree()`
- `isLeaf()` stopping criterion (10% esempi)
- `determineBestSplitNode()` selezione best attribute (TreeSet ordinato per varianza)
- Ricorsione `learnTree()` su rami figli
- `printTree()`, `toString()`, `printRules()`
- `getChild(index)` navigazione albero
- `getPredictedValue()` predizione foglia
- `salva()` / serializzazione
- Predizione end-to-end (commentato `predictClass()`)

---

#### **SplitNodeTest.java** - **MANCANTE** ⚠️
> **CLASSE BASE:** `SplitNode` (astratta) - logica comune split discreti/continui

**MANCA:** Test per:
- `setSplitInfo()` costruzione mappa partizioni
- `splitVariance` calcolo (somma varianze foglie figlie)
- `mapSplit` structure (SplitInfo: begin, end, comparator, splitValue)
- `compareTo()` per `TreeSet` ordering

---

#### **NodeTest.java** - **MANCANTE** ⚠️
> **CLASSE BASE ASTRATTA:** `Node` - campi comuni (id, range, variance)

**MANCA:** Test per:
- Costruttore calcola `variance` (SSE) correttamente
- `getIdNode()`, `getBeginExampleIndex()`, `getEndExampleIndex()`
- `getVariance()` restituisce SSE
- `getNumberOfChildren()` astratto
- `toString()` format

---

### 4. Package `server` - **ZERO TEST** ⚠️

| Classe | Descrizione | Test Mancanti |
|--------|-------------|---------------|
| `MultiServer` | Server socket multi-thread (porta 8080) | Avvio server, accept connessioni, gestione errori, shutdown |
| `ServerOneClient` | Handler per singolo client | Gestione richiesta, parsing query, risposta predizione, eccezioni |
| `UnknownValueException` | Eccezione input utente non valido | Messaggio, ereditarietà |

---

## 📊 MATRICE DI COPERTURA PER CLASSE SORGENTE

| Package | Classe Sorgente | Test Esistenti | Copertura Stimata | Priorità Mancanti |
|---------|----------------|----------------|-------------------|-------------------|
| **data** | `ContinuousAttribute` | 2/8 metodi | ~25% | Media |
| **data** | `DiscreteAttribute` | 4/10 metodi | ~40% | Media |
| **data** | `Attribute` (astratta) | 0 | 0% | - |
| **data** | `Data` | 10/15 metodi | ~65% | **Alta** (Quicksort testati ✓) |
| **data** | `TrainingDataException` | 1/2 | ~50% | Bassa |
| **database** | `Column` | 5/5 metodi | **100%** | - |
| **database** | `DbAccess` | 2/4 metodi | ~50% | Media (connection mgmt) |
| **database** | `TableSchema` | 5/6 metodi | ~85% | Bassa |
| **database** | `TableData` | 3/4 metodi | ~75% | Media |
| **database** | `Example` | 5/6 metodi | ~85% | Bassa |
| **database** | `DatabaseConnectionException` | 2/2 | **100%** | - |
| **database** | `EmptySetException` | 2/2 | **100%** | - |
| **tree** | `Node` (astratta) | 0 | **0%** | **Critica** |
| **tree** | `SplitNode` (astratta) | 0 | **0%** | **Critica** |
| **tree** | `DiscreteNode` | 0 | **0%** | **Critica** |
| **tree** | `ContinuousNode` | 0 | **0%** | **Critica** |
| **tree** | `LeafNode` | 1/4 metodi | ~25% | **Critica** |
| **tree** | `RegressionTree` | 0 | **0%** | **Critica** |
| **server** | `MultiServer` | 0 | **0%** | Media |
| **server** | `ServerOneClient` | 0 | **0%** | Media |
| **server** | `UnknownValueException` | 0 | **0%** | Bassa |

---

## 🎯 TEST MANCANTI - PRIORITÀ CRITICA

### **PRIORITÀ 1 - Core Algorithm (Regression Tree)**

| Classe | Test Mancanti Critici |
|--------|----------------------|
| **`RegressionTree`** | • `learnTree()` induzione ricorsiva completa<br>• `isLeaf()` stopping criterion (10%)<br>• `determineBestSplitNode()` selezione best attribute<br>• `printRules()` estrazione regole<br>• `getChild()` navigazione<br>• `getPredictedValue()` predizione<br>• Serializzazione `salva()` |
| **`DiscreteNode`** | • Costruttore partizionamento valori discreti<br>• `splitVariance` = somma varianze figli<br>• `getNumberOfChildren()` = n. valori distinti<br>• `getSplitInfo(i)` per ogni ramo<br>• `formulateQuery()` generazione condizioni<br>• `compareTo()` per TreeSet ordering |
| **`ContinuousNode`** | • Ricerca best threshold (midpoint tra valori ordinati)<br>• Partizionamento ≤ threshold / > threshold<br>• Calcolo varianza per split continui<br>• Edge case: tutti valori uguali |
| **`SplitNode`** | • `setSplitInfo()` costruzione `mapSplit`<br>• `SplitInfo` structure (begin, end, comparator, value)<br>• `splitVariance` calcolo |
| **`Node`** | • Costruttore calcola `variance` (SSE) su range<br>• Getters id, range, variance<br>• `toString()` format |

### **PRIORITÀ 2 - Data Layer (completamento)**

| Classe | Test Mancanti |
|--------|---------------|
| `Data` | `getNumberOfExamples()`, `getNumberOfExplanatoryAttributes()`, `toString()`, sort su sottorange, partizioni vuote |
| `ContinuousAttribute` | `toString()`, `equals()`/`hashCode()`, `compareTo()` |
| `DiscreteAttribute` | `toString()`, `equals()`/`hashCode()`, valori nulli, array vuoto |
| `Attribute` (astratta) | Test contratto interfaccia comune |

### **PRIORITÀ 3 - Database Layer (edge cases)**

| Classe | Test Mancanti |
|--------|---------------|
| `DbAccess` | `closeConnection()` idempotenza, riconnessione, timeout, exception handling |
| `TableSchema` | Tipi SQL non mappati, case sensitivity nomi tabelle/colonne |
| `TableData` | `getTransazioni()` con tabella inesistente (SQLException), colonne miste null/not-null |
| `Example` | `compareTo()` con tipi misti, `add()` null values, size() |

### **PRIORITÀ 4 - Server Layer**

| Classe | Test Necessari |
|--------|----------------|
| `MultiServer` | Avvio/stop server, gestione socket, concorrenza client multipli, graceful shutdown |
| `ServerOneClient` | Parsing request, invocazione `RegressionTree`, gestione `UnknownValueException`, risposta serializzata |
| `UnknownValueException` | Messaggio, ereditarietà |

---

## 🏷️ CLASSIFICAZIONE TEST ESISTENTI

| Categoria | File | Note |
|-----------|------|------|
| **Unit Test (puri, veloci)** | `ColumnTest`, `DatabaseConnectionExceptionTest`, `EmptySetExceptionTest`, `ExampleTest`, `ContinuousAttributeTest`, `DiscreteAttributeTest`, `TrainingDataExceptionTest` | Nessuna dipendenza esterna, eseguibili in isolamento |
| **Integration Test (DB reale)** | `DataTest`, `DbIntegrationTest`, `LeafNodeTest` | Richiedono MySQL attivo, tabelle specifiche, `@Tag("integration")` su `DbIntegrationTest` |
| **Algorithm Test** | `DataTest::ordinamentoCorretto_*` | Testano Quicksort su dati reali - **molto preziosi** |
| **Math Validation Test** | `LeafNodeTest::verificaCorrettezzaMatematica` | Unico test che valida matematica core (SSE) |

---

## 🚀 PIANO DI AZIONE CONSIGLIATO

### **Fase 1 - Fondamenta Tree (Critico)**
1. Creare `NodeTest.java` - test classe base astratta
2. Creare `SplitNodeTest.java` - test logica split comune
3. Creare `DiscreteNodeTest.java` - **RIEMPIRE IL FILE VUOTO**
4. Creare `ContinuousNodeTest.java` - **NUOVO FILE**
5. Creare `LeafNodeTest.java` - **AGGIUNGERE** test costruttore, getters, `getNumberOfChildren()==0`

### **Fase 2 - Algoritmo Completo (Critico)**
6. Creare `RegressionTreeTest.java` - **NUOVO FILE** (test end-to-end induzione albero)
   - Test con dataset piccolo controllato (mock Data o fixture)
   - Verifica struttura albero prodotta
   - Test `printRules()` output atteso
   - Test predizione su nuovi dati

### **Fase 3 - Completamento Data Layer (Media)**
7. Estendere `ContinuousAttributeTest`, `DiscreteAttributeTest`
8. Estendere `DataTest` (metodi non testati)

### **Fase 4 - Server Layer (Media)**
9. Creare `MultiServerTest.java`, `ServerOneClientTest.java` (richiedono mock socket o test container)

### **Fase 5 - Infrastructure**
10. Aggiungere `@Tag("unit")` ai test unitari puri
11. Configurare Maven/Gradle per separare unit vs integration tests
12. Aggiungere test fixtures / testcontainers per DB integration tests automatizzati

---

## 📝 NOTE TECNICHE IMPORTANTI

1. **Dipendenza DB Reale:** Molti test (`DataTest`, `DbIntegrationTest`, `LeafNodeTest`) richiedono MySQL attivo con schema specifico. Considerare **Testcontainers** per CI/CD.

2. **File Vuoto:** `Test/tree/DiscreteNodeTest.java` esiste ma è **completamente vuoto** (0 byte).

3. **Naming Convention:** Test usano italiano nei `@DisplayName` e commenti - mantenere coerenza.

4. **Quicksort Testato:** `DataTest` ha ottimi test per l'ordinamento (sia discreto che continuo) - **punto di forza attuale**.

5. **Math Validation:** `LeafNodeTest` valida SSE/media - **unico test matematico core**, estenderlo a `SplitNode`.

6. **Serializzazione:** `RegressionTree.salva()` usa Java serialization - testare round-trip.

7. **Server Non Testato:** Package `server` completamente privo di test - componente critico per deployment.

---

## 📈 METRICHE RIEPILOGATIVE

| Metrica | Valore |
|---------|--------|
| **File test totali** | 12 (1 vuoto) |
| **Classi sorgente testate** | 11/19 (58%) |
| **Classi sorgente NON testate** | 8/19 (42%) - *tutto tree + server* |
| **Metodi @Test totali** | ~50 |
| **Test Unitari puri** | ~35 |
| **Test Integrazione (DB)** | ~15 |
| **Copertura algoritmo core (tree)** | **~5%** (critico) |
| **Copertura data layer** | **~60%** |
| **Copertura database layer** | **~75%** |
| **Copertura server layer** | **0%** |

---

*Documento generato automaticamente dall'analisi del codebase MapServer - 2026-07-28*