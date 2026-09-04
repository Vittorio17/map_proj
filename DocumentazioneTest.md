# Documentazione Analisi Test - Progetto Map Regression Tree

> **Data analisi:** 2026-09-04  
> **Versione progetto:** commit a193d09 (main)  
> **Autore analisi:** Automated analysis

---

## 📋 Sommario Esecutivo

| Metrica | Valore |
|---------|--------|
| **File sorgenti Java (server)** | 20 file (~1809 righe) |
| **File sorgenti Java (client)** | 2 file (~230 righe) |
| **File di test esistenti** | 11 file (~632 righe) |
| **Copertura package `data`** | ✅ Buona (4 classi testate) |
| **Copertura package `database`** | ✅ Buona (5 classi testate) |
| **Copertura package `tree`** | ⚠️ Parziale (LeafNode sì, SplitNode/ContinuousNode/DiscreteNode/RegressionTree no) |
| **Copertura package `server`** | ❌ Assente |
| **Test di integrazione** | 1 file (`DbIntegrationTest` - richiede DB MySQL) |

---

## 🏗️ Architettura del Progetto

```
mapServer/
├── src/
│   ├── data/           # Core: Data, Attribute, ContinuousAttribute, DiscreteAttribute, TrainingDataException
│   ├── database/       # DB Access: DbAccess, TableData, TableSchema, Column, Example, EmptySetException, DatabaseConnectionException
│   ├── tree/           # ML: RegressionTree, Node, LeafNode, SplitNode, DiscreteNode, ContinuousNode, SplitInfo
│   └── server/         # Network: ServerOneClient, MultiServer, UnknownValueException
├── Test/
│   ├── data/           # 4 test files
│   ├── database/       # 5 test files (1 integration)
│   └── tree/           # 2 test files (1 vuoto)
├── lib/                # JUnit 1.10.2, MySQL Connector 8.0.17
└── RunTest.bat         # Esegue solo package 'data'
```

---

## ✅ Analisi Test Esistenti

### 1. Package `data` (4 file test)

| File Test | Classi Testate | # Test | Note |
|-----------|----------------|--------|------|
| `ContinuousAttributeTest.java` | `ContinuousAttribute` | 2 | Solo costruttore + getter name/index |
| `DiscreteAttributeTest.java` | `DiscreteAttribute` | 4 | Costruttore, dedup, iteratore, count |
| `TrainingDataExceptionTest.java` | `TrainingDataException` | 1 | Solo message getter |
| `DataTest.java` | `Data` | 8 | **Integration-style** - richiede MySQL + tabella `provaC` |

#### `DataTest.java` - Dettaglio (8 test)
| Test | Cosa Verifica | Tipo |
|------|---------------|------|
| `tabellaInesistente_throwsTrainingDataException` | Tabella non esistente → exception | Negativo |
| `tabellaConSingolaColonna_throwsTrainingDataException` | Tabella 1 colonna (manca target) → exception | Negativo |
| `targetNonNumerico_throwsTrainingDataException` | Target non numerico → exception | Negativo |
| `identificaTipoColonnaCorrettamente` | X=Discrete, Y=Continuous | Positivo |
| `getExplanatoryValue_ritornaValoreNonNullo` | Lettura valore valido | Positivo |
| `getExplanatoryValue_throwsIndexOutOfBoundsException` | Riga out-of-bounds → exception | Negativo |
| `getClassAttribute_attributoTargetCorretto` | Target C, index=2 | Positivo |
| `getClassValue_ritornaIndiceValido` | Lettura target senza errori | Positivo |
| `ordinamentoCorretto_discreteAttribute` | Quicksort alfabetico su X | Algoritmico |
| `ordinamentoCorretto_continuousAttribute` | Quicksort numerico su Y | Algoritmico |

> ⚠️ **Nota**: `DataTest` richiede database MySQL attivo con tabella `provaC` - NON è un unit test puro.

---

### 2. Package `database` (5 file test)

