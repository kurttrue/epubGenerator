package net.kurttrue.www.isgihgen;

import java.util.ArrayList;

import java.util.TreeMap;

import java.io.File;

public class InputHandler
{

	public InputHandler()
	{


	}

	public InputHandler setInputPath(String apath)
	{

		path = apath;

		return this;
	}

	/***

	Returns the absolute path of the path set in <b>InputHandler.setInputPath(String path)</b>.

	*/

	public String getRealPath(String fullPath)
	{

		//this method returns a file's path.
		//used to get full paths of files represented in the input file with relative paths
		StringBuffer returnBuffer = new StringBuffer("");

		String[] components = fullPath.split(File.separator);

		for(int i=0;i<components.length-1;i++)
		{
			String component = components[i];

			//leave out the superfluous dot component.
			if(!component.equals("."))
			{

				if(i>0)
				{
					returnBuffer.append(File.separator);
				}


			    returnBuffer.append(component);

		    }

		}

		return returnBuffer.toString();

	}

	/***

	Parse the input file.  Move content files.  Create the epub.

	*/


	public void parse()
	{

		if(path != null)
		{

		   navpointhandler = new NavPointHandler();

		   EasyElement rootEE = new EasyElement().buildFromPath(path);

		   //ncx is the root element for toc.ncx.
		   ncxEE = rootEE.getFirst(NCX);

		   String absPath = rootEE.getAbsPath();

		   //System.out.println("absPath->" + absPath);

		   //System.out.println("File.separator()->" + File.separator);

		   //local getRealPath() method tidies the path String, but String absPath and String realPath represent the same path.
		   realPath = this.getRealPath(absPath);

		   feedback.add("realPath->" + realPath);

		   EasyElement hierarchyEE = rootEE.getFirst(NavPointHandler.HIERARCHY);

		   for(EasyElement child : hierarchyEE.getChildren())
		   {

		      this.addToHierarchy(child, NavPointHandler.NOPARENT);

		   }

		   EasyElement contentEE = rootEE.getFirst(NavPointHandler.CONTENT);

		   EasyElement pathsEE = contentEE.getFirst(NavPointHandler.PATHS);

		   containerEE = rootEE.getFirst(CONTAINER);

		   EasyElement rootFilesEE = new EasyElement().setName(ROOTFILES);

		   EasyElement rootFileEE = new EasyElement().setName(ROOTFILE);

		   containerEE.addChild(rootFilesEE);

		   rootFilesEE.addChild(rootFileEE);

		   ArrayList<EasyElement> paths = pathsEE.getChildren();

		   EasyElement outputEE = rootEE.getFirst(OUTPUT);

		   //overwrite will be a zero-length string if outputEE does not have child DELETE
		   //or if child DELETE has no text value.
		   overwrite = outputEE.getFirst(DELETE).getText();

		   epubName = outputEE.getFirst(NAME).getText();

		   EasyElement outputPathsEE = outputEE.getFirst(NavPointHandler.PATHS);

           oebpsText = outputPathsEE.getFirst(OEBPS).getText();

           metainfText = outputPathsEE.getFirst(METAINF).getText();

           String outputBaseRaw = outputPathsEE.getFirst(ROOT).getText() + File.separator + DOCSDIR;

           //the replaceFirst() below converts the tilde to an absolute path representing the user's home directory.
           //(java.io.File.isAbsolute() returns false if you construct a file object with a path that starts with ~).
           outputBase = outputBaseRaw.replaceFirst("^~", System.getProperty("user.home"));

           String zipBaseRaw = outputPathsEE.getFirst(ROOT).getText() + File.separator + EPUBDIR;

           //this is the same replaceFirst() that creates String outputBaseRaw.
           //maybe later make this section less redundant by creating a baseRaw variable out of outputPathsEE.getFirst(ROOT).getText()
           zipBase = zipBaseRaw.replaceFirst("^~", System.getProperty("user.home"));

		   //outputRoot should really be called oebpsRoot.
		   outputRoot = outputBase + File.separator + oebpsText;

		   metaRoot = outputBase + File.separator + metainfText;

		   staticRoot = outputBase;

		   String opfPath = oebpsText + File.separator + OPFNAME;

		   rootFileEE.setAttribute(FULLPATH, opfPath);

           //MediaTypeHandler.getMediaType(String) doesn't require a path to a real file.
           //just a hypothentical path.
		   rootFileEE.setAttribute(MEDIATYPE, mediatypehandler.getMediaType(opfPath));

		   String outputText=null;



		   //if there's no text element in output->paths, text will be output to outputRoot.
		   if(outputPathsEE.hasChild(TEXT))
		   {
			   outputText = outputPathsEE.getFirst(TEXT).getText();
		   }
		   else
		   {
			   outputText = "";
		   }


		   boolean outputPathIsAbsolute = new File(outputRoot).isAbsolute();

		   StringBuffer outputRootBuffer = new StringBuffer("");

		   if(!outputPathIsAbsolute)
		   {

			  outputRootBuffer.append(realPath);

		      outputRootBuffer.append(File.separator);


		   }

		   outputRootBuffer.append(outputText);

		   outputRootBuffer.append(File.separator);

		   feedback.add("outputRootBuffer.toString()->" + outputRootBuffer.toString());

		   EasyElement opfEE = rootEE.getFirst(NavPointHandler.OPF);

		   packageEE = opfEE.getFirst(NavPointHandler.PACKAGE);

		   String uniqueid = packageEE.getAttributes().get(NavPointHandler.UNIQUEID);

		   if(!packageEE.hasChild(METADATA))
		   {
			   packageEE.addChild(new EasyElement().setName(METADATA));
		   }

           IOHandler iohandler = new IOHandler().setInputPath(realPath).setOutputPath(this.getOutputRoot() + File.separator + outputText);

           if(clear)
           {
              //delete any files previously written to the output path.
              //if calling routine calls InputHandler.setClear(CLEARYES).
              iohandler.clear();
	       }


		   for(EasyElement path : paths)
		   {


			   boolean isCover = false;

			   String realFilePath;  // = realPath + File.separator + path.getText();

			   //System.out.println(realFilePath + "->" + realFilePath);

			   //protected void handleIO(String realFilePath, IOHandler iohandler, String outputText, boolean isCover)


			   File checkfile = new File(path.getText());

			   if(checkfile.isAbsolute())
			   {
				   //System.out.println("absolute");
				   realFilePath = path.getText();
			   }
			   else
			   {
				   //System.out.println("NOT absolute");
				   realFilePath = realPath + File.separator + path.getText();
			   }

			   TreeMap<String, String> pathAttributes = path.getAttributes();

			   if(pathAttributes.containsKey(TYPE))
			   {

				     if(pathAttributes.get(TYPE).equals(COVER))
				     {
						 cover = new Cover();

						 isCover=true;

						 if(pathAttributes.containsKey(NavPointHandler.ID))
						 {
							 cover.setID(pathAttributes.get(NavPointHandler.ID));
						 }

				     }
			   }

			   this.handleIO(realFilePath, iohandler, outputText, isCover);








		   }


		   EasyElement docTitleEE = new EasyElement().setName(DOCTITLE);

		   docTitleEE.addChild(new EasyElement().setName(NavPointHandler.TEXT).setText(navpointhandler.getTitle()));

           ncxEE.addChild(docTitleEE);

		   ncxEE.addChild(navpointhandler.getNavMap());

		   //System.out.println("packageEE.getName()->" + packageEE.getName());


           /*
           for(String key : packageEE.getAttributes().keySet())
           {

			   System.out.println(key + "->" + packageEE.getAttributes().get(key));

		   }
		   */


           //packageEE is the root element for content.opf
           //manifestEE is the child of packageEE.
           EasyElement manifestEE = new EasyElement().setName(MANIFEST);
           //first add non-linear items to manifest.

           //id count in manifest is one-based.
           int counter = 1;

           for(String key : nonLinearMap.keySet())
           {
			   EasyElement nonLinearItem= nonLinearMap.get(key);

			   nonLinearItem.setAttribute(uniqueid, ITEM + counter);

			   manifestEE.addChild(nonLinearItem);

			   //a wordy way of saying...
			   //if the cover object's image path, matches the path of this non linear item...
			   //write the item id to the cover object.
			   if(cover!=null)
			   {



				   if(cover.hasImgPath())
				   {
					   TreeMap<String, String> nlattributes = nonLinearItem.getAttributes();

					   if(nlattributes.containsKey(HREF))
					   {

						feedback.add("nlattributes.get(HREF)->" + nlattributes.get(HREF) + " " + "cover.getImgPath()->" + cover.getImgPath());

					   	if(cover.getImgPath().equals(nlattributes.get(HREF)))
					   	{
						   cover.setImgID(ITEM + counter);
					   	}

				       }
				   }
			   }

			   counter++;
		   }






		   EasyElement spineEE = new EasyElement().setName(SPINE);

		   spineEE.setAttribute(TOC, NCX);


		   //write the cover itemref to the spine before writing all the linear items.
		   if(cover!=null)
		   {
			   EasyElement coverItem = new EasyElement().setName(ITEMREF);

			   coverItem.setAttribute(IDREF, cover.getID());

			   coverItem.setAttribute(LINEAR, LINEARNO);

		       spineEE.addChild(coverItem);


			   //the package->guide->reference element follows the convention of Gutenberg press content.opf
			   //the element doesn't really have a purpose in any epub reader that I know of.  -kt 25ag17
			   EasyElement refEE = new EasyElement().setName(REFERENCE);

			   refEE.setAttribute(TYPE, COVER);

			   refEE.setAttribute(HREF, cover.getPath());

			   refEE.setAttribute(TITLE, COVER.substring(0,1).toUpperCase() + COVER.substring(1).toLowerCase());

			   guideEE.addChild(refEE);



		   }



		   for(EasyElement linearItem : linearList)
		   {

			   String itemCountStr = ITEM + counter;

			   EasyElement spineItem = new EasyElement().setName(ITEMREF);

			   spineItem.setAttribute(LINEAR, LINEARYES);

			   spineItem.setAttribute(IDREF, itemCountStr);

			   linearItem.setAttribute(uniqueid, itemCountStr);

			   manifestEE.addChild(linearItem);

			   spineEE.addChild(spineItem);

			   counter++;
		   }

		   if(cover!=null)
		   {

			   EasyElement mCover = new EasyElement().setName(ITEM);

			   if(cover.hasPath())
			   {

			        mCover.setAttribute(HREF, cover.getPath());

		       }

		       mCover.setAttribute(NavPointHandler.ID, cover.getID());

			   mCover.setAttribute(MEDIATYPE, mediatypehandler.getMediaType(cover.getPath()));

			   EasyElement mdEE = packageEE.getFirst(METADATA);

			   for(String bkey : bibMap.keySet())
			   {
				   //mdEE.addChild(this.getBibMetaEE(bkey));
				   mdEE.addChild(bibMap.get(bkey));
			   }

			   mdEE.addChild(this.getCoverMetaEE());

			   manifestEE.addChild(mCover);

               iohandler.copy(new File(cover.getPath()).getName());


	       }

	       //copy the mimetype which is always the same in any epub.
	       //IOHandler mimehandler = new IOHandler().setInputPath(System.getProperty("user.dir") + File.separator + STATIC).setOutputPath(realPath + File.separator + staticRoot);
	       //swapped out line above for line below to support absolute paths for output.
	       IOHandler mimehandler = new IOHandler().setInputPath(System.getProperty("user.dir") + File.separator + STATIC).setOutputPath(this.getOutputRootParent());

           feedback.add("mimehandler input->" + System.getProperty("user.dir") + File.separator + STATIC);

           feedback.add("mimehandler output->" + realPath  + File.separator + staticRoot);

           mimehandler.copy(MIMETYPE);





		   EasyElement tocItem = new EasyElement().setName(ITEM);

		   tocItem.setAttribute(HREF, TOC + "." + NCX);

		   tocItem.setAttribute(MEDIATYPE, DEFAULTMTYPE);

		   tocItem.setAttribute(uniqueid, NCX);

		   manifestEE.addChild(tocItem);

		   packageEE.addChild(manifestEE);

		   packageEE.addChild(spineEE);

		   if(guideEE.getChildren().size()>0)
		   {
			   packageEE.addChild(guideEE);
		   }





		   feedback.add("hierarchyEE.getName()->" + hierarchyEE.getName());

		   feedback.add(NavPointHandler.UNIQUEID + "->" + uniqueid);

		   feedback.add("ncx debug String...");

		   feedback.add(this.getDebugStr(ncxEE, 0));

		   feedback.add("... end of ncx debug String.");




	    }
	    else
	    {

			exceptions.add(this.getClass().getName() + "exception: path is null.  call setInputPath()");
		}




	}

