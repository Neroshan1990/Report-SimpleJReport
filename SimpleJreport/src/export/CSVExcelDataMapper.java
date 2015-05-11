/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 *
 * @author neroshan kalhara abeywickrama gunarathna
 * email : gnerosh@gmail.com
 */
public class CSVExcelDataMapper {

    private Map<String, TreeMap<String, CellObject>> allDataDBbyDate;
    //private TreeSet<String> allDataCategoriesTree;
    
    private LinkedHashSet<String> allDataCategoriesTree;
    private ArrayList<Object> allDataCategoriesList;
    private ArrayList<Object> dataListReport;
    private ArrayList<String> orderedAllCategoryListHeader = null;
    private ArrayList<String> orderedAllCategoryList = null;
    
    public static final String CONSTANT_CATEGORY_TOTAL="Total";
    public static final String CONSTANT_CATEGORY_SUB_TOTAL="Sub-Total";
    
    private boolean addTotal=false;
    public boolean fileType;
    private String constRemove="~~@#@.";
    private Helper helper = new Helper();

    CSVExcelDataMapper() {
        reset();
    }
    
    CSVExcelDataMapper(boolean addTotal) {
        this.addTotal=addTotal;
        reset();
       
    }
    
    public void reset(){
        c=0;
        injectedMap=null;
        dummyMapData=null;
        allDataDBbyDate = new LinkedHashMap<String, TreeMap<String, CellObject>>();
        allDataCategoriesTree = new LinkedHashSet<String>();
        //allDataCategoriesTree = new TreeSet<String>();
        allDataCategoriesList = new ArrayList<Object>();
        dataListReport = new ArrayList<Object>();
        orderedAllCategoryList = new ArrayList<String>();
         if (this.addTotal) {
            TreeMap<String, CellObject> dList = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
            if (dList == null) {
                dList = new TreeMap<String, CellObject>();
                allDataDBbyDate.put(CONSTANT_CATEGORY_TOTAL, dList);
            }

            //dList.put(proCatCode, netSales);
        }
    }
    
     public void filterReset(){
         c=0;
        this.removeColumns=null;
        orderedAllCategoryListHeader=null;
        fefcMapFormuleExist=null;
        fefcMapFormuleNonExist=null;
        
        injectedMap=null;
        dummyMapData=null;        
        allDataDBbyDate = new LinkedHashMap<String, TreeMap<String, CellObject>>();      
        allDataCategoriesTree = new LinkedHashSet<String>();
        //allDataCategoriesTree = new TreeSet<String>();
        allDataCategoriesList = new ArrayList<Object>();
        orderedAllCategoryList = new ArrayList<String>();
         if (this.addTotal) {
            TreeMap<String, CellObject> dList = allDataDBbyDate.get(CONSTANT_CATEGORY_SUB_TOTAL);
            if (dList == null) {
                dList = new TreeMap<String, CellObject>();
                allDataDBbyDate.put(CONSTANT_CATEGORY_SUB_TOTAL, dList);
            }
            //dList.put(proCatCode, netSales);
        }
         if(removeRepeatColMapper!=null){
             for(String rcol : removeRepeatColMapper.keySet()){
                 ArrayList<String> repeatedList=removeRepeatColMapper.get(rcol);
                 if(repeatedList!=null){
                     repeatedList.clear();
                 }
             }
         }
         
    }

