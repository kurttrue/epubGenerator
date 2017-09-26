package net.kurttrue.www.epubGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class IOHandler
{

	public IOHandler()
	{


	}

	public IOHandler setOutputPath(String aoutputPath)
	{

		outputPath = aoutputPath;

		return this;

	}



	public IOHandler setInputPath(String ainputPath)
	{

		inputPath = ainputPath;

		return this;

	}


	public void clear()
	{

		if(outputPath!=null)
		{

			this.delete(outputPath);
		}
		else
		{

			exceptions.add(this.getClass().getName() + ".clear() exception: outputPath is null.");
		}

	}


	//deletes a directory recursively
	protected void delete(String path)
	{
		File file = new File(path);

		if(file.exists())
		{

			try
			{
				if(file.isDirectory())
				{

						String files[] = file.list();

						for(String temp : files)
						{

							File tempFile = new File(file, temp);

							this.delete(tempFile.getAbsolutePath());
							feedback.add("delete child " + tempFile.getAbsolutePath());
						}

						if(file.list().length==0)
						{

							file.delete();
							feedback.add("delete dir " + file.getAbsolutePath());
						}
						else
						{

							exceptions.add(this.getClass().getName() + " exception: cannot delete " + path + ".  Not empty.");
						}


				 }
				 else
				 {
					File deleteFile = new File(path);

					deleteFile.delete();
					feedback.add("delete file" + deleteFile.getAbsolutePath());

				 }

		    }
		    catch(Exception e)
		    {
				exceptions.add(this.getClass().getName() + " io exception: " + e.toString());
			}


		}
		else
		{
			exceptions.add(this.getClass().getName() + " exception. File not found: " + path);
		}

	}

	public void copy(String fileName)
	{


		String targetString = outputPath + File.separator + fileName;

		String inputString = inputPath + File.separator + fileName;

		File outputTarget = new File(targetString);

		File outputTargetPath = outputTarget.getParentFile();

		Path source = Paths.get(inputString);
		Path destination = Paths.get(targetString);

		if(!outputTargetPath.exists())
		{

			outputTargetPath.mkdirs();

		}

		try
		{
	    	Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

	    }
	    catch(Exception e)
	    {

			exceptions.add(MYNAME + " Files.copy() exception " + e.toString());
		}


	}


    public ArrayList<String> getExceptions()
    {

		return exceptions;

	}

	public ArrayList<String> getFeedback()
	{

		return feedback;
	}

protected ArrayList<String> exceptions = new ArrayList<String>();

protected ArrayList<String> feedback = new ArrayList<String>();

protected String MYNAME = this.getClass().getName();

protected String outputPath;

protected String inputPath;

}