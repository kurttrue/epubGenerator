package net.kurttrue.www.epubGenerator;

import java.util.ArrayList;

public class localNavPointTester
{
	  public static void main(String[] args)
      {


        System.out.println("localNavPointTester");

        if(args.length>0)
        {
			String inputpath = args[0];

			System.out.println("inputpath->" + inputpath);

			/*
			EasyElement easyelement = new EasyElement().buildFromPath(inputpath);

			System.out.println("easyelement.getName()->" + easyelement.getName());
			*/

			InputHandler inputhandler = new InputHandler().setInputPath(inputpath);

			inputhandler.parse();

			String ncxPath = inputhandler.getOutputRoot() + inputhandler.getTocName(InputHandler.INCLUDE);

			String packagePath = inputhandler.getOutputRoot() + inputhandler.getOpfName(InputHandler.INCLUDE);

			EasyElement ncxEE = inputhandler.getNcx();

			EasyElement packageEE = inputhandler.getPackage();

			System.out.println("ncxPath->" + ncxPath);

			DocHandler ncxDocHandler = new DocHandler(ncxEE).setOutputPath(ncxPath).setMethod(DocHandler.XML);

			ncxDocHandler.makeDocument();

			System.out.println("made ncx document.");

			ncxDocHandler.transform();

			DocHandler opfDocHandler = new DocHandler(packageEE).setOutputPath(packagePath).setMethod(DocHandler.XML);

			opfDocHandler.makeDocument();

			System.out.println("made opf document.");

			opfDocHandler.transform();

			//System.out.println("inputhandler.getContainerPath()->" + inputhandler.getContainerPath());

			DocHandler containerDocHandler = new DocHandler(inputhandler.getContainerEE()).setOutputPath(inputhandler.getContainerPath()).setMethod(DocHandler.XML);

            containerDocHandler.makeDocument();

			containerDocHandler.transform();

			ArrayList<String> exceptions = opfDocHandler.getExceptions();

			if(exceptions.size()>0)
			{

				System.out.println("exceptions...");

				for(String exception : exceptions)
				{
					System.out.println(exception);
				}

				System.out.println("... end of exceptions.");

		    }
		    else
		    {
				System.out.println("no exceptions.");
			}


			System.out.println("inputhandler.getFeedback()...");

			for(String f : inputhandler.getFeedback())
			{

				System.out.println(f);
			}

			System.out.println("zip input->" + inputhandler.getOutputBase());

			System.out.println("zip output->" + inputhandler.getZipBase());

		    ZipHandler ziphandler = new ZipHandler().setZipInputPath(inputhandler.getOutputBase()).setZipOutputPath(inputhandler.getZipBase());

		    ziphandler.zip();


            //String currentDir = System.getProperty("user.dir");

            //String currentDir = inputhandler.getRealPath();

            //System.out.println("currentDir->" + currentDir);

			//DocHandler ncxhandler = new DocHandler();

			/*
			System.out.println("navMap debug String...");

			System.out.println(inputhandler.getNavMapString());
			*/

		}
		else
		{

			System.out.println("supply input path as arg 0.");
		}


      }


}