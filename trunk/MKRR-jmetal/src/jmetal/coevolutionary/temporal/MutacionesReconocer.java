package jmetal.coevolutionary.temporal;

import jmetal.coevolutionary.base.Solution;
import jmetal.coevolutionary.base.SolutionSet;
import jmetal.coevolutionary.util.ParameterPackage;

public class MutacionesReconocer {


	private static void verQuePasa( Object object ){
		Solution solution = null ;
		int      islandId = 0    ;
		
		String classOfObj = object.getClass().getName();
		classOfObj = classOfObj.substring( 1 + classOfObj.lastIndexOf( '.' ) );
		
		if ( classOfObj.equalsIgnoreCase("Solution") ){
			solution = (Solution) object;
		} // if
		else if ( classOfObj.equalsIgnoreCase("ParameterPackage") ){
			ParameterPackage packed = (ParameterPackage) object;
			solution = packed.solution_;
			islandId = packed.islandId_;
		} // else if
		
		++islandId;
		solution.equals(solution);
		
	}// verQuePasa


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Solution s = new Solution();
		
		// 1.- Llamar a la funcion con s
		verQuePasa( s );
		
		// 2.- Llamar a la funcion con empaquetado
		verQuePasa( new ParameterPackage( s , 23 ) );
		
		// 3.- Meterle mierda
		
		verQuePasa( new SolutionSet(0, 1, 100) );

	} // main

} // MutacionesReconocer
