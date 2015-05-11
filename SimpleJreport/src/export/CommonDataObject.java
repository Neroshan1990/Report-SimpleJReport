/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package export;

/**
 *
 * @author Neroshank
 */
public class CommonDataObject {
    private Object objects[];
    public CommonDataObject(Object[] objects){
        this.objects=objects;
    }

    /**
     * @return the objects
     */
    public Object[] getObjects() {
        return objects;
    }

    /**
     * @param objects the objects to set
     */
    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}
