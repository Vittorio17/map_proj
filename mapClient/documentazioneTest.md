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