| File Test | Classi Testate | # Test | Note |
|-----------|----------------|--------|------|
| `ColumnTest.java` | `Column` | 5 | name, isNumber (true/false), toString, unknown type |
| `ExampleTest.java` | `Example` | 5 | add/get, toString, compareTo (equal/diff), iterator |
| `DatabaseConnectionExceptionTest.java` | `DatabaseConnectionException` | 2 | message, instanceof Exception |
| `EmptySetException.java` | `EmptySetException` | 2 | message contains "empty", instanceof Exception |
| `DbIntegrationTest.java` | `DbAccess`, `TableSchema`, `TableData` | 9 | **Integration** - @Tag("integration") |

#### `DbIntegrationTest.java` - Dettaglio (9 test @Tag integration)
| Test | Cosa Verifica |
|------|---------------|
| `dbAccess_connectionNotNull` | `initConnection()` produce connessione non nulla |
| `dbAccess_connectionIsValid` | `isValid(2)` restituisce true |
| `tableSchema_hasAttributes` | Tabella `playtennis` ha ≥1 attributi |
| `tableSchema_firstColumnNotNull` | `getColumn(0)` non null |
| `tableSchema_iteratorCoversAllColumns` | Iterator scorre tutte le colonne |
| `tableSchema_nonExistentTableEmpty` | Tabella inesistente → 0 attributi |
| `tableData_getTransazioni_notEmpty` | `getTransazioni("playtennis")` lista non vuota |
| `tableData_exampleSizeMatchesSchema` | Ogni Example ha size = num colonne schema |
| `tableData_distinctValues_notEmpty` | Colonna discreta ha valori distinti non vuoti |
| `tableData_emptyTable_throwsEmptySetException` | Tabella vuota `empty_test` → EmptySetException |

> ⚠️ **Prerequisiti**: MySQL localhost:3306, DB `MapDB`, user `MapUser/map`, tabella `playtennis` popolata, tabella `empty_test` vuota

---

### 3. Package `tree` (2 file test)

| File Test | Classi Testate | # Test | Note |
|-----------|----------------|--------|------|
| `leafNodeTest.java` | `LeafNode` | 1 | Media e varianza su prime 3 righe DB `provaC` |
| `DiscreteNodeTest.java` | *(vuoto - 0 test)* | 0 | File placeholder 1 riga |

#### `leafNodeTest.java` - Dettaglio (1 test)
| Test | Cosa Verifica |
|------|---------------|
| `verificaCorrettezzaMatematica` | LeafNode(begin=0,end=2) → media e varianza = calcolo manuale su y0,y1,y2 |

> ⚠️ **Nota**: Richiede database MySQL + tabella `provaC` - **NON unit test**

---

### 4. Package `server` - **ZERO TEST**

Nessun file di test per:
- `ServerOneClient`
- `MultiServer`
- `UnknownValueException`

---

### 5. Package `client` - **ZERO TEST**

Nessun file di test per:
- `MainTest` (classe main con logica socket)
- `Keyboard` (utility input parsing)

---

## 📊 Matrice Copertura per Classe Sorgente

