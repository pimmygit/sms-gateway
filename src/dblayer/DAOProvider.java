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
package dblayer;

import utils.ConfigStore;
import utils.Constants;

import dblayer.DatabaseTypes;

/*
 * Provides the correct instance of the data access object
 * depending on what database vendor is currently used.
 *
 * @author	Kliment Stefanov (kliment@hotmail.co.uk)
 */
public class DAOProvider
{
	/**
	 * Gets an instance of the DAO object according to the database type
	 *
	 * @throws	RuntimeException if invalid database type
	 */
	public static DAO getDAO() throws RuntimeException
	{
		DAO retVal = null;
		
		String type = ConfigStore.getInstance().getProperty(Constants.PROP_DB_TYPE, Constants.DEF_DB_TYPE).toLowerCase();

        if (type.equals(DatabaseTypes.DBTYPE_MYSQL))
        {
            retVal = DAOImplMySQL.getInstance();
        }
        else if (type.equals(DatabaseTypes.DBTYPE_DB2))
        {
            retVal = DAOImplDB2.getInstance();
        }
        else if (type.equals(DatabaseTypes.DBTYPE_ORACLE))
		{
			retVal = DAOImplOracle.getInstance();
		}
		else
		{
			throw new RuntimeException("Unsupported database type: " + type);
		}

		return retVal;
	}
}
