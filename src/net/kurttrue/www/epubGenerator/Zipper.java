package net.kurttrue.www.epubGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class Zipper
{

	public static void main(String[] args)
	{

		System.out.println("epubGenerator Zipper");

		String duty=null;

		HashMap<String, String> argMap = new HashMap<String, String>();

		for(int i=0;i<args.length;i++)
		{
			String[] pair = args[i].split("=");

			argMap.put(pair[0], pair[pair.length-1]);
		}

		for(String key : argMap.keySet())
		{
			System.out.println(key + "->" + argMap.get(key));
		}

		if(argMap.containsKey(DUTY))
		{
		    duty=argMap.get(DUTY);
		}
		else
		{

			System.out.println("Supply " + DUTY + " this way: " + DUTY + "=" + DUTY);
		}

		if(duty!=null)
		{

			if(duty.equals(DUTYZIP))
			{
				if(argMap.containsKey(OUTPUT) && argMap.containsKey(INPUT))
				{

					ZipHandler ziphandler = new ZipHandler().setZipInputPath(argMap.get(INPUT)).setZipOutputPath(argMap.get(OUTPUT));

					ziphandler.zip();

				}
				else
				{
					System.out.println("Supply args " + INPUT + " and " + OUTPUT);
				}

			}

		}


	}

public static final String DUTY = "duty";
public static final String INPUT = "input";
public static final String OUTPUT = "output";

public static final String DUTYZIP = "zip";



}