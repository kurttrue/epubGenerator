package net.kurttrue.www.epubGenerator;

//MediaTypeHandler is a low rent means of

import java.io.File;
import java.util.HashMap;

public class MediaTypeHandler
{

	public MediaTypeHandler()
	{

		this.inflate();
	}

	protected void inflate()
	{


		//standardMap is a way to match variations on an extension to the second component of the media-type attribute.
		//if the extenstion in path does not match a key in standardMap, the second component of the media-type attribute is the extension.
		//e.g. media-type="image/gif"
        standardMap.put(JPG, JPEG);
        standardMap.put(OPF, OPFTYPE);
        standardMap.put(HTM, HTMLTYPE);
        standardMap.put(HTML, HTMLTYPE);
        standardMap.put(XHTML, HTMLTYPE);

        genusMap.put(GIF, IMAGE);
        genusMap.put(JPEG, IMAGE);
        genusMap.put(JPG, IMAGE);
        genusMap.put(PNG, IMAGE);
        genusMap.put(SVGXML, IMAGE);
        genusMap.put(CSS, TEXT);
        genusMap.put(OPFTYPE, APPLICATION);
        genusMap.put(HTMLTYPE, APPLICATION);


	}

	public String getMediaType(String path)
	{


		StringBuffer returnBuffer = new StringBuffer("");

		String name = new File(path).getName();


		String genus;

		String[] components = name.split("\\.");

		String ext = components[components.length-1];

		String standardized;

		//if the extension doesn't have an entry in standardMap, assume the epub standard already knows about this extension.
		if(standardMap.containsKey(ext))
		{

			standardized = standardMap.get(ext);
		}
		else
		{
			standardized = ext;
		}

		if(genusMap.containsKey(standardized))
		{

			genus = genusMap.get(standardized);
		}
		else
		{
			//if the genus is unknown, assume it's an image.
			genus = IMAGE;
		}


		returnBuffer.append(genus);

		returnBuffer.append(File.separator);

		returnBuffer.append(standardized);

		return returnBuffer.toString();

	}


protected HashMap<String, String> standardMap = new HashMap<String, String>();
protected HashMap<String, String> genusMap = new HashMap<String, String>();

public static final String GIF = "gif";
public static final String JPEG = "jpeg";
public static final String JPG = "jpg";
public static final String PNG = "png";
public static final String SVGXML = "svg+xml";
public static final String CSS = "css";
public static final String OPFTYPE = "oebps-package+xml";
public static final String OPF = "opf";
public static final String HTM = "htm";
public static final String HTML = "html";
public static final String XHTML = "xhtml";

public static final String HTMLTYPE = "xhtml+xml";

public static final String IMAGE = "image";
public static final String APPLICATION = "application";
public static final String TEXT = "text";
public static final String AUDIO = "audio";


}