	protected TreeMap<String, String> getMetaMap(EasyElement ee)
	{
		//this method turns <meta name="name" content="content"/> tags into a TreeMap.

		TreeMap<String, String> returnMap = new TreeMap<String, String>();

		EasyElement headEE = ee.getFirst(HEAD);

		ArrayList<EasyElement> metas = headEE.getEasyElementsByName(META);

		for(EasyElement metaEE : metas)
		{

			TreeMap<String, String> attributes = metaEE.getAttributes();

			if(attributes.containsKey(NAME) && attributes.containsKey(NavPointHandler.CONTENT))
			{

				returnMap.put(attributes.get(NAME), attributes.get(NavPointHandler.CONTENT));

			}

		}

		return returnMap;

	}

	protected void handleIO(String realFilePath, IOHandler iohandler, String outputText, boolean isCover)
	{



			   File contentfile = new File(realFilePath);

	           String contentfileName = contentfile.getName();


			   if(contentfile.exists())
			   {
				   //System.out.println(realFilePath + " exists.");

				   EasyElement contentfileEE = new EasyElement().buildFromPath(realFilePath);

				   //isCover might be true based on the mother input file.
				   //but a meta element in the content file can also flag it as the cover file.
				   TreeMap<String, String> metaMap = this.getMetaMap(contentfileEE);
                   //the first condition is here because we don't want to reset isCover if mother input file says it's true.
				   if(!isCover)
				   {

				      if(metaMap.containsKey(TYPE))
				      {

					      isCover = metaMap.get(TYPE).equals(COVER);

						  if(isCover)
						  {

						      cover = new Cover();

						      if(metaMap.containsKey(NavPointHandler.ID))
						      {
								  cover.setID(metaMap.get(NavPointHandler.ID));
						      }

					      }


				      }

			        }

				   //System.out.println("contentfileEE.getName()->" + contentfileEE.getName());

                   //System.out.println("############look for attribute " + NavPointHandler.NAVPOINT);

                   boolean isLinear = false;

                   ArrayList<EasyElement> titleList = contentfileEE.getEasyElementsByAttribute(TITLETAG);

                   //increment this integer in the for loop so that you'll have a unique key in bibMap.
                   int tcounter = 0;

                   for(EasyElement tee : titleList)
			       {

					   TreeMap<String, String> attributeMap = tee.getAttributes();

					   String targetStr;

					   //the id attribute is the anchor tag for purposes of toc.ncx
					   if(attributeMap.containsKey(NavPointHandler.ID))
					   {
						   targetStr = "#" + attributeMap.get(NavPointHandler.ID);
					   }
					   else
					   {
						   targetStr = "";
					   }

					   String titleValue = attributeMap.get(TITLETAG);

					   //first split the contents of TITLETAG attribute on whitespace.
					   String[] pairs = titleValue.split("\\s+");

					   //then put the pairs into a map.
					   TreeMap<String, String> pairMap = new TreeMap<String, String>();

					   for(String pair : pairs)
					   {
						   String[] halves = pair.split(VALUEDELIM);

						   String key;


						   StringBuffer valueBuffer = new StringBuffer("");

						   //if you have no delimiter, value is a blank string.
						   if(halves.length>1)
						   {
							   key = halves[0];

							   //loop through halves[] array in case VALUEDELIM appears in the value.

							   for(int v=1;v<halves.length;v++)
							   {

								  //if you've split on the delim, and the delim is part of the value,
								  //put the delim back in the value.
								  if(v>1)
								  {
									 valueBuffer.append(VALUEDELIM);
							      }
							      valueBuffer.append(halves[v]);
							      //value = halves[v];
							   }
						   }
						   else
						   {
							   key = pair;
							   //value = "";
						   }

						   String value = valueBuffer.toString();

						   pairMap.put(key, value);

						   //NavPointHandler wants a NAVCATEGORY attribute, but that's not a legal XHTML attribute,
						   //so relying on this cheat.
						   tee.setAttribute(key, value);

						   //if this content file contains metadata for the navPoint in the toc document,
						   //then this contentfile needs to be designated as linear in content.opf
						   if(key.equals(NavPointHandler.NAVCATEGORY))
						   {
							   isLinear = false;
						   }
					   }

					   //System.out.println("targetStr->" + targetStr);

					   boolean includeInTitle = NavPointHandler.ISNOTTITLE;

					   //simplest way to mark up the title is to add a bib attribute to the title element, e.g.
					   //<h4 title="navcategory=book bib=dc:title">The owl and the pussycat</h4>



					   if(pairMap.containsKey(NavPointHandler.BIB))
					   {
						   String bibvalue = pairMap.get(NavPointHandler.BIB);

						   if(bibvalue.equals(NavPointHandler.EPUBTITLE))
						   {

							   includeInTitle = NavPointHandler.ISTITLE;
						   }

						   //NavPointHandler.getText() is recursive, so it gets the EasyElement.getText() value
						   //of EasyElement tee and all its children.
						   EasyElement bee = new EasyElement().setName(bibvalue).setText(navpointhandler.getText(tee));

						   bibMap.put(bibvalue + "_" + tcounter, bee);

						   //values that become attributes are formatted in title attribute this way...
						   //<htmltag title="bib=dc:title bib.attr_name=attr_value">text that becomes value of EasyElement bee</htmltag>
						   for(String bkey : pairMap.keySet())
						   {
							   String[] bpair = bkey.split("\\" + ATTRSPLITBY);

							   if(bpair.length>1)
							   {

								   String bibflag = bpair[0];

								   if(bibflag.equals(NavPointHandler.BIB))
								   {

                                       feedback.add("bibflag..." + bkey + "->" + pairMap.get(bkey));

									   String kvStr = pairMap.get(bkey);

									   String[] kvArray = kvStr.split(VALUEDELIM);

									   String attributeName=bpair[1];

									   String attributeValue=pairMap.get(bkey);

									   /*

									   //this should never evaluate to false.
									   if(kvArray.length>0)
									   {
										  attributeName=kvArray[0];
									   }


									   StringBuffer bpvalueBuffer = new StringBuffer("");

									   for(int bi=1;bi<kvArray.length;bi++)
									   {

										   if(bi>1)
										   {
											   bpvalueBuffer.append(VALUEDELIM);
										   }

										   bpvalueBuffer.append(kvArray[bi]);

									   }

									   attributeValue = bpvalueBuffer.toString();
									   */

									   if(attributeName!=null)
									   {

										   bee.setAttribute(attributeName, attributeValue);
									   }

							       }



							   }



						   }


					   }



					   //sending path.getText() doesn't solve the problem of what to do with absolute paths.
					   navpointhandler.addNavPoint(tee, outputText + File.separator + contentfileName + targetStr, includeInTitle);


					   tcounter++;

				   }


					/*

                   //the problem with the navPointList and is it relies on an illegal tag.
                   //so phasing out NAVCATEGORY and relying on a TITLE tag with a parsible value.
				   ArrayList<EasyElement> navPointList = contentfileEE.getEasyElementsByAttribute(NavPointHandler.NAVCATEGORY);

				   //if isLinear is already true based on contents of TITLETAG elements, don't change it to false.
				   if(!isLinear)
				   {
				       isLinear = navPointList.size()>0;
			       }


				   for(EasyElement npee : navPointList)
				   {

					   TreeMap<String, String> attributeMap = npee.getAttributes();

					   String targetStr;

					   //the id attribute is the anchor tag for purposes of toc.ncx
					   if(attributeMap.containsKey(NavPointHandler.ID))
					   {
						   targetStr = "#" + attributeMap.get(NavPointHandler.ID);
					   }
					   else
					   {
						   targetStr = "";
					   }

					   //System.out.println("targetStr->" + targetStr);

					   boolean includeInTitle = NavPointHandler.ISNOTTITLE;

					   //simplest way to mark up the title is to add a bib attribute to the title element, e.g.
					   //<h4 navPoint="book" id="booktitle" bib="epubtitle">The owl and the pussycat</h4>
					   if(attributeMap.containsKey(NavPointHandler.BIB))
					   {
						   String bibvalue = attributeMap.get(NavPointHandler.BIB);

						   if(bibvalue.equals(NavPointHandler.EPUBTITLE))
						   {

							   includeInTitle = NavPointHandler.ISTITLE;
						   }
					   }

					   //sending path.getText() doesn't solve the problem of what to do with absolute paths.
					   navpointhandler.addNavPoint(npee, outputText + File.separator + contentfileName + targetStr, includeInTitle);


				   }

				   */


				   if(isCover)
				   {

					   cover.setPath(outputText + File.separator + contentfileName);
					   //don't add the cover to the linear list, as it gets special handling.

				   }
                   else
                   {

					   //linearList is the list of hrefs that appear inside the manifest element in content.opf
					   EasyElement linearEE = new EasyElement().setName(ITEM);

					   linearEE.setAttribute(HREF, outputText + File.separator + contentfileName);

					   linearEE.setAttribute(MEDIATYPE, "application/xhtml+xml");

					   linearList.add(linearEE);

					   //copy the file from the input location to the output location.

					   iohandler.copy(contentfileName);

					   exceptions.addAll(iohandler.getExceptions());
			       }

	               //protected void extractNonLinear(EasyElement ee, String handle)

	               //these two calls ensure that images and css referenced in the content file go into the content.opf manifest.
	               this.extractNonLinear(contentfileEE, HREF, false, outputText);
	               this.extractNonLinear(contentfileEE, SRC, isCover, outputText);

           //move over the non linear files first (css, images).
           for(String nlname : nonLinearMap.keySet())
           {
			   iohandler.copy(nlname);
		   }





	/*


	               ArrayList<EasyElement> hrefList = contentfileEE.getEasyElementsByAttribute(HREF);



				   for(EasyElement hrefEE : hrefList)
				   {

					   //we know that hrefEE will have an attribute with the handle HREF
					   //because we retrieved it with the method EasyElement.getEasyElementsByAttribute(String).
					   String href = hrefEE.getAttributes().get(HREF);

					   //the same link might appear in more than one content document...
					   //so check to make sure we haven't already created an item for the href.
					   if(!nonLinearMap.containsKey(href))
					   {

						   nonLinearMap.put(href, this.linkToItem(hrefEE));
					   }

				   }

    */

			   }
			   else
			   {

				   //System.out.println(realFilePath + " does NOT exist.");
				   //if the file doesn't exist, check is it a pattern with a wildcard.
				   //like this... /path/to/directory/*.xhtml
				   ArrayList<String> matches = PathMatcher.getMatches(realFilePath);

				   for(String match : matches)
				   {

                       //this is where this method becomes recursive.
					   this.handleIO(match, iohandler, outputText, isCover);
				   }


				   //if the pattern submitted doesn't match any files, report that as an exception.
				   if(matches.size()<1)
				   {
					   exceptions.add(this.getClass().getName() + " exception: no matches found for path " + realFilePath);
				   }

			   }






	}


