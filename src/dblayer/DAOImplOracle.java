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

/**
 * Oracle specific extension of NetworkViewDAO implementation.
 *
 * @author	kliment@hotmail.co.uk
 */
public class DAOImplOracle extends DAOImpl
{
//  private static final String                             CLASS                       = DAOImplOracle.class.getSimpleName();

    private static DAOImplOracle                            ms_Instance                 = new DAOImplOracle();
	
	/**
	 * Private constructor.
	 */
	private DAOImplOracle() { }

	/**
	 * Singleton instance method.
	 */
	public static DAOImplOracle getInstance()
	{
		synchronized (ms_Instance)
        {
			return ms_Instance;
        }
	}

    /**
     * Get the Oracle specific function or keyword to retrieve the current timestamp, i.e. "SYSDATE"
     * 
     * @return "SYSDATE"
     */
    public String getCurrentTimestampFunction()
    {
        return "SYSTIMESTAMP";
    }
}
