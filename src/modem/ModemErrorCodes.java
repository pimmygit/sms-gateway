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

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Pimmy
 * Date: Nov 12, 2003
 * Time: 9:06:03 AM
 * To change this template use Options | File Templates.
 */
public class ModemErrorCodes extends Properties
{
    private static final long serialVersionUID = 3460627613699693703L;
    private static ModemErrorCodes _instance;

    public ModemErrorCodes()
    {
	    super();

	    // Mobile Equipment Error result code: +CME ERROR
	    setProperty("+CME ERROR: "+3, "Operation not allowed");

	    setProperty("+CME ERROR: "+4, "Operation not supported");

	    setProperty("+CME ERROR: "+5, "PH-SIM PIN required");

	    setProperty("+CME ERROR: "+10, "SIM not inserted");

	    setProperty("+CME ERROR: "+11, "SIM PIN required");

	    setProperty("+CME ERROR: "+12, "SIM PUK required");

	    setProperty("+CME ERROR: "+13, "SIM failure");

	    setProperty("+CME ERROR: "+16, "Incorrect password");

	    setProperty("+CME ERROR: "+17, "SIM PIN2 required");

	    setProperty("+CME ERROR: "+18, "SIMPUK2 required");

	    setProperty("+CME ERROR: "+20, "Memory full");

	    setProperty("+CME ERROR: "+21, "Invalid index");

	    setProperty("+CME ERROR: "+22, "Device not found");

	    setProperty("+CME ERROR: "+24, "Text too long");

	    setProperty("+CME ERROR: "+26, "Dial string too long");

	    setProperty("+CME ERROR: "+30, "No network service");

	    setProperty("+CME ERROR: "+32, "Emergency calls only");

	    setProperty("+CME ERROR: "+40, "Network personalisation PIN required");

	    // Message service failure result code: +CMS ERROR
	    setProperty("+CMS ERROR: "+1, "unassigned number");

	    setProperty("+CMS ERROR: "+8, "Operator determined barring");

	    setProperty("+CMS ERROR: "+10, "Call barred");

	    setProperty("+CMS ERROR: "+21, "SMS rejected");

	    setProperty("+CMS ERROR: "+27, "Destination out of service");

	    setProperty("+CMS ERROR: "+28, "Unidentified subscriber");

	    setProperty("+CMS ERROR: "+29, "Facility rejected");

	    setProperty("+CMS ERROR: "+30, "Unknown subscriber");

	    setProperty("+CMS ERROR: "+38, "Network out of order");

	    setProperty("+CMS ERROR: "+41, "Temporary failure");

	    setProperty("+CMS ERROR: "+42, "Network congestion");

	    setProperty("+CMS ERROR: "+47, "Resources unavialable");

	    setProperty("+CMS ERROR: "+69, "Facility not implemented");

	    setProperty("+CMS ERROR: "+81, "Invalid short message transfer reference");

	    setProperty("+CMS ERROR: "+95, "Invalid message");

	    setProperty("+CMS ERROR: "+96, "Invalid mandatory information");

	    setProperty("+CMS ERROR: "+97, "Message type non-existent or not implemented");

	    setProperty("+CMS ERROR: "+98, "Message not compatiable with short message protocol state");

	    setProperty("+CMS ERROR: "+99, "Information message non-existent or not implemented");

	    setProperty("+CMS ERROR: "+111, "Protocol error, unspecified");

	    setProperty("+CMS ERROR: "+127, "Message unknown");

	    setProperty("+CMS ERROR: "+301, "SMS ME reserved");

	    setProperty("+CMS ERROR: "+302, "Operation not allowed");

	    setProperty("+CMS ERROR: "+303, "Operation not supported");

	    setProperty("+CMS ERROR: "+304, "Invalid PUD mode");

	    setProperty("+CMS ERROR: "+305, "Invalid text mode");

	    setProperty("+CMS ERROR: "+310, "SIM not inserted");

	    setProperty("+CMS ERROR: "+311, "SIM PIN required");

	    setProperty("+CMS ERROR: "+312, " PH SIM PIN required");

	    setProperty("+CMS ERROR: "+313, " SIM failure");

	    setProperty("+CMS ERROR: "+316, "SIM PUK required");

	    setProperty("+CMS ERROR: "+317, "SIM PIN2 required");

	    setProperty("+CMS ERROR: "+318, "SIM PUK2 required");

	    setProperty("+CMS ERROR: "+321, "Invalid memory index");

	    setProperty("+CMS ERROR: "+322, "SIM memory full");

	    setProperty("+CMS ERROR: "+330, "SC address unknown");

	    setProperty("+CMS ERROR: "+500, "Unknown Error");

	    setProperty("+CMS ERROR: "+512, "MM establishment failure");

	    setProperty("+CME ERROR: "+512, "MM establishment failure");

	    setProperty("+CMS ERROR: "+513, "MM lower layer failure");

	    setProperty("+CME ERROR: "+513, "MM lower layer failuer");

	    setProperty("+CMS ERROR: "+514, "CP error");

	    setProperty("+CME ERROR: "+514, "CP error");

	    setProperty("+CMS ERROR: "+515, "Command processing");

	    setProperty("+CME ERROR: "+515, "Command processing");

	    setProperty("+CMS ERROR: "+516, "USSD error");

	    setProperty("+CME ERROR: "+516, "USSD error");
    }

    private static ModemErrorCodes instance()
    {
    	if(_instance==null)
	    {
		    synchronized(ModemErrorCodes.class)
		    {
			    if(_instance==null)
			    {
				    _instance=new ModemErrorCodes();
			    }
		    }
	    }
	    return _instance;
    }


    public static String getStringValue(String error)
    {
	    String code = instance().getProperty(error);
	    if(code == null)
	        return "ERROR_UNKNOWN";
	    else
	        return code;
    }


    public static void main(String[] s)
    {
	//for(int i=RASBASE;i<RASBASE+154;i++)System.out.println(i+"="+DialErrorCodes.getStringValue(i));

    }
}
