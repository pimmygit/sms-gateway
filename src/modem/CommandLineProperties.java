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
package modem;

import log.Logger;

import java.util.Properties;
import java.util.Hashtable;
import java.io.FileInputStream;

import utils.WProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Pimmy
 * Date: Nov 5, 2003
 * Time: 9:08:38 AM
 * To change this template use Options | File Templates.
 */

public class CommandLineProperties extends Hashtable<Object, Object>
{
    private static final long serialVersionUID = 4673098942194854586L;

    private static final String CLASS = CommandLineProperties.class.getSimpleName();
    
	public static final int INTEGER=0;
	public static final int STRING=1;


	public void defineProperty(String name,int type)
	{
		defineProperty(name,type,null);
	}

	public void defineProperty(String name,int type,String def)
	{
		CLProperty prop=new CLProperty(name,type,def);
		put(name,prop);
	}

	public void setProperty(String name,String value)
	{
		CLProperty prop=(CLProperty)get(name);
		if(prop==null)
		{
		    Logger.write(Logger.MINOR, CLASS, "Parse error because property " + name + " unknown: ignored");
		}
		else
		{
			prop.setValue(value);
		}
	}

	public String getProperty(String name)
	{
		return ((CLProperty)get(name)).getValue();
	}

	public void processArguments(String[] args)
	{
		for(int i=0;i<args.length;i++)
		{
			String arg=args[i];
			if(arg.charAt(0)!='-')
			{
			    Logger.write(Logger.MINOR, CLASS, "Bad format: Ignoring.." + arg);
			}
			else
			{
				arg=arg.substring(1);

				/* if one of parameters is version then print the version and quit */
				if(arg.equals("version"))
				{
					Properties p=new WProperties();
					try
					{
						FileInputStream fStream = new FileInputStream("version");
						p.load(fStream);
						fStream.close();
					}
					catch(Exception e)
					{
					    Logger.write(Logger.CRITICAL, CLASS, "Version information not available");
						System.exit(-1);
					}
					String ver=p.getProperty("version");
					if(ver!=null)
					{
					    Logger.write(Logger.INFO, CLASS, "Version: " +ver);
					}
					else
					{
					    Logger.write(Logger.CRITICAL, CLASS, "Version information not available");
					}
					System.exit(-1);
				}
				else
				{
					i++;
					System.out.println("Setting:"+arg+" "+args[i]);
					setProperty(arg,args[i]);
				}
			}
		}
	}

	/* Command line property. */
	private class CLProperty
    {
		String _name;
		String _default_value;
		String _value;
		int _type;
		boolean _is_set;

		CLProperty(String name,int type,String def)
		{
			if(type<INTEGER||type>STRING) /* Is is a valid type ? */
			{
				throw new RuntimeException("Invalid argument type:"+type);
			}
			if((def!=null)&&(type==INTEGER)) /* Is default value specified, and if so is it correct type ? */
			{
				try
				{
					Integer.parseInt(def);
				}
				catch(Exception e)
				{
					throw new RuntimeException("Invalid value for "+name+", must be integer value");
				}
			}
			_name=name;
			_type=type;
			_default_value=def;
			_is_set=false;
		}

		public String getValue()
		{
			if(_value!=null)
			{
				return _value;
			}
			else
			{
				return _default_value;
			}
		}

		public void setValue(String value)
		{
			if(_is_set)
			{
				throw new RuntimeException("Argument "+_name+" already specified");
			}
			else
			{
				if(_type==INTEGER)
				{
					try
					{
						Integer.parseInt(value);
					}
					catch(Exception e)
					{
						throw new RuntimeException("Invalid value for "+_name+", must be integer value");
					}
				}
				_value=value;
			}
		}
	}
}
