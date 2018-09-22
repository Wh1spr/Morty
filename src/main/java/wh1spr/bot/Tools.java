package wh1spr.bot;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public final class Tools {

	public static double round(double value) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.FLOOR);
	    return Math.abs(bd.doubleValue());
	}
	
	public static String getDateTimeStamp() {
		return getDateStamp() + "_" + getTimeStamp();
	}
	
	public static String getDateStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	public static String getTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	public static String getStringFromInputStream(InputStream i) {
		@SuppressWarnings("resource")
		Scanner s = (new Scanner(i)).useDelimiter("\\A");
		String res = s.hasNext()?s.next():"";
		s.close();
		return res;
	}
	public static boolean isNumeric(String str) {
	    return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	public static boolean isPosInteger(String str) {
		return str.matches("\\+?[^-]\\d*");
	}
}
