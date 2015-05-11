/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Neroshank
 */
public class FileFormatConvertFactory {
   
    private int formatType;
   
    public static final int TYPE_CSV=1;
    public static final int TYPE_XLS=2;
    
    private String sheetNo="";
    private PrintStream printStream=null;
    private CSVExcelHandler csvHandler=null;  
    private StringBuffer lineBuffer = null;
    private String line = "";
    private String delimiter = "";  
    
    private ArrayList<String> header;
    private ArrayList<String> columnHeader1;
    private ArrayList<String> columnHeader2;
    private ArrayList<Object> details;
    private ArrayList<String> summary;
    private ArrayList<Object> lineObjects;
    
    private ArrayList<Object> allDataArray;
    private Map<String,ArrayList<Object>> allDataArrayMap;
    
    private boolean showSuccesMsg=true;
    
    private String name;
    
    {
        allDataArray=new ArrayList<Object>();
    }
    
    public CSVExcelHandler getCSVHandler(){
        return csvHandler;
    }
    
    public FileFormatConvertFactory(String fileName){
        this.name=fileName;
       
    }
    
    public FileFormatConvertFactory(ArrayList<String> headers,
            ArrayList<String> columnHeader1,
            ArrayList<String> columnHeader2,
            ArrayList<Object>details, 
            ArrayList<String>summary,
            String fileName
             ){
        this.header=headers;
        this.columnHeader1=columnHeader1;
        this.columnHeader2=columnHeader2;
        this.details=details;
        this.summary=summary;
        this.name=fileName;
              
        allDataArray.add(this.header);
        allDataArray.add(this.columnHeader1);
        allDataArray.add(this.columnHeader2);
        allDataArray.add(this.details);
        allDataArray.add(this.summary);
    }
    
    public void reset(){
         allDataArray=new ArrayList<Object>();
         allDataArrayMap=null;
    }
    
    public void addDataList(ArrayList<Object> dataList){
        allDataArray.add(dataList);
    }
    
    public void addDataList(ArrayList<Object> dataList,String sheetNo){
        if(allDataArrayMap==null){
            allDataArrayMap=new LinkedHashMap<String,ArrayList<Object>>();
        }
            
        ArrayList<Object> allDataArrayT=allDataArrayMap.get(sheetNo);
        if(allDataArrayT==null){
            allDataArrayT=new ArrayList<Object>();
            allDataArrayMap.put(sheetNo,allDataArrayT);
        }
        allDataArrayT.add(dataList);
    }
    
    private ArrayList<String> zipPathFiles;
    
