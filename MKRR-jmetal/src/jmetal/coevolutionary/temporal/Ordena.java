package jmetal.coevolutionary.temporal;

import jmetal.coevolutionary.util.ArrayUtils;

public class Ordena {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		double[] vector = { 80.3 , 12.3 , 20.45 , 3.1 , 3.1 , 0.92 };
		int [] index;
		
		System.out.println("ANTES");
		for( int i=0 ; i<6 ; ++i )
			System.out.println("      vector[ "+i+" ] = " + vector[i] );
		index = ArrayUtils.sort( vector );
		
		System.out.println("DESPUES");
		for( int i=0 ; i<6 ; ++i ) {
			System.out.print("      vector[ "+i+" ] = " + vector[i] );
			System.out.println("      index[ "+i+" ] = " + index[i] );
		} // for
	} // main

}
