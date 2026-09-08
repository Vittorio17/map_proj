package tree;
import java.io.Serializable;

import data.Data;

/**
 * La classe astratta Node modella l'astrazione dell'entità nodo
 * (fogliare o intermedio) dell'albero di decisione
*/
abstract class Node implements Serializable{

    /**
     * Contatore dei nodi generati nell'albero.
     */
    static int idNodeCount = 0;
    /**
     * Identificativo numerico del nodo
     */
    private int idNode;
    /**
     * Indice nell'array del training set del primo
     * esempio coperto dal nodo corrente
     */
    private int beginExampleIndex;
    /**
     * Indice nell'array del training set dell'ultimo
     * esempio coperato dal nodo corrente
     */
    private int endExampleIndex;
    /**
     * Valore dell'SSE (Errore Quadratico Medio)
     */
    private double variance;

    /**
     * Costruttore, avvalora gli attributi primitivi di classe, inclusa la varianza la quale
     * viene calcolata rispetto all'attributo da predire nel sotto-insieme del training coperto
     * dal nodo.
     * @param trainingSet Oggetto di classe Data contente il training set completo
     * @param beginExampleIndex Estremo sinistro del sottoinsieme del training set
     * @param endExampleIndex Estremo destro del sottoinsieme del training set
     */
    Node(Data trainingSet,int beginExampleIndex,int endExampleIndex){
        this.beginExampleIndex = beginExampleIndex;
        this.endExampleIndex = endExampleIndex;
        idNode = idNodeCount;
        idNodeCount = idNodeCount + 1;

        //Calcolo della media dei valori target
        double media = 0;
        for(int i=beginExampleIndex;i<=endExampleIndex;i++){
            media = media + trainingSet.getClassValue(i);
        }
        media = media/(endExampleIndex-beginExampleIndex+1);
        //Calcolo dell'SSE
        variance = 0;
        for(int i=beginExampleIndex;i<=endExampleIndex;i++){
            variance = variance + ((trainingSet.getClassValue(i)-media)*(trainingSet.getClassValue(i)-media));
        }
    }

    /**
     * Restituisce l'ID del nodo.
     * @return Identificativo numerico del nodo.
     */
    int getIdNode(){
        return idNode;
    }

    /**
     * Restituisce l'inizio del sotto-insieme.
     * @return Indice del primo esempio del sottoinsieme rispetto
     * al training set complessivo.
     */
    int getBeginExampleIndex(){
        return beginExampleIndex;
    }

    /**
     * Restituisce l'inizio del sotto-insieme.
     * @return Indice dell'ultimo esempio del sottoinsieme rispetto
     * al training set complessivo
     */
    int getEndExampleIndex(){
        return endExampleIndex;
    }

    /**
     * Calcola la varianza degli esempi nel nodo.
     * @return Valore dello SSE dell'attributo da predire rispetto al nodo corrente.
     */
    double getVariance(){
        return variance;
    }

    /**
     * Restituisce il numero di figli del nodo.
     * @return Valore del numero di nodi sottostanti.
     */
    abstract int getNumberOfChildren();

    /**
     * @return Concatena in un oggetto String i valori beginExampleIndex,endExampleIndex, variance
    */
    public String toString() {
        return "Nodo: [Examples:" + beginExampleIndex + "-" + endExampleIndex + "] variance:" + variance;
    }
}
