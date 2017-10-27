package net.kurttrue.www.isgihgen;

import java.io.Console;
import java.util.HashMap;
import java.util.ArrayList;

	/**
	* <h1>Generator: entry point</h1>
	* Generator is the entry point to the app.
	* It parses the user's request (input=[path_to_input]) and creates an InputHandler and an EpubFactory, which are the classes that do the parsing and output building.
	* If input file requests an overwrite, Generator will prompt the user for a confirmation.
    *
    * @author Kurt True
    * @version 1.0
    * @since 2017-09-25
	*/


public class Generator
{



	  public static void main(String[] args)
	  {

		  System.out.println("Generator");

		  HashMap<String, String> argMap = new HashMap<String, String>();

		  Console c = System.console();

		  ArrayList<String> feedback = new ArrayList<String>();

		  if(c==null)
		  {

			  System.out.println("no console");
		  }
		  else
		  {



				  for(int i=0;i<args.length;i++)
				  {
					  System.out.println("arg" + i + "->" + args[i]);

					  String[] pair = args[i].split("=");

					  if(pair.length==1)
					  {
						  argMap.put(pair[0],"");
					  }
					  else if(pair.length>1)
					  {
						  argMap.put(pair[0],pair[1]);
					  }

				  }

				  if(argMap.containsKey(INPUT))
				  {

				     String inputfile = argMap.get(INPUT);

				     feedback.add("inputfile->" + inputfile);

				     InputHandler inputhandler = new InputHandler().setInputPath(inputfile);

				     inputhandler.parse();

				     String overwrite = inputhandler.getOverwrite();

				     boolean deleteOutputDir = false;

				     if(overwrite.toUpperCase().equals(InputHandler.DELETEYES.toUpperCase()))
				     {

			            String confirmDelete = c.readLine("Delete output directory at " + inputhandler.getOutputRoot() +  " recursively? (yes/no) ");

			            String confirmToUpper = confirmDelete.toUpperCase();

					    if(confirmToUpper.equals("Y") || confirmToUpper.equals(InputHandler.DELETEYES))
					    {
							deleteOutputDir = true;
						}


				     }
				     else
				     {
						 deleteOutputDir = true;
					 }

					 inputhandler.setClear(deleteOutputDir);

					 EpubFactory epubfactory  = new EpubFactory().setInputHandler(inputhandler);

				     epubfactory.proceed();

				     feedback.addAll(epubfactory.getFeedback());



			      }


			  }

			  for(String fline : feedback)
			  {

				  System.out.println(fline);
			  }

	  }


public static final String INPUT = "input";

}