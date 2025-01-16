/******************************************************************
 *
 * SMS Gateway
 * 
 * (C) Copyright Pimmy (Kliment Stefanov). 2016  
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

import log.Logger;

public class IMEI
{
    private static final String                         CLASS                       = IMEI.class.getSimpleName();

    private String										_imeiString					= null;
    private long										_imeiLong					= 0;
	private boolean										_valid						= false;
	
	public IMEI(String imei)
	{
		if (checkValidity(imei))
		{
			_valid																	= true;
		}
	}
	
	public String getString()
	{
		return _imeiString;
	}
	
	public long getLong()
	{
		return _imeiLong;
	}
	
	public boolean isValid()
	{
		return _valid;
	}
	     
    private boolean checkValidity(String imei)
    {
    	long 											longFormat					= 0;
        int												d							= 0;
        int												sum							= 0;
        
    	try
    	{
    		longFormat																= Long.parseLong(imei);
    		_imeiLong																= longFormat;
    	}
    	catch (NumberFormatException nfe)
    	{
            Logger.write(Logger.MINOR, CLASS, "Invalid format supplied: " + imei);
    		return false;
    	}
    	
        if(imei.length() != 15)
        {
            Logger.write(Logger.MINOR, CLASS, "Invalid length supplied: " + imei);
    		return false;
        }
        
        for(int i=15; i>=1; i--)
        {
            d																		= (int)(longFormat%10);
             
            if(i%2 == 0)
            {
                d																	= 2*d;							// Doubling every alternate digit
            }
            sum																		= sum + sumDig(d);				// Finding sum of the digits
             
            longFormat																= longFormat/10;
        }
                
        if(sum%10 != 0)
        {
            Logger.write(Logger.MINOR, CLASS, "Invalid number: " + imei);
    		return false;
        }
        
        return true;
    }
    
    /*
     * Function for finding and returning sum of digits of a number
     */
    private int sumDig(int n)
    {
        int												a							= 0;
        
        while(n > 0)
        {
            a																		= a + n%10;
            n																		= n/10;
        }
        
        return a;
    }
}