| Package | Classe Sorgente | Test Unitari | Test Integrazione | Note |
|---------|-----------------|--------------|-------------------|------|
| **data** | `Attribute` (astratta) | ❌ | ❌ | Testata indirettamente via subclass |
|  | `ContinuousAttribute` | ✅ 2 test | - | Solo getter base |
|  | `DiscreteAttribute` | ✅ 4 test | - | Buona: costruttore, dedup, iteratore, count |
|  | `TrainingDataException` | ✅ 1 test | - | Solo message |
|  | `Data` | ⚠️ 8 test | ⚠️ 8 test | **Tutti integration-style** (richiedono DB) |
| **database** | `Column` | ✅ 5 test | - | Completo per class semplice |
|  | `Example` | ✅ 5 test | - | Completo per class semplice |
|  | `DatabaseConnectionException` | ✅ 2 test | - | Completo |
|  | `EmptySetException` | ✅ 2 test | - | Completo |
|  | `DbAccess` | ❌ | ✅ 2 test | Solo via integration test |
|  | `TableSchema` | ❌ | ✅ 4 test | Solo via integration test |
|  | `TableData` | ❌ | ✅ 4 test | Solo via integration test |
| **tree** | `Node` (astratta) | ❌ | ❌ | Testata indirettamente |
|  | `LeafNode` | ⚠️ 1 test | ⚠️ 1 test | Solo media/varianza - **richiede DB** |
|  | `SplitNode` (astratta) | ❌ | ❌ | **NESSUN TEST DIRETTO** |
|  | `DiscreteNode` | ❌ | ❌ | File test vuoto |
|  | `ContinuousNode` | ❌ | ❌ | **NESSUN TEST** |
|  | `SplitNode.SplitInfo` | ❌ | ❌ | Inner class non testata |
|  | `RegressionTree` | ❌ | ❌ | **CLASSE PRINCIPALE - ZERO TEST** |
| **server** | `ServerOneClient` | ❌ | ❌ | Logica socket + protocollo |
|  | `MultiServer` | ❌ | ❌ | Main server |
|  | `UnknownValueException` | ❌ | ❌ | Exception custom |
| **client** | `MainTest` | ❌ | ❌ | Client main + protocollo |
|  | `Keyboard` | ❌ | ❌ | Utility parsing input |

---

## 🔴 Gap Critici - Test Mancanti Prioritari

### Priority 1: CORE ALGORITMO (Regression Tree)

| Classe | Metodi Chiave Non Testati | Perché Critico |
|--------|---------------------------|----------------|
| **`RegressionTree`** | `learnTree()`, `determineBestSplitNode()`, `isLeaf()`, `printTree()`, `toString()`, `getCurrentNodeQuery()`, `getChild()`, `getPredictedValue()`, `printRules()`, `salva()`, `carica()` | **CLASSE CENTRALE** - nessun test |
| **`SplitNode`** | `setSplitInfo()` (astratto), `testCondition()` (astratto), `getVariance()`, `getNumberOfChildren()`, `getSplitInfo()`, `formulateQuery()`, `compareTo()` | Base per Discrete/ContinuousNode |
| **`DiscreteNode`** | `setSplitInfo()`, `testCondition()`, `toString()` | Split discreti - core algorithm |
| **`ContinuousNode`** | `setSplitInfo()` (logica best split variance), `testCondition()` (<=/>) | Split continui - core algorithm |
| **`LeafNode`** | `getPredictedClassValue()`, `getNumberOfChildren()` | Solo 1 test integration-style |

### Priority 2: DATA PROCESSING (Unit Test Puri)

| Classe | Metodi Chiave Non Testati | Perché Importante |
|--------|---------------------------|-------------------|
| **`Data`** | `sort()`, `quicksort()`, `partition(DiscreteAttribute)`, `partition(ContinuousAttribute)`, `swap()`, `getExplanatoryValue()`, `getClassValue()`, `getExplanatoryAttribute()`, `getClassAttribute()`, `getNumberOfExamples()`, `getNumberOfExplanatoryAttributes()`, `toString()` | **Tutti i test attuali richiedono DB** - servono unit test con mock data |
| **`DiscreteAttribute`** | `iterator()` edge cases, empty values, single value | Già 4 test - aggiungere edge cases |

### Priority 3: DATABASE LAYER (Unit Test con Mock)

| Classe | Metodi Chiave Non Testati | Strategia |
|--------|---------------------------|-----------|
| **`DbAccess`** | `initConnection()`, `getConnection()`, `closeConnection()` | Mock DriverManager / in-memory DB (H2) |
| **`TableSchema`** | Costruttore, `getNumberOfAttributes()`, `getColumn()`, `iterator()` | Mock `DbAccess` + `Connection` |
| **`TableData`** | `getTransazioni()`, `getDistinctColumnValues()` | Mock `DbAccess` + `ResultSet` |

### Priority 4: SERVER/NETWORK

| Classe | Metodi Chiave Non Testati | Strategia |
|--------|---------------------------|-----------|
| **`ServerOneClient`** | `run()` switch cases 0,1,2,3, exception handling | Mock Socket + ObjectStreams |
| **`MultiServer`** | `main()`, accept loop | Integration test con test container |
| **`UnknownValueException`** | Costruttore, message | Semplice unit test |

