package net.kurttrue.www.isgihgen;

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
	* <h1>EasyElement: abstracts org.w3c.dom classes</h1>
    * EasyElement and DocHandler are convenience classes that abstract away java's DOM and SAX classes.
    * EasyElement allows the calling routine to read and write XML data in a (relatively) intuitive and non wordy way.
    *
    * @author Kurt True
    * @version 1.06
    * @since 2018-02-03
	*/


public class EasyElement
{

	public EasyElement()
	{

	}

	/***

	Make an <b>EasyElement</b> representing the root element and its decendants contained in the xml document at path <b>inputPath</b>.

	*/

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

	/***

	Make an EasyElement that represents org.w3c.dom.Element <b>element</b>.

	*/

	public EasyElement buildFromElement(Element element)
	{

		populate(element);

		return this;
	}

	/***

	Set the tag name for this EasyElement.

	*/


	public EasyElement setName(String aname)
	{
		name = aname;

		return this;
	}

	/***

	Set the text value for this EasyElement.

	*/

	public EasyElement setText(String atext)
	{

		text = atext.trim();

		return this;
	}

	/***

	Return all EasyElements in the tree (<b>this</b> and all descendants of <b>this</b>) that have the name <b>ename</b>.

	*/

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

    /*
    //put this one aside 1f18. -kt

	public ArrayList<EasyElement> getXPathUncles(String handle, String value, String tg_name)
	{

		//this method solves the problem of finding the author for a particular story or page...
		//but it can also find any element that has an Uncle relationship with the Element with the attribute represented by handle->value

		ArrayList<EasyElement> ees = new ArrayList<EasyElement>();

        //ArrayList<EasyElement> namematches = this.getEasyElementsByName(tg_name);



		return ees;
	}

	*/

   /***

     Returns true if this has an attribute anywhere in its tree that matches handle->value.

   */

    public EasyElement getCacheable()
    {

		return this.getCacheable(this);

	}

	/***

	Returns a copy of EasyElement iee nested within EasyElements that correspond to iee's antecedents.
	(Useful for creating a cache of Documents.)

	*/

	protected EasyElement getCacheable(EasyElement iee)
	{

	    EasyElement oee = iee;

	    //if iee has no parent, nothing to do.
	    if(iee.hasParent())
	    {

			EasyElement iparent = iee.getParent();

			oee = iparent.copy(NOCHILDREN);

			oee.addChild(iee.copy(COPYCHILDREN));

			//if you find a parent, go recursive.
			if(oee.hasParent())
			{
				oee = this.getCacheable(oee);
			}
		}

	    return oee;

	}

	/***

	True if this EasyElement has a parent EasyElement.  (False for a root element.)

	*/

	public boolean hasParent()
	{

		return parent != null;
	}


	/***

	   Convert text node represented by variable text into child EasyElement childname->text
	   if calling routine calls this method, getText() for EasyElement this will return a blank string.

	*/

	public void textToChild(String childname)
	{

		textToChild=true;

		this.addChild(childname, text);

	}


	/***

	   Returns true if this has an attribute anywhere in its tree that matches handle->value.

	*/

	public boolean hasAttributePair(String handle, String value)
	{

		//this method returns true if this has an attribute anywhere in its tree that matches handle->value.

		boolean returnBoolean = false;

		ArrayList<EasyElement> matches = this.getEasyElementsByAttribute(handle);

		for(EasyElement match : matches)
		{

			TreeMap<String, String> attMap = match.getAttributes();

			if(attMap.containsKey(handle))
			{

				if(attMap.get(handle).equals(value))
				{

					//return true if a matching attribute found.
					returnBoolean=true;
				}
			}
		}

		return returnBoolean;


	}

 /***

		Returns a stripped down EasyElement that represents all XPaths to elements that match name tgname.


 */

	public EasyElement getXPathsForName(String tgname)
	{

		//this method returns a stripped down EasyElement that represents all XPaths to elements that match name tgname.

		EasyElement returnEE = null;


        if(this.getEasyElementsByName(tgname).size()>0)
        {

			returnEE = this.copy(NOCHILDREN);

			for(EasyElement child : this.getChildren())
			{
	             if(child.getXPathsForName(tgname)!=null)
	             {
					 returnEE.addChild(child.getXPathsForName(tgname));
				 }
			}
		}

		return returnEE;

	}

    /***

	  Returns a stripped down EasyElement that represents all XPaths to elements that have attributes that match handle->value.


    */


