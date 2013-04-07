/**
 * 
 */
package jmetal.coevolutionary.temporal;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author alberto
 *
 */
public class Plain2XML {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		if ( args.length==3 ){
			String filename = args[0];
			BufferedReader bf = new BufferedReader( new FileReader(filename) );
			FileOutputStream stream = new FileOutputStream(filename+".xml" );
			OutputStreamWriter out = new OutputStreamWriter(stream, "US-ASCII");
			int width  = Integer.parseInt(args[1]);
			int height = Integer.parseInt(args[2]);
			out.write("<?xml version=\"1.0\"?>\n\n");
			out.write("<matrix height=\""+height+"\" ");
			out.write("width=\""+width+"\">\n");
			for(int j=0;j<height;++j){
				out.write("\t<row>\n");
				for(int i=0;i<width;++i){
					String dataLine = bf.readLine();
					// Convert the string value in double value
					double value = (dataLine != null)? Double.parseDouble( dataLine ) : 0.0;
					out.write("\t\t<item>"+value+"</item>\n");
				} // for
				out.write("\t</row>\n");
			} // for
			out.write("</matrix>\n");
			bf.close();
			out.close();
		} // if
		else {
			System.out.println("Sintax: Plain2XML filename width heigth");
		} // else
	} // main

} // Plain2XML
