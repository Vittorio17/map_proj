# Documentazione dei Test - MapClient

## Package: utility
### Classe Testata: Keyboard

---

### Test 1 (testReadIntConInputNonValido)

**Cosa testa:** Il comportamento difensivo di `readInt()` in caso di input non numerico.

**Descrizione:** Inserendo la stringa "ciao" come input per `readInt()`, il test verifica che il metodo restituisca esattamente `Integer.MIN_VALUE` (-2147483648) e che il contatore degli errori venga incrementato di 1. Questo conferma che l'eccezione `NumberFormatException` viene catturata internamente senza propagarsi al chiamante, implementando l'architettura difensiva della classe Keyboard.

**Input:** "ciao\n"

**Output atteso:**
- Valore restituito: `Integer.MIN_VALUE` (-2147483648)
- Incremento contatore errori: +1

---

### Test 2 (testReadDoubleConInputNonValido)

**Cosa testa:** Il comportamento difensivo di `readDouble()` in caso di input non numerico.

**Descrizione:** Inserendo la stringa "ciao" come input per `readDouble()`, il test verifica che il metodo restituisca `Double.NaN` e che il contatore degli errori venga incrementato. Questo conferma che l'eccezione `NumberFormatException` viene gestita internamente senza interrompere il flusso del programma.

**Input:** "ciao\n"

**Output atteso:**
- Valore restituito: `Double.NaN`
- Incremento contatore errori: +1

---

### Test 3 (testReadCharConInputNonValido)

**Cosa testa:** Il comportamento di `readChar()` in caso di errore di lettura.

**Descrizione:** Verifica che in caso di errore durante la lettura di un carattere, il metodo restituisca `Character.MIN_VALUE` e incrementi il contatore errori, mantenendo la coerenza con gli altri metodi della classe.

**Input:** Input che causa eccezione

**Output atteso:**
- Valore restituito: `Character.MIN_VALUE`
- Incremento contatore errori: +1

---

### Test 4 (testIncrementoContatoreErroriMultiplo)

**Cosa testa:** La corretta accumulazione del contatore errori attraverso errori consecutivi.

**Descrizione:** Eseguendo due letture errate consecutive (prima `readInt()` poi `readDouble()` con input non validi), il test verifica che il contatore degli errori venga incrementato correttamente a 2, dimostrando che il contatore mantiene lo stato tra diverse chiamate ai metodi della classe.

**Input:** "errore1\n" per readInt, "errore2\n" per readDouble

**Output atteso:**
- Contatore errori finale: 2

---

### Test 5 (testReadIntConInputValido)

**Cosa testa:** Il corretto funzionamento di `readInt()` con input valido.

**Descrizione:** Inserendo il valore numerico "42", il test verifica che `readInt()` restituisca correttamente il valore intero 42 e che il contatore degli errori non venga incrementato, confermando che il comportamento normale non è influenzato dalla gestione degli errori.

**Input:** "42\n"

**Output atteso:**
- Valore restituito: 42
- Contatore errori: invariato (0)

---

### Test 6 (testResetErrorCountIgnoraParametro)

**Cosa testa:** L'anomalia del metodo `resetErrorCount(int count)` che ignora il parametro passato.

**Descrizione:** Questo test evidenzia un comportamento controintuitivo del metodo `resetErrorCount(int count)`. Nonostante la firma del metodo accetti un parametro intero `count`, l'implementazione ignora completamente questo valore e reimposta sempre il contatore a 0. Il test dimostra questa anomalia chiamando il metodo con valori diversi (10 e -1) e verificando che in entrambi i casi il contatore viene reimpostato a 0. Questo rappresenta un caso di design dubbio: il parametro suggerisce una funzionalità che non esiste, potendo confondere gli utilizzatori della classe.

**Input:** 
- Generazione di errori per avere `errorCount > 0`
- `resetErrorCount(10)` 
- Generazione di altri errori
- `resetErrorCount(-1)`

**Output atteso:**
- Dopo `resetErrorCount(10)`: contatore errori = 0 (non 10)
- Dopo `resetErrorCount(-1)`: contatore errori = 0 (non -1 o errore)

**Anomalia riscontrata:** Il parametro `count` è mantenuto nella firma del metodo probabilmente per compatibilità con versioni precedenti, ma non viene utilizzato. Il Javadoc alla linea 34 di `Keyboard.java` documenta questo comportamento con la dicitura "parametro non utilizzato mantenuto per compatibilità".

---

### Test 7 (testReadWordSingoloToken)

**Cosa testa:** La corretta estrazione di un singolo token tramite `readWord()`.

**Descrizione:** Il test verifica che `readWord()` utilizzi `StringTokenizer` per estrarre solo il primo token delimitato da spazi bianchi. Con input "Hello World", il metodo deve restituire "Hello" e non l'intera riga, dimostrando che la delimitazione avviene correttamente.

**Input:** "Hello World\n"

**Output atteso:**
- Valore restituito: "Hello"
- Contatore errori: 0

---

### Test 8 (testReadStringConcatenaToken)

**Cosa testa:** La concatenazione di tutti i token fino a fine riga tramite `readString()`.