### Priority 5: CLIENT

| Classe | Metodi Chiave Non Testati | Strategia |
|--------|---------------------------|-----------|
| **`Keyboard`** | `readString()`, `readWord()`, `readBoolean()`, `readChar()`, `readInt()`, `readLong()`, `readFloat()`, `readDouble()`, error handling | Mock `System.in` con `ByteArrayInputStream` |
| **`MainTest`** | `main()` protocollo socket | Integration test con server mock |

---

## 🎯 Piano Dettagliato Test Da Aggiungere

### A. Unit Test Puri per `tree` package (Nessun DB richiesto)

#### `SplitNodeTest.java` (nuovo)
```java
// Test per classe astratta - usa sottoclasse concreta per testare metodi comuni
- testGetVariance_returnsSplitVariance()
- testGetNumberOfChildren_returnsMapSplitSize()
- testGetSplitInfo_returnsCorrectSplitInfo()
- testFormulateQuery_returnsFormattedString()
- testCompareTo_lowerVarianceReturnsNegative()
- testCompareTo_higherVarianceReturnsPositive()
- testCompareTo_equalVarianceReturnsZero()
- testToString_containsAttributeNameAndVariance()
```

#### `DiscreteNodeTest.java` (riempire file vuoto)
```java
- testSetSplitInfo_createsCorrectPartitionsForDiscreteValues()
- testSetSplitInfo_handlesSingleValuePartition()
- testTestCondition_exactMatchReturnsChildIndex()
- testTestCondition_noMatchReturnsMinusOne()
- testTestCondition_multipleValuesCorrectIndex()
- testToString_containsDiscreteLabelAndChildren()
```

#### `ContinuousNodeTest.java` (nuovo)
```java
- testSetSplitInfo_findsBestSplitMinimizingVariance()
- testSetSplitInfo_handlesDuplicateValues()
- testSetSplitInfo_removesUselessSplitSingleElementPartition()
- testTestCondition_lessThanOrEqualReturnsChild0()
- testTestCondition_greaterThanReturnsChild1()
- testTestCondition_noMatchReturnsMinusOne()
- testToString_containsContinuousLabelAndOperators()
```

#### `LeafNodeTest.java` (convertire a unit test puro)
```java
// Attuale: integration test con DB provaC
// NUOVI - unit test con Data mockato:
- testConstructor_calculatesMeanCorrectly()
- testConstructor_calculatesVarianceCorrectly()
- testGetPredictedClassValue_returnsMean()
- testGetNumberOfChildren_returnsZero()
- testToString_containsLeafLabelAndClassValue()
- testConstructor_singleExample_varianceZero()
- testConstructor_allSameValues_varianceZero()
```

#### `RegressionTreeTest.java` (nuovo - **PIÙ IMPORTANTE**)
```java
// Costruzione albero
- testConstructor_learnTreeCreatesRootNode()
- testConstructor_learnTreeCreatesChildTreesWhenSplit()
- testConstructor_learnTreeCreatesLeafWhenIsLeaf()
- testIsLeaf_trueWhenExamplesBelowThreshold()
- testIsLeaf_falseWhenExamplesAboveThreshold()

// determineBestSplitNode
- testDetermineBestSplitNode_selectsLowestVarianceSplit()
- testDetermineBestSplitNode_sortsDataByBestAttribute()
- testDetermineBestSplitNode_returnsNullWhenNoAttributes()

// Navigazione e predizione
- testGetCurrentNodeQuery_returnsNullForLeaf()
- testGetCurrentNodeQuery_returnsFormattedQueryForSplit()
- testGetChild_validIndexReturnsChildTree()
- testGetChild_invalidIndexThrowsUnknownValueException()
- testGetPredictedValue_returnsValueForLeaf()
- testGetPredictedValue_returnsNullForSplitNode()

// Serializzazione
- testSalva_carica_roundTripPreservesTreeStructure()
- testSalva_carica_preservesPredictedValues()

// Rules printing
- testPrintRules_outputsCorrectFormat()
```

