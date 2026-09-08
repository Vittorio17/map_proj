package tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.Data;
import data.Attribute;
import data.DiscreteAttribute;
import data.ContinuousAttribute;

/**
 * Classe di test per verificare i comportamenti, il calcolo della varianza 
 * e l'astrazione definiti nella classe SplitNode.
 */
public class SplitNodeTest {

    private Data trainingSet;
    private SplitNode nodoSplit;
    private Attribute attributo;

    /**
     * Setup iniziale: istanzia il training set e un nodo di split di base.
     * Dato che SplitNode è astratta, utilizziamo la sua estensione concreta
     * DiscreteNode per verificarne i metodi ereditati.
     * 
     * @throws Exception se si verificano errori di connessione al database.
     */
    @BeforeEach
    public void setUp() throws Exception {
        trainingSet = new Data("provaC"); 
        attributo = trainingSet.getExplanatoryAttribute(0);
        
        int inizio = 0;
        int fine = trainingSet.getNumberOfExamples() - 1;
        
        if (attributo instanceof DiscreteAttribute) {
            nodoSplit = new DiscreteNode(trainingSet, inizio, fine, (DiscreteAttribute) attributo);
        } else {
            nodoSplit = new ContinuousNode(trainingSet, inizio, fine, (ContinuousAttribute) attributo);
        }
    }

    /**
     * Verifica la corretta associazione dell'attributo indipendente al nodo.
     */
    @Test
    public void testGetAttribute() {
        assertEquals(attributo, nodoSplit.getAttribute(),"Il metodo getAttribute deve restituire l'esatto attributo fornito al costruttore");
    }

    /**
     * Verifica il calcolo della varianza generata a seguito del partizionamento.
     */
    @Test
    public void testCalcoloSplitVariance() {
        assertTrue(nodoSplit.getVariance() >= 0.0, "La splitVariance deve essere >= 0");
    }

    /**
     * Verifica la corretta gestione dei rami figli e della classe interna SplitInfo.
     */
    @Test
    public void testGestioneFigliESplitInfo() {
        assertTrue(nodoSplit.getNumberOfChildren() > 0,"Il nodo deve aver popolato mapSplit con almeno un ramo");
        
        assertNotNull(nodoSplit.getSplitInfo(0),"Il recupero dell'oggetto SplitInfo per un indice valido non deve essere nullo");
    }

    /**
     * Verifica che la query predittiva venga formulata sintatticamente in modo corretto.
     */
    @Test
    public void testFormulateQuery() {
        String query = nodoSplit.formulateQuery();
        assertTrue(query.contains(attributo.getName()), "La query finale deve contenere il nome dell'attributo di split testato");
        assertTrue(query.contains(":"), "La query deve seguire il formato di output atteso con i separatori");
    }

    /**
     * Verifica il funzionamento dell'interfaccia Comparable basata sulla varianza.
     * 
     * @throws Exception se si verificano errori di connessione al database.
     */
    @Test
    public void testCompareTo() throws Exception {
        SplitNode nodoUguale;
        if (attributo instanceof DiscreteAttribute) {
            nodoUguale = new DiscreteNode(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, (DiscreteAttribute) attributo);
        } else {
            nodoUguale = new ContinuousNode(trainingSet, 0, trainingSet.getNumberOfExamples() - 1, (ContinuousAttribute) attributo);
        }
        
        assertEquals(0, nodoSplit.compareTo(nodoUguale), "Il confronto (compareTo) tra due nodi aventi la medesima varianza deve restituire 0");
    }
}