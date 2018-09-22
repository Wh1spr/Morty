package wh1spr.bot;

public class MainTest {

	public static void main(String[] args) {
		String[] strings = new String[] {"abc","123","5.5","345678900","", "-6789", "3"};
		for (String s : strings) {
			System.out.println(String.format("%s -- IsPosInteger() = %b", s, Tools.isPosInteger(s)));
		}
		
		
	}
}
