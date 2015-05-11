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
public class CSVExcelDataMappingFactory {
    private static CSVExcelDataMappingFactory instance;
    private CSVExcelDataMapper mapper;
    private CSVExcelDataMappingFactory(){
    
    }
    
    public static CSVExcelDataMappingFactory getSharedInstance(){
        if(instance==null){
            instance=new CSVExcelDataMappingFactory();
        }
        return instance;
    }
    
    public CSVExcelDataMapper newCSVDataMapper(){
        mapper=new CSVExcelDataMapper();
        return mapper;
    }
    
    public CSVExcelDataMapper newCSVDataMapper(boolean addTotal){
        mapper=new CSVExcelDataMapper(addTotal);
        return mapper;
    }
}
