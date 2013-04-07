package jmetal.coevolutionary.util;

import jmetal.coevolutionary.base.Solution;

/**
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class ParameterPackage {
	
	public Solution solution_;
	public int      islandId_;

	public ParameterPackage( Solution solution , int islandId ){
		solution_ = solution;
		islandId_ = islandId;
	} // ParameterPackage
	
} // ParameterPackage
