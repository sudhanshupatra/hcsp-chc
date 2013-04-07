package jmetal.coevolutionary.temporal;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLreader {

	public static void main(String argv[]) {

		double[][] matrix;
		try {
			File file = new File("/Users/alberto/Downloads/HCSP instances/u_c_hihi.0.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			if ( doc.getDocumentElement().getNodeName().equalsIgnoreCase("matrix") ) {
				// Extract the attributes height and width
				int heigth = Integer.parseInt( doc.getDocumentElement().getAttribute("height") );
				int width  = Integer.parseInt( doc.getDocumentElement().getAttribute("width")  );
				// Define the matrix
				matrix = new double[heigth][width];
				NodeList rowList = doc.getElementsByTagName("row");

				if ( heigth==rowList.getLength() )
					// read the rows
					for( int j=0 ; j<rowList.getLength() ; ++j ){
						Node rowNode = rowList.item(j);

						if( rowNode.getNodeType()==Node.ELEMENT_NODE ){

							Element items = (Element) rowNode;
							NodeList itemList = items.getElementsByTagName("item");

							if( width==itemList.getLength() )
								// read the items of the row
								for( int i=0; i<itemList.getLength() ; ++i ){
									Element fstNmElmnt = (Element) itemList.item(i);
									NodeList fstNm = fstNmElmnt.getChildNodes();
									matrix[j][i]=Double.parseDouble(((Node) fstNm.item(0)).getNodeValue());
								} // for
							else {
								String message = "The width value is incorrect (must be ";
								message = message + itemList.getLength() + "), fix it!";
								java.lang.ArrayStoreException ex;
								ex = new java.lang.ArrayStoreException( message );
								ex.printStackTrace();
							} // else
						} // if

					} // for
				else {
					String message = "The height value is incorrect (must be ";
					message = message + rowList.getLength() + "), fix it!";
					java.lang.ArrayStoreException ex;
					ex = new java.lang.ArrayStoreException( message );
					ex.printStackTrace();
				} // else
				System.out.println("hei="+matrix.length+"  wid="+matrix[0].length);
			} // if
			else {
				java.lang.IllegalStateException ex;
				String message = "Root tag must be \"matrix\", not \"";
				message = message + doc.getDocumentElement().getNodeName() + "\"";
				ex = new java.lang.IllegalStateException( message );
				ex.printStackTrace();
			} // else
		} // try
		catch ( java.lang.NumberFormatException e ){
			System.out.println("Attributes height or width doesn't declared!");
			e.printStackTrace();
		} // catch
		catch (Exception e) {
			e.printStackTrace();
		} // catch
	}
}