    protected EasyElement getCoverMetaEE()
    {

		EasyElement returnElement = new EasyElement().setName(META);

        if(cover.hasImgID())
        {

		   returnElement.setAttribute(NavPointHandler.CONTENT, cover.getImgID());

	    }

	    returnElement.setAttribute("name", COVER);


		return returnElement;

	}

    protected EasyElement getBibMetaEE(String key)
    {

		//this method and its eponymous chil turn an EasyElement extracted from content
		//into an child of package->metadata in content.opf
		EasyElement returnEE = new EasyElement().setName(key);

		if(bibMap.containsKey(key))
		{
			EasyElement sourceEE = bibMap.get(key);

			//set the text with the recursive method in NavPointHandler
			returnEE.setText(navpointhandler.getText(sourceEE));

			//now check does this EasyElement have attributes that need to be added to element with name key.
			TreeMap<String, String> attributes = sourceEE.getAttributes();


			for(String sourcekey : attributes.keySet())
			{

				String[] pair = sourcekey.split("\\" + ATTRSPLITBY);

				/*
				if(pair.length>1)
				{

					StringBuffer kvBuffer = new StringBuffer("");

					//if this condition is true, there's a attribute that needs to be added to returnEE.
					if(pair[0].equals(key))
					{

						//this is a very cautious loop.  it accommodates an attribute name that contains the delimiter string.
						for(int i=1;i<pair.length;i++)
						{
							if(i>1)
						    {
								kvBuffer.append(ATTRSPLITBY);
							}

							kvBuffer.append(pair[i]);
						}
					}

					//now split the key/value string by the equal sign.
					String[] kvpair = kvBuffer.toString().split(VALUEDELIM);

					int kvlen = kvpair.length;

					if(kvlen>1)
					{
						returnEE.setAttribute(kvpair[0], kvpair[1]);
					}
					else if(kvlen==1)
					{
						returnEE.setAttribute(kvpair[0],"");
					}
					//not sure how this could ever happen.
					else
					{
						exceptions.add(this.getClass().getName() + " exception: zero length attribute pair.");
					}

				}
				*/

			}

		}


		return returnEE;

	}




