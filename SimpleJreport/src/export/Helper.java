package export;

import java.text.DecimalFormat;

public class Helper {

	public String getDecimalFormat(Object st, boolean db) {
        boolean isXls = db;

        String number = "0.00";
        if (isXls) {
        try {
            if (st != null || !st.equals("")) {
                DecimalFormat df = new DecimalFormat("#,###,###,###,##0.00");
                Double numberDouble = Double.parseDouble(String.valueOf(st).toString());
                number = "" + df.format(numberDouble);
            } else {
                System.out.println(st);
            }
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
      
            } else {
            number = String.valueOf(st).toString();
            }
        return number;
    }
	
}
