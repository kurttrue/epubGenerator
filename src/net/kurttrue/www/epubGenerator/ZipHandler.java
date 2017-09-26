package net.kurttrue.www.epubGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.util.ArrayList;

public class ZipHandler
{


	public ZipHandler()
	{

	}

	public ZipHandler setZipInputPath(String azipInputPath)
	{
		zipInputPath = azipInputPath;

		return this;

	}

	public ZipHandler setZipOutputPath(String azipOutputPath)
	{

		zipOutputPath = azipOutputPath;

		return this;
	}

	public void addFileName(String filename)
	{
		filenames.add(filename);

	}

	public void zip()
	{

		try
		{
			File zdir = new File(new File(zipOutputPath).getParent());

			if(!zdir.exists())
			{
				zdir.mkdirs();
			}

			fos = new FileOutputStream(zipOutputPath);
			zos = new ZipOutputStream(fos);

			this.addSourceDirectory(zipInputPath);

    		//close the ZipOutputStream.
    		zos.close();




	     }
	     catch(Exception e)
	     {
			 exceptions.add("ZipHandler.zip() exception: " + e.toString());
			 //System.out.println("ZipHandler.zip() exception: " + e.toString());
		 }

	}

	protected void addSourceDirectory(String entry)
	{
		try
		{

			File inputDir = new File(entry);

			ArrayList<String> directories = new ArrayList<String>();

            String[] filearray = inputDir.list();

 			//the two for loops below ensure that file mimetype is the first entry in the epub file.
            //(which is what the epub standard demands).
            for(int i=0; i<filearray.length; i++)
            {
				//System.out.println("file: " + filearray[i]);

				File file = new File(entry + "/" + filearray[i]);

				if(file.isDirectory())
				{
					//System.out.println(filearray[i] + " is a directory.");

					//this is where you get recursive.
					//this.addSourceDirectory(entry + File.separator + filearray[i]);
					directories.add(filearray[i]);

				}
				else
				{
					//System.out.println(filearray[i] + " is a file.");

					this.addSourceFile(entry + File.separator + filearray[i]);
				}

			}

			//don't add directories until you've added all the files.
			for(String directory : directories)
			{
				//this is where you get recursive
				this.addSourceDirectory(entry + File.separator + directory);
			}

	    }
	    catch(Exception e)
	    {
			//System.out.println("ZipHandler.addSourceDirectory() exception: " + e.toString());

			exceptions.add("ZipHandler.addSourceDirectory() exception: " + e.toString());
		}

	}

	protected void addSourceFile(String entry)
	{

			try
			{
				byte[] buffer = new byte[1024];

                String entryName = entry.substring(zipInputPath.length() + 1)
                    .replace('\\', '/');

				//System.out.println("entry->" + entryName);

				ZipEntry ze = new ZipEntry(entryName);
				zos.putNextEntry(ze);
    		    FileInputStream in = new FileInputStream(entry);

				int len;
				while ((len = in.read(buffer)) > 0)
				{
					zos.write(buffer, 0, len);
				}

				in.close();
				zos.closeEntry();
			}
			catch(Exception e)
			{
				exceptions.add("ZipHandler exception: " + e.toString());
				//System.out.println("ZipHandler.addSourceFile() exception: " + e.toString());
			}

	}


	public void zipFile()
	{
     //this method only zips a single directory.  it doesn't zip recursively.
		try
		{


    		File zdir = new File(new File(zipOutputPath).getParent());

    		if(!zdir.exists())
    		{
				zdir.mkdirs();
			}

	  		fos = new FileOutputStream(zipOutputPath);
    		zos = new ZipOutputStream(fos);

            File inputDir = new File(zipInputPath);

            String[] filearray = inputDir.list();

            for(int i=0; i<filearray.length; i++)
            {
				//System.out.println("file: " + filearray[i]);
				filenames.add(filearray[i]);
			}



    		for(String entry : filenames)
    		{

				byte[] buffer = new byte[1024];

				ZipEntry ze = new ZipEntry(entry);
				zos.putNextEntry(ze);
    		    FileInputStream in = new FileInputStream(zipInputPath + "/" + entry);

				int len;
				while ((len = in.read(buffer)) > 0)
				{
					zos.write(buffer, 0, len);
				}

				in.close();
				zos.closeEntry();

			}

    		//close the ZipOutputStream.
    		zos.close();



		}
		catch(IOException e)
		{

			//System.out.println("zip exception: " + e.toString());
			exceptions.add("Zip exception: " + e.toString());
		}

	}

	public ArrayList<String> getExceptions()
	{

		return exceptions;
	}

protected FileOutputStream fos;
protected ZipOutputStream zos;

protected String zipInputPath=null;
protected String zipOutputPath=null;
protected ArrayList<String> filenames = new ArrayList<String>();
protected ArrayList<String> exceptions = new ArrayList<String>();

}