	protected void extractNonLinear(EasyElement ee, String handle, boolean isCover, String outputText)
	{

				   ArrayList<EasyElement> hrefList = ee.getEasyElementsByAttribute(handle);

				   int counter=0;

				   feedback.add("extractNonLinear(ee.getName()->" + ee.getName() + ", handle->" + handle + ", isCover->" + isCover + ", outputText->" + outputText);


				   for(EasyElement hrefEE : hrefList)
				   {

					   //we know that hrefEE will have an attribute with handle handle
					   //because we retrieved it with the method EasyElement.getEasyElementsByAttribute(String).
					   String value = hrefEE.getAttributes().get(handle);

					   //the same link might appear in more than one content document...
					   //so check to make sure we haven't already created an item for the href
					   //and copied it to the output directory.
					   if(!nonLinearMap.containsKey(handle))
					   {

						   nonLinearMap.put(value, this.linkToItem(hrefEE, outputText));

					   }

					   //if this is the root element for the cover document
					   //and the attribute handle is "src"
					   //and this is the first "src" attribute...
					   if(isCover && handle.equals(SRC) && counter==0)
					   {

						   feedback.add("cover.setImgPath()->" + value);
						   cover.setImgPath(outputText + File.separator + value);

					   }

					   counter++;


				   }


	}


