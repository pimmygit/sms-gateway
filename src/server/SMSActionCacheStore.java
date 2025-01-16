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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dblayer.DAO;
import dblayer.DAOProvider;
import log.Logger;
import utils.Constants;

/**
 * Each mobile number may have several actions to be executed.
 * This cache will contain the action set per mobile number.
 */
public class SMSActionCacheStore
{
    private static final String                         CLASS                       = SMSActionCacheStore.class.getSimpleName();
    
	private static SMSActionCacheStore					_instance					= null;
	
	private static Map<String, SMSAction> 				_actionsMap					= Collections.synchronizedMap(new HashMap<String, SMSAction>());
	
	private DAO											_dao						= DAOProvider.getDAO();

	private SMSActionCacheStore()
	{
		_actionsMap.clear();
	}

	/**
	 * Get an instance of the singleton in the VM.
	 * 
	 * @return the singleton instance
	 */
	public static SMSActionCacheStore getInstance() 
	{
		synchronized (SMSActionCacheStore.class) 
		{
			if (_instance == null) 
			{
				_instance = new SMSActionCacheStore();

				Logger.write(Logger.DEBUG, CLASS, "Instantiated singleton.");
			}

			return _instance;
		}
	}
	
	/**
	 * Retrieve user action for the given mobile number:
	 * - Check if the cache contains the action.
	 * - Either return the value from the cache or pull it from the DB adding it to the cache.
	 */
	public SMSAction get(String smsNumber)
	{
		// To eliminate differences in the format (0044 vs +44 vs 0), we use only the last eight characters from the mobile number,
		// which should be sufficient to uniquely identify the SIM/client.
		String											trimmedSmsNumber			= smsNumber.substring(smsNumber.length() - 8);
		SMSAction										action						= _actionsMap.get(trimmedSmsNumber);
		
		if (action == null)
		{
			action																	= _dao.getSmsAction(trimmedSmsNumber);
			
			if (action.getAll().isEmpty())
			{
				// Set default action
				action.put(Constants.ACTION_DB_ARCHIVE, Calendar.getInstance().getTime());
				Logger.write(Logger.INFO, CLASS, "Using default action (" + Constants.ACTION_DB_ARCHIVE + ") for number: " + smsNumber);
			}
			
			_actionsMap.put(trimmedSmsNumber, action);
		}
		
		return action;
	}
	
	/**
	 * Add SMS Action to the cache
	 */
	public void put(String number, int newAction)
	{
		_actionsMap.put(number, new SMSAction());
		_dao.saveSmsAction(number, newAction);
	}
	
	/**
	 * Forces a re-cache of the data.
	 */
	public void purge() 
	{
		synchronized (_actionsMap) 
		{
			_actionsMap.clear();
		}

		Logger.write(Logger.DEBUG, CLASS, "Cache data purge completed.");
	}

}
