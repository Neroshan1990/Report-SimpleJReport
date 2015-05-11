/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author neroshank
 */
public class CSVExcelHandler {

    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String FILE_TYPE_XLS = "xls";
    public static final String FILE_TYPE_XLSX = "xlsx";

    private Map<String, ArrayList<Object>> dataWithSheets;
    private String outFileName;
    private String fileType = FILE_TYPE_XLS;
    private String delimater=",";
    
    /**
     *
     * @param outFileName
     */
    public CSVExcelHandler(String outFileName) {
        this.outFileName = outFileName;
        this.fileType = FILE_TYPE_XLSX;
        dataWithSheets = new LinkedHashMap<String, ArrayList<Object>>();
    }

    /**
     *
     * @param accept outputfile name along with file type.
     */
    public CSVExcelHandler(String outFileName, String type) {
        this.outFileName = outFileName;
        this.fileType = type;
        dataWithSheets = new LinkedHashMap<String, ArrayList<Object>>();
    }

    /**
     *
     * @param accept a single csv file.
     */
    public void initCSVData(String csvFileName) {
        //REad file and create ArrayList<String> data
        processCSVFileData(new String[]{csvFileName});
    }

    /**
     *
     * @param accept multiple csv files.
     */
    public void initCSVData(String... csvFileName) {
        //REad file and create ArrayList<String> data
        processCSVFileData(csvFileName);
    }

    /**
     *
     * @param accept an array of csv file comma separated data.
     */
    public void initCSVData(ArrayList<Object> data) {
        this.dataWithSheets.put("SHEET0", data);

    }

    /**
     *
     * @param accept an array of csv file comma separated data divided to
     * sheets.
     */
    public void initCSVData(Map<String, ArrayList<Object>> dataWithSheets) {
        this.dataWithSheets = dataWithSheets;

    }
    
    public void appendToArray(String valueLine,String sheetNo) {
        ArrayList<Object> data = null;
        if(dataWithSheets.get(sheetNo)!=null){
            data=dataWithSheets.get(sheetNo);
        }else{
            data=new ArrayList<Object>();
            dataWithSheets.put(sheetNo, data);
        }
        dataAddToRows(data, valueLine);
        
    }
    
    private ArrayList<Object> dataAddToRows(ArrayList<Object> data, String currentLine) {
        ArrayList<String> currentLineData = new ArrayList<String>();
        if (currentLine.contains(delimater)) {
            String strar[] = currentLine.split(delimater);
            for (int j = 0; j < strar.length; j++) {
                byte ptext[] = strar[j].getBytes(UTF_8);
                String value = new String(ptext, UTF_8);
                currentLineData.add(value);
            }

        } else {
            currentLineData.add(currentLine);
        }
        data.add(currentLineData);
        return data;
    }

    public void appendToArray(ArrayList<Object> valueLine,String sheetNo) {
        ArrayList<Object> data = null;
        if(dataWithSheets.get(sheetNo)!=null){
            data=dataWithSheets.get(sheetNo);
        }else{
            data=new ArrayList<Object>();
            dataWithSheets.put(sheetNo, data);
        }
        data.add(valueLine);
    }
    
    public Map<String, ArrayList<Object>> getSheetObjectData(){
        return dataWithSheets;
    }

