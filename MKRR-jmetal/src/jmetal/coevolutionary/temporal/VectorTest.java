package jmetal.coevolutionary.temporal;

import java.util.Vector;

public class VectorTest {

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Vector[] data = new Vector[2];
		
		for( int i=0 ; i<2 ; ++i )
			data[i] = new Vector();
		
		Object[] mojon = new Object[10];
		
		for(int i=0;i<10;++i){
			mojon[i] = (Integer) i;
			(data[0]).add( (Integer) i );
//			(data[0])[i] = (Integer) i;
		}

	}

}
