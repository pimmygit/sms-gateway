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
package server;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains what actions the SMS Gateway should take once it receives an SMS Message from particular number.
 * All actions are stored in the database for each customer account with default action DB_ARCHIVE.
 * 
 * User may have more than one action specified and we keep the timestamp when the action was added to the mobile number.
 * 
 * @author pimmy
 *
 */
public class SMSAction
{
	private Map<Integer, Date>							_actionList					= new HashMap<Integer, Date>();
		
	public SMSAction() { }
	
	public SMSAction(int action)
	{
		_actionList.put(action, Calendar.getInstance().getTime());
	}
	
	public void put(int action, Date timestamp)
	{
		_actionList.put(action, timestamp);
	}
	
	public Map<Integer, Date> getAll()
	{
		return _actionList;
	}
	
	public boolean has(int action)
	{
		return _actionList.containsKey(action);
	}
}