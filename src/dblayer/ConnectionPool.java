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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import log.Logger;

import utils.ConfigStore;
import utils.Constants;
import utils.Utils;

/**
 * JDBC Connection Pool.
 *
 * @author  kliment@hotmail.co.uk
 */
public class ConnectionPool implements Constants
{
    private static final String                             CLASS                       = "ConnectionPool";

    // Private members.
    private String                                          _PoolName                   = null;
    private String                                          _Postfix                    = null;
    private String                                          _DatabaseType               = null;
    private String                                          _URL                        = null;
    private String                                          _DatabaseName               = null;
    private String                                          _Host                       = null;
    private String                                          _Port                       = null;
    private String                                          _Username                   = null;
    private String                                          _Password                   = null;
    private String                                          _DefaultFetchSize           = null;
    private String                                          _ConnectionPropsFile        = null;
    private Properties                                      _ConnectionProps            = null;
    private String                                          _TestQuery                  = null;
    private List<PoolEntry>                                 _Pool                       = new ArrayList<PoolEntry>();

    /**
     * Constructor.
     */
    public ConnectionPool()
    {
        _PoolName = "READ";
        _Postfix = "";
    }

    /**
     * Constructor.
     *
     * @param   poolName    Connection pool name
     */
    public ConnectionPool(String poolName)
    {
        _PoolName = poolName;
        _Postfix = "." + _PoolName;
    }


    public String getPoolName()
    {
        return _PoolName;
    }

    /**
     * Gets a connection from the pool. This checks to see if the pool is
     * initialised or not and initialises as appropriate. Connections will
     * by default be returned with autoCommit set to true
     *
     * @return  Connection
     * @throws  SQLException
     */
    public synchronized Connection getConnection()
        throws SQLException
    {
        Connection retVal = null;

        // Check initialisation.
        if (_URL == null)
        {
            initialise();
        }

        // Iterate until we get a connection or an exception occurs.
        while (retVal == null)
        {
            // Check pool.
            if (_Pool.isEmpty())
            {
                // Nothing in the pool, new connection required.
                retVal = DriverManager.getConnection(_URL, _ConnectionProps);
            }
            else
            {
                // Get pool entry.
                PoolEntry entry = _Pool.remove(_Pool.size() - 1);

                // Get connection from entry, will be null if expired.
                retVal = entry.getConnection();
            }
        }
        if ("READ".equals(_PoolName))
        {
            retVal.setReadOnly(true);
        }
        else
        {
            retVal.setReadOnly(false);
        }
        retVal.setAutoCommit(true);

        Logger.write(Logger.DEBUG, CLASS, "Connection retrieved. Connection Pool '" + _PoolName + "' has size " + _Pool.size());

        return retVal;
    }

    /**
     * Returns a connection to the pool.
     *
     * @param   conn    Connection to return
     */
    public synchronized void returnConnection(Connection conn)
    {
        if (conn != null)
        {
            _Pool.add(new PoolEntry(conn));
            Logger.write(Logger.DEBUG, CLASS, "Connection returned. Connection Pool '" + _PoolName + "' has size " + _Pool.size());
        }
    }

    /**
     * Reset connection pool.
     */
    public synchronized void reset()
    {
        // Iterate over pool, closing connections.
        Iterator<PoolEntry> i = _Pool.iterator();
        while (i.hasNext())
        {
            PoolEntry entry = i.next();
            Connection c = entry.getConnection();
            if (c != null)
            {
                try
                {
                    c.close();
                }
                catch (Exception e)
                {
                    // Don't care.
                }
            }
        }
        _Pool.clear();

        // NULL out the URL to force a reinitialisation.
        _URL = null;
    }

