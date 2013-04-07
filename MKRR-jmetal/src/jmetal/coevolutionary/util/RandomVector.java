package jmetal.coevolutionary.util;


/**
 * Class to obtain random vectors
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public final class RandomVector {


	/** This method obtains a vector of random values where all values are different (if possible)
	 * @param numberOfRandomValues the number of different values desired
	 * @param maxValue the rank of the values <b>[0 .. maxValues-1 ]</b>
	 * @return The random vector
	 */
	public static int[] getRandomVector_Int( int numberOfRandomValues , int maxValue ){
		int     newValue;
		boolean repeated;
		int     i , j;
		int[] vectorToReturn = new int[numberOfRandomValues];
		
		boolean flag = ( maxValue>=numberOfRandomValues );
		
		for( i=0 ; i<numberOfRandomValues ; ++i ){
			newValue = (int) Math.floor( 1 + (double) maxValue * Math.random() ) - 1;
			repeated = false;
			j        = 0;
			while( !repeated && (j<i) ){
				repeated = ( newValue==vectorToReturn[j] );
				if (repeated) {
					repeated=false;
					newValue = (int) Math.floor( 1 + (double) maxValue * Math.random() ) - 1;
					j = ( flag )? 0 : j+1 ;
				} // if
				else
					++j;
			} // while
			vectorToReturn[i] = newValue;
		} // for
		
		return vectorToReturn;
	} // getRandomVector

} // RandomVector
