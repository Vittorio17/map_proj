package tree;
import data.Data;
import java.util.List;
import java.util.ArrayList;

import data.Attribute;

/**
 * Modella l'astrazione dell'entità nodo di split (continuo o discreto) 
 * estendendo la superclasse Node.
 */
abstract class SplitNode extends Node implements Comparable<SplitNode>{
	
    /**
     * Inner Class che aggrega tutte le informazioni riguardanti un nodo di split.
     */
	class SplitInfo{
        /** Valore dell'attributo indipendente che definisce lo split*/
		private Object splitValue;
        /** Identificativo numerico del ramo originante dallo split*/
		private int numberChild;
        /** Indice iniziale del sotto-insieme di training coperto dallo split*/
        private int beginIndex;
        /** Indice finale del sotto-insieme di training coperto dallo split*/
        private int endIndex;
        /** Operatore matematico che definisce il test nel nodo*/
		private String comparator="=";

        /**
         * Costruttore per split a valori discreti[cite: 48].
         * @param splitValue valore dello split.
         * @param beginIndex indice iniziale del sotto-insieme.
         * @param endIndex indice finale del sotto-insieme.
         * @param numberChild identificativo del figlio.
         */
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
		}

        /**
         * Costruttore per split a valori continui.
         * @param splitValue valore dello split.
         * @param beginIndex indice iniziale del sotto-insieme.
         * @param endIndex indice finale del sotto-insieme.
         * @param numberChild identificativo del figlio.
         * @param comparator operatore di confronto.
         */
		SplitInfo(Object splitValue,int beginIndex,int endIndex,int numberChild, String comparator){
			this.splitValue=splitValue;
			this.beginIndex=beginIndex;
			this.endIndex=endIndex;
			this.numberChild=numberChild;
			this.comparator=comparator;
		}

        int getBeginindex(){
			return beginIndex;			
		}

		int getEndIndex(){
			return endIndex;
		}

        /**
         * Restituisce il valore discreto dello split.
         * @return valore dello split.
         */
		Object getSplitValue(){
			return splitValue;
		}

        /**
         * Concatena le informazioni dello split in una stringa
         * @return stringa rappresentativa dello SplitInfo.
         */
		public String toString(){
			return "[Examples:" + beginIndex + "-" + endIndex + "]";
		}
        
        /**
         * Restituisce l'operatore usato per il test.
         * @return operatore matematico del test.
         */
        String getComparator(){
			return comparator;
		}
	}

    /** Oggetto Attribute che modella l'attributo indipendente su cui è generato lo split*/
	private Attribute attribute;	
    /** Lista per memorizzare gli split candidati (rami figli)*/
    protected List<SplitInfo> mapSplit = new ArrayList<SplitInfo>();
    /** Valore di varianza a seguito del partizionamento indotto dallo split*/
	private double splitVariance;
	
    /**
     * Metodo astratto per impostare le informazioni di split.
     * @param trainingSet training set complessivo.
     * @param beginExampelIndex indice iniziale.
     * @param endExampleIndex indice finale.
     * @param attribute attributo di split.
     */	
    abstract void setSplitInfo(Data trainingSet,int beginExampelIndex, int endExampleIndex, Attribute attribute);
	
    /**
     * Metodo astratto per modellare la condizione di test.
     * @param value valore dell'attributo da testare.
     * @return identificativo dello split positivo.
     */
	abstract int testCondition (Object value);
	
    /**
     * Costruttore di SplitNode. Invoca il costruttore della superclasse, ordina il training set 
     * e determina gli split.
     * @param trainingSet training set completo.
     * @param beginExampleIndex indice iniziale del sotto-insieme.
     * @param endExampleIndex indice finale del sotto-insieme.
     * @param attribute attributo indipendente usato per lo split.
     */
	SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute){
			super(trainingSet, beginExampleIndex,endExampleIndex);
			this.attribute=attribute;
			trainingSet.sort(attribute, beginExampleIndex, endExampleIndex); // order by attribute
			setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
						
			//compute variance
			splitVariance=0;
			for(int i=0;i<mapSplit.size();i++){
					double localVariance=new LeafNode(trainingSet, mapSplit.get(i).getBeginindex(),mapSplit.get(i).getEndIndex()).getVariance();
					splitVariance+=(localVariance);
			}
	}
	
    /**
     * Restituisce l'attributo usato per lo split.
     * @return l'oggetto Attribute usato per lo split.
     */
	Attribute getAttribute(){
		return attribute;
	}
	
    /** @return valore dello splitVariance*/
	double getVariance(){
		return splitVariance;
	}
	
    /** @return numero di rami originanti nel nodo corrente*/
	int getNumberOfChildren(){
		return mapSplit.size();
	}
	
    /**
     * Restituisce le informazioni per il ramo specificato.
     * @param child indice del ramo nell'array mapSplit.
     * @return oggetto SplitInfo corrispondente.
     */
	SplitInfo getSplitInfo(int child){
		return mapSplit.get(child);
	}

	/**
     * Concatena le informazioni di ciascun test in una stringa finale per la predizione.
     * @return stringa contenente la query formulata.
     */
	String formulateQuery(){
		String query = "";
		for(int i=0;i<mapSplit.size();i++)
			query+= (i + ":" + attribute.getName() + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue())+"\n";
		return query;
	}
	
    /**
     * Concatena le informazioni del nodo di split, inclusa la varianza e i dettagli dei rami.
     * @return stringa rappresentativa del nodo di split.
     */
	public String toString() {
        return "SPLIT : attribute=" + attribute.getName() + " " + super.toString() + " Split Variance: " + splitVariance + "\n";
    }

    /**
     * Confronta il nodo corrente con il nodo DiscreteNode
     * @param o Il nodo di split da confrontare.
     * @return 0 se le varianze sono uguali, -1 gain minore, 
     *         1 gain maggiore.
     */
    public int compareTo(SplitNode o) {
        if (this.getVariance() < o.getVariance()) {
            return -1;
        } else if (this.getVariance() > o.getVariance()) {
            return 1;
        } else {
            return 0;
        }
    }
}
