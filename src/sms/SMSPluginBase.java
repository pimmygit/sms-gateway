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
package sms;

public abstract class SMSPluginBase implements SMSPlugIn
{
    private static final String                         CLASS                       = SMSPluginBase.class.getSimpleName();
    
	/**
	 * Returns the Longitude of the GIS coordinate
	 * 
	 * @return float Longitude
	 */
	public float getLongitude()
	{
		float longitude = 0;
		
		return longitude;
	}
	
	/**
	 * Returns the Latitude of the GIS coordinate
	 * 
	 * @return float Latitude
	 */
	public float getLatitude()
	{
		float latitude = 0;
		
		return latitude;
	}

}