	protected void addToHierarchy(EasyElement easyelement, String parent)
	{

		String name = easyelement.getName();

		feedback.add("addToHierarchy().name->" + name);

		navpointhandler.addRule(parent, name);

		navpointhandler.addCategory(name);

	    ArrayList<EasyElement> children = easyelement.getChildren();

		for(EasyElement child : children)
		{

			this.addToHierarchy(child, name);
		}

	}

	protected String EasyElementToString(EasyElement ee)
	{


		/*this methods extracts a tree out of a xml or xhtml tree

		  for example...

		  <div class="booktitle">

		     <p>
		       The True Believer</br>
		       a reflection on political cults</br>
		       in the 20th Century
		     </p>

		  </div>

		*/
		StringBuffer returnBuffer = new StringBuffer("");

		//if there's already text in the buffer, add a space.
		if(returnBuffer.length()>0)
		{

			returnBuffer.append(" ");
		}

		returnBuffer.append(ee.getText().trim());

		for(EasyElement child : ee.getChildren())
		{

			returnBuffer.append(this.EasyElementToString(child));

		}

		return returnBuffer.toString();

	}


	public String getNavMapString()
	{
		//used for debugging the navMap
		StringBuffer returnBuffer = new StringBuffer("");


		EasyElement navMapEE = navpointhandler.getNavMap();

		returnBuffer.append(this.getDebugStr(navMapEE, 0));


		return returnBuffer.toString();

	}


