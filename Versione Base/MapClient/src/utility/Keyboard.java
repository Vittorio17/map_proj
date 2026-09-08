package utility;

import java.io.*;
import java.util.*;

/**
 * Rappresenta la classe che gestisce l'input da tastiera.
 *
 * @author Lewis and Loftus
 */
public class Keyboard {
	/**
	 * Indica se gli errori di input devono essere visualizzati.
	*/
	private static boolean printErrors = true;
	
	/**
	 * Rappresenta un contatore degli errori
	 */
	private static int errorCount = 0;

	/**
	 * Restituisce il numero attuale di errori.
	 *
	 * @return il numero di errori verificatisi
	 */
	public static int getErrorCount() {
		return errorCount;
	}

	/**
	 * Reimposta a zero il conteggio attuale degli errori.
	 *
	 * @param count parametro non utilizzato mantenuto per compatibilità
	 */
	public static void resetErrorCount(int count) {
		errorCount = 0;
	}

	/**
	 * Indica se gli errori di input vengono attualmente stampati sullo standard output.
	 *
	 * @return true se gli errori vengono stampati, false altrimenti
	 */
	public static boolean getPrintErrors() {
		return printErrors;
	}

	/**
	 * Imposta se gli errori di input devono essere visualizzati sullo standard output.
	 *
	 * @param flag true per mostrare i messaggi di errore, false per nasconderli
	 */
	public static void setPrintErrors(boolean flag) {
		printErrors = flag;
	}

	/**
	 * Incrementa il conteggio degli errori e visualizza il messaggio specificato 
	 * se la stampa degli errori è abilitata.
	 *
	 * @param str il messaggio di errore da mostrare
	*/
	private static void error(String str) {
		errorCount++;
		if (printErrors)
			System.out.println(str);
	}

	// ************* Tokenized Input Stream Section ******************

	/**
	 * Memorizza la porzione residua di un token già letto ma non ancora interamente consumata.
	 */
	private static String current_token = null;

	/**
	 * Gestisce la scomposizione in singoli token della riga di testo correntemente letta.
	 */
	private static StringTokenizer reader;

	/**
	 * Buffer di lettura collegato allo standard input (System.in) per l'acquisizione delle righe.
	 */
	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	/**
	 * Recupera il token successivo dallo stream di input, assumendo che possa trovarsi
	 * sulle righe successive.
	 *
	 * @return la stringa che rappresenta il token successivo
	 */
	private static String getNextToken() {
		return getNextToken(true);
	}

	/**
	 * Recupera il token successivo dallo stream di input o riutilizza quello precedentemente letto.
	 *
	 * @param skip true se devono essere ignorati i caratteri di spaziatura e le righe vuote
	 * @return il token estratto
	 */
	private static String getNextToken(boolean skip) {
		String token;

		if (current_token == null)
			token = getNextInputToken(skip);
		else {
			token = current_token;
			current_token = null;
		}

		return token;
	}

	/**
	 * Estrae il token successivo direttamente dal reader di input.
	 *
	 * @param skip determina se consumare e saltare eventuali righe successive o spazi
	 * @return il token letto oppure null in caso di errore o fine dello stream
	*/
	private static String getNextInputToken(boolean skip) {
		final String delimiters = " \t\n\r\f";
		String token = null;

		try {
			if (reader == null)
				reader = new StringTokenizer(in.readLine(), delimiters, true);

			while (token == null || ((delimiters.indexOf(token) >= 0) && skip)) {
				while (!reader.hasMoreTokens())
					reader = new StringTokenizer(in.readLine(), delimiters,
							true);

				token = reader.nextToken();
			}
		} catch (Exception exception) {
			token = null;
		}

		return token;
	}

	/**
	 * Verifica se non ci sono più token disponibili sulla riga di input corrente.
	 *
	 * @return true se la riga corrente è terminata, false altrimenti
	*/
	public static boolean endOfLine() {
		return !reader.hasMoreTokens();
	}

