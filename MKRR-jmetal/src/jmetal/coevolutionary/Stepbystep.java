package jmetal.coevolutionary;


import jmetal.coevolutionary.base.DecisionVariables;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.util.JMException;


/**
 * Se ha elegido la opción de diseño de que Stepbystep sea un interface ya que
 * eso permite mayor grado de poliforfismo del que se obtiene con una clase
 * abstracta. Además de este manera interfiere menos con lo que ya está
 * desarrollado en jmetal.
 * 
 * @author Juan A. Cañero
 * @version 1.0
 */
public interface Stepbystep {

	/**
	 * The method takes care of initializing all the necessary objects, as well
	 * as the initial population.
	 * @param islands Number of islands to use
	 * @throws JMException 
	 */
	public abstract void setup( int islands ) throws JMException;
	
	/**
	 * This method generates a new offspring.
	 * @throws JMException 
	 */
	public abstract void generation() throws JMException;
	
	/**
	 * This method ends the generation: ranking, crowding, ...
	 */
	public abstract void postGeneration();
	
	/**
	 * @return The best solution
	 */
	public abstract DecisionVariables[] getBestSolutions();
	
	/**
	 * Recibe la mejor solucion del resto para formar la solucion total y poder
	 * "rankearla" bien.
	 */
	public abstract void setBestSolutions( DecisionVariables[] bestSolution , int islandId );
	
	/**
	 * Recibe la mejor solucion del resto para formar la solucion total pero no
	 * la rankea.
	 */
	public abstract void setUpBestSolutions( DecisionVariables[] bestSolution , int islandId );

	/**
	 * Ejecuta código que se debe hacer después del while 
	 */
	public abstract void postExecution();
	
	/**
	 * Evaluate the stop condition of the main loop
	 * @return a boolean value
	 */
	public abstract boolean stopCondition();
	
	/**
	 * Evaluates the first population
	 * @throws JMException
	 */
	public abstract void evaluatePopulation() throws JMException;
	
	/**
	 * Get the final <code>SolutionSet</code>
	 * @return The final population
	 */
	public abstract SolutionSet getFinalSolutionSet();


} // Stepbystep
