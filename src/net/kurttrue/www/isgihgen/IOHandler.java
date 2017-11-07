package net.kurttrue.www.isgihgen;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/***

    * <h1>IOHandler: copies content files to output location and optionally removes old content.</h1>
    *
    * @author Kurt True
    * @version 1.01
    * @since 2017-11-07

*/

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

	public IOHandler setInputFile(File ainputFile)
	{

		inputFile = ainputFile;

		return this;
	}


    /***
    Removes previous content.  If this method isn't called, IOHandler will overwrite files with the same name but will not delete anything.  Therefore, if the calling routine does not call clear(), a file that has been removed from the input directory will not be removed from the output directory.
    */

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

	/***

	Copy file at inputPath/fileName to outputPath/fileName.  Overwrite if output/fileName exists.

	*/

	public void write(String content, String fileName)
	{

		BufferedWriter bw = null;
		FileWriter fw = null;

		try
		{
			String target = outputPath + File.separator + fileName;

			fw = new FileWriter(target);
			bw = new BufferedWriter(fw);
			bw.write(content);


		}
		catch(IOException e)
		{
			exceptions.add(this.getClass().getName() + " exception"  + e.toString());
		}

        finally
        {

			try
			{

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			}
			catch (IOException ex)
			{

			  exceptions.add(this.getClass().getName() + " exception: " + ex.toString());

			}

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

protected File inputFile;

}
