package tree;
import data.Data;

/**
 * La classe LeafNode modella l'entità nodo fogliare.
 * Una foglia non effettua split ma contiene il valore predetto per gli esempi
 * che ricadono nella sua partizione.
 */
class LeafNode extends Node{

    /** Valore predetto per la porzione di training set coperto dal nodo. */
    private double predictedClassValue;

    /**
     * Costruttore di LeafNode. Invoca il costruttore della superclasse e 
     * calcola la media dei valori dell'attributo di classe.
     * @param trainingSet training set complessivo.
     * @param beginExampleIndex indice iniziale del sotto-insieme.
     * @param endExampleIndex indice finale del sotto-insieme.
     */
    LeafNode(Data trainingSet,int beginExampleIndex,int endExampleIndex){
        super(trainingSet,beginExampleIndex,endExampleIndex);

        double media = 0;
        for(int i=beginExampleIndex;i<=endExampleIndex;i++){
            media = media + trainingSet.getClassValue(i);
        }
        predictedClassValue = media/(endExampleIndex-beginExampleIndex+1);
    }

    /**
     * Restituisce il valore predetto dalla foglia.
     * @return valore della media calcolata nel costruttore.
     */
    double getPredictedClassValue(){
        return this.predictedClassValue;
    }

    /**
     * Una foglia non ha rami figli, quindi restituisce sempre 0.
     * @return 0.
     */
    @Override
    int getNumberOfChildren(){
        return 0;
    }

    /**
     * Concatena le informazioni del nodo padre con il valore predetto dalla foglia.
     * @return stringa rappresentativa della foglia.
     */
    @Override
    public String toString(){
        return "LEAF : class=" + getPredictedClassValue() + " " + super.toString(); 
    }
}