    int c = 0;
    public void getDBdataForCrossTable(String sql, DatabaseManager db, String rowHeaderField, String colHeaderField, String valueField) {
        allDataCategoriesTree.clear();
        ResultSet rs = null;

        try {
            
            //for (String sql : queries) {
                rs = db.retrieve(sql);
                while (rs.next()) {
                    String proCatCode = rs.getString(colHeaderField);
                    if(colHeaderField.equalsIgnoreCase("Month")){
                        proCatCode = proCatCode.trim();
                    }
                    double netSales = rs.getDouble(valueField);
                    String outlet = rs.getString(rowHeaderField);
                    if (outlet != null) {
                        if (proCatCode == null) {
                            proCatCode = " ";
                        }
                    }
                    if (outlet != null && proCatCode != null) {
                        TreeMap<String, CellObject> dList = allDataDBbyDate.get(outlet);
                        if (dList == null) {
                            dList = new TreeMap<String, CellObject>();
                            allDataDBbyDate.put(outlet, dList);
                        }

                        dList.put(proCatCode, new CellObject(netSales, String.format("%.2f", netSales)));

                    }
                    if(addTotal){
                        TreeMap<String, CellObject> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
                        if(dListTot.containsKey(proCatCode)){
                            double currentNetSalesTot=Double.parseDouble(dListTot.get(proCatCode).toString())+netSales;
                            dListTot.put(proCatCode, new CellObject(currentNetSalesTot ,String.format("%.2f", currentNetSalesTot)));
                        }else{
                            dListTot.put(proCatCode, new CellObject(netSales,String.format("%.2f", netSales)));
                        }
                    }
                    if (proCatCode != null) {
                        allDataCategoriesTree.add(proCatCode);
                    }

                }
                if (c == 0) {
                    orderedAllCategoryList.add(" ");
                }
                for (String value : allDataCategoriesTree) {
                    orderedAllCategoryList.add(value);

                }
                
                c++;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
    
     public void getDBdataForCrossTable(String sql, DatabaseManager db, String rowHeaderField, String colHeaderField, String valueField,Map<String, String> keyPairColumns) {
        allDataCategoriesTree.clear();
        ResultSet rs = null;

        try {
            
            //for (String sql : queries) {
                rs = db.retrieve(sql);
                while (rs.next()) {
                    String proCatCode = rs.getString(colHeaderField);
                    double netSales = rs.getDouble(valueField);
                    String outlet = rs.getString(rowHeaderField);
                    if (outlet != null) {
                        if (proCatCode == null) {
                            proCatCode = " ";
                        }
                    }
                    if (outlet != null && proCatCode != null) {
                        TreeMap<String, CellObject> dList = allDataDBbyDate.get(outlet);
                        if (dList == null) {
                            dList = new TreeMap<String, CellObject>();
                            allDataDBbyDate.put(outlet, dList);
                        }

                        dList.put(proCatCode,new CellObject(netSales,String.format("%.2f", netSales)));

                    }
                    if(addTotal){
                        TreeMap<String, CellObject> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
                        if(dListTot.containsKey(proCatCode)){
                            double currentNetSalesTot=Double.parseDouble(dListTot.get(proCatCode).toString())+netSales;
                            dListTot.put(proCatCode,new CellObject(currentNetSalesTot, String.format("%.2f", currentNetSalesTot)));
                        }else{
                            dListTot.put(proCatCode, new CellObject(netSales,String.format("%.2f", netSales)));
                        }
                    }
                    if (proCatCode != null) {
                        allDataCategoriesTree.add(proCatCode);
                    }
                    
                     for (String field : keyPairColumns.keySet()) {
                        String valueName = keyPairColumns.get(field);
                        netSales = rs.getDouble(field);
                        if (outlet != null) {
                            if (valueName == null) {
                                valueName = " ";
                            }
                        }
                        if (outlet != null && valueName != null) {
                            TreeMap<String, CellObject> dList = allDataDBbyDate.get(outlet);
                            if (dList == null) {
                                dList = new TreeMap<String, CellObject>();
                                allDataDBbyDate.put(outlet, dList);
                            }

                            dList.put(valueName, new CellObject(netSales,String.format("%.2f",netSales)));

                        }
                        if (addTotal) {
                            TreeMap<String, CellObject> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
                            if (dListTot.containsKey(valueName)) {
                                double currentNetSalesTot = Double.parseDouble(dListTot.get(valueName).toString()) + netSales;
                                dListTot.put(valueName, new CellObject(currentNetSalesTot,String.format("%.2f", currentNetSalesTot)));
                            }else{
                                dListTot.put(valueName, new CellObject(netSales,String.format("%.2f", netSales)));
                            }
                        }
                        if (valueName != null) {
                            allDataCategoriesTree.add(valueName);
                        }
                    }

                }
                if (c == 0) {
                    orderedAllCategoryList.add(" ");
                }
                for (String value : allDataCategoriesTree) {
                    orderedAllCategoryList.add(value);

                }
               
                c++;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    private LinkedHashMap<String,String> injectedMap;
     
    public void injectMapData(LinkedHashMap<String, String> dList){
        injectedMap=new LinkedHashMap<String, String>();
        this.injectedMap.putAll(dList);
       
    }
    
    public LinkedHashMap<String,String> dummyMapData;
    public Map<String,Integer> orderHeaderMap;
    public boolean removeFirstRowGroupCol=false;
        
    //neroshank report mode checked
    public void getDBdataForCrossTable(String sql, DatabaseManager db, Map<String, String> keyPairIncremental,String columnHeader,String columnHeaderID,String rowHeader) {
         allDataCategoriesTree.clear();
         if(orderedAllCategoryListHeader==null){
             orderHeaderMap=new HashMap<String, Integer>();
             orderedAllCategoryListHeader=new ArrayList<String>();
             orderedAllCategoryListHeader.add(" ");
             if(orderedAllCategoryList!=null){
             for(int count=0;count<(orderedAllCategoryList.size()-1);count++){
                        orderedAllCategoryListHeader.add(" ");
                    }
             }
             
         }
        ResultSet rs = null;
        ResultSetMetaData rsmd=null;
        int countR=0;
        try {
            
            //for (String sql : queries) {
            rs = db.retrieve(sql);
            int colCount = 0;
            int colCountDummy = 0;
            //int resetColCount=0;
            if (dummyMapData != null) {
                for (String key : dummyMapData.keySet()) {
                    //set language translated value
                    orderedAllCategoryListHeader.add(dummyMapData.get(key));
                    for (int count = 0; count < (keyPairIncremental.size() - 1); count++) {
                        orderedAllCategoryListHeader.add(" ");
                    }

                }

            }
            while (rs.next()) {
                    String proCatCode = rs.getString(columnHeader);
                    if(proCatCode!=null)
                        proCatCode=proCatCode.trim();
                    else
                        proCatCode=" ";
                    if(columnHeaderID!=null){
                        String id=rs.getString(columnHeaderID);
                        proCatCode+=("("+id+")");
                    }
                    String outlet = rs.getString(rowHeader);
                    if (outlet != null) {
                        if (proCatCode == null) {
                            proCatCode = " ";
                        }
                    }
                    if(isRowGrouped){
                       if(prefixRowHeaders!=null){
                            StringBuilder strB=new StringBuilder();
                            for(String prefix : prefixRowHeaders){
                                if(rs.getString(prefix)!=null){
                                    strB.append(rs.getString(prefix));
                                }                                
                            }
                            strB.append(outlet);
                            outlet=strB.toString();
                        }else{
                            if(prefixeRowHeaderField==null){
                                outlet="#"+countR+"#"+outlet;
                                ++countR;
                            }else{
                                outlet="#"+rs.getString(prefixeRowHeaderField)+"#"+outlet;
                            }
                        }
                    }
                    boolean exist=false;
                    if (dummyMapData != null) {
                        colCount=0;
                        for (String key : dummyMapData.keySet()) {                           
                            if(key.contains(proCatCode)){
                                colCount=colCountDummy;   
                                exist=true;
                                break;
                            }
                            colCountDummy+=keyPairIncremental.size();
                        }
                        colCountDummy=0;
                        if(!exist){
                            dummyMapData.put(proCatCode, "0");
                            orderedAllCategoryListHeader.add(proCatCode);
                             for (int count = 0; count < (keyPairIncremental.size() - 1); count++) {
                                orderedAllCategoryListHeader.add(" ");
                            }
                        }
                        
                        for(String key : dummyMapData.keySet()){
                             if(key.contains(proCatCode)){
                                colCount=colCountDummy;   
                                exist=true;
                            }
                            for (String field : keyPairIncremental.keySet()) {
                                String valueName = keyPairIncremental.get(field);
                                valueName=valueName.trim();
                                TreeMap<String, CellObject> dList = allDataDBbyDate.get(outlet);
                                if (dList == null) {
                                    dList = new TreeMap<String, CellObject>();
                                    allDataDBbyDate.put(outlet, dList);
                                }
                                //prefix for unique identity
                                  try {
                                    if (dList.containsKey("#" + colCountDummy + "#" + valueName) && 
                                            dList.get("#" + colCountDummy + "#" + valueName) != null) {
                                        //System.out.println("------------------------------------count" + valueName);
                                      
                                    } else {
                                        dList.put("#" + colCountDummy + "#" + valueName, new CellObject(new Double(0), "0"));
                                    }
                                } catch (Exception e) {
                                    dList.put("#" + colCountDummy + "#" + valueName, new CellObject(new Double(0),"0"));
                                }
                                //dList.put("#" + colCountDummy + "#" + valueName, "");
                                if (valueName != null) {
                                    allDataCategoriesTree.add("#" + colCountDummy + "#" + valueName);
                                }
                               colCountDummy++;
                            }
                             
                        }
                        colCountDummy=0;
                    }
                    else{
                    //colCountDummy=0;
                        
                        for(String header:orderHeaderMap.keySet()){
                            if(header.trim().equals(proCatCode.trim())){
                                exist=true;
                                break;
                            }
                        }
                        if(exist){
                            //resetColCount=colCount;
                            try {
                                colCount=orderHeaderMap.get(proCatCode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                            //System.out.println("------------------------------------count"+colCount);
                        }else{
                            //if (dummyMapData == null){
                                int maxCount=0;
                                for(String header:orderHeaderMap.keySet()){
                                    maxCount+=orderHeaderMap.get(header);
                                }
                                colCount=maxCount+2;
                                orderHeaderMap.put(proCatCode, colCount);
                                orderedAllCategoryListHeader.add(proCatCode);
                                for (int count = 0; count < (keyPairIncremental.size() - 1); count++) {
                                    orderedAllCategoryListHeader.add(" ");
                                }
                            ///}
                        }
                       
                    }
                   
                    for(String field : keyPairIncremental.keySet()){
                        String valueName = keyPairIncremental.get(field);
                        valueName=valueName.trim();
                        String defaultVal="";
                        double netSales = 0;
                        long numeric=0;
                        boolean isNumeric = false;
                        Object obj = rs.getObject(field);
                        try {
                            defaultVal=rs.getString(field);
                            if(obj instanceof Double){
                                netSales = (Double)obj;//Double.parseDouble(defaultVal);
                                defaultVal = String.format("%.2f", netSales);
                                isNumeric=true;
                            }else if(obj instanceof Integer || obj instanceof Long || obj instanceof Short || obj instanceof BigDecimal){
                                numeric=Long.parseLong(defaultVal);
                                obj=numeric;
                                isNumeric=true;
                            }
                            
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        if (outlet != null && valueName != null) {
                            TreeMap<String, CellObject> dList = allDataDBbyDate.get(outlet);
                            if (dList == null) {
                                dList = new TreeMap<String, CellObject>();
                                allDataDBbyDate.put(outlet, dList);
                            }
                            //prefix for unique identity
                            //dList.put("#"+colCount+"#"+valueName, defaultVal);
                            try {
                                if (dList.containsKey("#" + colCount + "#" + valueName) && dList.get("#" + colCount + "#" + valueName) != null && isNumeric) {
                                    //System.out.println("------------------------------------count" + valueName);
                                    if (obj instanceof Double) {
                                        double currentNetSalesTot = Double.parseDouble(dList.get("#" + colCount + "#" + valueName).toString()) + netSales;
                                        dList.put("#" + colCount + "#" + valueName,new CellObject(currentNetSalesTot, String.format("%.2f", currentNetSalesTot)));
                                    } else {
                                        long allCount = Long.parseLong(dList.get("#" + colCount + "#" + valueName).toString()) + numeric;
                                        dList.put("#" + colCount + "#" + valueName, new CellObject(allCount,"" + allCount));
                                    }
                                } else {
                                    dList.put("#" + colCount + "#" + valueName,new CellObject(obj, defaultVal));
                                }
                            } catch (Exception e) {
                                dList.put("#"+colCount+"#"+valueName, new CellObject(defaultVal,defaultVal));
                            }
                           

                        }
                        if(addTotal && isNumeric){
                            TreeMap<String, CellObject> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
                            if(dListTot.containsKey("#"+colCount+"#"+valueName)){
                                if(obj instanceof Double){
                                    double currentNetSalesTot=Double.parseDouble(dListTot.get("#"+colCount+"#"+valueName).toString())+netSales;
                                    dListTot.put("#"+colCount+"#"+valueName, new CellObject(currentNetSalesTot,String.format("%.2f", currentNetSalesTot)));
                                }else{
                                    long allCount=Long.parseLong(dListTot.get("#"+colCount+"#"+valueName).toString())+numeric;
                                    dListTot.put("#"+colCount+"#"+valueName, new CellObject(allCount,""+allCount));
                                }
                            }else{
                                if(obj instanceof Double){
                                    dListTot.put("#"+colCount+"#"+valueName, new CellObject(netSales,String.format("%.2f", netSales)));
                                }else{
                                    dListTot.put("#"+colCount+"#"+valueName,new CellObject(numeric, ""+numeric));
                                }
                            }
                        }
                        if (valueName != null && !exist) {
                            allDataCategoriesTree.add("#"+colCount+"#"+valueName);
                        }
                        //if (dummyMapData == null){
                        //if(!exist){
                            colCount++;
                        //}else{
                        //    colCount=resetColCount;
                        //}
                        //}
                    }

                }
              
                if (c == 0) {
                    orderedAllCategoryList.add(" ");
                }
                for (String value : allDataCategoriesTree) {
                    orderedAllCategoryList.add(value);

                }
                
                c++;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void totalMechanismHandler(TreeMap<String, CellObject> dListTot,long numeric,Object obj,String valueName,double netSales){
       //TreeMap<String, String> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
        if (dListTot.containsKey(valueName)) {
            if (obj instanceof Double) {
                double currentNetSalesTot = Double.parseDouble(dListTot.get(valueName).toString()) + netSales;
                dListTot.put(valueName, new CellObject(currentNetSalesTot,String.format("%.2f", currentNetSalesTot)));
            } else {
                long allCount = Long.parseLong(dListTot.get(valueName).toString()) + numeric;
                dListTot.put(valueName, new CellObject(allCount,"" + allCount));
            }
        } else {
            if (obj instanceof Double) {
                dListTot.put(valueName, new CellObject(netSales,String.format("%.2f", netSales)));
            } else {
                dListTot.put(valueName, new CellObject(numeric,"" + numeric));
            }
        }
    }
    
    public String addTotAfterColVal;
    private Map<String,ArrayList<String>> removeRepeatColMapper;
    
    public void addRemoveRepeatedCols(ArrayList<String> removeRepeatCols){
        if(removeRepeatCols!=null){
            removeRepeatColMapper=new HashMap<String,ArrayList<String>>();
            for(String rcol : removeRepeatCols){
                removeRepeatColMapper.put(rcol,new ArrayList<String>());
            }
        }else{
            removeRepeatColMapper=null;
        }
    }
    
    //neroshank
    public boolean getDBdataForCrossTable(String sql, DatabaseManager db, Map<String, String> keyPair) {
         if(removeRepeatColVal!=null){
            repeatedColVals=new ArrayList<String>();
        }
        if(addTotAfterColVal!=null && addTotAfterColVal.length()>0){
            TreeMap<String, CellObject> dList = allDataDBbyDate.get(CONSTANT_CATEGORY_SUB_TOTAL);
            if (dList == null) {
                dList = new TreeMap<String, CellObject>();
                allDataDBbyDate.put(CONSTANT_CATEGORY_SUB_TOTAL, dList);
            }
        }
        allDataCategoriesTree.clear();
        ResultSet rs = null;
        boolean hasData=false;
        String changeStr=null;
        int totalNoRowCount=0;
        int count2=0;
        try {
            
            //for (String sql : queries) {
                rs = db.retrieve(sql);
                if (rs.next()) {
                    rs.last();
                    totalNoRowCount = rs.getRow();
                    rs.beforeFirst();
                }
                int rowCounter=1;
                while (rs.next()) {
                    count2++;
                    hasData=true;
                    //String proCatCode = rs.getString("productgroupcode");
                    //double netSales = rs.getDouble("netsales");
                    String outlet = constRemove+rowCounter;
                    for (String field : keyPair.keySet()) {
                        String valueName = keyPair.get(field);
                        String defaultVal="";
                        double netSales =0;
                        long numeric=0;
                        boolean isNumeric=false;
                        Object obj = rs.getObject(field);
                        try {
                            defaultVal=rs.getString(field);
                            if(obj instanceof Double){
                                netSales = (Double)obj;//Double.parseDouble(defaultVal);
                                defaultVal = String.format("%.2f", netSales);
                                isNumeric=true;
                            }else if(obj instanceof Integer || obj instanceof Long || obj instanceof Short || obj instanceof BigDecimal){
                                numeric=Long.parseLong(defaultVal);
                                obj=numeric;
                                isNumeric=true;
                            }
                             String orginDefaultVal=defaultVal;
                             //Remove repeat columns
                                if(removeRepeatColVal!=null && removeRepeatColVal.equals(field)){
                                    boolean addRCV=true;
                                    for(String repeat:repeatedColVals){                         
                                        //System.out.println("Repeat "+repeat +" "+removeRepeatColVal+" ---------"+field);
                                        if(repeat.equals(defaultVal)){
                                            //System.out.println("Repeat "+repeat +" "+" "+defaultVal+" "+removeRepeatColVal+" ---------"+field);
                                            defaultVal=constRemove+defaultVal;
                                            addRCV=false;
                                            break;
                                        }
                                    }
                                    if(defaultVal!=null && addRCV)
                                    repeatedColVals.add(defaultVal);
                                    
                                }
                                //neroshank new repeat col multi function
                                if(removeRepeatColMapper!=null){
                                    for(String fieldCol : removeRepeatColMapper.keySet()){
                                        if (fieldCol != null && fieldCol.equals(field)) {
                                            ArrayList<String> repeatedColValsL=removeRepeatColMapper.get(fieldCol);
                                            boolean addRCV = true;
                                            for (String repeat : repeatedColValsL) {
                                                //System.out.println("Repeat "+repeat +" "+removeRepeatColVal+" ---------"+field);
                                                if (repeat.equals(defaultVal)) {
                                                    //System.out.println("Repeat "+repeat +" "+" "+defaultVal+" "+removeRepeatColVal+" ---------"+field);
                                                    defaultVal = constRemove + defaultVal;
                                                    addRCV = false;
                                                    break;
                                                }
                                            }
                                            if (defaultVal != null && addRCV) {
                                                repeatedColValsL.add(defaultVal);
                                            }

                                        }
                                    }
                                }
                            
                            //Add total after a specific row group
                            if (addTotAfterColVal != null && addTotAfterColVal.equals(field)) {
                              
                               if (changeStr != null && !changeStr.equalsIgnoreCase(defaultVal)) {
                                   if (c == 0) {
                                       orderedAllCategoryList.add(" ");
                                   }
                                   for (String value : allDataCategoriesTree) {
                                       orderedAllCategoryList.add(value);

                                   }

                                   c++;
                                   ArrayList<String> removeColumns=this.removeColumns;
                                   //neroshank extract tot before reset
                                    TreeMap<String, CellObject> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
                                    allDataDBbyDate.remove(CONSTANT_CATEGORY_TOTAL);
                                    fillReportData();
                                    filterReset();
                                    allDataDBbyDate.put(CONSTANT_CATEGORY_TOTAL,dListTot);
                                    rowCounter=1;
                                    outlet = constRemove+rowCounter;
                                    changeStr=defaultVal;
                                    if(removeColumns!=null){
                                        this.removeColumns=removeColumns;
                                    }
                               }
                               //System.out.println("-----------Not Add totoal "+defaultVal+" changeStr "+changeStr);
                                
                               changeStr=defaultVal;
                               
                                
                            }
                            
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        if (outlet != null) {
                            if (valueName == null) {
                                valueName = " ";
                            }
                        }
                        if (outlet != null && valueName != null) {
                            TreeMap<String, CellObject> dList = allDataDBbyDate.get(outlet);
                            if (dList == null) {
                                dList = new TreeMap<String, CellObject>();
                                allDataDBbyDate.put(outlet, dList);
                            }

                            dList.put(valueName, new CellObject(obj,defaultVal));

                        }
                        if (addTotal && isNumeric) {
                            TreeMap<String, CellObject> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
                            totalMechanismHandler(dListTot, numeric, obj, valueName, netSales);
                            dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_SUB_TOTAL);
                            if(dListTot!=null){
                                totalMechanismHandler(dListTot, numeric, obj, valueName, netSales);
                            }
                            //neroshank moved
//                            if (dListTot.containsKey(valueName)) {
//                                if(obj instanceof Double){
//                                    double currentNetSalesTot = Double.parseDouble(dListTot.get(valueName)) + netSales;
//                                    dListTot.put(valueName, String.format("%.2f", currentNetSalesTot));
//                                }else{
//                                    long allCount=Long.parseLong(dListTot.get(valueName))+numeric;
//                                    dListTot.put(valueName, ""+allCount);
//                                }
//                            }else{
//                                if(obj instanceof Double){
//                                    dListTot.put(valueName, String.format("%.2f", netSales));
//                                 }else{
//                                    dListTot.put(valueName, ""+numeric);
//                                }    
//                            }
                        }
                        if (valueName != null) {
                            allDataCategoriesTree.add(valueName);
                        }
                    }
                    ++rowCounter;
                }
                if (c == 0) {
                    orderedAllCategoryList.add(" ");
                }
                for (String value : allDataCategoriesTree) {
                    orderedAllCategoryList.add(value);

                }
               
                c++;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasData;
    }
    
    private ArrayList<String> prefixRowHeaders;
    public void addPrefixRowHeaders(ArrayList<String> prefixRowHeaders){
        this.prefixRowHeaders=prefixRowHeaders;
    }
    
    private ArrayList<String> repeatedColVals;
    public String removeRepeatColVal;
    public String prefixeRowHeaderField;
    public boolean isRowGrouped=false;
    //private 
    public boolean getDBdataForCrossTable(String sql, DatabaseManager db, String rowHeaderField, Map<String, String> keyPair) {
        boolean found = false;
        if(removeRepeatColVal!=null){
            repeatedColVals=new ArrayList<String>();
        }
        allDataCategoriesTree.clear();
        ResultSet rs = null;
        String changeStr=null;
        int countR=0;
        try {
            
            //for (String sql : queries) {
                rs = db.retrieve(sql);
                while (rs.next()) {
                    found = true;
                    //String proCatCode = rs.getString("productgroupcode");
                    //double netSales = rs.getDouble("netsales");
                    String outlet = rs.getString(rowHeaderField);
                    if(isRowGrouped){
                        if(prefixRowHeaders!=null){
                            StringBuilder strB=new StringBuilder();
                            for(String prefix : prefixRowHeaders){
                                if(rs.getString(prefix)!=null){
                                    strB.append(rs.getString(prefix));
                                }                                
                            }
                            strB.append(outlet);
                            outlet=strB.toString();
                        }else{
                            if(prefixeRowHeaderField==null){
                                outlet="#"+countR+"#"+outlet;
                                ++countR;
                            }else{
                                outlet="#"+rs.getString(prefixeRowHeaderField)+"#"+outlet;
                            }
                        }
                    }
                    for (String field : keyPair.keySet()) {
                       String valueName = keyPair.get(field);
                        String defaultVal="";
                        double netSales =0;
                        long numeric=0;
                        boolean isNumeric=false;
                        Object obj = rs.getObject(field);
                        try {
                            defaultVal=rs.getString(field);
                            if(obj instanceof Double){
                                netSales = (Double)obj;//Double.parseDouble(defaultVal);
                                defaultVal = String.format("%.2f", netSales);
                                //defaultVal= helper.getDecimalFormat(netSales, d)
                                isNumeric=true;
                            }else if(obj instanceof Integer || obj instanceof Long || obj instanceof Short || obj instanceof BigDecimal){
                                numeric=Long.parseLong(defaultVal);
                                obj=numeric;
                                isNumeric=true;
                            }
                            String orginDefaultVal=defaultVal;
                                if(removeRepeatColVal!=null && removeRepeatColVal.equals(field)){
                                    boolean addRCV=true;
                                    for(String repeat:repeatedColVals){                         
                                        //System.out.println("Repeat "+repeat +" "+removeRepeatColVal+" ---------"+field);
                                        if(repeat.equals(defaultVal)){
                                            //System.out.println("Repeat "+repeat +" "+" "+defaultVal+" "+removeRepeatColVal+" ---------"+field);
                                            defaultVal=constRemove+defaultVal;
                                            addRCV=false;
                                            break;
                                        }
                                    }
                                    if(defaultVal!=null && addRCV)
                                    repeatedColVals.add(defaultVal);
                                    
                                }
                                
                                 //neroshank new repeat col multi function
                                if(removeRepeatColMapper!=null){
                                    for(String fieldCol : removeRepeatColMapper.keySet()){
                                        if (fieldCol != null && fieldCol.equals(field)) {
                                            ArrayList<String> repeatedColValsL=removeRepeatColMapper.get(fieldCol);
                                            boolean addRCV = true;
                                            for (String repeat : repeatedColValsL) {
                                                //System.out.println("Repeat "+repeat +" "+removeRepeatColVal+" ---------"+field);
                                                if (repeat.equals(defaultVal)) {
                                                    //System.out.println("Repeat "+repeat +" "+" "+defaultVal+" "+removeRepeatColVal+" ---------"+field);
                                                    defaultVal = constRemove + defaultVal;
                                                    addRCV = false;
                                                    break;
                                                }
                                            }
                                            if (defaultVal != null && addRCV) {
                                                repeatedColValsL.add(defaultVal);
                                            }

                                        }
                                    }
                                }
                                
                                 //Add total after a specific row group
                            if (addTotAfterColVal != null && addTotAfterColVal.equals(field)) {
                              
                               if (changeStr != null && !changeStr.equalsIgnoreCase(orginDefaultVal)) {
                                   if (c == 0) {
                                       orderedAllCategoryList.add(" ");
                                   }
                                   for (String value : allDataCategoriesTree) {
                                       orderedAllCategoryList.add(value);

                                   }

                                   c++;
                                    fillReportData();
                                    filterReset();
                                    countR=1;
                                    outlet = constRemove+countR;
                                    changeStr=orginDefaultVal;
                               }
                               //System.out.println("-----------Not Add totoal "+defaultVal+" changeStr "+changeStr);
                                
                               changeStr=orginDefaultVal;
                               
                                
                            }
                                              
                            
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        if (outlet != null && valueName != null) {
                            TreeMap<String, CellObject> dList = allDataDBbyDate.get(outlet);
                            if (dList == null) {
                                dList = new TreeMap<String, CellObject>();
                                allDataDBbyDate.put(outlet, dList);
                            }
                            if((removeRepeatColVal!=null && removeRepeatColVal.equals(field) || removeRepeatColMapper!=null && multiRemoveColsExist(field)) && defaultVal.contains(constRemove) 
                                    && dList.get(valueName)!=null ){
                                String val=dList.get(valueName).toString();
                                if(val.contains(constRemove)){
                                    dList.put(valueName, new CellObject(obj,defaultVal));
                                }
                            }else{
                                try {
                                    if (dList.containsKey(valueName) && dList.get(valueName) != null && isNumeric) {
                                        //System.out.println("------------------------------------count" + valueName);
                                        if (obj instanceof Double) {
                                            double currentNetSalesTot = Double.parseDouble(dList.get(valueName).toString()) + netSales;
                                            dList.put(valueName, new CellObject(currentNetSalesTot,String.format("%.2f", currentNetSalesTot)));
                                        } else {
                                            long allCount = Long.parseLong(dList.get(valueName).toString()) + numeric;
                                            dList.put(valueName, new CellObject(allCount,"" + allCount));
                                        }
                                    } else {
                                        dList.put(valueName, new CellObject(obj,defaultVal));
                                    }
                                } catch (Exception e) {
                                    dList.put(valueName, new CellObject(obj,defaultVal));
                                }
                                //dList.put(valueName, defaultVal);
                            }

                        }
                        if (addTotal && isNumeric) {
                            TreeMap<String, CellObject> dListTot = allDataDBbyDate.get(CONSTANT_CATEGORY_TOTAL);
                            if (dListTot.containsKey(valueName)) {
                                if(obj instanceof Double){
                                    double currentNetSalesTot = Double.parseDouble(dListTot.get(valueName).toString()) + netSales;
                                    dListTot.put(valueName, new CellObject(currentNetSalesTot,String.format("%.2f", currentNetSalesTot)));
                                }else{
                                    long allCount=Long.parseLong(dListTot.get(valueName).toString())+numeric;
                                    dListTot.put(valueName, new CellObject(allCount,""+allCount));
                                }
                            }else{
                                if(obj instanceof Double){
                                    dListTot.put(valueName, new CellObject(netSales,String.format("%.2f", netSales)));
                                 }else{
                                    dListTot.put(valueName, new CellObject(numeric,""+numeric));
                                }    
                            }
                        }
                        if (valueName != null) {
                            allDataCategoriesTree.add(valueName);
                        }
                    }

                }
                
                if (c == 0) {
                    orderedAllCategoryList.add(" ");
                }
                for (String value : allDataCategoriesTree) {
                    orderedAllCategoryList.add(value);

                }
              
                c++;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return found;
    }
    
    private boolean multiRemoveColsExist(String field){
        boolean status=false;
        for(String f : removeRepeatColMapper.keySet()){
            if(f.equals(field)){
                status=true;
                break;
            }
        }
        return status;
    }
    
   
    private ArrayList<FromulaData> fefcMapFormuleExist;
    private ArrayList<FromulaData> fefcMapFormuleNonExist;
    
   
    private ArrayList<String> removeColumns;
    
    public void removeColumns(ArrayList<String> removeColumns){
        this.removeColumns=removeColumns;
    }
    
    public void formulaEquationForColumns( ArrayList<String> fefc,String newCatName,boolean isExistColumn){
        if(isExistColumn){
            if(fefcMapFormuleExist==null)
                fefcMapFormuleExist=new ArrayList<FromulaData>();
            
            fefcMapFormuleExist.add(new FromulaData(newCatName,fefc));
        }else{
             if(fefcMapFormuleNonExist==null)
                fefcMapFormuleNonExist=new ArrayList<FromulaData>();
            orderedAllCategoryList.add(newCatName);
            fefcMapFormuleNonExist.add(new FromulaData(newCatName,fefc));
        }
    }
    
    
    private double formulaProcess(String column,double formulaTot,TreeMap<String, CellObject> dList,int increase,ArrayList<String> fefc){
        double currentVal=0;
        for(int i=0;i<fefc.size();i+=2){
            try{
                String ccol=fefc.get(i);
                if(ccol.equals(column)){
                    try{
                        currentVal=Double.parseDouble(dList.get(column).toString());
                    }catch(Exception e){
                    }
                    if(increase==0){
                        formulaTot=currentVal;
                        continue;
                    }
                    if(i>0){
                        String symbol=fefc.get(i-1);
                        if(symbol.equals("-")){
                            formulaTot-=currentVal;
                        }
                        if(symbol.equals("+")){
                            formulaTot+=currentVal;
                        }
                    }else{
                        formulaTot+=currentVal;
                    }
                    
                }
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("-----------------------------------------------------"+column);
            }
        }
        return formulaTot;
    }
    public static int TYPE_COL_PRIMITIVE=0;
    public static int TYPE_COL_CHAR=1; 
    private Map<String,Integer> colTypesMapper;
    private void proceedColTypesMapper(){
        colTypesMapper=new HashMap<String,Integer>();
        for(String cat : orderedAllCategoryList){
            boolean modified=false;
            colTypesMapper.put(cat, TYPE_COL_CHAR);
            for(String key : allDataDBbyDate.keySet()){
                TreeMap<String, CellObject> dList = allDataDBbyDate.get(key);
                for(String catSub : dList.keySet()){
                    if(cat.equalsIgnoreCase(catSub)){
                        modified=true;
                        String val=dList.get(catSub).toString();
                        if(val==null){
                            continue;
                        }
                        val=val.trim();
                        if(isPrimitive(val,false) || val.equals("0")){
                            colTypesMapper.put(catSub, TYPE_COL_PRIMITIVE);
                        }else{
                            try {
                                if (colTypesMapper.get(catSub)!=null && colTypesMapper.get(catSub) == TYPE_COL_PRIMITIVE && val.length()<1) {

                                } else {
                                    colTypesMapper.put(catSub, TYPE_COL_CHAR);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("----------------"+catSub);
                                colTypesMapper.put(catSub, TYPE_COL_CHAR);
                            }
                                      
                        }
                    }
                }
            }
            if(!modified){
                colTypesMapper.put(cat, TYPE_COL_PRIMITIVE);
            }
        }
    }
    public void addMainHeader(ArrayList<String> mainHeaders){
        ArrayList<Object> row=new ArrayList<Object>();
        CommonDataObject dataRowCatHead = new CommonDataObject(mainHeaders.toArray());
        row.add(dataRowCatHead);
        dataListReport.addAll(row);
    }
    public boolean isSorted=false;
    public ArrayList<Object> fillReportData() {
  
        if(injectedMap!=null){
           ArrayList<String> tempOrder=new ArrayList<String>();
           for(int i = 0 ; i<orderedAllCategoryList.size();i++){
               String order  = orderedAllCategoryList.get(i);
               if(injectedMap.containsKey(order.trim())){
                   tempOrder.add(order);
               }
           }
           for(String order:tempOrder){
               orderedAllCategoryList.remove(order);
           }
           orderedAllCategoryList.addAll(injectedMap.keySet());
        }
        proceedColTypesMapper();
        if(orderedAllCategoryListHeader!=null){
            CommonDataObject dataRowCatHead = new CommonDataObject(orderedAllCategoryListHeader.toArray());
            allDataCategoriesList.add(dataRowCatHead);
        }
        ArrayList<String> formatedorderedAllCategoryList=new ArrayList<String>();
        for(String cat : orderedAllCategoryList){
            boolean status=false;
            if (this.removeColumns != null) {
                for (String col : removeColumns) {
                    if(cat.equals(col)){
                        status=true;
                        break;
                    }
                }
            }
            if(status)
                continue;
            String[] pair=cat.split("#\\d*#");
            String currentCat=(pair!=null && pair.length==2?pair[1]:cat);
            if(injectedMap!=null){
                String langTransCat=injectedMap.get(currentCat);
                if(langTransCat!=null){
                    currentCat=langTransCat;
                }
            }
            formatedorderedAllCategoryList.add(currentCat);
        }
        CommonDataObject dataRowCat = new CommonDataObject(formatedorderedAllCategoryList.toArray());
        allDataCategoriesList.add(dataRowCat);
        dataListReport.addAll(allDataCategoriesList);
        //Total data
        CommonDataObject dataRTot=null;
        //Sub Total data
        CommonDataObject dataRSubTot=null;
        
        ArrayList<String> enumeratedKeys=new ArrayList<String>();
        if(isSorted){
            SortedSet<String> keys = new TreeSet<String>(allDataDBbyDate.keySet());
            //enumeratedKeys.addAll(keys);
            for(String keySoted : keys){
                enumeratedKeys.add(keySoted);
            }
            isSorted=false;
        }else{
            enumeratedKeys.addAll(allDataDBbyDate.keySet());
        }
        for (String key : enumeratedKeys) {
            ArrayList<Object> dataRow = new ArrayList<Object>();
            if(!key.contains(constRemove) && !(removeFirstRowGroupCol && !key.contains(CONSTANT_CATEGORY_TOTAL))){
               dataRow.add(key);
            }else{
                dataRow.add(" ");
            }
           
            //neroshank dispatch
            if (fefcMapFormuleNonExist != null) {
                for (FromulaData data : fefcMapFormuleNonExist) {
                   data.formulaTot=0;
                   data.increase=0;
                }
            }
            //neroshank dispatch
            if (fefcMapFormuleExist != null) {
                for (FromulaData data : fefcMapFormuleExist) {
                   data.formulaTot=0;
                   data.increase=0;
                }
            }
            
            TreeMap<String, CellObject> dList = allDataDBbyDate.get(key);
           
            if (fefcMapFormuleNonExist != null) {
                //            //neroshank dispatch
                for (FromulaData data : fefcMapFormuleNonExist) {
                    if (data.getFefc() != null && data.getNewCatName() != null) {
                        dList.put(data.getNewCatName(),new CellObject(new Double(0.0), "0.0"));
                    }
                }
            }
            if (fefcMapFormuleExist != null) {
                //neroshank dispatch 
                for (FromulaData data : fefcMapFormuleExist) {
                    ArrayList<String> fefcT = data.getFefc();
                    String newCatNameT = data.getNewCatName();
                    if (fefcT != null && newCatNameT != null) {
                        for (int i = 0; i < fefcT.size(); i += 2) {
                            String k = fefcT.get(i);
                            data.formulaTot = formulaProcess(k, data.formulaTot, dList, data.increase, fefcT);
                            ++data.increase;
                        }
                        System.out.println("" + dList.put(newCatNameT,new CellObject(data.formulaTot,helper.getDecimalFormat(data.formulaTot, fileType))));
                        System.out.println(data.formulaTot + "-------------after" + dList.get(newCatNameT));
                    }
                }
            }
             
           
            for (String k : orderedAllCategoryList) {
                if(fefcMapFormuleNonExist!=null){ 
                    //neroshank dispatch 
                    for (FromulaData data : fefcMapFormuleNonExist) {
                        ArrayList<String> fefcT = data.getFefc();
                        String newCatNameT = data.getNewCatName();
                        if (fefcT != null && newCatNameT != null && !k.equals(newCatNameT)) {
                            data.formulaTot = formulaProcess(k, data.formulaTot, dList, data.increase, fefcT);
                            ++data.increase;
                            dList.put(newCatNameT, new CellObject(data.formulaTot,helper.getDecimalFormat(data.formulaTot, fileType)));
                        }
                    }
                }

                boolean status = false;
                if (this.removeColumns != null) {
                    for (String col : removeColumns) {
                        if (k.equals(col)) {
                            status = true;
                            break;
                        }
                    }
                }
                if (status) {
                    continue;
                }
                if (dList.get(k) != null) {
                    CellObject cellObject = dList.get(k);
                     if(cellObject!=null && cellObject.toString()!=null && !cellObject.toString().contains(constRemove)){
                        String val=cellObject.toString();
                        boolean state=isPrimitive(val,true);
                        try {
                             if(state){
                                 Double dVal=Double.parseDouble(val);
                                 val=helper.getDecimalFormat(dVal,fileType);
                             }
                        } catch (Exception e) {
                        }
                        //dataRow.add(dList.get(k));
                        if(val.length()<1){
                            if (colTypesMapper != null && colTypesMapper.get(k) != null && colTypesMapper.get(k) == TYPE_COL_PRIMITIVE) {
                                dataRow.add(new CellObject(new Double(0), "0"));
                            } else {
                                dataRow.add(new CellObject("",""));
                            }
                        }else{
                            dataRow.add(cellObject);
                        }
                    }else{
                         if (colTypesMapper != null &&  colTypesMapper.get(k)!=null && colTypesMapper.get(k) == TYPE_COL_PRIMITIVE) {
                             dataRow.add(new CellObject(new Double(0), "0"));
                         } else {
                             dataRow.add(new CellObject("",""));
                         }
                    }
                    //dataRow.add(dList.get(k));
                } else {
                    //continue;
                    if (colTypesMapper != null &&  colTypesMapper.get(k)!=null && colTypesMapper.get(k) == TYPE_COL_PRIMITIVE) {
                        dataRow.add(new CellObject(new Double(0), "0"));
                    } else {
                        dataRow.add(new CellObject("",""));
                    }
                }
            }
            dataRow.remove(1);
            if(!key.equals(CONSTANT_CATEGORY_TOTAL) && !key.equals(CONSTANT_CATEGORY_SUB_TOTAL)){
                CommonDataObject dataR = new CommonDataObject(dataRow.toArray());
                dataListReport.add(dataR);
            }else if(key.equals(CONSTANT_CATEGORY_TOTAL)){
                dataRTot = new CommonDataObject(dataRow.toArray());
            }else if(key.equals(CONSTANT_CATEGORY_SUB_TOTAL)){
                dataRSubTot = new CommonDataObject(dataRow.toArray());
            }
        }
        //adding sub total
        if(dataRSubTot!=null){
            dataListReport.add(new CommonDataObject(new CellObject[]{new CellObject(" "," ")}));
            dataListReport.add(dataRSubTot);
            dataListReport.add(new CommonDataObject(new CellObject[]{new CellObject(" "," ")}));
        }
        //adding total
        if(dataRTot!=null){
            dataListReport.add(new CommonDataObject(new CellObject[]{new CellObject(" "," ")}));
            dataListReport.add(dataRTot);
            dataListReport.add(new CommonDataObject(new CellObject[]{new CellObject(" "," ")}));
        }
       
        c=0;
        injectedMap=null;
        dummyMapData=null;
        return dataListReport;
    }
    
     private boolean isPrimitive(String value,boolean isFloatOnly){
        boolean status=true;
        if(isFloatOnly){
            if(value.length()<1 || !value.contains("."))
              return false;
        }else{
            if(value.length()<1)
              return false;
        }
        for(int i = 0;i<value.length();i++){
            char c=value.charAt(i);
            if(Character.isDigit(c) || c=='.'){
                
            }else{
                status=false;
                break;
            }
        }
        return status;
    }
     
    
    public static void main(String[] args) {
        String test ="#1#Total Amount\n" +
"é�¢å€¼æ€»é¢�";
        System.out.println(""+test.split("#\\d*#")[1]);
    }

    /**
     * @return the orderedAllCategoryListHeader
     */
    public ArrayList<String> getOrderedAllCategoryListHeader() {
        return orderedAllCategoryListHeader;
    }

    /**
     * @param orderedAllCategoryListHeader the orderedAllCategoryListHeader to set
     */
    public void setOrderedAllCategoryListHeader(ArrayList<String> orderedAllCategoryListHeader,int offset) {
        //if(orderedAllCategoryListHeader!=null){
            if(this.orderedAllCategoryListHeader==null){
                this.orderedAllCategoryListHeader=new ArrayList<String>();
            }else{
                offset+=this.orderedAllCategoryListHeader.size();
            }
            for(int i=0;i<orderedAllCategoryList.size()-offset;i++){
                this.orderedAllCategoryListHeader.add(" ");
            }
            for(String val : orderedAllCategoryListHeader){
                this.orderedAllCategoryListHeader.add(val);
            }
            
        //}
    }

}
