package tree;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.Data;
import data.ContinuousAttribute;

public class ContinuousNodeTest {

    private Data trainingSet;
    private ContinuousNode node;

    @BeforeEach
    public void setUp() throws Exception {
        trainingSet = new Data("provaC"); 
        // Cerchiamo un attributo continuo nel dataset
        ContinuousAttribute attribute = null;
        for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++) {
            if (trainingSet.getExplanatoryAttribute(i) instanceof ContinuousAttribute) {
                attribute = (ContinuousAttribute) trainingSet.getExplanatoryAttribute(i);
                break;
            }
        }
        node = new ContinuousNode(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, attribute);
    }

    @Test
    public void testGenerazioneRami() {
        assertTrue(node.getNumberOfChildren() > 0, "Il calcolo della varianza minima deve generare dei rami figli");
    }

    @Test
    public void testValutazioneSoglia() {
        int risultato = node.testCondition(1.0);
        assertTrue(risultato >= -1 && risultato < node.getNumberOfChildren(), "La valutazione dell'operatore relazionale deve restituire un branch valido o -1");
    }
}