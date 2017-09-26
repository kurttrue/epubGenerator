package net.kurttrue.www.epubGenerator;

import java.util.ArrayList;

public class EpubFactory
{

	public EpubFactory()
	{


	}


	public EpubFactory(InputHandler ainputhandler)
	{
		inputhandler = ainputhandler;

	}

    public EpubFactory setInputHandler(InputHandler ainputhandler)
    {

		inputhandler = ainputhandler;

		return this;
	}

	public void proceed()
	{
		if(inputhandler==null)
		{
			exceptions.add(this.getClass().getName() + "exception: InputHandler inputhandler is null.");
		}
		else
		{

			if(inputhandler.getClear())
			{
			     feedback.add("Deleting previous output.");
			}
			else
			{
				 feedback.add("Not deleting previous output.");
			}

			String ncxPath = inputhandler.getOutputRoot() + inputhandler.getTocName(InputHandler.INCLUDE);

			String packagePath = inputhandler.getOutputRoot() + inputhandler.getOpfName(InputHandler.INCLUDE);

			EasyElement ncxEE = inputhandler.getNcx();

			EasyElement packageEE = inputhandler.getPackage();

			DocHandler ncxDocHandler = new DocHandler(ncxEE).setOutputPath(ncxPath).setMethod(DocHandler.XML);

			ncxDocHandler.makeDocument();

			ncxDocHandler.transform();

			DocHandler opfDocHandler = new DocHandler(packageEE).setOutputPath(packagePath).setMethod(DocHandler.XML);

			opfDocHandler.makeDocument();

			opfDocHandler.transform();

			DocHandler containerDocHandler = new DocHandler(inputhandler.getContainerEE()).setOutputPath(inputhandler.getContainerPath()).setMethod(DocHandler.XML);

            containerDocHandler.makeDocument();

			containerDocHandler.transform();

		    ZipHandler ziphandler = new ZipHandler().setZipInputPath(inputhandler.getOutputBase()).setZipOutputPath(inputhandler.getZipBase());

		    ziphandler.zip();

		    feedback.addAll(inputhandler.getFeedback());


		    exceptions.addAll(inputhandler.getExceptions());

		    exceptions.addAll(ziphandler.getExceptions());


		}

	}

	public ArrayList<String> getFeedback()
	{

		return feedback;
	}

	public ArrayList<String> getExceptions()
	{

		return exceptions;
	}


private InputHandler inputhandler=null;

private ArrayList<String> feedback = new ArrayList<String>();

private ArrayList<String> exceptions = new ArrayList<String>();



}