package jmetal.coevolutionary.util;

import java.util.Arrays;
import java.util.Comparator;

import jmetal.coevolutionary.util.comparator.Pair;
import jmetal.coevolutionary.util.comparator.PairComparator;

/** Class to perform operations in arrays.
 * 
 * The following methods should be standard in Java library
 * 
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class ArrayUtils {

	public static final int INDEX_NOT_FOUND = -1; ///< Error value


	/** Sorts the specified vector according to ascending order and
	 * returns the original indices of each element of the original vector.
	 * @param vector the vector to sort
	 * @return the original indices of original vector
	 */
	public static int[] sort( double[] vector ){
		int len = vector.length;

		// Stores the original vector in a buffer (vector of pairs)
		Pair[] buffer = new Pair[ len ];
		
		for( int i=0 ; i<len ; ++i )
			buffer[i] = new Pair( i , vector[i] );
		
		// Sort the buffer via the Pair comparator
		Comparator<Pair> comparator = new PairComparator();
		Arrays.sort( buffer , comparator );

		int [] index  = new int[  len ];

		// Prepare the data to return
		for( int i=0 ; i<len ; ++i ) {
			vector[i] = buffer[i].value_;
			index[i] = buffer[i].position_;
		} // for
		
		return( index );
	} // sort


	/** Finds the index of the given value in the vector starting at the given position
	 * 
	 * @param vector the vector to search through, may be <code>null</code>
	 * @param value Value to find
	 * @param start the index to start searching at
	 * @return The index of the value within the vector, returns <code>INDEX_NOT_FOUND</code> if not found or <code>null</code> vector in input
	 */
	public static int indexOf( int[] vector, int value , int start) {

		int len = vector.length;
		start = (start < 0)? 0 : start;
		if ( vector == null )
			return INDEX_NOT_FOUND;

		for ( int index=start ; index<len ; ++index )
			if ( value==vector[ index ] )
				return index;

		return INDEX_NOT_FOUND;
	} // indexOf


	/** Swaps the values of two indices of the vector
	 * 
	 * @param vector the vector
	 * @param idx1 first index
	 * @param idx2 second index
	 */
	public static void swap( int[] vector , int idx1 , int idx2 ) {
		int len = vector.length;

		idx1 = (idx1 < 0)? 0 : ( (idx1 >= len)? len-1 : idx1 );
		idx2 = (idx2 < 0)? 0 : ( (idx2 >= len)? len-1 : idx2 );
		
		int value = vector[ idx1 ];
		
		vector[ idx1 ] = vector[ idx2 ];
		vector[ idx2 ] = value;
	} // swap


	/** This method returns the maximum value of a vector
	 * 
	 * @param vector a vector of numbers
	 * @return the maximum value
	 */
	public static double getMax( double[] vector ){
		double value;
		double max   = vector[0];
		int    len   = vector.length;
		
		do {
			--len;
			value = vector[len];
			if ( value > max )
				max = value;
		} while( len>1 );
		
		return max;
	} // getMax

} // ArrayUtils