    /**
     *
     * @param accept generate excel file defined by file type extention with
     * sheets.
     */
    public void generateXlsx() {
        try {
            XSSFWorkbook xssfWb = new XSSFWorkbook();
            Workbook hwb = new SXSSFWorkbook(xssfWb, 200);
            //Workbook hwb = new XSSFWorkbook();
            for (String sheetName : dataWithSheets.keySet()) {
                Sheet sheet = hwb.createSheet(sheetName);
                ArrayList<Object> dataList = dataWithSheets.get(sheetName);
                if(dataList.size()>5000){
                    //int remainder=dataList.size()%10000; 
                    //int upper=dataList.size()+(10000-remainder);
                    //int shift= (int)upper/10000;
                    for (int start = 0; start < dataList.size(); start += 5000) {
                        int end = Math.min(start + 5000, dataList.size());
                        System.out.print(start+" () "+ end);
                        List<Object> sublist = dataList.subList(start, end);
                        addToSheet(start,end,sheet,sublist,hwb);
                    }  
                }else{
                    addToSheet(0,dataList.size(),sheet,dataList,hwb);
                }
            }
            FileOutputStream fileOut = null;
            if(hwb instanceof XSSFWorkbook) {
                fileOut = new FileOutputStream(outFileName + "." + fileType+"x");
            }else{
                fileOut = new FileOutputStream(outFileName + "." + fileType);
            }
            hwb.write(fileOut);
            fileOut.close();
            //System.out.println("Excel file has been generated");
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        
    }
    
    private void addToSheet(int start, int end, Sheet sheet, List<Object> dataList, Workbook wb) {
        int count = 0;
        Row row = null;
        Cell cell = null;
        String data = null;
        CellStyle cellStyleDouble = wb.createCellStyle();
        DataFormat hssfDataFormatDouble = wb.createDataFormat();
        cellStyleDouble.setDataFormat(hssfDataFormatDouble.getFormat("#,##0.00"));
        
        CellStyle cellStyleQty = wb.createCellStyle();
        DataFormat hssfDataFormatQty = wb.createDataFormat();
        cellStyleQty.setDataFormat(hssfDataFormatQty.getFormat("#,##0"));
        CellObject t = null;
        Object v = null;
        Object actualObject = null;
        DecimalFormat df = new DecimalFormat("####0.00");
        for (int k = start; k < end; k++) {
            ArrayList ardata = (ArrayList) dataList.get(count);
            row = sheet.createRow((short) 0 + k);
            for (int p = 0; p < ardata.size(); p++) {
                cell = row.createCell((short) p);
                data = ardata.get(p).toString();
                if(data==null || data.equals("(null)")|| data.equals("null")){
                    data="";
                }
                v = ardata.get(p);
                if(v!=null && v instanceof CellObject){
                    t=(CellObject)v;
                    if(t!=null && t.getActualValue()!=null){
                        actualObject = t.getActualValue();
                        if(actualObject instanceof Double){                           
                            Double newDbl=Double.parseDouble(df.format((Double)actualObject));
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(cellStyleDouble);
                            cell.setCellValue(newDbl);
                        }else if(actualObject instanceof Integer){
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(cellStyleQty);
                            cell.setCellValue((Integer)actualObject);
                        }else if(actualObject instanceof Long){
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(cellStyleQty);
                            cell.setCellValue((Long)actualObject);
                        }else{
                            data = data.replaceAll("\"", "");
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            cell.setCellValue(data);
                        }
                    }else{
                        data = data.replaceAll("\"", "");
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cell.setCellValue(data);
                    }
                }else{
                    data = data.replaceAll("\"", "");
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellValue(data);
                }
            }
            count++;
        }
    }

    
    /**
     *
     * @param accept process csv comma separated data in to an array.
     */
    private void processCSVFileData(String[] csvFileNames) {
        try {
            int count = 1;
            for (String fileName : csvFileNames) {

                FileInputStream fis = new FileInputStream(fileName);
                DataInputStream mis = new DataInputStream(fis);
                int i = 0;
                ArrayList<Object> data = new ArrayList<Object>();
                String currentLine = "";
                while ((currentLine = mis.readLine()) != null) {
                    dataAddToRows(data, currentLine);
                    i++;
                }
                mis.close();
                fis.close();
                dataWithSheets.put("Sheet " + count, data);
                count++;
            }

        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        //CSVHandler with multiple csv files in to single csv file with provided extention
        CSVExcelHandler csvh = new CSVExcelHandler("test", CSVExcelHandler.FILE_TYPE_XLSX);
        csvh.initCSVData("testC.csv", "testC.csv");
        csvh.generateXlsx();

        //CSVHandler with multiple csv files in to single csv file with default extention (xlsx)
        CSVExcelHandler csvh1 = new CSVExcelHandler("test");
        csvh1.initCSVData("testC.csv", "testC.csv");
        csvh1.generateXlsx();

        ArrayList<String> data1 = new ArrayList<String>();
        data1.add("Name");
        data1.add("DOB");
        data1.add("Age");
        ArrayList<String> data2 = new ArrayList<String>();
        data2.add("Test1");
        data2.add("1.1.1");
        data2.add("1");
        ArrayList<String> data3 = new ArrayList<String>();
        data3.add("Test2");
        data3.add("2.2.2");
        data3.add("2");
        ArrayList<Object> dataAll = new ArrayList<Object>();
        dataAll.add(data1);
        dataAll.add(data2);
        dataAll.add(data3);

        //CSVHandler with an array of String data
        CSVExcelHandler csvh2 = new CSVExcelHandler("test");
        csvh2.initCSVData(dataAll);
        csvh2.generateXlsx();

        //CSVHandler with an array of String data
        CSVExcelHandler csvh3 = new CSVExcelHandler("test");
        LinkedHashMap<String, ArrayList<Object>> dataWithSheets = new LinkedHashMap<String, ArrayList<Object>>();
        dataWithSheets.put("Sheet1", dataAll);
        dataWithSheets.put("Sheet2", dataAll);
        csvh3.initCSVData(dataWithSheets);
        csvh3.generateXlsx();

    }

    /**
     * @return the delimater
     */
    public String getDelimater() {
        return delimater;
    }

    /**
     * @param delimater the delimater to set
     */
    public void setDelimater(String delimater) {
        this.delimater = delimater;
    }

}