	protected String getDebugStr(EasyElement ee, int indent)
	{

		StringBuffer returnBuffer = new StringBuffer("");

		int stdIndent = 2;

	returnBuffer.append(this.repeat(indent, " "));

		returnBuffer.append(ee.getName());


		if(ee.getText().length()>0)
		{
			returnBuffer.append("->");

			returnBuffer.append(ee.getText().trim());
		}


		returnBuffer.append("\n");

		TreeMap<String, String> attributes = ee.getAttributes();

		if(attributes.size()>0)
		{

			returnBuffer.append(this.repeat(indent + stdIndent, " "));

			int counter = 0;

			for(String key : attributes.keySet())
			{

				if(counter>0)
				{
					returnBuffer.append(", ");
				}

				returnBuffer.append(key + "->" + attributes.get(key));

				counter++;

			}

	    returnBuffer.append("\n");

	    }



        for(EasyElement child : ee.getChildren())
        {

			returnBuffer.append(this.getDebugStr(child, indent + (stdIndent*2)));
		}


		return returnBuffer.toString();

	}

	protected String repeat(int ii, String out)
	{

		StringBuffer returnBuffer = new StringBuffer("");

		for(int i=0;i<ii;i++)
		{
			returnBuffer.append(out);
		}


		return returnBuffer.toString();

	}


