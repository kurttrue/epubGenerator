package net.kurttrue.www.isgihgen;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.File;

public class PathMatcher
{


	public static ArrayList<String> getMatches(String path)
	{

		ArrayList<String> returnList = new ArrayList<String>();

		//calling routine should have determined before calling this method that File(path).exists()=false.
		//it's either a pattern with a wildcard, or an input error in the mother input file.
	    File file = new File(path);

	    File parent = file.getParentFile();

		//make the wildcard meaningful to java.util.regex.Matcher, and escape any dots.
		String pattern = file.getName().replace(".", "\\.").replace("*", ".*");

	    Pattern p = Pattern.compile(pattern);

	    if(parent.isDirectory())
	    {

  		    File[] flist = parent.listFiles();

			for(File child : flist)
			{

				Matcher m = p.matcher(child.getName());

				if(m.matches())
				{

					returnList.add(parent.getPath() + File.separator + child.getName());
				}


			}


		}

		return returnList;

	}


}