#### `SplitNode_SplitInfoTest.java` (nuovo - inner class)
```java
- testSplitInfo_discreteConstructor_setsAllFields()
- testSplitInfo_continuousConstructor_setsComparator()
- testGetBeginIndex_returnsCorrectValue()
- testGetEndIndex_returnsCorrectValue()
- testGetSplitValue_returnsCorrectValue()
- testGetComparator_returnsEqualsForDiscrete()
- testGetComparator_returnsLessOrEqualGreaterForContinuous()
- testToString_containsExampleRange()
```

---

### B. Unit Test Puri per `data` package (Mock Data, no DB)

#### `DataUnitTest.java` (nuovo - separato da DataTest integration)
```java
// Usa reflection o package-private per iniettare List<Example> mock
// Costruttore package-private o factory method per testing

// getExplanatoryValue
- testGetExplanatoryValue_validIndicesReturnsCorrectValue()
- testGetExplanatoryValue_invalidRowIndexThrowsException()
- testGetExplanatoryValue_invalidAttrIndexThrowsException()

// getClassValue
- testGetClassValue_validIndexReturnsDouble()
- testGetClassValue_invalidIndexThrowsException()

// getExplanatoryAttribute
- testGetExplanatoryAttribute_validIndexReturnsAttribute()
- testGetExplanatoryAttribute_invalidIndexThrowsException()

// getClassAttribute
- testGetClassAttribute_returnsContinuousAttributeWithCorrectNameIndex()

// sort / quicksort / partition (DiscreteAttribute)
- testSort_discreteAttribute_sortsAlphabetically()
- testSort_discreteAttribute_emptyRangeNoOp()
- testSort_discreteAttribute_singleElementNoOp()
- testSort_discreteAttribute_alreadySortedNoChange()
- testSort_discreteAttribute_reverseSortedBecomesSorted()
- testPartition_discreteAttribute_returnsCorrectPivotIndex()

// sort / quicksort / partition (ContinuousAttribute)
- testSort_continuousAttribute_sortsNumerically()
- testSort_continuousAttribute_withDuplicates()
- testSort_continuousAttribute_negativeValues()
- testPartition_continuousAttribute_returnsCorrectPivotIndex()

// Edge cases quicksort
- testQuicksort_largeDataset_performsCorrectly()
- testQuicksort_allEqualValues_noInfiniteLoop()

// toString
- testToString_containsAllExamplesWithIndices()
```

#### `AttributeTest.java` (nuovo - per classe astratta)
```java
// Test via subclass concrete
- testGetName_returnsConstructorName()
- testGetIndex_returnsConstructorIndex()
```

#### `ContinuousAttributeTest.java` (estendere esistente)
```java
// Aggiungere ai 2 test esistenti:
- testEquals_sameNameIndexReturnsTrue() // se implements equals
- testHashCode_consistentWithEquals()   // se implements hashCode
```

#### `DiscreteAttributeTest.java` (estendere esistente)
```java
// Aggiungere ai 4 test esistenti:
- testConstructor_emptyValuesArray_zeroDistinctValues()
- testConstructor_nullValuesArray_throwsNPE()
- testIterator_emptySet_noIteration()
- testIterator_singleValue_oneIteration()
- testGetNumberOfDistinctValue_afterDedup_correctCount()
```

#### `TrainingDataExceptionTest.java` (estendere esistente)
```java
// Aggiungere al 1 test esistente:
- testConstructor_nullMessage_handlesGracefully()
- testConstructor_emptyMessage_returnsEmptyString()
- testCause_canWrapUnderlyingException()
```

---

### C. Unit Test per `database` package (Mock JDBC)

#### `DbAccessUnitTest.java` (nuovo)
```java
// Usa Mockito o H2 in-memory database
- testInitConnection_success_loadsDriverAndConnects()
- testInitConnection_driverNotFound_throwsDatabaseConnectionException()
- testInitConnection_invalidCredentials_throwsDatabaseConnectionException()
- testGetConnection_beforeInit_returnsNull()
- testGetConnection_afterInit_returnsConnection()
- testCloseConnection_closesConnectionAndPrintsMessage()
- testCloseConnection_nullConnection_noOp()
- testCloseConnection_sqlException_handlesGracefully()
```

