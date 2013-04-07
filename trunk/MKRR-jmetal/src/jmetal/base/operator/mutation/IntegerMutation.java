/**
 * BitFlipMutation.java
 * @author Juan J. Durillo
 * @version 1.0
 * 
 */
package jmetal.base.operator.mutation;

import jmetal.base.Configuration;
import jmetal.base.Solution;
import jmetal.base.Configuration.SolutionType_;
import jmetal.base.variable.*;
import jmetal.base.DecisionVariables;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.base.Operator;

 /**
  * This class implements the mutation of integer genes.
  * NOTE: the operator is applied to integer solutions and it is applied to the 
  * whole solution as a single variable.
  */
public class IntegerMutation extends Operator{
    
  /**
   * Constructor
   * Creates a new instance of the Bit Flip mutation operator
   */
  public IntegerMutation() {    
  } // IntegerMutation

 /**
  * Perform the mutation operation
  * @param probability Mutation probability
  * @param solution The solution to mutate
 * @throws JMException 
  */
  public void doMutation(double probability, Solution solution) throws JMException {        
    try {
      int vars = solution.numberOfVariables();
      for (int i = 0; i < vars; i++) {                
          if (PseudoRandom.randDouble() < probability) {
        	double minBound = solution.getDecisionVariables().variables_[i].getLowerBound();
        	double maxBound = solution.getDecisionVariables().variables_[i].getUpperBound();
            ((Int) solution.getDecisionVariables().variables_[i]).setValue((double)Math.round(PseudoRandom.randDouble(minBound, maxBound)));
        }
      }
            
    } catch (ClassCastException e1) {
      Configuration.logger_.severe("IntegerMutation.doMutation: " + 
          "ClassCastException error" + e1.getMessage()) ;
      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".doMutation()") ;    
    }                
  } // doMutation
  
  /**
  * Executes the operation
  * @param object An object containing a solution to mutate
  * @return An object containing the mutated solution
   * @throws JMException 
  */
  public Object execute(Object object) throws JMException {
    Solution solution = (Solution)object;
    
    if ((solution.getType() != SolutionType_.Int)) {
      Configuration.logger_.severe("IntegerMutation.execute: the solution " +
          "is not of the right type. The type should be 'Int' " +
          ", but " +solution.getType() + " is obtained");

      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".execute()") ;
    } // if 
     
    Double probability = (Double)getParameter("probability");     
    if (probability == null)
    {
      Configuration.logger_.severe("IntegerMutation.execute: probability not " +
      "specified");
      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".execute()") ;  
    } 
        
    doMutation(probability.doubleValue(),solution);
    return solution;
   } // execute
} // BitFlipMutation
