package jmetal.coevolutionary.temporal;

import jmetal.coevolutionary.util.Latexize;
import jmetal.coevolutionary.util.PrintfFormat;

public class Numeritos {

	
	public static String strPre(double inValue){

		PrintfFormat p = new PrintfFormat("%15.3e");
		return(p.sprintf(inValue));
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double n=2.233482837472;
		
		System.out.println("La n es = " + strPre(n) + " Y en latex es " + Latexize.Double(n,24) );

		n=Math.PI;
		System.out.println("La n es = " + strPre(n) + " Y en latex es " + Latexize.Double(n,24) );
		
		n=0.3;
		System.out.println("La n es = " + strPre(n) + " Y en latex es " + Latexize.Double(n,24) );
		
		n=2318732181283272638123.0;
		System.out.println("La n es = " + strPre(n) + " Y en latex es " + Latexize.Double(n,24) );
		
	}


}