    public boolean copyFileToDirectory(String clientDir,String outfileName){
        try {
            FileOutputStream out=new FileOutputStream(clientDir+outfileName);
            File f = new File(outfileName);
            if(!f.exists()){
            	out.close();
                throw new FileNotFoundException();
            }
            FileInputStream in = new FileInputStream(f);
            byte[] b = new byte[1024];

                int count;

                while ((count = in.read(b)) > 0) {
                    System.out.println();

                    out.write(b, 0, count);
                }

           in.close();
           out.close();
           return true;
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
        
    }
    
    public void exportSingleReportFileZip(String fileName,String clientDir,int type){
        formatType=type;
        if(zipPathFiles==null){
            zipPathFiles=new ArrayList<String>();
        }
        String path=clientDir+fileName;
        System.out.println(""+path);
        processFile(path);
        zipPathFiles.add(path);
    }
    
    public void createZip(String clientDir){
        zipFile(zipPathFiles,clientDir);
    }
    
    public void deleteZipFilePaths(){
        for(String fileN : zipPathFiles){
            File file=new File(fileN);
            if(file.exists()){
                file.delete();
            }
        }
    }
    
     //neroshank
     private void zipFile(ArrayList<String> filenames,String clientDir) {

        String output = clientDir+name + ".zip";
        try {

            // out put file 
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));
            // name the file inside the zip  file 
            for (int i = 0; i < filenames.size(); i++) {
                File f = new File(filenames.get(i));
                if (!f.exists()) {
                    continue;
                }
                FileInputStream in = new FileInputStream(f);
                out.putNextEntry(new ZipEntry(filenames.get(i)));

                byte[] b = new byte[1024];

                int count;

                while ((count = in.read(b)) > 0) {
                    System.out.println();

                    out.write(b, 0, count);
                }

                in.close();
            }
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean exportReport(String clientDir,int type){
    	formatType=type;
    	if(formatType == TYPE_CSV)
    		name+=".csv";
    	
    	if(zipPathFiles==null){
    		return processFile(clientDir+"/"+name);
    	}else{
    		createZip("");
            deleteZipFilePaths();
            return copyFileToDirectory(clientDir, name);
    	}
    }
    
    
    private void dataFilterHandler(){
        switch(formatType){
            case TYPE_CSV :  lineBuffer.append(line);
                            printStream.println(lineBuffer.toString());
                            lineBuffer = new StringBuffer();
                            break;
            case TYPE_XLS : csvHandler.appendToArray(lineObjects, sheetNo);
                            break;
        }
    }
    
    private boolean isAddingRowIndex=false;
    public int skipColumns=-1;//specify on which column to start entering row data.  
    public void setSkipColumns(int skipColumns){
        this.skipColumns=skipColumns;
    }
    public int getSkipColumns(){
        return this.skipColumns;
    }
    
    private ArrayList<Object>  dataRowHandler(Object value,int rowCount,StringBuilder lineLocal){
        ArrayList<Object> lineData=new ArrayList<Object>();
        int rCount=0;//this variable helps to put data on 0 index or will start from 1
        if(value instanceof CommonDataObject){
            if(isAddingRowIndex){
                lineLocal.append(rowCount);
                lineData.add(new CellObject(rowCount, ""+rowCount));
                rCount++;
            }
            CommonDataObject object=(CommonDataObject)value;
            Object[] data=(Object[])object.getObjects();
            boolean isSticOn=true;//default made it true so its easy to handle the case
            for(Object v : data){
                if(this.skipColumns!=-1 && isSticOn){
                    if(rCount<this.skipColumns){
                        rCount++;
                        continue;
                    }else{
                        rCount=0;
                        isSticOn=false;
                    }
                }
                if(v instanceof CellObject){
                    CellObject t=(CellObject)v;
                    if(rCount>0){
                        lineLocal.append(delimiter);
                    }
                    lineLocal.append(t.getActualValue());
                    lineData.add(t);
                }
                rCount++;
            }
            
        }else if(value instanceof String){
            String t=(String)value;
            lineLocal.append(t);
        }
        return lineData;
        
    }
    
    private Map<String,ArrayList<Object>> externalData;
    
    public void addExternalSheetData(Map<String,ArrayList<Object>> externalData){
         this.externalData=externalData;
    }
    
    private boolean processFile(String directory){       
        boolean success = false;                        
        FileOutputStream outputFile;                  
        StringBuilder lineLocal = null;      
        if(formatType==TYPE_CSV){
            delimiter = ",";  
            try{
                outputFile = new FileOutputStream(directory);
                printStream = new PrintStream(outputFile);
                lineBuffer = new StringBuffer();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(formatType==TYPE_XLS){
            delimiter = ";";
            sheetNo="Sheet1";
            csvHandler=new CSVExcelHandler(directory,CSVExcelHandler.FILE_TYPE_XLS); 
            csvHandler.setDelimater(delimiter); 
            if(externalData!=null){
                Map<String,ArrayList<Object>> data=csvHandler.getSheetObjectData();
                if(data!=null){
                    for(String key : externalData.keySet()){
                        data.put(key, externalData.get(key));
                    }
                }
            }
        }
        
        try {
            if (formatType == TYPE_XLS && allDataArrayMap != null) {
                for (String sheet : allDataArrayMap.keySet()) {
                    Object objectT=allDataArrayMap.get(sheet);
                    ArrayList<Object> allDataArrayT=(ArrayList<Object>)objectT;
                    for(Object object : allDataArrayT){
                        ArrayList<Object> dataList = (ArrayList<Object>) object;
                        int rowCount = 1;
                        for (Object value : dataList) {
                            lineLocal = new StringBuilder();
                            lineObjects = dataRowHandler(value, rowCount,lineLocal);
                            line = lineLocal.toString();
                            csvHandler.appendToArray(lineObjects, sheet);
                            rowCount++;
                        }
                        rowCount = 1;
                    }
                }
            } else {
            	System.out.println("---------------------CSV data IN ");
                for (Object object : allDataArray) {
                    ArrayList<Object> dataList = (ArrayList<Object>) object;
                    int rowCount = 1;
                    for (Object value : dataList) {
                        lineLocal = new StringBuilder();
                        lineObjects = dataRowHandler(value, rowCount,lineLocal);
                        line = lineLocal.toString();
                        System.out.println("---------------------CSV data "+line);
                        dataFilterHandler();
                        rowCount++;
                    }
                    rowCount = 1;
                }
            }

           if(printStream!=null)
                printStream.close();
           if(formatType==TYPE_XLS){
             csvHandler.generateXlsx();
           }
           
           if(showSuccesMsg){
        	   
            //JOptionPane.showMessageDialog(null, Language.getLanguageString("REPORT_SAVE_CSV_FILE"));
           }
           return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
            //JOptionPane.showMessageDialog(null, Language.getLanguageString("ERROR_REPORT_SAVE_CSV_FILE"));
        }
    }

    /**
     * @return the showSuccesMsg
     */
    public boolean isShowSuccesMsg() {
        return showSuccesMsg;
    }

    /**
     * @param showSuccesMsg the showSuccesMsg to set
     */
    public void setShowSuccesMsg(boolean showSuccesMsg) {
        this.showSuccesMsg = showSuccesMsg;
    }

    /**
     * @return the isAddingRowIndex
     */
    public boolean isIsAddingRowIndex() {
        return isAddingRowIndex;
    }

    /**
     * @param isAddingRowIndex the isAddingRowIndex to set
     */
    public void setIsAddingRowIndex(boolean isAddingRowIndex) {
        this.isAddingRowIndex = isAddingRowIndex;
    }
    
   
}
