package jmetal.coevolutionary.util.comparator;


/** Data pair
 * 
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class Pair {

	public int    position_ ;
	public double value_    ;


	/** Constructor
	 * @param position The position of a certain <code>value</code>
	 * @param value The value of a certain  <code>position</code>
	 */
	public Pair( int position , double value ){

		position_=position;
		value_=value;
	} // Pair

} // Pair