#### `TableSchemaUnitTest.java` (nuovo)
```java
// Mock Connection, DatabaseMetaData, ResultSet
- testConstructor_existingTable_populatesColumns()
- testConstructor_nonExistentTable_zeroAttributes()
- testGetNumberOfAttributes_returnsColumnCount()
- testGetColumn_validIndex_returnsColumn()
- testGetColumn_invalidIndex_throwsException()
- testIterator_iteratesAllColumns()
```

#### `TableDataUnitTest.java` (nuovo)
```java
// Mock Connection, Statement, ResultSet
- testGetTransazioni_populatesExamplesWithCorrectTypes()
- testGetTransazioni_emptyResultSet_throwsEmptySetException()
- testGetTransazioni_sqlException_propagates()
- testGetDistinctColumnValues_numericColumn_returnsSortedDoubles()
- testGetDistinctColumnValues_stringColumn_returnsSortedStrings()
- testGetDistinctColumnValues_sqlException_propagates()
```

#### `ColumnTest.java` (estendere - già 5 test, buono)
```java
// Edge cases
- testConstructor_nullName_handlesGracefully()
- testConstructor_nullType_treatsAsNonNumber()
- testIsNumber_caseInsensitiveNumber()
- testToString_specialCharactersInName()
```

#### `ExampleTest.java` (estendere - già 5 test, buono)
```java
// Edge cases
- testAdd_nullValue_storesNull()
- testGet_negativeIndex_throwsException()
- testGet_indexEqualSize_throwsException()
- testCompareTo_differentSizes_handlesGracefully()
- testIterator_emptyExample_zeroIterations()
- testToString_emptyExample_emptyString()
```

---

### D. Test per `server` package

#### `UnknownValueExceptionTest.java` (nuovo)
```java
- testConstructor_messageStoredCorrectly()
- testInstanceOf_Exception()
- testGetMessage_returnsConstructorMessage()
```

#### `ServerOneClientTest.java` (nuovo - richiede mock socket)
```java
// Mock Socket, ObjectInputStream, ObjectOutputStream
- testRun_case0_dataAcquisitionSuccess_sendsOK()
- testRun_case0_dataAcquisitionFailure_sendsErrorMessage()
- testRun_case1_treeConstruction_savesFileAndSendsOK()
- testRun_case2_loadTreeSuccess_sendsOK()
- testRun_case2_loadTreeFailure_sendsErrorMessage()
- testRun_case3_predictionLeaf_sendsOKAndValue()
- testRun_case3_predictionSplit_sendsQUERYThenNavigates()
- testRun_case3_unknownValueException_sendsErrorMessage()
- testRun_defaultCase_ignored()
- testRun_clientDisconnect_closesResources()
- testRun_ioExceptionOnRead_closesResources()
```

#### `MultiServerTest.java` (nuovo - integration style)
```java
// Avvia server su porta random, connetti client, verifica handshake
- testMain_startsServerOnPort()
- testAcceptLoop_createsServerOneClientPerConnection()
```

---

### E. Test per `client` package

#### `KeyboardTest.java` (nuovo - **FACILE, alto valore**)
```java
// Mock System.in con ByteArrayInputStream
- testReadString_readsFullLine()
- testReadString_emptyInput_returnsEmptyString()
- testReadWord_readsSingleToken()
- testReadWord_multipleTokens_readsFirst()
- testReadBoolean_trueCaseInsensitive()
- testReadBoolean_falseCaseInsensitive()
- testReadBoolean_invalid_returnsFalseAndIncrementsError()
- testReadChar_singleChar_returnsChar()
- testReadChar_multiChar_returnsFirstAndBuffersRest()
- testReadInt_validInteger_returnsInt()
- testReadInt_invalid_returnsMinValueAndIncrementsError()
- testReadLong_validLong_returnsLong()
- testReadFloat_validFloat_returnsFloat()
- testReadDouble_validDouble_returnsDouble()
- testGetErrorCount_incrementsOnErrors()
- testResetErrorCount_resetsToZero()
- testSetPrintErrors_truePrintsErrors()
- testSetPrintErrors_falseSuppressesErrors()
- testEndOfLine_noTokens_returnsTrue()
```

