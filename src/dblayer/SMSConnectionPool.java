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

import java.sql.Connection;
import java.sql.SQLException;

import dblayer.ConnectionPool;

public class SMSConnectionPool
{
    // Different connection pool names.
    private static final String                             POOL_WRITE              = "WRITE";
 
    // The Pools Panel.
    private static ConnectionPool                           _ReadPool               = new ConnectionPool();
    private static ConnectionPool                           _WritePool              = new ConnectionPool(POOL_WRITE);

    /**
     * Gets a read connection from the pool. 
     *
     * @return  Connection
     * @throws  SQLException
     */
    public static Connection getConnection()
            throws SQLException
    {
        return _ReadPool.getConnection();
    }

    public static ConnectionPool getConnectionPool()
    {
        return _ReadPool;
    }
 
    /**
     * Returns a read connection to the pool.
     *
     * @param   conn    Connection to return
     */
    public static void returnConnection(Connection conn)
    {
        _ReadPool.returnConnection(conn);
    }
 
    /**
     * Gets a write connection from the pool.
     * 
     * @return  Connection
     * @throws  SQLException
     */
    public static Connection getWriteConnection()
            throws SQLException
    {
        return _WritePool.getConnection();
    }
 
    /**
     * Returns a write connection to the pool.
     * 
     * @param   conn    Connection to return
     */
    public static void returnWriteConnection(Connection conn)
    {
        _WritePool.returnConnection(conn);
    }
 
    /**
     * Reset connection pool.
     */
    public static void reset()
    {
        System.out.println("SMS ConnectionPool Reset");
        _ReadPool.reset();
        _WritePool.reset();
    }
}
