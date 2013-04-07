package jmetal.coevolutionary.base.operator.selection;


import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Operator;
import jmetal.util.JMException;


/**
 * Class implementing a selection operator factory.
 * 
 * @author Juanjo Durillo
 * @author Juan A. Ca�ero
 * @version 1.1
 */
public class SelectionFactory {


  /**
   * Gets a selection operator through its name.
   * @param name of the operator
   * @return the operator
   * @throws JMException 
   */
  public static Operator getSelectionOperator(String name) throws JMException {
    if (name.equalsIgnoreCase("BinaryTournament"))
      return new BinaryTournament();
    else if (name.equalsIgnoreCase("BinaryTournament2"))
      return new BinaryTournament2();
    else if (name.equalsIgnoreCase("PESA2Selection"))
      return new PESA2Selection();
    else if (name.equalsIgnoreCase("RandomSelection"))
      return new RandomSelection();    
    else if (name.equalsIgnoreCase("RankingAndCrowdingSelection"))
      return new RankingAndCrowdingSelection();
    else if (name.equalsIgnoreCase("DifferentialEvolutionSelection"))
      return new DifferentialEvolutionSelection();
    else if (name.equalsIgnoreCase("TournamentFour"))
      return new TournamentFour();
    else if (name.equalsIgnoreCase("TournamentFour2"))
        return new TournamentFour2();
    else {
      Configuration.logger_.severe("Operator '" + name + "' not found ");
      throw new JMException("Exception in " + name + ".getSelectionOperator()") ;
    } // else    
  } // getSelectionOperator
    
} // SelectionFactory