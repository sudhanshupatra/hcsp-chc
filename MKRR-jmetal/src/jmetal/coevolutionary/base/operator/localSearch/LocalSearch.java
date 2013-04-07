package jmetal.coevolutionary.base.operator.localSearch;

import jmetal.coevolutionary.base.Operator;

/**
 * Abstract class representing a generic local search operator
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero (features added)
 * @version 1.1
 */
public abstract class LocalSearch extends Operator{ 

	private static final long serialVersionUID = -3243846293089587688L;

	/**
	 * Returns the number of evaluations made by the local search operator
	 */
	public abstract int getEvaluations();
	
} // LocalSearch
