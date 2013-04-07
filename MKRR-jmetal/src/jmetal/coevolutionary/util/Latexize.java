
package jmetal.coevolutionary.util;


/** Class to convert values in latex
 * 
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class Latexize {
	

	/** This method converts a double value in a string that represents a number
	 * in LaTeX
	 * 
	 * @param value The double value
	 * @return The number in latex format
	 */
	public static String Double( double value ) {
		
		PrintfFormat pattern = new PrintfFormat("%.3e");
		String num = pattern.sprintf(value);

		String latex = "$";
		boolean isNegative=false;

		int ind = num.indexOf("e+");
		if ( ind == -1 ){
			ind = num.indexOf("e-");
			isNegative=true;
		} // if
		
		latex += num.substring( 0, ind );
		if ( num.charAt(ind+3) != '0' ) {
			
			latex += "\\cdot10^{";

			if ( isNegative )
				latex+="-";

			if ( num.charAt(ind+2) == '0' )
				latex += num.substring(ind+3);
			else
				latex += num.substring(ind+2);

			latex+="}$";
		} // if
		else
			latex+="$";

		return latex;
	} // Double

	
	/** This method converts a double value in a string that represents a number
	 * in LaTeX without the dollar simbols
	 * 
	 * @param value The double value
	 * @return The number in latex format
	 */
	public static String DoubleWithoutDollars( double value ) {
		
		PrintfFormat pattern = new PrintfFormat("%.3e");
		String num = pattern.sprintf(value);

		String latex = "";
		boolean isNegative=false;

		int ind = num.indexOf("e+");
		if ( ind == -1 ){
			ind = num.indexOf("e-");
			isNegative=true;
		} // if
		
		latex += num.substring( 0, ind );
		if ( num.charAt(ind+3) != '0' ) {
			
			latex += "\\cdot10^{";

			if ( isNegative )
				latex+="-";

			if ( num.charAt(ind+2) == '0' )
				latex += num.substring(ind+3);
			else
				latex += num.substring(ind+2);

			latex+="}";
		} // if

		return latex;
	} // DoubleWithoutDollars
	

	/** This method converts a double value in a string that represents a number
	 * in LaTeX, the second parameter controls the number of characters returned
	 * 
	 * @param value
	 * @param wide length of the string returned (if it's possible)
	 * @return
	 */
	public static String Double( double value , int wide ) {
		
		String latex = Double( value );
		
		if( latex.length() >= wide )
			return latex;
		else {
			return rep(' ',wide-latex.length()) + latex;
		} // else
	} // Double


	/**
	 * @param character
	 * @param count
	 * @return
	 */
	private static String rep( char character , int count ) {
		char[] t = new char[count];
		
		for( int i=0 ; i<count ; ++i)
			t[i] = character;

		return new String( t );
	} // rep
	
	
	/**
	 * @param tag
	 * @return
	 */
	public static String String( String tag ) {
		String s = "";
		
		for( int i=0 ; i<tag.length() ; ++i){
			if ( tag.charAt(i)=='_' )
				s+="\\";
			s+=tag.charAt(i);
		} // for
		return s;
	} // String

} // Latexize
