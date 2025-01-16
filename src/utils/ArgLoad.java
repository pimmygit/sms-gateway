/******************************************************************
 *
 * SMS Gateway
 * 
 * (C) Copyright Pimmy (Kliment Stefanov). 2014  
 * kliment@hotmail.co.uk
 * All Rights Reserved
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 * RESTRICTED RIGHTS:
 *
 * This file may have been supplied under a license.
 * It may be used, disclosed, and/or copied only as permitted
 * under such license agreement. Any copy must contain the
 * above copyright notice and this restricted rights notice.
 * Use, copying, and/or disclosure of the file is strictly
 * prohibited unless otherwise provided in the license agreement.
 *
 ******************************************************************/
package utils;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Pimmy
 * Date: Nov 5, 2003
 * Time: 10:01:28 AM
 * To change this template use Options | File Templates.
 */
public class ArgLoad
{
	public static String[] loadArgumentList(String fileName)
	{
		Vector<String> tmp=new Vector<String>();
		Properties p=loadArguments(fileName);
		if(p!=null)
		{
			Enumeration<?> names=p.propertyNames();
			while(names.hasMoreElements())
			{
				String name=(String)names.nextElement();
				String val=p.getProperty(name);
				//System.out.println(name+":"+val);
				if(val!=null && val.length()!=0)
				{
					tmp.addElement("-"+name);
					tmp.addElement(val);
				}
			}
			String[] s=new String[tmp.size()];
			for(int i=0;i<tmp.size();i++)
			{
				s[i]=(String)tmp.elementAt(i);
			}
			return s;
		}
		else
		{
			//System.out.println("NULL");
		}
		return new String[0];
	}

	private static Properties loadArguments(String fileName)
	{
		Properties p=null;
		try
		{
			p=new Properties();
			FileInputStream fStream = new FileInputStream(fileName);
			p.load(fStream);
			fStream.close();
		}
		catch(Exception e)
		{
			return null;
		}
		return p;
	}
}