    public NavPointHandler getNavPointHandler()
    {

		return navpointhandler;

	}


	public String getOutputRootParent()
	{

		//getOutputRoot returns the OEBPS directory, but for mimetype, you need the directory just below that one.
		//ergo, this seemingly superfluous method.

		StringBuffer returnBuffer = new StringBuffer("");

		String start = this.getOutputRoot();

		returnBuffer.append(new File(start).getParent());

		return returnBuffer.toString();

	}


	public String getOutputRoot()
	{

		StringBuffer returnBuffer = new StringBuffer("");

		//this method does a lot of null checking for things that should never be null
		//but might be null if the input xml is missing some required elements.

		//what it really returns is the output path up to and including OEBPS

		boolean baseIsAbsolute = false;

		if(outputBase != null)
		{

			File file = new File(outputBase);

			baseIsAbsolute = file.isAbsolute();

		}

		//System.out.println("outputBase->" + outputBase);
		//System.out.println("baseIsAbsolute->" + baseIsAbsolute);

		//if the input xml expresses the output target as absolute, realPath is irrelevant.
		//but if output target is relative, then you need realPath.
		//(realPath is a String representing the absolute path of the directory that input xml file came from.)

		if(realPath != null && !baseIsAbsolute)
		{

			returnBuffer.append(realPath);
		}

		if(outputRoot != null)
		{

			if(returnBuffer.length()>0)
			{

				returnBuffer.append(File.separator);
			}

			returnBuffer.append(outputRoot);
		}


		return returnBuffer.toString();

	}

	protected EasyElement linkToItem(EasyElement ee, String outputText)
	{



	   String relative;

       //path to images and css are expressed in the linear documents as relative to the linear documents' parent directory.
       //so refs to these non-linear items need to include the parent directory of the linear items.

	   if(outputText.length()>0)
	   {

		   relative = outputText + File.separator;
	   }
	   else
	   {

		   relative = "";
	   }



        //this element converts an element with an src or html attribute to an item in the content.opf manifest.
        EasyElement returnEE = new EasyElement().setName(ITEM);

        TreeMap<String, String> attributes = ee.getAttributes();

        if(attributes.containsKey(TYPE))
        {
			returnEE.setAttribute(MEDIATYPE, attributes.get(TYPE));
		}

		if(attributes.containsKey(HREF))
		{
			returnEE.setAttribute(HREF, relative + attributes.get(HREF));
		}

		if(attributes.containsKey(XLINKHREF))
		{
			returnEE.setAttribute(HREF, relative + attributes.get(HREF));
		}

		//<image src="[path_to_image]" in content file becomes <item href="[path_to_image]"/> in content.opf>
		if(attributes.containsKey(SRC))
		{

			String src = attributes.get(SRC);

			returnEE.setAttribute(HREF, relative + src);


			returnEE.setAttribute(MEDIATYPE, mediatypehandler.getMediaType(src));
			//returnEE.setAttribute(MEDIATYPE, "typetest");
		}



        return returnEE;

	}

	protected EasyElement getNcx()
	{


		EasyElement returnEE;

		if(ncxEE != null)
		{
			returnEE = ncxEE;
		}
		else
		{

			returnEE = new EasyElement().setName(NCX);

		}

		return returnEE;

	}

	protected EasyElement getPackage()
	{


		EasyElement returnEE;

		if(packageEE != null)
		{
			returnEE = packageEE;
		}
		else
		{

			returnEE = new EasyElement().setName(NavPointHandler.PACKAGE);

		}

		return returnEE;

	}

	protected String getOpfName(boolean includeSeparator)
	{

		StringBuffer returnBuffer = new StringBuffer("");

		if(includeSeparator)
		{

			returnBuffer.append(File.separator);
		}

		returnBuffer.append(OPFNAME);

		return returnBuffer.toString();
	}

    public EasyElement getContainerEE()
    {

		EasyElement returnEE;

		if(containerEE != null)
		{

			returnEE = containerEE;
		}
		else
		{
			returnEE = new EasyElement().setName(CONTAINER);
		}

		return returnEE;

	}

	public String getContainerPath()
	{
		StringBuffer returnBuffer = new StringBuffer("");

		returnBuffer.append(realPath);

		returnBuffer.append(File.separator);

		if(metaRoot != null)
		{

		  returnBuffer.append(metaRoot);

		  returnBuffer.append(File.separator);

	    }

		returnBuffer.append(CONTAINERNAME);

		return returnBuffer.toString();
	}

	protected String getTocName(boolean includeSeparator)
	{

		StringBuffer returnBuffer = new StringBuffer("");

		if(includeSeparator)
		{

			returnBuffer.append(File.separator);
		}

		returnBuffer.append(TOC);

		returnBuffer.append(".");

		returnBuffer.append(NCX);

		return returnBuffer.toString();

	}

