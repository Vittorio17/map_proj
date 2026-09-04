package tree;

import data.Data;
import data.DiscreteAttribute;

public class DiscreteNodeTest{

    public void testCreazioneETestCondizione() throws Exception {
        // Carica i dati reali dal database (Tabella provaC)
        Data trainingSet = new Data("provaC"); 
        
        // Attributo discreto X
        String[] valoriDiscreti = {"A", "B"}; 
        DiscreteAttribute attribute = new DiscreteAttribute("X", 0, valoriDiscreti);

        // Istanzia il nodo sull'intero dataset
        int inizio = 0;
        int fine = trainingSet.getNumberOfExamples() - 1;
        DiscreteNode node = new DiscreteNode(trainingSet, inizio, fine, attribute);

        // Verifiche sui rami creati
        assertEquals(2, node.getNumberOfChildren(), "Il nodo deve generare esattamente 2 figli per 'A' e 'B'");

        // Verifica la condizione di ricerca per valori esistenti (devono restituire l'indice del ramo, 0 o 1)
        int indiceA = node.testCondition("A");
        assertTrue(indiceA == 0 || indiceA == 1, "Deve restituire un indice valido per il ramo 'A'");
        
        int indiceB = node.testCondition("B");
        assertTrue(indiceB == 0 || indiceB == 1, "Deve restituire un indice valido per il ramo 'B'");

        // Verifica per valore inesistente
        assertEquals(-1, node.testCondition("Z"), "Deve restituire -1 se il valore cercato non esiste");
    }
}