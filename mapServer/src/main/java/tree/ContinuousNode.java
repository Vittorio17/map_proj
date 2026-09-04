package tree;

import java.util.ArrayList;
import java.util.List;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;



public class ContinuousNode extends SplitNode{

    /**
     * Istanzia un oggetto invocando il costruttore della superclasse.
     * @param trainingSet training set complessivo.
     * @param beginExampleIndex indice iniziale del sotto-insieme.
     * @param endExampleIndex indice finale del sotto-insieme.
     * @param attribute attributo continuo su cui si definisce lo split.
    */
    ContinuousNode(Data trainingSet, int beginExampleIndex,int endExampleIndex, ContinuousAttribute attribute){
        super(trainingSet,beginExampleIndex,endExampleIndex,attribute);
    }
	
    /**
     * Istanzia oggetti SplitInfo per ciascuno dei valori continui dell'attributo presenti 
     * nel sotto-insieme di training corrente.
     * @param trainingSet training set complessivo.
     * @param beginExampleIndex indice iniziale.
     * @param endExampleIndex indice finale.
     * @param attribute attributo indipendente di split.
     */
	void setSplitInfo(Data trainingSet,int beginExampleIndex, int endExampleIndex, Attribute attribute){
			//Update mapSplit defined in SplitNode -- contiene gli indici del partizionamento
			Double currentSplitValue= (Double)trainingSet.getExplanatoryValue(beginExampleIndex,attribute.getIndex());
			double bestInfoVariance=0;
			List <SplitInfo> bestMapSplit=null;
			
			for(int i=beginExampleIndex+1;i<=endExampleIndex;i++){
				Double value=(Double)trainingSet.getExplanatoryValue(i,attribute.getIndex());
				if(value.doubleValue()!=currentSplitValue.doubleValue()){
				//	System.out.print(currentSplitValue +" var ");
					double localVariance=new LeafNode(trainingSet, beginExampleIndex,i-1).getVariance();
					double candidateSplitVariance=localVariance;
					localVariance=new LeafNode(trainingSet, i,endExampleIndex).getVariance();
					candidateSplitVariance+=localVariance;
					//System.out.println(candidateSplitVariance);
					if(bestMapSplit==null){
						bestMapSplit=new ArrayList<SplitInfo>();
						bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
						bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
						bestInfoVariance=candidateSplitVariance;
					}
					else{		
												
						if(candidateSplitVariance<bestInfoVariance){
							bestInfoVariance=candidateSplitVariance;
							bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i-1,0,"<="));
							bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex,1,">"));
						}
					}
					currentSplitValue=value;
				}
			}
			mapSplit=bestMapSplit;
			//rimuovo split inutili (che includono tutti gli esempi nella stessa partizione)
			
			if((mapSplit.get(1).getBeginindex()==mapSplit.get(1).getEndIndex())){
				mapSplit.remove(1);
				
			}		
	}

    /**
     * Confronta il valore in input con splitValue di ciascuno SplitInfo in mapSplit.
     * @param value valore dell'attributo da testare.
     * @return identificativo dello split (indice dell'array) con cui il test è positivo.
     */
    @Override
    int testCondition(Object value){
        double v = (Double) value;
        for (SplitInfo splitInfo : mapSplit) {
            double splitValue = (Double) splitInfo.getSplitValue();
            
            if (splitInfo.getComparator().equals("<=") && v <= splitValue) {
                return mapSplit.indexOf(splitInfo);
            } else if (splitInfo.getComparator().equals(">") && v > splitValue) {
                return mapSplit.indexOf(splitInfo);
            }
        }
        return -1;
    }

    /**
     * Invoca il metodo della superclasse specializzandolo per attributi continui.
     * @return stringa rappresentativa del nodo.
     */
    @Override
    public String toString() {
        String s = "CONTINUOUS " + super.toString() + "\n";
        for (int i = 0; i < getNumberOfChildren(); i++) {
            String op = (i == 0) ? "<=" : ">";
            s += "child " + i + " split value" + op + mapSplit.get(i).getSplitValue() + mapSplit.get(i).toString() + "\n";
        }
        return s;
    }
}
