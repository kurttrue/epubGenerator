package net.kurttrue.www.epubGenerator;

//EasyElement is a convenience class that turns an xml file into a collection of nested objects.

import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.io.File;

import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;


	/**
	* <h1>EasyElement</h1>
    * EasyElement and DocHandler are convenience classes that abstract away java's DOM and SAX objects.
    * These classes allow us to read and write XML data in a (relatively) intuitive and non wordy way.
    *
    * @author Kurt True
    * @version 1.0
    * @since 2017-09-25
	*/


public class EasyElement
{

	public EasyElement()
	{

	}

	public EasyElement buildFromPath(String inputPath)
	{


		try
		{

			  	  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				  DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				  File inputFile = new File(inputPath);

	              Document inputdoc = dBuilder.parse(inputFile);

	              absPath = inputFile.getAbsolutePath();

				  Element inputroot = inputdoc.getDocumentElement();

				  //optional, but recommended
				  //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				  inputroot.normalize();

				  this.populate(inputroot);



	    }
	    catch(Exception e)
	    {

                  exceptions.add(e.toString());
		}

		return this;

	}

	public EasyElement buildFromElement(Element element)
	{

		populate(element);

		return this;
	}


	public EasyElement setName(String aname)
	{
		name = aname;

		return this;
	}

	public EasyElement setText(String atext)
	{

		text = atext.trim();

		return this;
	}

	public ArrayList<EasyElement> getEasyElementsByName(String ename)
	{


		//this method returns all EasyElements in the tree that contain have the name represented by String ename.
		ArrayList<EasyElement> returnList = new ArrayList<EasyElement>();

		if(this.getName().equals(ename))
		{

			returnList.add(this);
		}

		for(EasyElement child: children)
		{
		    //recursion here...
		    returnList.addAll(child.getEasyElementsByName(ename));
		}

        return returnList;

	}

	public ArrayList<EasyElement> getEasyElementsByAttribute(String attname)
	{

		//this method returns all EasyElements in the tree that contain an attribute with the handle represented by String attname.
		ArrayList<EasyElement> returnList = new ArrayList<EasyElement>();

		if(this.getAttributes().containsKey(attname))
		{

			returnList.add(this);
		}

		for(EasyElement child : children)
		{

			//recursion here...
			returnList.addAll(child.getEasyElementsByAttribute(attname));

		}

		return returnList;

	}

	public void populate(Element element)
	{



		 this.setName(element.getNodeName());


		 //remmed this out on 31ag17 on account of Element.getTextContent()
		 //returns text content of current Element and all its descendants.
		 /*
		 if(element.getTextContent()!=null)
		 {
			 this.setText(element.getTextContent());
		 }
		 */

         NamedNodeMap aMap =  element.getAttributes();

         for (int a = 0; a < aMap.getLength(); a++)
         {

			Node attribute = aMap.item(a);

			String value;

			if(attribute.getNodeName()!=null)
			{

				if(attribute.getNodeValue()!=null)
				{
					value = attribute.getNodeValue();
				}
				else
				{
					value = BLANK;
				}

				attributes.put(attribute.getNodeName(), value);

		    }

		 }

		 NodeList nodelist = element.getChildNodes();

         for (int n = 0; n < nodelist.getLength(); n++)
         {

			 Node node = nodelist.item(n);

			 if (node.getNodeType() == Node.ELEMENT_NODE)
			 {

				 Element child = (Element) node;

				 this.addChild(new EasyElement().buildFromElement(child));
			 }
			 else if(node.getNodeType() == Node.TEXT_NODE)
			 {

			     this.setText(node.getNodeValue());
			 }


	     }

	}

    public String getName()
    {

		StringBuffer returnBuffer = new StringBuffer("");

		if(name != null)
		{

			returnBuffer.append(name);
		}

		return returnBuffer.toString();
	}

	public String getText()
	{

		StringBuffer returnBuffer = new StringBuffer("");

		if(text != null)
		{

			returnBuffer.append(text);
		}

		return returnBuffer.toString();
	}

	public TreeMap<String, String> getAttributes()
	{

		return attributes;

	}


    public ArrayList<EasyElement> getChildren()
    {

		return children;
	}

	public boolean hasChild(String childName)
	{

		boolean returnBoolean = false;

		if(childMap.containsKey(childName))
		{
			if(childMap.get(childName).size()>0)
			{

				returnBoolean = true;
			}
		}

		return returnBoolean;

	}

	public EasyElement getFirst(String childName)
	{

		//returns the child EasyElement for name childName
		//or an empty EasyElement if no children by that name.
		EasyElement returnElement;

		if(childMap.containsKey(childName))
		{

		    ArrayList<EasyElement> childList = childMap.get(childName);

		    if(childList.size()>0)
		    {
		       returnElement = childMap.get(childName).get(0);
		    }
		    else
		    {
				returnElement = new EasyElement();
			}

		}
		else
		{

			returnElement = new EasyElement();
		}

		return returnElement;
	}

	public ArrayList<EasyElement> getChildrenByName(String childName)
	{

		ArrayList<EasyElement> returnList = new ArrayList<EasyElement>();

		if(childMap.containsKey(childName))
		{
		   ArrayList<EasyElement> childList = childMap.get(childName);

		   for(EasyElement element : childList)
		   {
			  returnList.add(element);
		   }



	    }



		return returnList;
	}

    //you can add a key as a key, value pair if you're in a hurry...
	public void addChild(String key, String value)
	{
		EasyElement child = new EasyElement().setName(key).setText(value);

		children.add(child);

		if(!childMap.containsKey(key))
		{
			childMap.put(key, new ArrayList<EasyElement>());
		}

		childMap.get(key).add(child);

	}

    //...or you can add a child by adding a new EasyElement.
	public void addChild(EasyElement child)
	{
		String childName = child.getName();

		children.add(child);

		if(!childMap.containsKey(childName))
		{
			childMap.put(childName, new ArrayList<EasyElement>());
		}

		childMap.get(childName).add(child);

	}

	public void setAttribute(String key, String value)
	{
		attributes.put(key, value);

	}


    public ArrayList<String> getExceptions()
    {
		return exceptions;
	}

	public String getAbsPath()
	{

		StringBuffer returnBuffer = new StringBuffer("");

		if(absPath != null)
		{
			returnBuffer.append(absPath);
		}

		return returnBuffer.toString();

	}


protected ArrayList<String> exceptions = new ArrayList<String>();

protected ArrayList<EasyElement> children = new ArrayList<EasyElement>();

protected HashMap<String, ArrayList<EasyElement>> childMap = new HashMap<String, ArrayList<EasyElement>>();

protected TreeMap<String, String> attributes = new TreeMap <String, String>();

protected String name=null;

protected String text=null;

protected String absPath = null;

public static final String BLANK = "_blank_";

}