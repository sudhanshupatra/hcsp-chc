/**
 * This package contains the basic ingredients to be used by the metaheuristics
 * developed under jMetal.
 */

package jmetal.coevolutionary.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.Stepbystep;


/**
 * This is the main class of jMetal, and all the metaheuristics inherit from it.
 *  An instance ob ject from Algorithm will probably require some application-speciﬁc
 *  parameters, that can be added and accessed by using the methods addParameter()
 *  and getParameter(), respectively. Similarly, an algorithm will also use some
 *  operators, so there are methods for incorporating them (addOperator()) and to
 *  get them (getOperator()). The most important method in Algorithm is execute(),
 *  which starts the execution of the algorithm.
 *  
 *  @author Juan J. Durillo
 *  @author Juan A. Cañero
 *  @version 1.0
 */
public abstract class Algorithm implements Serializable,Stepbystep {

	private static final long serialVersionUID = 2320519298799562853L;

	/** 
	 * Stores the operators used by the algorithm, such as selection, crossover,
	 * etc.
	 */
	protected Map<String,Operator> operators_ = null;

	/** 
	 * Stores algorithm specific parameters. For example, in NSGA-II these
	 * parameters include the population size and the maximum number of function
	 * evaluations.
	 */
	protected Map<String,Object> inputParameters_ = null;  

	/** 
	 * Stores output parameters, which are retrieved by Main object to 
	 * obtain information from an algorithm.
	 */
	protected Map<String,Object> outPutParameters_ = null;
	
	/**
	 * Stores the final solution. Only is assigned when the method postGeneration() is executed
	 */
	protected SolutionSet solutionSetToReturn_ = null;


	/**
	 * Offers facilities for add new operators for the algorithm. To use an
	 * operator, an algorithm has to obtain it through the 
	 * <code>getOperator</code> method.
	 * @param name The operator name
	 * @param operator The operator
	 */
	public void addOperator(String name, Operator operator){
		if (operators_ == null) {
			operators_ = new HashMap<String,Operator>();
		}        
		operators_.put(name,operator);
	} // addOperator 


	/**
	 * Gets an operator through his name. If the operator doesn't exist or the name 
	 * is wrong this method returns null. The client of this method have to check 
	 * the result of the method.
	 * @param name The operator name
	 * @return The operator if exists, null in another case.
	 */
	public Operator getOperator(String name){
		return operators_.get(name);
	} // getOperator   


	/**
	 * Sets an input parameter to an algorithm. Typically,
	 * the method is invoked by a Main object before running an algorithm. 
	 * The parameters have to been inserted using their name to access them through 
	 * the <code>getInputParameter</code> method.
	 * @param name The parameter name
	 * @param object Object that represent a parameter for the
	 * algorithm.
	 */
	public void setInputParameter(String name, Object object){
		if (inputParameters_ == null) {
			inputParameters_ = new HashMap<String,Object>();
		}        
		inputParameters_.put(name,object);
	} // setInputParameter  


	/**
	 * Gets an input parameter through its name. Typically,
	 * the method is invoked by an object representing an algorithm
	 * @param name The parameter name
	 * @return Object representing the parameter or null if the parameter doesn't
	 * exist or the name is wrong
	 */
	public Object getInputParameter(String name){
		return inputParameters_.get(name);
	} // getInputParameter


	/**
	 * Sets an output parameter that can be obtained by invoking 
	 * <code>getOutputParame</code>. Typically this algorithm is invoked by an
	 * algorithm at the end of the <code>execute</code> to retrieve output 
	 * information
	 * @param name The output parameter name
	 * @param object Object representing the output parameter
	 */  
	public void setOutputParameter(String name, Object object) {
		if (outPutParameters_ == null) {
			outPutParameters_ = new HashMap<String,Object>();
		}        
		outPutParameters_.put(name,object);
	} // setOutputParameter  


	/**
	 * Gets an output parameter through its name. Typically,
	 * the method is invoked by a Main object after the execution of an algorithm.
	 * @param name The output parameter name
	 * @return Object representing the output parameter, or null if the parameter
	 * doesn't exist or the name is wrong.
	 */
	public Object getOutputParameter(String name) {
		return outPutParameters_.get(name);
	} // getOutputParameter
	
	
	/**
	 * This method copy an algorithm
	 * @return A copy of the algorithm
	 */
	public abstract Algorithm clone();


	public abstract int getPopulationSize();

} // Algorithm