	public ArrayList<String> getFeedback()
	{

		return feedback;
	}

	public String getOutputBase()
	{
		StringBuffer returnBuffer = new StringBuffer("");

		returnBuffer.append(realPath);

		returnBuffer.append(File.separator);


		if(outputBase != null)
		{
			returnBuffer.append(outputBase);

		}



		return returnBuffer.toString();
	}

	public String getZipBase()
	{
		StringBuffer returnBuffer = new StringBuffer("");

		boolean pathIsAbsolute = new File(zipBase).isAbsolute();

		if(!pathIsAbsolute)
		{

		   returnBuffer.append(realPath);
		   returnBuffer.append(File.separator);

	    }

			returnBuffer.append(zipBase);

			returnBuffer.append(File.separator);

		if(epubName != null)
	    {
			returnBuffer.append(epubName);
		}
		else
		{
			returnBuffer.append("output.epub");
		}

		return returnBuffer.toString();
	}

	public String getOverwrite()
	{
		StringBuffer returnBuffer = new StringBuffer("");

		if(overwrite!=null)
		{
			returnBuffer.append(overwrite);
		}

		return returnBuffer.toString();
	}

	public ArrayList<String> getExceptions()
	{
		return exceptions;
	}

	public boolean getClear()
	{

		return clear;
	}

	public void setClear(boolean aclear)
	{
		//clear indicates if output path should be cleared first before new data written.
		//default is false.
		clear = aclear;
	}



protected TreeMap<String, EasyElement> nonLinearMap = new TreeMap<String, EasyElement>();

protected ArrayList<EasyElement> linearList = new ArrayList<EasyElement>();

public static final String MANIFEST = "manifest";

public static final String SPINE = "spine";

public static final String HREF = "href";

public static final String XLINKHREF = "xlink:href";

public static final String SRC = "src";

public static final String TYPE = "type";

public static final String MEDIATYPE = "media-type";

public static final String ITEM = "item";

public static final String ITEMREF = "itemref";

public static final String OUTPUT = "output";

public static final String TEXT = "text";

public static final String ROOT = "root";

public static final String TOC = "toc";

public static final String NCX = "ncx";

public static final String OEBPS = "oebps";

public static final String IDREF = "idref";

public static final String DOCTITLE = "docTitle";

public static final String LINEAR = "linear";

public static final String LINEARYES = "yes";

public static final String LINEARNO = "no";

public static final String COVER = "cover";

public static final String DEFAULTMTYPE = "application/x-dtbncx+xml";

public static final String HEAD = "head";

public static final String META = "meta";

public static final String METADATA = "metadata";

public static final String METAINF = "meta-inf";

public static final String OPFNAME = "content.opf";

public static final String GUIDE = "guide";

public static final String REFERENCE = "reference";

public static final String TITLE = "title";

public static final String TITLETAG = "title";

public static final String CONTAINER = "container";

public static final String ROOTFILES = "rootfiles";

public static final String ROOTFILE = "rootfile";

public static final String FULLPATH = "full-path";

public static final String CONTAINERNAME = "container.xml";

public static final String STATIC = "static";

public static final String MIMETYPE = "mimetype";

public static final String NAME = "name";

//directory where the unzipped contents go...
public static final String DOCSDIR = "docs";

//directory where the zipped contents go...
public static final String EPUBDIR = "epub";

//name of element in input file that indicates if the user wants a recursive delete of all old content in output destination.
public static final String DELETE = "delete";

public static final String DELETEYES = "yes";

public static final String HTMLROOT = "html";

protected String oebpsText=null;

protected String metainfText=null;

protected String staticRoot = null;

protected String outputRoot = null;

protected EasyElement ncxEE = null;

protected EasyElement packageEE = null;

protected Cover cover = null;

protected ArrayList<String> exceptions = new ArrayList<String>();

protected ArrayList<String> feedback = new ArrayList<String>();

protected String path = null;

protected String realPath = null;

protected NavPointHandler navpointhandler = null;

protected EasyElement guideEE = new EasyElement().setName(GUIDE);

protected MediaTypeHandler mediatypehandler = new MediaTypeHandler();

protected EasyElement containerEE=null;

protected String metaRoot=null;

protected String outputBase=null;

protected String zipBase=null;

protected String epubName=null;

protected String overwrite=null;

protected boolean clear = CLEARNO;

//map of the EasyElements extracted from contentfile that are parsed and written to content.opf's metadata element.
protected TreeMap<String, EasyElement> bibMap = new TreeMap<String, EasyElement>();

public static final boolean INCLUDE = true;
public static final boolean INCLUDEYES = true;
public static final boolean INCLUDENO = false;

public static final boolean CLEARYES = true;
public static final boolean CLEARNO = false;

public static final String VALUEDELIM = "=";

//for splitting BIB sub values, e.g., title="bib=dc:title bib.fu=bar"
public static final String ATTRSPLITBY = ".";



}