	public EasyElement getXPathsForAttribute(String handle, String value)
	{

		//this method returns a stripped down EasyElement that represents all XPaths to elements that have attributes that match handle->value.

		EasyElement returnEE = null;

		if(this.hasAttributePair(handle, value))
		{

			     returnEE=this.copy(NOCHILDREN);

			     for(EasyElement child : this.getChildren())
			     {
					 if(child.getXPathsForAttribute(handle, value)!=null)
					 {
                         returnEE.addChild(child.getXPathsForAttribute(handle, value));
					 }
				 }


		}


		return returnEE;

	}

	/***

	Makes a copy of this EasyElement, with or without children.

	*/

	public EasyElement copy(boolean copychildren)
	{

		EasyElement returnEE = new EasyElement();

		returnEE.setName(this.getName());

		returnEE.setText(this.getText());

		for(String key : this.getAttributes().keySet())
		{
			returnEE.setAttribute(key, this.getAttributes().get(key));
		}

		if(copychildren)
		{

			for(EasyElement child : this.getChildren())
			{
				returnEE.addChild(child.copy(copychildren));
			}


	    }

		if(parent!=null)
		{
			//never call parent.copy(COPYCHILDREN), as that will cause an endless loop.
			returnEE.setParent(parent.copy(NOCHILDREN));
		}



		return returnEE;

	}

	/***

	Return all EasyElements in the tree (<b>this</b> and all descendants of <b>this</b>) that have an attribute with the name <b>attname</b>.

	*/

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

	/***

	This method is called from within the <b>EasyElement</b> class.  A calling routine could use this method instead of <b>EasyElement.buildFromElement(org.w3c.dom.Element)</b> to populate the <b>EasyElement</b>.

	*/

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

			     if(node.getNodeValue().trim().length()>0)
			     {
			         this.setText(node.getNodeValue());


			     }
			 }


	     }

	}

	/***

	The tag name.  Corresponds to <b>org.w3c.dom.Element.getTagName()</b>


	*/

    public String getName()
    {

		StringBuffer returnBuffer = new StringBuffer("");

		if(name != null)
		{

			returnBuffer.append(name);
		}

		return returnBuffer.toString();
	}

	/***

	Text value of the <b>EasyElement</b>.  Corresponds to <b>org.w3c.dom.getTextContent()</b>.

	*/

	public String getText()
	{

		StringBuffer returnBuffer = new StringBuffer("");

		if(text != null)
		{

			//textToChild means calling class has converted text to a child EasyElement
			//if(!textToChild)
			//{

			  returnBuffer.append(text);
		    //}
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

	/***

	<b>True</b> if <b>EasyElement</b> has a child with the name <b>childName</b>.

	*/


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

	/***

	A convenience class helpful when the calling routine knows that an <b>EasyElement</b> has only one child with the name <b>childName</b>.  Returns an empty <b>ArrayList&#60;EasyElement&#62;</b> if <b>EasyElement</b> has no child with the name <b>childName</b>.

	*/

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

	/***

	Returns all children with the name <b>childName</b>.

	*/

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

	public void setParent(EasyElement ee)
	{

		parent = ee;

	}


	public EasyElement getParent()
	{

		return parent;
	}

	/***

	A convenience method for adding a child without invoking the <b>EasyElement</b> constructor.

	*/

    //you can add a key as a key, value pair if you're in a hurry...
	public void addChild(String key, String value)
	{
		EasyElement child = new EasyElement().setName(key).setText(value);

		child.setParent(this);

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

		child.setParent(this);

		String childName = child.getName();

		children.add(child);

		if(!childMap.containsKey(childName))
		{
			childMap.put(childName, new ArrayList<EasyElement>());
		}

		childMap.get(childName).add(child);

	}


    /***

    Add an xml attribute key value pair to the EasyElement.  Corresponds to <b>org.w3c.dom.setAttribute(String, String)</b>.  In <b>EasyElement</b> attributes are represented by a <b>TreeMap<String, String></b>.

    */

	public void setAttribute(String key, String value)
	{
		attributes.put(key, value);

	}


    public ArrayList<String> getExceptions()
    {
		return exceptions;
	}

	/***

	If the calling routine populated this <b>EasyElement</b> with method <b>EasyElement.buildFromPath(String inputPath)</b>, this method returns the absolute path of inputPath.  Otherwise, it returns a blank <b>String</b>.

	*/

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

protected EasyElement parent = null;

//converts to true if textToChild() method called.
protected boolean textToChild = false;

public static final String BLANK = "_blank_";

public static final boolean COPYCHILDREN=true;

public static final boolean NOCHILDREN=false;



}