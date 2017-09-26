package net.kurttrue.www.epubGenerator;

import java.util.ArrayList;

import java.util.regex.*;

import java.io.File;


public class localIOTester
{
	  public static void main(String[] args)
      {

		  System.out.println("IOHandler tester.");

		  if(args.length==0)
		  {

			System.out.println("supply path as arg 0");
		  }
		  else
		  {

			  String apath = args[0];

			  System.out.println("args[0]->" + apath);

			  String[] pair = apath.split("=");

			  String path = pair[1];

			  System.out.println("path->" + path);

			  File file = new File(path);

			  if(file.exists())
			  {
				  System.out.println("path " + path + " exists.");

			  }
			  else
			  {
				  System.out.println("path " + path + " does NOT exist.");

				  if(file.isAbsolute())
				  {

					  System.out.println(path + " is absolute.");
				  }
				  else
				  {

					  System.out.println(path + " is NOT absolute");
				  }

				  File parent = file.getParentFile();

				  System.out.println("parent.getPath()->" + parent.getPath());

				  System.out.println("file.getName()->" + file.getName());

                  String pattern = file.getName().replace(".", "\\.").replace("*", ".*");

                  //String pattern = "([^\\s]+(\\.(?i)(txt|doc|csv|pdf|xhtml))$)";

                  System.out.println("pattern->" + pattern);

				  Pattern p = Pattern.compile(pattern);



				  if(parent.isDirectory())
				  {

					  System.out.println(parent.getPath() + " is a directory.");

					  File[] flist = parent.listFiles();

					  for(File child : flist)
					  {
						  Matcher m = p.matcher(child.getName());

						  if(m.matches())
						  {

							  System.out.println("MATCH on " + child.getName());
						  }
						  else
						  {
							  System.out.println("no match on " + child.getName());
						  }

						  System.out.println(child.getName());
					  }
			      }
			      else
			      {

					  System.out.println(parent.getPath() + " is NOT a directory.");
				  }

		      }

			  //IOHandler handler = new IOHandler();

			  //handler.delete(deletePath);

			  System.out.println("calling PathMatcher");

			  ArrayList<String> matches = PathMatcher.getMatches(path);

			  System.out.println("matches.size()->" + matches.size());

			  for(String match : matches)
			  {

				  System.out.println("match->" + match);
			  }



		  }


      }


}