#### `MainTestTest.java` (nuovo - integration con mock server)
```java
// Avvia mock server, connetti client, verifica protocollo
- testMain_learnTree_sendsCorrectProtocolSequence()
- testMain_loadTree_sendsCorrectProtocolSequence()
- testMain_predictionPhase_handlesQUERYAndOK()
- testMain_invalidHost_printsErrorAndExits()
- testMain_connectionRefused_printsErrorAndExits()
```

---

## 🛠️ Infrastruttura Test Necessaria

### Dipendenze da Aggiungere (Maven/Gradle consigliato)

```xml
<!-- pom.xml suggerito -->
<dependencies>
    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-params</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Mocking -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- In-memory DB per integration test -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.224</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers per integration test reali (opzionale) -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <version>1.19.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Struttura Directory Test Consigliata

```
mapServer/
├── src/test/java/
│   ├── data/
│   │   ├── DataUnitTest.java           # NEW - pure unit tests
│   │   ├── ContinuousAttributeTest.java
│   │   ├── DiscreteAttributeTest.java
│   │   ├── AttributeTest.java          # NEW
│   │   └── TrainingDataExceptionTest.java
│   ├── database/
│   │   ├── ColumnTest.java
│   │   ├── ExampleTest.java
│   │   ├── DatabaseConnectionExceptionTest.java
│   │   ├── EmptySetExceptionTest.java  # rinominato da EmptySetException.java
│   │   ├── DbAccessUnitTest.java       # NEW
│   │   ├── TableSchemaUnitTest.java    # NEW
│   │   ├── TableDataUnitTest.java      # NEW
│   │   └── DbIntegrationTest.java      # esistente - @Tag integration
│   ├── tree/
│   │   ├── LeafNodeTest.java           # rifattorizzato unit
│   │   ├── DiscreteNodeTest.java       # NEW - riempire vuoto
│   │   ├── ContinuousNodeTest.java     # NEW
│   │   ├── SplitNodeTest.java          # NEW
│   │   ├── SplitNode_SplitInfoTest.java # NEW
│   │   └── RegressionTreeTest.java     # NEW - CRITICO
│   ├── server/
│   │   ├── UnknownValueExceptionTest.java  # NEW
│   │   ├── ServerOneClientTest.java        # NEW
│   │   └── MultiServerTest.java            # NEW
│   └── client/                          # NEW package
│       ├── KeyboardTest.java            # NEW
│       └── MainTestTest.java            # NEW
```

### Configurazione Esecuzione Test

```xml
<!-- surefire config per separare unit vs integration -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <excludedGroups>integration</excludedGroups>
    </configuration>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals><goal>integration-test</goal></goals>
        </execution>
    </executions>
    <configuration>
        <groups>integration</groups>
    </configuration>
</plugin>
```

**Comandi:**
```bash
# Solo unit test (veloci, no DB)
mvn test

# Solo integration test (richiedono DB/MySQL)
mvn verify -Dgroups=integration

