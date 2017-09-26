package net.kurttrue.www.epubGenerator;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;

import org.w3c.dom.Element;

public class NavPointHandler
{

	public NavPointHandler()
	{

       String xyz="123";

	}

	//add a rule that handles parent/child relationship.
	public void addRule(String parent, String child)
	{

		ruleMap.put(child, parent);

		if(parent.equals(NOPARENT))
		{
			eve = child;
		}

	}


	public String getParentCategory(String child)
	{

		StringBuffer returnBuffer = new StringBuffer("");

		if(ruleMap.containsKey(child))
		{
			returnBuffer.append(ruleMap.get(child));
		}

		return returnBuffer.toString();

	}


    public void addCategory(String category)
    {

		categoryMap.put(category, new ArrayList<EasyElement>());
	}

	//a convenience method so calling routine doesn't have to specify a value for isTitle
	public void addNavPoint(EasyElement easyelement, String contentURL)
	{
		this.addNavPoint(easyelement, contentURL, ISNOTTITLE);
	}


	public void addNavPoint(EasyElement easyelement, String contentURL, boolean isTitle)
	{


         //easyelement is just a temp space that we get so we can determine the value for
         //navMap->navPoint->navLabel->text
         //EasyElement easyelement = new EasyElement().buildFromElement(element);

         //if calling routine didn't mess up, category will be the value of the "navcategory" attribute of Element element.
         TreeMap<String, String> attributes = easyelement.getAttributes();

         if(attributes.containsKey(NAVCATEGORY))
         {

		      feedback.add(this.getClass().getName() + " found " + NAVCATEGORY);

		      String category = attributes.get(NAVCATEGORY);

		      String text = this.getText(easyelement);

		      EasyElement navPointEE = new EasyElement().setName(NAVPOINT);

		      EasyElement navLabelEE = new EasyElement().setName(NAVLABEL);

		      EasyElement textEE = new EasyElement().setName(TEXT);

		      EasyElement contentEE = new EasyElement().setName(CONTENT);

		      contentEE.setAttribute("src", contentURL);

		      textEE.setText(text);

		      //if the Element contains a value that belongs in ncx->docTitle
		      if(isTitle)
		      {
				 titleList.add(text);
			  }

		      navPointEE.addChild(navLabelEE);

		      navPointEE.addChild(contentEE);

		      String navPointCounterString = Integer.toString(navPointCounter);

		      navPointEE.setAttribute(PLAYORDER, navPointCounterString);

		      navPointEE.setAttribute(ID, DEFAULTIDPREFIX + navPointCounterString);

		      //the navPointCounter is linear, though the navMap is a tree.
		      navPointCounter++;

		      navLabelEE.addChild(textEE);

		      feedback.add("category->" + category);

		      if(categoryMap.containsKey(category))
		      {

				 feedback.add("category match found.");

				 categoryMap.get(category).add(navPointEE);

				 if(ruleMap.containsKey(category))
				 {

					 String parent = ruleMap.get(category);

					 //the top level navPoint won't have a parent.
					 if(!parent.equals(NOPARENT))
					 {

						 if(categoryMap.containsKey(parent))
						 {
							 ArrayList<EasyElement> ees = categoryMap.get(parent);

							 if(ees.size()>0)
							 {
								 EasyElement parentEE = ees.get(ees.size()-1);

								 parentEE.addChild(navPointEE);
							 }
							 else
							 {
								 exceptions.add(this.getClass().getName() + " exception: no parent navPoint found for " + text);
							 }
						 }
						 else
						 {

							 	 exceptions.add(this.getClass().getName() + " exception: no category found for " + text);

					     }

				     }
				 }
				 else
				 {

					exceptions.add(this.getClass().getName() + " exception: no rule for category " + category);

				 }

			  }
			  else
			  {
				  exceptions.add(this.getClass().getName() + " exception: unknown category " + category);
		      }

		 }
		 else
		 {

			 feedback.add("NavPointHandler did not find " + NAVCATEGORY);
		 }

	}


	public String getText(EasyElement easyelement)
	{

		StringBuffer returnBuffer = new StringBuffer("");

		if(easyelement.getText().length()>0)
		{

			returnBuffer.append(easyelement.getText());
		}
		else
		{

			if(easyelement.getChildren().size()>0)
			{

				//this is where method becomes recursive.
				//if the element doesn't have text, look for text in the element's first child.
				returnBuffer.append(this.getText(easyelement.getChildren().get(0)));

			}
		}

		return returnBuffer.toString();

	}


    public ArrayList<String> getExceptions()
    {
		return exceptions;
	}

	public ArrayList<String> getFeedback()
	{

		return feedback;
	}

	public EasyElement getNavMap()
	{

		EasyElement navMap = new EasyElement().setName(NAVMAP);

		//the navMap can be pointy or flat.
		//if it's pointy, there will only be one eve EasyElement (in other words, navMap will have only one child, that child might have many chldren and grandchildren.)
		for(EasyElement eveEE : categoryMap.get(eve))
		{

			navMap.addChild(eveEE);
		}

		return navMap;

	}

	public void addToTitle(String titleEntry)
	{
		//title might be one or more children of a div or span.
		titleList.add(titleEntry);

	}


	protected String getTitle()
	{
		StringBuffer returnBuffer = new StringBuffer("");

		for(String titleEntry : titleList)
		{

			returnBuffer.append(titleEntry);
		}

		return returnBuffer.toString();
	}






//map of navMap child/parent relationships.
protected HashMap<String, String> ruleMap = new HashMap<String, String>();

//map of hierarchy categories to navMap elements.
protected HashMap<String, ArrayList<EasyElement>> categoryMap = new HashMap<String, ArrayList<EasyElement>>();

public static final String NOPARENT = "noparent";

public static final String NAVMAP = "navMap";

//not using this constant, as NAVPOINT and NAVCATEGORY are redundant.
public static final String NAVPOINT = "navPoint";

public static final String NAVLABEL = "navLabel";

public static final String TEXT = "text";

public static final String PLAYORDER = "playOrder";

public static final String ID = "id";

public static final String DEFAULTIDPREFIX = "np-";

public static final String NAVCATEGORY = "navcategory";

public static final String CONTENT = "content";

public static final String EPUBROOT = "epub";

public static final String HIERARCHY = "hierarchy";

public static final String PACKAGE = "package";

public static final String UNIQUEID = "unique-identifier";

public static final String PATHS = "paths";

public static final String PATH = "path";

public static final String BIB = "bib";

public static final String AUTHOR = "author";

public static final String EPUBTITLE = "dc:title";

public static final String OPF = "opf";

protected ArrayList<String> exceptions = new ArrayList<String>();

protected ArrayList<String> feedback = new ArrayList<String>();

//the category that has no parent category.
protected String eve = null;

//navPoint@playOrder attribute is one-based.
protected int navPointCounter = 1;

public static final boolean ISTITLE = true;
public static final boolean ISNOTTITLE = false;

protected ArrayList<String> titleList = new ArrayList<String>();



}