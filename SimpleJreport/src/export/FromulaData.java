/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package export;

import java.util.ArrayList;

/**
 *
 * @author neroshank
 */
public class FromulaData {
    private ArrayList<String> fefc;
    private String newCatName;
    public double formulaTot=0;
    public int increase=0;
    
    public FromulaData(String newCatName,ArrayList<String> fefc){
        this.newCatName=newCatName;
        this.fefc=fefc;
    }
    /**
     * @return the fefc
     */
    public ArrayList<String> getFefc() {
        return fefc;
    }

    /**
     * @param fefc the fefc to set
     */
    public void setFefc(ArrayList<String> fefc) {
        this.fefc = fefc;
    }

    /**
     * @return the newCatName
     */
    public String getNewCatName() {
        return newCatName;
    }

    /**
     * @param newCatName the newCatName to set
     */
    public void setNewCatName(String newCatName) {
        this.newCatName = newCatName;
    }
}
