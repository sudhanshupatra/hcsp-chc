package jmetal.coevolutionary.problems;

import java.lang.reflect.Constructor;

import jmetal.base.Configuration;
import jmetal.coevolutionary.base.Problem;
import jmetal.util.JMException;


/**
 * This class represents a factory for problems.
 * @author Juan J. Durillo
 * @author Juan A. Ca–ero (New kind of problems added)
 * @version 1.1
 */
public class ProblemFactory {


	/**
	 * Creates an object representing a problem with one island
	 * @param name Name of the problem
	 * @param params Parameters characterizing the problem
	 * @return The object representing the problem
	 * @throws JMException 
	 * @throws JMException 
	 */
	public Problem getProblem( String name, Object[] params ) throws JMException{
		
		return this.getProblem( name , params , 1 );
	} // getProblem


	/**
	 * Creates an object representing a problem
	 * @param name Name of the problem
	 * @param params Parameters characterizing the problem
	 * @param numberOfIslands Number of islands to use
	 * @return The object representing the problem
	 * @throws JMException 
	 */
	@SuppressWarnings("unchecked")
	public Problem getProblem( String name, Object[] params , int numberOfIslands ) throws JMException {
		// Params are the arguments
		// The number of argument must correspond with the problem constructor params

		String base = "jmetal.coevolutionary.problems.";
		if (name.substring(0,name.length()-1).equalsIgnoreCase("DTLZ"))
			base += "DTLZ.";
		if (name.substring(0,name.length()-1).equalsIgnoreCase("WFG"))
			base += "WFG.";
		if (name.substring(0,name.length()-1).equalsIgnoreCase("ZDT"))
			base += "ZDT.";    
		if (name.substring(0,name.length()-3).equalsIgnoreCase("ZZJ07"))
			base += "ZZJ07.";        
		if (name.substring(0,name.length()-3).equalsIgnoreCase("LZ07"))
			base += "LZ07.";        
		if (name.substring(0,name.length()-4).equalsIgnoreCase("ZZJ07"))
			base += "ZZJ07.";    
		if (name.substring(0,name.length()-3).equalsIgnoreCase("LZ06"))
			base += "LZ06.";
		if (name.substring(0,name.length()-4).equalsIgnoreCase("u_c_"))
			base += "scheduling.";
		if (name.substring(0,name.length()-4).equalsIgnoreCase("u_i_"))
			base += "scheduling.";
		if (name.substring(0,name.length()-4).equalsIgnoreCase("u_s_"))
			base += "scheduling.";


		try {
			Class problemClass = Class.forName( base+name );
			Constructor [] constructors = problemClass.getConstructors();
			int i = 0;
			// find the constructor
			while ((i < constructors.length) && 
					(constructors[i].getParameterTypes().length != params.length)) {
				i++;
			} // while
			// constructors[i] is the selected one constructor
			Problem problem = (Problem)constructors[i].newInstance( params );
			return problem;
		} // try
		catch(Exception e) {
			Configuration.logger_.severe("ProblemFactory.getProblem: " +
					"Problem '"+ name + "' does not exist or constructor does not match. "  +
			"Please, check the problem names in jmetal/coevolutionary/problems") ;
			throw new JMException("Exception in " + name + ".getProblem()") ;
		} // catch            
	} // getProblem

} // ProblemFactory
