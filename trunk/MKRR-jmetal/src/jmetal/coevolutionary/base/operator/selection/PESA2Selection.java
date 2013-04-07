package jmetal.coevolutionary.base.operator.selection;


import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Operator;
import jmetal.coevolutionary.base.archive.AdaptiveGridArchive;
import jmetal.coevolutionary.base.Solution;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;


/** 
 * This class implements a selection operator as the used in PESA-II 
 * algorithm.
 * 
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class PESA2Selection extends Operator{      

	private static final long serialVersionUID = 9047198685534169181L;

	/**
	 * Performs the operation
	 * @param object Object representing a SolutionSet. This solution set
	 * must be an instancen <code>AdaptiveGridArchive</code>
	 * @return the selected solution
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public Object execute(Object object,int none) throws JMException    {
		try {
			AdaptiveGridArchive archive = (AdaptiveGridArchive)object;
			int selected;        
			int hypercube1 = archive.getGrid().randomOccupiedHypercube();
			int hypercube2 = archive.getGrid().randomOccupiedHypercube();                                        

			if (hypercube1 != hypercube2){
				if (archive.getGrid().getLocationDensity(hypercube1) < 
						archive.getGrid().getLocationDensity(hypercube2)) {

					selected = hypercube1;

				} // if
				else if (archive.getGrid().getLocationDensity(hypercube2) <
						archive.getGrid().getLocationDensity(hypercube1)) {

					selected = hypercube2;
				} // else if
				else {
					if (PseudoRandom.randDouble() < 0.5) {
						selected = hypercube2;
					} // if
					else {
						selected = hypercube1;
					} // else
				} // else
			} // if
			else { 
				selected = hypercube1;
			} // else
			int base = PseudoRandom.randInt(0,archive.size()-1);
			int cnt = 0;
			while (cnt < archive.size()){   
				Solution individual = archive.get((base + cnt)% archive.size());        
				if (archive.getGrid().location(individual) != selected){
					cnt++;                
				} // if
				else {
					return individual;
				} // else
			} // while
			return archive.get((base + cnt) % archive.size());
		} // try
		catch (ClassCastException e) {
			Configuration.logger_.severe("PESA2Selection.execute: ClassCastException. " +
					"Found" + object.getClass() + "Expected: AdaptativeGridArchive") ;
			Class cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;  
		} // catch
	} //execute

} // PESA2Selection
