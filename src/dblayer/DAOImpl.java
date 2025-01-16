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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import log.Logger;
import server.SMSAction;
import utils.Constants;

/**
 * Main interface to the database. Generic
 * Only generic SQL statements are contained within this class.
 * All database-specific SQL should be implemented in the subclasses.
 *
 * @author	kliment@hotmail.co.uk
 */
public abstract class DAOImpl implements DAO, Constants
{
    private static final String                             CLASS                       = "DAOImpl";

    
    /**
     * Saves SMS message. Status reflects successful sending
     * 
     * @param number    Mobile number
     * @param message   SMS Message
     * @param status    Status
     * @throws RuntimeException
     */
    public void saveSentSMS(String number, String message, boolean status)
            throws RuntimeException
    {
        Connection                                          conn                        = null;
        PreparedStatement                                   stmt                        = null;
        ResultSet                                           rs                          = null;
        String                                              query                       = null;
        
        try
        {
            conn = SMSConnectionPool.getConnection();

            query = "INSERT INTO " + Constants.DB_TN_SMSMESSAGESENT + "(" +
                        Constants.DB_FN_MOBILENUMBER + ", " +
                        Constants.DB_FN_MESSAGETEXT + ", " +
                        Constants.DB_FN_DATETIME + ", " +
                        Constants.DB_FN_STATUS +
                    ") VALUES (?, ?, '" + getCurrentTimestampFunction() + "', ?)";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, number);
            stmt.setString(2, message);
            stmt.setBoolean(3, status);

            Logger.write(Logger.DEBUG, CLASS, "SQL [" + number + "],[" + message + "],[" + status + "]: " + query);
            rs = stmt.executeQuery();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error executing query: " + query, e);
        }
        finally
        {
            closeReadDBResources(conn, stmt, rs);
        }
    }
    
    /**
     * Saves SMS message.
     * 
     * @param number    Mobile number
     * @param message   SMS Message
     * @throws RuntimeException
     */
    public void saveReceivedSMS(String number, String message)
            throws RuntimeException
    {
        Connection                                          conn                        = null;
        PreparedStatement                                   stmt                        = null;
        ResultSet                                           rs                          = null;
        String                                              query                       = null;
        
        try
        {
            conn = SMSConnectionPool.getConnection();

            query = "INSERT INTO " + Constants.DB_TN_SMSMESSAGERECEIVED + "(" +
                        Constants.DB_FN_MOBILENUMBER + ", " +
                        Constants.DB_FN_MESSAGETEXT + ", " +
                        Constants.DB_FN_DATETIME +
                    ") VALUES (?, ?, '" + getCurrentTimestampFunction() + "')";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, number);
            stmt.setString(2, message);

            Logger.write(Logger.DEBUG, CLASS, "SQL [" + number + "],[" + message + "]: " + query);
            rs = stmt.executeQuery();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error executing query: " + query, e);
        }
        finally
        {
            closeReadDBResources(conn, stmt, rs);
        }
    }
    
    /**
     * Save the SMS Gateway action for the given SMS Number.
     * 
     * @param smsNumber		Mobile number
     * @param action		SMS Gateway action for this mobile number
     * @throws RuntimeException
     */
    public void saveSmsAction(String smsNumber, int action)
            throws RuntimeException
    {
        Connection                                          conn                        = null;
        PreparedStatement                                   stmt                        = null;
        ResultSet                                           rs                          = null;
        String                                              query                       = null;
        
        try
        {
            conn = SMSConnectionPool.getConnection();

            query = "INSERT INTO " + Constants.DB_TN_SMSACTION + "(" +
                        Constants.DB_FN_MOBILENUMBER + ", " +
                        Constants.DB_FN_ACTION + ", " +
                        Constants.DB_FN_DATETIME +
                    ") VALUES (?, ?, '" + getCurrentTimestampFunction() + "')";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, smsNumber);
            stmt.setInt(2, action);

            Logger.write(Logger.DEBUG, CLASS, "SQL: " + query);
            rs = stmt.executeQuery();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error executing query: " + query, e);
        }
        finally
        {
            closeReadDBResources(conn, stmt, rs);
        }
    }
    
    /**
     * Save the SMS Gateway action for the given SMS Number.
     * 
     * @param smsNumber		Mobile number
     * @throws RuntimeException
     * @return SMSAction object listing the actions for the given mobile number.
     */
    public SMSAction getSmsAction(String trimmedSmsNumber)
            throws RuntimeException
    {
    	SMSAction											smsAction					= new SMSAction();
    	
        Connection                                          conn                        = null;
        PreparedStatement                                   stmt                        = null;
        ResultSet                                           rs                          = null;
        String                                              query                       = null;
        
        try
        {
            conn = SMSConnectionPool.getConnection();

            query = "SELECT * FROM " + Constants.DB_TN_SMSACTION + " WHERE " + Constants.DB_FN_MOBILENUMBER + " LIKE ?";
            Logger.write(Logger.DEBUG, CLASS, "SQL[%" + trimmedSmsNumber + "]: " + query);

            stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + trimmedSmsNumber);

            rs = stmt.executeQuery();
            
            while (rs.next())
            {
            	smsAction.put(rs.getInt(Constants.DB_FN_ACTION), rs.getTimestamp(Constants.DB_FN_DATETIME));
            }
            
            if (smsAction.getAll().isEmpty())
            {
				Logger.write(Logger.INFO, CLASS, "No action found in the database for number: " + trimmedSmsNumber);
            } else {
				Logger.write(Logger.INFO, CLASS, "Found " + smsAction.getAll().size() + " actions for number pattern: " + trimmedSmsNumber);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error executing query: " + query, e);
        }
        finally
        {
            closeReadDBResources(conn, stmt, rs);
        }
        
        return smsAction;
    }
    
    /**
     * Convenience method to close all database objects used within method. It should be called from finally method to
     * properly release WRITE database resources.
     *
     * @param conn Connection
     * @param stmt PreparedStatement
     * @param rs ResultSet
     */
    protected void closeWriteDBResources(Connection conn, PreparedStatement stmt, ResultSet rs)
    {
        closeDBResources(conn,stmt,rs,true);
    }

    /**
     * Convenience method to close all database objects used within method. It should be called from finally method to
     * properly release READ database resources.
     *
     * @param conn Connection
     * @param stmt PreparedStatement
     * @param rs ResultSet
     */
    protected void closeReadDBResources(Connection conn, PreparedStatement stmt, ResultSet rs)
    {
        closeDBResources(conn,stmt,rs,false);
    }

    /*
     * (non-javadoc)
     */
    private void closeDBResources(Connection conn, PreparedStatement stmt, ResultSet rs, boolean isWriteConnection)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
                rs = null;
            }
            catch (Exception e)
            {
            }
        }
        if (stmt != null)
        {
            try
            {
                stmt.close();
                stmt = null;
            }
            catch (Exception e)
            {
            }
        }

        if (conn != null)
        {
            if (isWriteConnection)
            {
                SMSConnectionPool.returnWriteConnection(conn);
            }
            else
            {
                SMSConnectionPool.returnConnection(conn);
            }
        }
    }
}