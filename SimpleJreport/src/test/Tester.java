package test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import export.CSVExcelDataMapper;
import export.CSVExcelDataMappingFactory;
import export.DatabaseManager;
import export.FileFormatConvertFactory;

public class Tester {
	public static void main(String[] args){
		FileFormatConvertFactory cf = new FileFormatConvertFactory("PromotionDisountOutlet");
		String startdate="2013-01-01";
		String enddate="2015-01-01";
		 String querySummary = "SELECT i.location,l.locationcode, "
                 + "	item.promoid AS promoid,"
                 + "      pm.promoname AS promodetail,"
                 + "      count(*) as totalInvoice,"
                 + "       (CASE"
                 + "           WHEN (SUM(item.retailsalesprice *item.quantity)) IS NOT NULL THEN round((SUM(item.retailsalesprice *item.quantity))) "
                 + "           ELSE 0"
                 + "       END) AS beforediscount,"
                 + "       (CASE"
                 + "           WHEN ((SUM(item.retailsalesprice *item.quantity)) - SUM(item.discountamount)) IS NOT NULL THEN round(((SUM(item.retailsalesprice *item.quantity)) - SUM(item.discountamount))) "
                 + "           ELSE 0 "
                 + "       END) AS amountpayable, "
                 + "      (CASE "
                 + "          WHEN SUM(item.discountamount) IS NOT NULL THEN round(SUM(item.discountamount)) "
                 + "          ELSE 0 "
                 + "       END) AS discountamount "
                 + "FROM invoiceinfo i "
                 + "INNER JOIN invoiceitem item ON i.invoiceid = item.invoice "
                 + "INNER JOIN promotioninfo pm ON item.promoid = pm.promoid "
                 + "INNER JOIN locationinfo l ON l.locationid = i.location "
                 + "WHERE to_char(i.invoicedate,'YYYY-MM-DD') >= '" + startdate + "' "
                 + " AND to_char(i.invoicedate,'YYYY-MM-DD') <= '" + enddate + "' "
                 + " AND i.status = 1 "
                 //+ " AND i.location=1 "
                 + " AND item.retailsalesprice > 0 "
                 + " AND (pm.promotype = 2 or pm.promotype=5) "
                 + " AND item.promoid  > 0 "
                 + " GROUP BY "
                 + "        l.locationcode,"
                 + "        i.location,"
                 + "        pm.promoname,"
                 + "        item.promoid";

         CSVExcelDataMappingFactory factory = CSVExcelDataMappingFactory.getSharedInstance();
         CSVExcelDataMapper mapper = factory.newCSVDataMapper(true);
         mapper.fileType=true;    

         Map<String, String> keyPair = new Hashtable<String, String>();
         keyPair.put("totalInvoice", " Total Invoice\n" + "交易笔数");//
         keyPair.put("beforediscount", "Total Amount\n" + "面值总额");//
         keyPair.put("discountamount", "Discount\n");
         keyPair.put("amountpayable", "Amount Payable\n" + "实际金额");//
         
         DatabaseManager db=new DatabaseManager("localhost", "localshop", "postgres", "postgres");
         
         mapper.getDBdataForCrossTable(querySummary, db, keyPair, "promodetail", "promoid", "locationcode");
         ArrayList<Object> datListReport = mapper.fillReportData();

         cf.addDataList(datListReport);
         cf.exportReport("D:/temp",FileFormatConvertFactory.TYPE_CSV);
	}
}