	// ************* Reading Section *********************************

	/**
	 * Legge un'intera riga di testo dallo standard input fino a fine riga.
	 *
	 * @return la stringa letta da input, oppure null in caso di errore
	*/
	public static String readString() {
		String str;

		try {
			str = getNextToken(false);
			while (!endOfLine()) {
				str = str + getNextToken(false);
			}
		} catch (Exception exception) {
			error("Error reading String data, null value returned.");
			str = null;
		}
		return str;
	}

	/**
	 * Legge una singola parola (delimitata da spazi) dallo standard input.
	 *
	 * @return la stringa della singola parola, oppure null in caso di errore
	*/
	public static String readWord() {
		String token;
		try {
			token = getNextToken();
		} catch (Exception exception) {
			error("Error reading String data, null value returned.");
			token = null;
		}
		return token;
	}

	/**
	 * Legge un valore booleano dallo standard input.
	 *
	 * @return true se il token letto corrisponde a "true", false altrimenti o in caso di errore
	*/
	public static boolean readBoolean() {
		String token = getNextToken();
		boolean bool;
		try {
			if (token.toLowerCase().equals("true"))
				bool = true;
			else if (token.toLowerCase().equals("false"))
				bool = false;
			else {
				error("Error reading boolean data, false value returned.");
				bool = false;
			}
		} catch (Exception exception) {
			error("Error reading boolean data, false value returned.");
			bool = false;
		}
		return bool;
	}

	/**
	 * Legge un singolo carattere dallo standard input.
	 *
	 * @return il carattere letto, oppure Character.MIN_VALUE in caso di errore
	*/
	public static char readChar() {
		String token = getNextToken(false);
		char value;
		try {
			if (token.length() > 1) {
				current_token = token.substring(1, token.length());
			} else
				current_token = null;
			value = token.charAt(0);
		} catch (Exception exception) {
			error("Error reading char data, MIN_VALUE value returned.");
			value = Character.MIN_VALUE;
		}

		return value;
	}

	/**
	 * Legge un intero a 32 bit (int) dallo standard input.
	 *
	 * @return il valore intero convertito, oppure Integer.MIN_VALUE in caso di errore
	 */
	public static int readInt() {
		String token = getNextToken();
		int value;
		try {
			value = Integer.parseInt(token);
		} catch (Exception exception) {
			error("Error reading int data, MIN_VALUE value returned.");
			value = Integer.MIN_VALUE;
		}
		return value;
	}

	/**
	 * Legge un intero a 64 bit (long) dallo standard input.
	 *
	 * @return il valore long convertito, oppure Long.MIN_VALUE in caso di errore
	 */
	public static long readLong() {
		String token = getNextToken();
		long value;
		try {
			value = Long.parseLong(token);
		} catch (Exception exception) {
			error("Error reading long data, MIN_VALUE value returned.");
			value = Long.MIN_VALUE;
		}
		return value;
	}

	/**
	 * Legge un numero a virgola mobile in singola precisione (float) dallo standard input.
	 *
	 * @return il valore float convertito, oppure Float.NaN in caso di errore
	 */
	public static float readFloat() {
		String token = getNextToken();
		float value;
		try {
			value = (new Float(token)).floatValue();
		} catch (Exception exception) {
			error("Error reading float data, NaN value returned.");
			value = Float.NaN;
		}
		return value;
	}

	/**
	 * Legge un numero a virgola mobile in doppia precisione (double) dallo standard input.
	 *
	 * @return il valore double convertito, oppure Double.NaN in caso di errore
	 */
	public static double readDouble() {
		String token = getNextToken();
		double value;
		try {
			value = (new Double(token)).doubleValue();
		} catch (Exception exception) {
			error("Error reading double data, NaN value returned.");
			value = Double.NaN;
		}
		return value;
	}
}
