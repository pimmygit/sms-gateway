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

import java.util.regex.Pattern;

public class IMSI
{
    private String										_imsiString					= null;
	
	public IMSI(String imsi)
	{
		_imsiString																	= imsi;
	}
	
	public String getString()
	{
		return _imsiString;
	}
		
	public boolean isValid()
	{
		String											pattern						= "^\\+[1-9]{1}[0-9]{3,14}$";
    	
        if (Pattern.matches(pattern, _imsiString))
        {
        	return true;
        } else {
        	return false;
        }
    }
}
