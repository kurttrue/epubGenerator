package net.kurttrue.www.epubGenerator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.TreeMap;
import java.util.ArrayList;

public class DocHandler
{

	public DocHandler()
	{


	}

	public DocHandler(EasyElement arootEE)
	{
		rootEE  = arootEE;
	}

	public DocHandler setOutputPath(String aoutputPath)
	{

		outputPath = aoutputPath;

		return this;

	}

	public DocHandler setMethod(String amethod)
	{

		docMethod = amethod;

		return this;
	}


    public Document getDocument()
    {

		return doc;
	}

    public void transform()
    {

		try
		{

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		String myMethod;

		if(docMethod==null)
		{
			myMethod=XML;
		}
		else
		{
			myMethod=docMethod;
		}

		File checkDir = new File(outputPath).getParentFile();

		//if the target directory doesn't exist, create it.
		if(!checkDir.exists())
		{

		    checkDir.mkdirs();

		}



        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter( fileOutputStream, "UTF-8" );

		StreamResult result = new StreamResult(outputStreamWriter);

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
           transformer.setOutputProperty(OutputKeys.METHOD, myMethod);
           transformer.setOutputProperty(OutputKeys.INDENT, "yes");
           transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		transformer.transform(source, result);

		}
		catch (TransformerException tfe)
		{
		  exceptions.add(tfe.toString());
	    }
	    catch (FileNotFoundException fne)
	    {
			exceptions.add(fne.toString());
		}
		catch(UnsupportedEncodingException usce)
		{
			exceptions.add(usce.toString());
		}


	}


	public void makeDocument()
	{


		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("company");
			doc.appendChild(this.getElement(rootEE));
	    }
	    catch(Exception e)
	    {

			exceptions.add(this.getClass().getName() + " getDocument() exception: " + e.toString());

		}



	}

	protected Element getElement(EasyElement ee)
	{

	     Element element = doc.createElement(ee.getName());

	     TreeMap<String, String> attributes = ee.getAttributes();

	     for(String key : attributes.keySet())
	     {
			 element.setAttribute(key, attributes.get(key));
	     }

	     String text = ee.getText();

	     if(text.length()>0)
	     {
			 element.appendChild(doc.createTextNode(text));
		 }

		 //this is where method becomes recursive.
		 for(EasyElement child : ee.getChildren())
		 {

			 element.appendChild(this.getElement(child));
		 }

		 return element;

	}

	public ArrayList<String> getExceptions()
	{

		return exceptions;
	}



public static final String XML = "xml";

public static final String HTML = "html";

protected String docMethod = null;

protected EasyElement rootEE=null;

protected Document doc;

protected String outputPath = null;

protected ArrayList<String> exceptions = new ArrayList<String>();

}