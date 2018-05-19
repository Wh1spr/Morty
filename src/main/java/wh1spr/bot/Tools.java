package wh1spr.bot;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Tools {

	public static double round(double value) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.FLOOR);
	    return Math.abs(bd.doubleValue());
	}
}
