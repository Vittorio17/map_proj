package tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.Data;
import data.DiscreteAttribute;

public class DiscreteNodeTest {

    private Data trainingSet;
    private DiscreteNode node;

    @BeforeEach
    public void setUp() throws Exception {
        trainingSet = new Data("provaC"); 
        String[] valoriDiscreti = {"A", "B"}; 
        DiscreteAttribute attribute = (DiscreteAttribute) trainingSet.getExplanatoryAttribute(0);
        node = new DiscreteNode(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, attribute);
    }

    @Test
    public void testGenerazioneRami() {
        assertEquals(2, node.getNumberOfChildren(), "Il nodo deve generare esattamente due figli per i valori distinti 'A' e 'B'");
    }

    @Test
    public void testCondizioneValoreEsistente() {
        int indiceA = node.testCondition("A");
        assertTrue(indiceA == 0 || indiceA == 1, "La ricerca di 'A' deve restituire un indice di branch valido (0 o 1)");
    }

    @Test
    public void testCondizioneValoreInesistente() {
        assertEquals(-1, node.testCondition("Z"), "La ricerca di un valore non mappato nello split deve restituire -1");
    }
}