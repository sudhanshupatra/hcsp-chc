package jmetal.coevolutionary.base.operator.localSearch;

import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Operator;
import jmetal.util.JMException;


/**
 * Class implementing a local search factory.
 * 
 * @author Anowar El Amouri
 * @author Juan A. Ca–ero
 * @version 1.1
 */
public class LocalSearchFactory {

	/**
	 * Gets a local search operator through its name.
	 * @param name of the operator
	 * @return the operator
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public static Operator getLocalSearchOperator(String name) throws JMException{

		if ( name.equalsIgnoreCase( "LMCTSLocalSearch" ) )
			return new LMCTSLocalSearch();
		else if ( name.equalsIgnoreCase( "MutationLocalSearch" ) )
			return new MutationLocalSearch( null , null );
		else {
			Configuration.logger_.severe( "Operator '" + name + "' not found " );
			Class cls = java.lang.String.class;
			String name2 = cls.getName() ;    
			throw new JMException( "Exception in " + name2 + ".getLocalSearchOperator()" );
		} // else
	} // getLocalSearchOperator

} // LocalSearchFactory
