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

import modem.CommandLineProperties;
import sms.SMGArgument;
import utils.ArgLoad;
import log.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Pimmy
 * Date: Nov 5, 2003
 * Time: 9:04:58 AM
 * To change this template use Options | File Templates.
 */
public class ModemArguments extends CommandLineProperties implements SMGArgument
{

    private static final long serialVersionUID = 3102576972446168390L;
    private static final String CLASS = ModemArguments.class.getSimpleName();
    
    public ModemArguments()
    {
	    // Default Modem Settings
	    defineProperty("portwait", CommandLineProperties.INTEGER, "60");

	    // Default AT Commands
        defineProperty("errorlevel",CommandLineProperties.STRING,"AT+CMEE=1");
	    defineProperty("manufacturer",CommandLineProperties.STRING,"AT+CGMI");
	    defineProperty("messagemode",CommandLineProperties.STRING,"AT+CMGF=1");
	    defineProperty("setmessage",CommandLineProperties.STRING,"AT+CNMI=1,2,2,1,0");
	    defineProperty("smsc",CommandLineProperties.STRING,"AT+CSCA");
        defineProperty("validity", CommandLineProperties.STRING,"AT+CSMP");
	    defineProperty("send",CommandLineProperties.STRING,"AT+CMGS");
        defineProperty("get",CommandLineProperties.STRING,"AT+CMGL=\"ALL\"");
        defineProperty("remove", CommandLineProperties.STRING,"AT+CMGD");
    }

	public void processArgument(String propsFileName)
    {
	    /* Load the properties from a config file, ignore the once passed from the command line */
	    String[] arg=ArgLoad.loadArgumentList(propsFileName);
	    super.processArguments(arg);
	    Logger.write(Logger.INFO, CLASS, "Arguments Loaded: " + propsFileName);
	}

	public String getProperty(String name)
	{
	    return super.getProperty(name);
	}
}
