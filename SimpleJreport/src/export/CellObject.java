/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package export;

/**
 *
 * @author neroshank
 */
public class CellObject {
    
    private Object actualValue;
    private String stringValue;
    
    public CellObject(String stringValue) {
        this.actualValue = stringValue;
        this.stringValue = stringValue;
    }

    public CellObject(Object actualValue, String stringValue) {
        this.actualValue = actualValue;
        this.stringValue = stringValue;
    }

    public Object getActualValue() {
        return actualValue;
    }

    public void setActualValue(Object actualValue) {
        this.actualValue = actualValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
    
}