# Tutti
mvn verify
```

---

## 📈 Metriche di Qualità Target

| Metrica | Attuale | Target |
|---------|---------|--------|
| **Line Coverage (unit)** | ~15% | ≥ 80% |
| **Branch Coverage (unit)** | ~10% | ≥ 70% |
| **Classi coperte da unit test** | 9/22 (41%) | 20/22 (91%) |
| **Test esecuzione < 5s (unit)** | N/A | 100% |
| **Test deterministici (no flaky)** | N/A | 100% |

---

## 🚀 Ordine di Implementazione Consigliato

### Sprint 1 - Foundation (Week 1)
1. ✅ Setup Maven/Gradle + JUnit 5 + Mockito
2. ✅ `KeyboardTest.java` - facile, alto valore, no dipendenze
3. ✅ `UnknownValueExceptionTest.java` - banale
3. ✅ Rinominare `EmptySetException.java` → `EmptySetExceptionTest.java`
4. ✅ Riempire `DiscreteNodeTest.java` (attualmente vuoto)

### Sprint 2 - Core Algorithm (Week 2)
5. ✅ `SplitNodeTest.java` (base per Discrete/Continuous)
6. ✅ `ContinuousNodeTest.java` (logica best split critica)
7. ✅ `DiscreteNodeTest.java` completare
8. ✅ `LeafNodeTest.java` convertire a unit test puro
9. ✅ `SplitNode_SplitInfoTest.java`

### Sprint 3 - Heart of System (Week 3)
10. ✅ **`RegressionTreeTest.java`** - **PIÙ IMPORTANTE**
11. ✅ `DataUnitTest.java` - separare da integration test
12. ✅ `AttributeTest.java`

### Sprint 4 - Database Layer (Week 4)
13. ✅ `DbAccessUnitTest.java` (con H2 o Mockito)
14. ✅ `TableSchemaUnitTest.java`
15. ✅ `TableDataUnitTest.java`

### Sprint 5 - Server/Client (Week 5)
16. ✅ `ServerOneClientTest.java`
17. ✅ `MultiServerTest.java` (integration)
18. ✅ `MainTestTest.java` (integration con mock server)
19. ✅ Estendere test esistenti con edge cases

### Sprint 6 - Polish & CI (Week 6)
20. ✅ Configurare CI (GitHub Actions / GitLab CI)
21. ✅ JaCoCo coverage reports
22. ✅ Mutation testing (PITest)
23. ✅ Documentazione README testing

---

## 📝 Convenzioni di Naming Test

```java
// Pattern: methodName_scenario_expectedBehavior
// Esempi:
testGetExplanatoryValue_validIndex_returnsCorrectValue()
testGetExplanatoryValue_invalidRowIndex_throwsIndexOutOfBoundsException()
testLearnTree_sufficientExamples_createsSplitNode()
testLearnTree_insufficientExamples_createsLeafNode()

// DisplayName in italiano (come esistenti) o inglese
@DisplayName("Verifica che getExplanatoryValue lancia eccezione per riga inesistente")
@DisplayName("Verify getExplanatoryValue throws for invalid row index")

// Tag per categorizzazione
@Tag("unit")           // default, veloci
@Tag("integration")    // richiedono DB/servizi esterni
@Tag("slow")           // > 1 secondo
```

---

## 🔍 Test Esistenti da Refactorare

| File | Problema | Azione |
|------|----------|--------|
| `DataTest.java` | Tutti integration-style, richiedono MySQL | **Split in 2**: `DataUnitTest.java` (mock) + `DataIntegrationTest.java` (DB) |
| `leafNodeTest.java` | Integration style, package lowercase `tree` vs `Tree` | Rinominare `LeafNodeTest.java`, convertire a unit test |
| `DiscreteNodeTest.java` | File vuoto (1 riga) | Implementare completamente |
| `EmptySetException.java` | Nome file sbagliato (manca `Test`) | Rinominare `EmptySetExceptionTest.java` |

---

## 📋 Checklist Finale

- [ ] Setup build system (Maven/Gradle)
- [ ] Separare unit vs integration test
- [ ] Implementare 18 nuovi file test (vedi sopra)
- [ ] Refactorare 3 file test esistenti
- [ ] Aggiungere Mockito + H2 dependencies
- [ ] Configurare CI pipeline
- [ ] Aggiungere coverage reporting (JaCoCo)
- [ ] Documentare come eseguire test in README
- [ ] Target: 80% line coverage, 70% branch coverage

---

## 📎 Riferimenti Codice

- **RunTest.bat attuale**: Esegue solo `--select-package=data` → **limitato**
- **JUnit version**: 1.10.2 (vecchio - aggiornare a 5.10.x)
- **MySQL Connector**: 8.0.17 (aggiornare per compatibilità)
- **Package structure**: `tree` (lowercase) vs `Test/tree` - inconsistente

---

*Documento generato automaticamente dall'analisi del codebase. Aggiornare man mano che i test vengono implementati.*