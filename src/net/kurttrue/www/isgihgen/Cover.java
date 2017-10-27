package net.kurttrue.www.isgihgen;

	/**
	* <h1>Cover: represents the epub's cover.</h1>
    * This class represents the cover of the epub, which is typically an xhtml that contains an img tag referring to a jpeg or png.
    *
    * @author Kurt True
    * @version 1.0
    * @since 2017-09-25
	*/


public class Cover
{

	public Cover()
	{

	}

	public Cover(String apath)
	{

		path = apath;

	}

	public void setPath(String apath)
	{
		path = apath;
	}

	public void setImgPath(String aimgpath)
	{
		imgpath = aimgpath;
	}

	public void setID(String aid)
	{

		id = aid;
	}

	public void setImgID(String aimgid)
	{
		imgid = aimgid;
	}


    public boolean hasImgID()
    {

		return imgid!=null;

	}


	public boolean hasPath()
	{
		return path!=null;
	}

	public boolean hasImgPath()
	{
		return imgpath!=null;

	}

	public boolean hasID()
	{
		return id!=null;
	}


	public String getPath()
	{

		return path;
	}

	public String getImgPath()
	{
		return imgpath;
	}

	public String getID()
	{

		String returnStr;

		if(id!=null)
		{
			returnStr = id;
		}
		else
		{

			returnStr = DEFAULTCOVERID;
		}

		return returnStr;
	}

	public String getImgID()
	{

		return imgid;
	}




public static final String DEFAULTCOVERID = "itemcover";

protected String path = null;

protected String imgpath = null;

protected String imgid = null;

protected String id = null;

}