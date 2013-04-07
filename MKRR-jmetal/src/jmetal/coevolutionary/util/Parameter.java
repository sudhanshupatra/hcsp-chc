package jmetal.coevolutionary.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jmetal.base.Configuration;

/**
 * Parameter.java
 *
 * @author Anowar El Amouri
 * @author Juan A. Ca–ero
 * @version 1.0
 */
public class Parameter {

	
	/** 
	 * Reads path of ETC values 
	 * @return path the path of the ETC values file
	 */
	public static String readPath(){
		String path = "";

		try{
			// Ask for ETC File path
			BufferedReader BR = new BufferedReader( new InputStreamReader(System.in) ); 
			String pathReaded = BR.readLine();
			path = pathReaded;
		} // try
		catch( IOException e ){
			throw new RuntimeException( e );
		} // catch

		return( path );
	} // readPath


	/** 
	 * Reads Computation time percentage
	 * @return percentage the percentage used to calculate computation time
	 */
	public static double readPercentage(){
		double percentage = 0.0;
		
		try{
			// Ask for Computation Percentage value
			BufferedReader BR1 = new BufferedReader(new InputStreamReader(System.in)); 
			System.out.println(" Please, enter the computation percentage! "); 
			String val = BR1.readLine();
			double per = Double.parseDouble(val);
			percentage = per;
		} // try
		catch( IOException e ){
			String cause = "Unknown";
			Configuration.logger_.severe( cause );
			throw new RuntimeException( cause , e );
		} // catch

		return (percentage);
	} // readPercentage


} // Parameter
