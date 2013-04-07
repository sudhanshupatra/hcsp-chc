package jmetal.coevolutionary.experiments;


import java.util.Properties;
import jmetal.coevolutionary.base.Algorithm;
import jmetal.coevolutionary.base.Problem;
import jmetal.util.JMException;


/**
 * Abstract Settings class
 *
 * @author Antonio J. Nebro
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public abstract class Settings {
	protected Problem problem_ ;
	public int     populationSize_ = 0;

	/**
	 * Constructor
	 */
	public Settings( Problem problem ) {
		problem_ = problem ;
	} // Constructor


	/**
	 * Default configure method
	 * @return A problem with the default configuration
	 * @throws jmetal.util.JMException
	 */
	abstract public Algorithm configure() throws JMException ;

	/**
	 * Configure method. Change the default configuration
	 * @param settings
	 * @return A problem with the settings indicated as argument
	 * @throws jmetal.util.JMException
	 */
	abstract public Algorithm configure( Properties settings ) throws JMException ;

	
	/**
	 * Change the problem to solve
	 * @param problem
	 */
	void setProblem( Problem problem ) {
		problem_ = problem ;
	} // setProblem

} // Settings
