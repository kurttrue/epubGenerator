package net.kurttrue.www.isgihgen;

import java.util.ArrayList;
import java.io.File;

public class localInputHandlerTester
{
	  public static void main(String[] args)
      {

		  System.out.println("localInputHandlerTester");

		  if(args.length>0)
		  {

			  String path=args[0];

			  System.out.println("path->" + path);

			  InputHandler handler = new InputHandler().setInputPath(path);

			  handler.parse();

			  String outputRootParent = handler.getOutputRootParent();

			  String outputRoot = handler.getOutputRoot();

			  String realPath = handler.getRealPath(path);

			  String zipBase = handler.getZipBase();

			  String userrel = "~/epub/owlandpussycat/OEBPS";

			  File file = new File(userrel);

			  String fixed = userrel.replaceFirst("^~", System.getProperty("user.home"));

			  String abs = file.getAbsolutePath();

			  System.out.println("fixed->" + fixed + "\n\n");

			  System.out.println("abs->" + abs);

			  System.out.println("home->" + System.getProperty("user.home"));

			  System.out.println("you->" + System.getProperty("user.name"));

			  System.out.println("outputRootParent->" + outputRootParent);

			  System.out.println("outputRoot->" + outputRoot);

			  System.out.println("realPath->" + realPath);

			  System.out.println("zipBase->" + zipBase);

		  }
		  else
		  {

			  System.out.println("supply input path as arg 0.");
		  }

      }


}