**Descrizione:** Il test verifica che `readString()` recuperi il primo token e poi iteri concatenando tutti i token successivi fino a quando `endOfLine()` restituisce true. Con input "Hello World Test", il metodo deve restituire "HelloWorldTest" (senza spazi, poiché i delimitatori vengono saltati durante l'iterazione).

**Input:** "Hello World Test\n"

**Output atteso:**
- Valore restituito: "HelloWorldTest"
- Contatore errori: 0

---

### Test 9 (testReadCharConBuffer)

**Cosa testa:** Il meccanismo di buffering in `readChar()` che salva i caratteri rimanenti in `current_token`.

**Descrizione:** Il test dimostra che `readChar()` estrae il primo carattere e memorizza la sottostringa rimanente nella variabile statica `current_token` per le successive operazioni di lettura. Con input "ab\ncd\n":
- Prima chiamata: estrae 'a', salva "b" nel buffer
- Seconda chiamata: legge 'b' dal buffer senza consumare nuovo input
- Terza chiamata: consuma la riga successiva e legge 'c'

Questo meccanismo permette letture carattere per carattere senza perdere i dati in eccesso.

**Input:** "ab\ncd\n"

**Output atteso:**
- Prima lettura: 'a'
- Seconda lettura: 'b' (dal buffer)
- Terza lettura: 'c' (nuova riga)
- Contatore errori: 0

---

## Package: (default)
### Classe Testata: MainTest

---

### Test 1 (testSelezioneOpzioneLearn)

**Cosa testa:** La corretta gestione dell'input per l'opzione di apprendimento da database.

**Descrizione:** Simula l'utente che seleziona l'opzione 1 (Learn Regression Tree from data) e inserisce il nome della tabella. Il test utilizza `System.setIn(new ByteArrayInputStream(...))` per iniettare un flusso di input fittizio, automatizzando il collaudo senza necessità di presidio manuale. Verifica che i valori letti corrispondano alle aspettative.

**Input:** "1\ntestTable\nn\n"

**Output atteso:**
- Decision: 1
- TableName: "testTable"
- Risposta: 'n'

**Nota tecnica:** Il test completo richiederebbe un server socket attivo. Qui si verifica solo la corretta lettura dell'input.

---

### Test 2 (testSelezioneOpzioneLoad)

**Cosa testa:** La corretta gestione dell'input per l'opzione di caricamento da archivio.

**Descrizione:** Simula l'utente che seleziona l'opzione 2 (Load Regression Tree from archive) e inserisce il nome del file. Il test verifica che l'input venga correttamente interpretato come intero per la decisione e come stringa per il nome file.

**Input:** "2\narchiveFile\n"

**Output atteso:**
- Decision: 2
- FileName: "archiveFile"

---

### Test 3 (testCicloIterazioneRipeti)

**Cosa testa:** Il ciclo di iterazione con risposta affermativa per ripetere la predizione.

**Descrizione:** Simula l'utente che risponde 'y' per ripetere la predizione e successivamente 'n' per terminare. Verifica che il meccanismo di lettura caratteri gestisca correttamente le risposte consecutive.

**Input:** "y\nn\n"

**Output atteso:**
- Prima risposta: 'y'
- Seconda risposta: 'n'

---

### Test 4 (testSelezioneNonValidaPoiCorretta)

**Cosa testa:** La gestione di input non validi seguiti da input corretti.

**Descrizione:** Simula l'utente che inserisce valori non validi (3 e una stringa "abc") prima di inserire il valore corretto (1). Verifica che il sistema continui a leggere l'input fino a ottenere un valore valido, dimostrando la resilienza del ciclo di input.

**Input:** "3\nabc\n1\ntestTable\n"

**Output atteso:**
- Primo input: 3 (non valido nel contesto del ciclo do-while)
- Secondo input: 1 (valido)
- TableName: "testTable"

**Nota:** Il contatore errori potrebbe essere incrementato per l'input non valido, ma il sistema non si blocca.

---

## Strategia di Testing per MainTest

La classe `MainTest` presenta una sfida particolare per il testing automatizzato: richiede una connessione socket attiva per funzionare completamente. La strategia adottata prevede:

1. **Input Injection**: Utilizzo di `System.setIn(new ByteArrayInputStream(...))` per simulare la digitazione utente.
2. **Test Parziali**: Verifica delle singole parti di input senza richiedere una connessione socket reale.
3. **Mocking Avanzato**: Per test completi, sarebbe necessario implementare un mock del server socket o utilizzare framework come Mockito.

---

## Note sull'Architettura Difensiva

La classe `Keyboard` implementa un pattern difensivo dove:

1. **Nessuna propagazione di eccezioni**: Tutti i metodi di lettura catturano le eccezioni internamente tramite blocchi try-catch.

2. **Valori sentinella**: In caso di errore, vengono restituiti valori sentinella specifici per tipo:
   - `Integer.MIN_VALUE` per `readInt()`
   - `Long.MIN_VALUE` per `readLong()`
   - `Float.NaN` per `readFloat()`
   - `Double.NaN` per `readDouble()`
   - `Character.MIN_VALUE` per `readChar()`
   - `null` per `readString()` e `readWord()`
   - `false` per `readBoolean()`

3. **Tracciamento errori**: Il contatore statico `errorCount` permette di verificare se si sono verificati errori durante l'input.

4. **Feedback configurabile**: Il flag `printErrors` permette di abilitare/disabilitare la stampa dei messaggi di errore.
