package data;

import java.io.Serializable;

/**
 * La classe astratta Attribute modella un generico attributo, 
 * che può essere di tipo discreto o continuo.
 */
public abstract class Attribute implements Serializable{

    /** Nome simbolico dell'attributo. */
    private String name;
    /** Indice numerico dell'attributo. */
    private int index;

    
    /**
     * Costruttore di classe. Inizializza i membri name e index con i valori passati in input.
     * @param name  Nome simbolico da assegnare all'attributo.
     * @param index Identificatore numerico dell'attributo
     */
    Attribute(String name,int index){
        this.name = name;
        this.index = index;
    }

    /**
     * Restituisce il nome simbolico dell'attributo
     * @return Il valore memorizzato in name
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Restituisce l'identificativo numerico dell'attributo.
     * @return Il valore memorizzato in index
     */
    public int getIndex(){
        return this.index;
    }
}