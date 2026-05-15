import data.Data;
import data.TrainingDataException;
import tree.RegressionTree;
import tree.UnknownValueException;
import utility.Keyboard;

/**
 * Classe di test per l'acquisizione del training set, l'apprendimento 
 * del modello e la predizione interattiva.
 */
public class MainTest {

    public static void main(String[] args) {
		char repeatMain = 'y';

        do {
            System.out.println("Training set:");
            String fileName = Keyboard.readString(); // Acquisizione nome file
            
            try {
                System.out.println("Starting data acquisition phase!");
                Data trainingSet = new Data(fileName); // Può lanciare TrainingDataException
                
                System.out.println("Starting learning phase!");
                RegressionTree tree = new RegressionTree(trainingSet);
                
                System.out.println("********* RULES **********");
                tree.printRules();
                System.out.println("*************************");
                
                System.out.println("********* TREE **********");
                tree.printTree();
                System.out.println("*************************");
                
                char repeatPred = 'y';
                do {
                    System.out.println("Starting prediction phase!");
                    try {
                        // Stampa il risultato Double della predizione
                        System.out.println(tree.predictClass());
                    } catch (UnknownValueException e) {
                        // Stampa l'oggetto eccezione per includere il nome della classe
                        // Output: tree.UnknownValueException: messaggio
                        System.out.println(e);
                    }
                    
                    System.out.println("Would you repeat ? (y/n)");
                    repeatPred = Keyboard.readChar();
                } while (repeatPred == 'y' || repeatPred == 'Y');

            } catch (TrainingDataException e) {
                System.out.println(e);
            }
            
            System.out.println("Would you repeat with a new training set? (y/n)");
            repeatMain = Keyboard.readChar();
            
        } while (repeatMain == 'y' || repeatMain == 'Y');
	}
}