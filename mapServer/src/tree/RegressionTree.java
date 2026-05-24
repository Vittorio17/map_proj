package tree;
import data.Attribute;
import data.Data;
import data.ContinuousAttribute;
import data.DiscreteAttribute;
import server.UnknownValueException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TreeSet;
/**
 * La classe RegressionTree modella l'entità dell'intero albero di decisione come
 * insieme di sotto-alberi.
 */
public class RegressionTree implements Serializable{

    /** Radice dell'albero o del sotto-albero corrente. */
    private Node root;
    /** Array di sotto-alberi originanti nel nodo root. */    
    private RegressionTree[] childTree;

    /**
     * Istanzia un sotto-albero dell'intero albero.
     */
    RegressionTree(){

    }

    /**
     * Istanzia un sotto-albero e avvia l'induzione dell'albero dai dati in input.
     * @param trainingSet training set complessivo
     */
    public RegressionTree(Data trainingSet){	
		learnTree(trainingSet,0,trainingSet.getNumberOfExamples()-1,trainingSet.getNumberOfExamples()*10/100);
	}
		
    /**
     * Genera ricorsivamente la struttura dell'albero.
     * @param trainingSet             Il dataset di addestramento.
     * @param begin                   Indice inferiore dell'intervallo.
     * @param end                     Indice superiore dell'intervallo.
     * @param numberOfExamplesPerLeaf Numero minimo di esempi per foglia.
     */
	private void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf){
		if(isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)){
			//determina la classe che compare più frequentemente nella partizione corrente
			root=new LeafNode(trainingSet,begin,end);
		}
		else //split node
		{
			root=determineBestSplitNode(trainingSet, begin, end);	
			if(root.getNumberOfChildren()>1){
				childTree=new RegressionTree[root.getNumberOfChildren()];
				for(int i=0;i<root.getNumberOfChildren();i++){
					childTree[i]=new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).getBeginindex(), ((SplitNode)root).getSplitInfo(i).getEndIndex(), numberOfExamplesPerLeaf);
				}
			}
			else
				root=new LeafNode(trainingSet,begin,end);
			
		}
	}

    /**
     * Verifica se il sotto-insieme corrente di dati deve essere considerato una foglia.
     * @param trainingSet training set complessivo.
     * @param begin indice iniziale del sotto-insieme.
     * @param end indice finale del sotto-insieme.
     * @param numberOfExamplesPerLeaf numero minimo di esempi per foglia (10% del totale).
     * @return true se il numero di esempi è inferiore o uguale al limite, false altrimenti.
     */
    private boolean isLeaf(Data trainingSet,int begin,int end, int numberOfExamplesPerLeaf){
        return (end-begin+1) <= numberOfExamplesPerLeaf;
    }


    /**
     * Seleziona l'attributo migliore su cui effettuare lo split.
     * @param trainingSet training set complessivo.
     * @param begin indice iniziale della partizione corrente.
     * @param end indice finale della partizione corrente.
     * @return il nodo di split che minimizza la varianza.
     */
    private SplitNode determineBestSplitNode(Data trainingSet,int begin,int end){
        TreeSet<SplitNode> sortedSplits = new TreeSet<SplitNode>();

        for(int i=0;i<trainingSet.getNumberOfExplanatoryAttributes();i++){
            Attribute attribute = trainingSet.getExplanatoryAttribute(i);
            
            if(attribute instanceof DiscreteAttribute){
                DiscreteNode currentNode = new DiscreteNode(trainingSet, begin,end, (DiscreteAttribute) attribute);
                sortedSplits.add(currentNode);
            }else if(attribute instanceof ContinuousAttribute){
                ContinuousNode currentNode = new ContinuousNode(trainingSet, begin,end,(ContinuousAttribute)attribute);
                sortedSplits.add(currentNode);
            }
        }
        SplitNode bestNode = sortedSplits.first();
        if(bestNode!=null){
            trainingSet.sort(bestNode.getAttribute(), begin, end);
        }
        return bestNode;
    }
    
    /**
     * Visualizza le informazioni dell'intero albero.
     */
    public void printTree(){
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}
		
    /**
     * Concatena le informazioni di root e childTree[] correnti.
     * @return oggetto String con le informazioni dell'intero albero
     */
    @Override
    public String toString(){
		String tree=root.toString()+"\n";
		
		if( root instanceof LeafNode){
		
		}
		else //split node
		{
			for(int i=0;i<childTree.length;i++)
				tree +=childTree[i];
		}
		return tree;
	}

    /**
     * Restituisce la query del nodo corrente se è un nodo di split,
     * null se è una foglia.
     */
    public String getCurrentNodeQuery() {
        if (root instanceof LeafNode) return null;
        return ((SplitNode) root).formulateQuery();
    }

    /**
     * Restituisce il sotto-albero figlio all'indice specificato.
     * @param index indice del ramo scelto dall'utente
     * @throws UnknownValueException se l'indice non è valido
     */
    public RegressionTree getChild(int index) throws UnknownValueException {
        if (index < 0 || index >= root.getNumberOfChildren()) {
            throw new UnknownValueException("The answer should be an integer between 0 and " + (root.getNumberOfChildren() - 1) + "!");
        }
        return childTree[index];
    }

    /** Restituisce il valore predetto se è un nodo foglia, null altrimenti */
    public Double getPredictedValue() {
        if (root instanceof LeafNode)
            return ((LeafNode) root).getPredictedClassValue();
        return null;
    }

    /* Metodo non più utilizzabile in quanto il server non può interagire
    con la tastiera
    
    public Double predictClass() throws UnknownValueException {
        // Caso base: Il nodo corrente è una foglia
        if (root instanceof LeafNode) {
            LeafNode rt = (LeafNode) root;
            return rt.getPredictedClassValue();
        } 
        // Caso ricorsivo: Il nodo corrente è un nodo di split
        else {
            int risp;
            // Visualizza la query dello split (es: "0:X=A, 1:X=B")
            System.out.println(((SplitNode) root).formulateQuery());
            
            // Acquisizione dell'input tramite la classe Keyboard
            risp = Keyboard.readInt();
            
            // Verifica validità dell'input
            if (risp < 0 || risp >= root.getNumberOfChildren()) {
                throw new UnknownValueException("The answer should be an integer between 0 and " + (root.getNumberOfChildren() - 1) + "!");
            }
            
            // Invocazione ricorsiva sul sotto-albero scelto dall'utente
            return childTree[risp].predictClass();
        }
    }*/

    /**
     * Scandisce ciascun ramo dell'albero dalla radice alla foglia.
     */
    public void printRules() {
        System.out.println("********* RULES **********");
        if (root != null) {
            this.printRules("");
        }
        System.out.println("**************************");
    }

    /**
     * Supporta printRules() concatenando le informazioni di root al precedente current.
     * @param current percorso accumulato dai nodi superiori.
     */
    private void printRules(String current) {
        if (root instanceof SplitNode) {
            SplitNode split = (SplitNode) root;
            
            for(int i=0;i<split.getNumberOfChildren();i++){
                SplitNode.SplitInfo info = split.getSplitInfo(i);
                String condition = split.getAttribute().getName() + info.getComparator() + info.getSplitValue();
            
                String prossimoPercorso;
                if(current.equals("")){ //Verifica se current è la prima condizione
                    prossimoPercorso = condition;
                }else{
                    prossimoPercorso = current + " AND " + condition;
                }
                childTree[i].printRules(prossimoPercorso); //Passiamo al sotto-albero figlio
            }
        }else if(root instanceof LeafNode){
            LeafNode leaf = (LeafNode) root;
            System.out.println(current + " ==> Class=" + leaf.getPredictedClassValue());
        }
    }

    public void salva(String fileName) throws IOException{
        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(this);
        out.close();
    }

    public static RegressionTree carica(String fileName) throws IOException, ClassNotFoundException{
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        RegressionTree tree = (RegressionTree) in.readObject();
        in.close();
        return tree;
    }
}
