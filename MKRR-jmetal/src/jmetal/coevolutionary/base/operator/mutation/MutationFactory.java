package jmetal.coevolutionary.base.operator.mutation;


import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Operator;
import jmetal.util.JMException;


/**
 * Class implementing a mutation factory.
 * 
 * @author Juanjo Durillo
 * @author Juan A. Ca–ero (New operators added)
 * @version 1.1
 */
public class MutationFactory {

	/**
	 * Gets a crossover operator through its name.
	 * @param name of the operator
	 * @return the operator
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public static Operator getMutationOperator(String name) throws JMException{

		if (name.equalsIgnoreCase("PolynomialMutation"))
			return new PolynomialMutation(20);
		else if (name.equalsIgnoreCase("BitFlipMutation"))
			return new BitFlipMutation();
		else if (name.equalsIgnoreCase("SwapMutation"))
			return new SwapMutation();
		else if (name.equalsIgnoreCase("RebalanceMutation"))
			return new RebalanceMutation();
		else if (name.equalsIgnoreCase("PermMutation"))
			return new PermMutation();
		else {
			Configuration.logger_.severe("Operator '" + name + "' not found ");
			Class cls = java.lang.String.class;
			String name2 = cls.getName() ;    
			throw new JMException("Exception in " + name2 + ".getMutationOperator()") ;
		} // else    
	} // getMutationOperator

} // MutationFactory
