package jmetal.coevolutionary.util.comparator;

import java.util.Comparator;


/** Implements the comparator for the class <code>Pair</code>.
 * 
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class PairComparator implements Comparator<Pair> {


	public int compare( Pair o1, Pair o2 ) {

		if( o1.value_ < o2.value_ )
			return -1;
		else
			if( o1.value_ > o2.value_ )
				return 1;
			else
				return 0;
	} // compare

} // PairComparator
