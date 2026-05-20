package tree;

import data.Data;

import java.util.ArrayList;

import data.Attribute;
import data.DiscreteAttribute;

/**
 * Modella l'entità nodo di split relativo ad un attributo indipendente discreto.
 */
class DiscreteNode extends SplitNode {

    /**
     * Istanzia un oggetto invocando il costruttore della superclasse.
     * @param trainingSet training set complessivo.
     * @param beginExampleIndex indice iniziale del sotto-insieme.
     * @param endExampleIndex indice finale del sotto-insieme.
     * @param attribute attributo discreto su cui si definisce lo split.
     */
    DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
        super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
    }

    /**
     * Istanzia oggetti SplitInfo per ciascuno dei valori discreti dell'attributo presenti 
     * nel sotto-insieme di training corrente.
     * @param trainingSet training set complessivo.
     * @param beginExampleIndex indice iniziale.
     * @param endExampleIndex indice finale.
     * @param attribute attributo indipendente di split.
     */
    @Override
    void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
        mapSplit = new ArrayList<SplitInfo>();
        
        //Identificare le partizioni e popolare mapSplit
        int currentBegin = beginExampleIndex;
        Object lastValue = trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());

        for (int i = beginExampleIndex + 1; i <= endExampleIndex; i++) {
            Object currentValue = trainingSet.getExplanatoryValue(i, attribute.getIndex());
            if (!currentValue.equals(lastValue)) {
                // Trovata la fine di una partizione (es. fine della partizione 'B')
                mapSplit.add(new SplitInfo(lastValue, currentBegin, i - 1, mapSplit.size()));
                currentBegin = i;
                lastValue = currentValue;
            }
        }
        // Inserimento dell'ultima partizione
        mapSplit.add(new SplitInfo(lastValue, currentBegin, endExampleIndex, mapSplit.size()));
    }

    /**
     * Confronta il valore in input con splitValue di ciascuno SplitInfo in mapSplit.
     * @param value valore dell'attributo da testare.
     * @return identificativo dello split (indice dell'array) con cui il test è positivo.
     */
    @Override
    int testCondition(Object value) {
        for(SplitInfo splitInfo : mapSplit){
            if(splitInfo.getSplitValue().equals(value)){
                return mapSplit.indexOf(splitInfo);
            }
        }
        return -1;
    }

    /**
     * Invoca il metodo della superclasse specializzandolo per attributi discreti.
     * @return stringa rappresentativa del nodo.
     */
    @Override
    public String toString() {
        String s = "DISCRETE " + super.toString() + "\n";
        for (int i = 0; i < getNumberOfChildren(); i++) {
            s += "child " + i + " split value=" + mapSplit.get(i).getSplitValue() + mapSplit.get(i).toString() + "\n";
        }
        return s;
    }
}