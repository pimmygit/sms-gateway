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
 * DB2 specific extension of the DAO implementation.
 */
public class DAOImplDB2 extends DAOImpl
{
//  private static final String                             CLASS                       = DAOImplDB2.class.getSimpleName();
    private static DAOImplDB2                               ms_Instance                 = new DAOImplDB2();
  
    /**
    * Private constructor.
    */
    private DAOImplDB2() { }

    /**
     * Singleton instance method.
     */
    public static DAOImplDB2 getInstance()
    {
        synchronized (ms_Instance)
        {
            return ms_Instance;
        }
    }

    /**
     * Get the DB2 specific function or keyword to retrieve the current timestamp, i.e. "CURRENT TIMESTAMP"
     *
     * @return "CURRENT TIMESTAMP"
     */
    public String getCurrentTimestampFunction()
    {
        return "CURRENT TIMESTAMP";
    }
}