    /**
     * Pool initialiser.
     */
    private synchronized void initialise()
    {
        int                                                 keyColumnWidth              = 40;
        
        try
        {
            // Read properties.
            ConfigStore cs = ConfigStore.getInstance();

            // Initialise logger (must be done after properties)
            _DatabaseType                                                               = cs.getProperty(PROP_DB_TYPE, DEF_DB_TYPE).toLowerCase();
            _Host                                                                       = cs.getProperty(PROP_DB_HOST + _Postfix, cs.getProperty(PROP_DB_HOST, DEF_DB_HOST));
            _Port                                                                       = cs.getProperty(PROP_DB_PORT + _Postfix, cs.getProperty(PROP_DB_PORT, DEF_DB_PORT));
            _DatabaseName                                                               = cs.getProperty(PROP_DB_NAME, DEF_DB_NAME);
            _Username                                                                   = cs.getProperty(PROP_DB_USERNAME + _Postfix, cs.getProperty(PROP_DB_USERNAME, DEF_DB_USERNAME));
            _Password                                                                   = cs.getProperty(PROP_DB_PASSWORD + _Postfix, cs.getProperty(PROP_DB_PASSWORD, DEF_DB_PASSWORD));
            _DefaultFetchSize                                                           = cs.getProperty(PROP_DEFAULT_FETCH_SIZE + _Postfix, cs.getProperty(PROP_DEFAULT_FETCH_SIZE));
            _ConnectionPropsFile                                                        = cs.getProperty(CONNECTION_PROPERTIES_FILE + _Postfix, cs.getProperty(CONNECTION_PROPERTIES_FILE));

            Logger.write(Logger.INFO, CLASS, "Database connection:");
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(PROP_DB_TYPE, keyColumnWidth) +                          ": " + _DatabaseType);
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(PROP_DB_HOST + _Postfix, keyColumnWidth) +               ": " + _Host);
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(PROP_DB_PORT + _Postfix, keyColumnWidth) +               ": " + _Port);
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(PROP_DB_NAME, keyColumnWidth) +                          ": " + _DatabaseName);
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(PROP_DB_USERNAME + _Postfix, keyColumnWidth) +           ": " + _Username);
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(PROP_DB_PASSWORD + _Postfix, keyColumnWidth) +           ": ********");
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(PROP_DEFAULT_FETCH_SIZE + _Postfix, keyColumnWidth) +    ": " + _DefaultFetchSize);
            Logger.write(Logger.INFO, CLASS, Utils.bringToStringLength(CONNECTION_PROPERTIES_FILE + _Postfix, keyColumnWidth) + ": " + _ConnectionPropsFile);

            // Initialize connection properties object with System properties backing
            _ConnectionProps = new Properties();

            // If m_ConnectionPropsFile is set then load in properties from that file
            if(_ConnectionPropsFile != null)
            {
                // First check if m_ConnectionPropsFile is an absolute path
                File connectionPropsFile = new File(_ConnectionPropsFile);

                // If not absolute, then look for file under configurationHome
                if(!connectionPropsFile.isAbsolute())
                {
                    connectionPropsFile = new File(cs.getDirConfig(), _ConnectionPropsFile);
                }

                // Load the properties file if it exists and is readable
                if(connectionPropsFile.exists() && connectionPropsFile.canRead())
                {
                    try
                    {
                        _ConnectionProps.load(new FileInputStream(connectionPropsFile));
                        Logger.write(Logger.INFO, CLASS, "Database connection properties file '" + connectionPropsFile + "' successfully read.");
                    }
                    catch(IOException ioe)
                    {
                        Logger.write(Logger.INFO, CLASS, "Failed to read connection properties file: " + _ConnectionPropsFile);
                        Logger.write(Logger.INFO, CLASS, ioe.toString());

                    }
                    catch(IllegalArgumentException iae)
                    {
                        Logger.write(Logger.INFO, CLASS, "Invalid connection properties file: " + _ConnectionPropsFile);
                        Logger.write(Logger.INFO, CLASS, iae.toString());
                    }
                }
                else
                {
                    Logger.write(Logger.INFO, CLASS, "Connection properties file does not exist: " + _ConnectionPropsFile);
                }
            }

            // Add to the connection properties
            if(_DatabaseType.equals(DatabaseTypes.DBTYPE_ORACLE))
            {
                if(_DefaultFetchSize != null)
                {
                    _ConnectionProps.setProperty("defaultRowPrefetch", _DefaultFetchSize);
                }
            }
            else if(_DatabaseType.equals(DatabaseTypes.DBTYPE_DB2))
            {
                if(_DefaultFetchSize != null)
                {
                    _ConnectionProps.setProperty("block size", _DefaultFetchSize);
                }
            }
            else if (_DatabaseType.equals(DatabaseTypes.DBTYPE_MYSQL))
            {
                if (_DefaultFetchSize != null)
                {
                    _ConnectionProps.setProperty("defaultFetchSize", _DefaultFetchSize);
                }
            }

            _ConnectionProps.put("user", _Username);
            _ConnectionProps.put("password", _Password);


            // Load the database driver.
            String driver = getJdbcDriverName();
            Class.forName(driver);

            // If database.url is not configured then construct it
            if(_URL == null)
            {
                // Construct the JDBC URL.
                _URL = getJdbcURL();
            }

            // Get the test query.
            _TestQuery = getJdbcTestQuery();

            // Display status.
            Logger.write(Logger.INFO, CLASS, "Database connection pool initialized: " + _PoolName);
            Logger.write(Logger.INFO, CLASS, "Database connection driver: " + driver);
            Logger.write(Logger.INFO, CLASS, "Database connection URL: " + _URL);
        }
        catch (Exception e)
        {
            Logger.write(Logger.CRITICAL, CLASS, "Error initializing connection pool '" + _PoolName + "': " + e);
            Logger.write(Logger.CRITICAL, CLASS, "Exit.");
            System.exit(-1);
        }
    }

    /**
     * Gets the appropriate driver name from the database type.
     *
     * @return  driver name
     * @throws  Exception
     */
    private String getJdbcDriverName()
        throws Exception
    {
        String retVal = null;

        if (_DatabaseType.equals(DatabaseTypes.DBTYPE_MYSQL))
        {
            retVal = MYSQL_DRIVER;
        }
        else if (_DatabaseType.equals(DatabaseTypes.DBTYPE_DB2))
        {
            retVal = DB2_DRIVER;
        }
        else if (_DatabaseType.equals(DatabaseTypes.DBTYPE_ORACLE))
        {
            retVal = ORACLE_DRIVER;
        }
        else
        {
            throw new Exception("Unsupported database type: " + _DatabaseType);
        }

        return retVal;
    }

    /**
     * Gets the appropriate test query from the database type.
     *
     * @return  Test query
     * @throws  Exception
     */
    private String getJdbcTestQuery()
        throws Exception
    {
        String retVal = null;

        if (_DatabaseType.equals(DatabaseTypes.DBTYPE_MYSQL))
        {
            retVal = MYSQL_TEST_QUERY;
        }
        else if (_DatabaseType.equals(DatabaseTypes.DBTYPE_DB2))
        {
            retVal = DB2_TEST_QUERY;
        }
        else if (_DatabaseType.equals(DatabaseTypes.DBTYPE_ORACLE))
        {
            retVal = ORACLE_TEST_QUERY;
        }
        else
        {
            throw new Exception("Unsupported database type: " + _DatabaseType);
        }

        return retVal;
    }

    /**
     * Gets the appropriate URL for the database type.
     *
     * @return  JDBC URL
     * @throws  Exception
     */
    private String getJdbcURL()
        throws Exception
    {
        String retVal = null;

        if (_DatabaseType.equals(DatabaseTypes.DBTYPE_MYSQL))
        {
            retVal = MYSQL_URL_PREFIX + _Host + ":" + _Port + "/" + _DatabaseName + MYSQL_URL_PARAMS;
        }
        else if (_DatabaseType.equals(DatabaseTypes.DBTYPE_DB2))
        {
            retVal = DB2_URL_PREFIX + _Host + ":" + _Port + "/" + _DatabaseName + DB2_URL_PARAMS;
        }
        else if (_DatabaseType.equals(DatabaseTypes.DBTYPE_ORACLE))
        {
            retVal = ORACLE_URL_PREFIX + _Host + ":" + _Port + ":" + _DatabaseName + ORACLE_URL_PARAMS;
        }
        else
        {
            Logger.write(Logger.CRITICAL, CLASS, "Unsupported database type: " + _DatabaseType);
            throw new Exception("Unsupported database type: " + _DatabaseType);
        }

        return retVal;
    }

    /**
     * Encapsulates a connection pool entry.
     */
    private class PoolEntry
    {
        // Expiry time.
        private static final long   HOURS           = 3600000;
        private static final long   EXPIRY_TIME     = 2 * HOURS;

        // Private members.
        private Connection  m_Connection    = null;
        private long        m_Timestamp     = 0;

        /**
         * Constructor. Timestamps the connection.
         *
         * @param   conn    Connection
         */
        public PoolEntry(Connection conn)
        {
            // Store connection.
            m_Connection = conn;

            // Timestamp.
            m_Timestamp = System.currentTimeMillis();
        }

        /**
         * Gets the connection stored by this pool entry.
         *
         * @return  Connection or null if it has expired.
         */
        public Connection getConnection()
        {
            Connection retVal = null;

            // Check expiry time & validity.
            long age = System.currentTimeMillis() - m_Timestamp;
            if ( (age < EXPIRY_TIME) && (isConnectionValid()) )
            {
                retVal = m_Connection;
            }
            return retVal;
        }

        /**
         * Check if connection valid.
         *
         * @return  true/false
         */
        public boolean isConnectionValid()
        {
            boolean retVal = false;

            Statement stmt = null;
            ResultSet rs = null;
            try
            {
                // Check for closed connection.
                if (!m_Connection.isClosed())
                {
                    // Do a test query on the connection.
                    stmt = m_Connection.createStatement();
                    rs = stmt.executeQuery(_TestQuery);
                    if ((rs != null) &&
                        (rs.next()))
                    {
                        retVal = true;
                    }
                }
            }
            catch (Exception e)
            {
                retVal = false;
            }
            finally
            {
                if (stmt != null) try { stmt.close(); } catch (SQLException s) {}
                if (rs != null) try { rs.close(); } catch (SQLException s) {}
            }

            return retVal;
        }
    }
}