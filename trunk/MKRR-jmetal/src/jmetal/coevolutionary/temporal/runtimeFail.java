package jmetal.coevolutionary.temporal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class runtimeFail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader BR;
		try {
			BR = new BufferedReader( new FileReader("noexiste.txt") );
			String pathReaded = BR.readLine();
			System.out.println(pathReaded);
		} // try
		catch ( FileNotFoundException a ) {
			System.out.println("Excepcion a");
			throw new RuntimeException( a );
		} // catch
		catch ( IOException b ) {
			System.out.println("Excepcion b");
			throw new RuntimeException( b );
		} // catch

		System.out.println("Ya estoy aqui porque he llegado");
	} // main

} //runtimeFail
