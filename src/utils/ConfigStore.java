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
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import log.Logger;

import utils.Constants;

public class ConfigStore
{
    private final static String                             CLASS                       = ConfigStore.class.getSimpleName();

    // Obtain the program startup location from the Java -D property
    private final static String                             SMSG_HOME                   = System.getProperty("user.dir"); //System.getProperty("SMSG_HOME");
    private final static String                             SMSG_DIR_CONFIG             = SMSG_HOME + Constants.DIR_CONFIG + "/";
    private final static String                             SMSG_DIR_LOG                = SMSG_HOME + Constants.DIR_LOG + "/";
    private final static String                             SMSG_FILE_PROPS             = SMSG_DIR_CONFIG + Constants.FILE_PROPS_SMSGATE;
    private final static String                             SMSG_FILE_LOG               = SMSG_DIR_LOG + Constants.FILE_LOG_SMSGATE;
    
    private static final long                               FILE_CHECK_TIMEOUT          = 60000;

    private static ConfigStore                              _Instance                   = null;

    private File                                            _gatePropsFile              = null;
    private Properties                                      _gateProperties             = null;

    // Listeners.
    private Vector<ConfigStoreListener>                     _Listeners                  = new Vector<ConfigStoreListener>();

    // Timers to check for configuration updates.
    private Timer                                           _Timer                      = new Timer(true);
    private long                                            _LastModified               = 0;

    /**
     * Constructs instance of {@link ConfigStore}.
     */
    private ConfigStore()
    {
        // Ensure the program has been started with -D Java option,
        // as well as verify the provided location. SMS_HOME is specified like this:
        // "%JAVA_HOME%//java" -DSMGHOME=%SMS_HOME% -cp %SMS_HOME%//libs//comm.jar;%SMS_HOME%//libs//SMSManager.jar gui.SMSManager
        if (SMSG_HOME == null || "".equals(SMSG_HOME)) {
            System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Empty startup location has been defined. Example: java -DSMSG_HOME=C://Pimmy//smsgate -cp....");
            System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Exit.");
            System.exit(-1);
        } else {
            // Verify if the location exist
            File home = new File(SMSG_HOME);
            if (!home.exists()) {
                System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Home Directory ["+SMSG_HOME+"] does not exist.");
                System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Exit.");
                System.exit(-1);
            }
            // Verify if the location is valid
            File startup = new File(home, "smsgate.cfg");
            if (!startup.exists()) {
                System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Home Directory ["+SMSG_HOME+"] is wrong.");
                System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Exit.");
                System.exit(-1);
            }
        }
        
        _gatePropsFile = new File(SMSG_FILE_PROPS);
        
        // Get last updated time & start update timer.
        _LastModified = _gatePropsFile.lastModified();
        _Timer.schedule(new ConfigStoreFileCheck(), FILE_CHECK_TIMEOUT, FILE_CHECK_TIMEOUT);

        // Load properties.
        reloadProps();
    }

    /**
     * Retrieve the instance of the config.
     */
    public static synchronized ConfigStore getInstance()
    {
        if (_Instance == null)
        {
            _Instance = new ConfigStore();
        }
        return _Instance;
    }
    
    /**
     * This is used when shutting down.
     */
    public static synchronized void nullInstance()
    {
        if (_Instance != null)
        {
            _Instance.stopUpdateChecking();
            _Instance = null;
        }
    }

    /**
     * Stop any update checking.
     */
    public void stopUpdateChecking()
    {
        _Timer.cancel();
    }

    /**
     * Reload configuration properties.
     */
    public void reloadProps()
    {
        FileInputStream fStream = null;
        // Loading SMS Manager variables
        _gateProperties = new Properties();
        try
        {
            System.out.println(Logger.getFormattedTimestamp() + "DEBUG    " + CLASS + "    Loading properties from: " + SMSG_FILE_PROPS);
            fStream = new FileInputStream(_gatePropsFile);
            if (fStream != null)
            {
                _gateProperties.load(fStream);
                
                // SMSC Number may need normalization
                _gateProperties.setProperty(Constants.PROP_PROVIDER_SMSC, getNormalizedSMSC());
                
                printProperties(_gateProperties);
            }
        }
        catch(IOException e)
        {
            System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Unable to load properties from file: " + SMSG_FILE_PROPS);
            System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "    Exit.");
            System.exit(-1);
        }
        finally
        {
            if (fStream != null)
            {
                try { fStream.close(); } 
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }


    /**
     * Returns the location of the product home directory.
     *
     * @return  String
     */
    public String getDirHome()
    {
        return SMSG_HOME;
    }

    /**
     * Returns the location of the product home directory.
     *
     * @return  String
     */
    public String getDirConfig()
    {
        return SMSG_DIR_CONFIG;
    }

    /**
     * Returns the absolute path to the configuration file.
     *
     * @return  String
     */
    public String getFileConfig()
    {
        return SMSG_FILE_PROPS;
    }

    /**
     * Returns the location of the product home directory.
     *
     * @return  String
     */
    public String getDirLog()
    {
        return SMSG_DIR_LOG;
    }

    /**
     * Returns the absolute path of the log file.
     *
     * @return  String
     */
    public String getFileLog()
    {
        return SMSG_FILE_LOG;
    }

    /**
     * Returns property from a configuration file for given <code>key</code> property name.
     * This will automatically append the prefix part of the property name.
     *
     * @param   key     Key of property to get.
     * @return  String value of property.
     */
    public String getProperty(String key)
    {
        return _gateProperties.getProperty(key);
    }

    /**
     * Sets a server property. Auto-appends prefix part of prop name.
     *
     * @param   key     Key of property to set.
     * @param   value   New value.
     */
    public void setProperty(String key, String value)
    {
        _gateProperties.setProperty(key, value);
    }

    /**
     * Get a server property. This will automatically append the prefix part
     * of the property name.
     *
     * @param   key     Key of property to get.
     * @param   def     Default value.
     * @return property value
     */
    public String getProperty(String key, String def)
    {
        String retVal = getProperty(key);
        if (retVal == null)
        {
            return def;
        }
        else
        {
            return retVal;
        }
    }

    /**
     * Get a server property. This will automatically append the prefix part
     * of the property name.
     *
     * @param   key     Key of property to get.
     * @param   def     Default value.
     * @return property value
     */
    public int getProperty(String key, int def)
    {
        String strVal = getProperty(key);
        if (strVal == null)
        {
    		Logger.write(Logger.MINOR, CLASS, "Property '" + key + "' not found in '" +  SMSG_FILE_PROPS + "'. Using default: " + def);
            return def;
        }
        else
        {
        	try
        	{
        		return Integer.parseInt(strVal);
        	} 
        	catch (Exception e) 
        	{
        		Logger.write(Logger.MINOR, CLASS, "Invalid property format for property '" + key + "'. Expected Integer, found '" + strVal + "'. Using default: " + def);
        		return def;
        	}
        }
    }

    /**
     * Get a server property. This will automatically append the prefix part
     * of the property name.
     *
     * @param   key     Key of property to get.
     * @param   def     Default value.
     * @return property value
     */
    public long getProperty(String key, long def)
    {
        String strVal = getProperty(key);
        if (strVal == null)
        {
            return def;
        }
        else
        {
        	try
        	{
        		return Long.parseLong(strVal);
        	} 
        	catch (Exception e) 
        	{
        		Logger.write(Logger.MINOR, CLASS, "Invalid property format for property '" + key + "'. Expected Integer, found '" + strVal + "'. Using default: " + def);
        		return def;
        	}
        }
    }

    /**
     * Get a server property. This will automatically append the prefix part
     * of the property name.
     *
     * @param   key     Key of property to get.
     * @param   def     Default value.
     * @return property value
     */
    public boolean getProperty(String key, boolean def)
    {
        String strVal = getProperty(key);
        if (strVal == null)
        {
            return def;
        }
        else
        {
            return Boolean.valueOf(strVal).booleanValue();
        }
    }

    /**
     * Get a set of properties all starting with the supplied key.
     *
     * @param   keyStart    Start of key
     * @return  Map of key/value pairs
     */
    public Map<String, String> getProperties(String keyStart)
    {
        HashMap<String, String> retVal = new HashMap<String, String>();

        String prefix = keyStart + ".";
        Iterator<Object> keys = _gateProperties.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String)keys.next();
            if (key.startsWith(prefix))
            {
                // Key matches prefix - strip off prefix and add to map.
                String newKey = key.substring(prefix.length());
                retVal.put(newKey, _gateProperties.getProperty(key));
            }
        }

        return retVal;
    }
    
    /**
     * Retrieves and if needed normalizes the Mobile network provider's SMSC number
     * 
     * @return SMSC number
     */
    private String getNormalizedSMSC() 
    {
        String                                              countryCode                 = getProperty(Constants.PROP_PROVIDER_COUNTRY_CODE);
        String                                              smscNumber                  = getProperty(Constants.PROP_PROVIDER_SMSC);
        
        if (countryCode == null)
        {
            System.out.println(Logger.getFormattedTimestamp() + "MAJOR    " + CLASS + "    Provider's country code is not set in properties file:" + SMSG_FILE_PROPS);
        }
        
        if (smscNumber == null)
        {
            System.out.println(Logger.getFormattedTimestamp() + "MAJOR    " + CLASS + "    Provider's SMSC number is not set in properties file:" + SMSG_FILE_PROPS);
            return null;
        }
        
        // Normalize the format of the SMSC number
        smscNumber = smscNumber.replaceAll(" ", "");
        if (smscNumber.startsWith("00"))
        {
            smscNumber.replaceFirst("00", "+");
            return smscNumber;
        }
        if (smscNumber.startsWith("0"))
        {
            if (countryCode != null)
            {
                smscNumber.replaceFirst("0", "+" + countryCode);
                return smscNumber;
            }
        }
        if (smscNumber.startsWith("+"))
        {
            return smscNumber;
        }

        System.out.println(Logger.getFormattedTimestamp() + "MAJOR    " + CLASS + "    Possibly Wrong SMSC number:" + smscNumber);
        return smscNumber;
    }

    /**
     * Add a listener to the list of listeners which should be notified
     * about configuration change.
     *
     * @param   listener
     */
    public void addListener(ConfigStoreListener listener)
    {
        _Listeners.add(listener);
    }

    /**
     * Remove a configuration change listener.
     *
     * @param   listener
     */
    public void removeListener(ConfigStoreListener listener)
    {
        _Listeners.remove(listener);
    }

    /**
     * Private inner class to implement a timer
     */
    private class ConfigStoreFileCheck extends TimerTask
    {
        /**
         * Checks to see whether a config store requires updating.
         */
        @Override
        public void run()
        {
            // Check for a configuration update.
            long lastModified = _gatePropsFile.lastModified();
            if (lastModified > _LastModified)
            {
                // Configuration updated, store new modification time.
                _LastModified = lastModified;

                // Reload properties.
                System.out.println(Logger.getFormattedTimestamp() + "CONFIG   " + CLASS + "     Reloading configuration due to recent file modification: " + _gatePropsFile.getAbsoluteFile());
                reloadProps();

                // Inform any listeners.
                ConfigStoreListener listener = null;
                Iterator<ConfigStoreListener> i = _Listeners.iterator();
                while (i.hasNext())
                {
                    listener = i.next();
                    if (listener != null)
                    {
                        listener.configStoreUpdated();
                    }
                }
            }
        }
    }

    /**
     * Prints all available properties
     * 
     * @param Properties
     */
    private void printProperties(Properties props)
    {
        int                                                 keyColumnWidth              = 40;

        System.out.println(Logger.getFormattedTimestamp() + "CONFIG   " + CLASS + "    **********************************************************************");
        System.out.println(Logger.getFormattedTimestamp() + "CONFIG   " + CLASS + "    * Properties: " + SMSG_FILE_PROPS);
        System.out.println(Logger.getFormattedTimestamp() + "CONFIG   " + CLASS + "    * --------------------------------------------------------------------");

        Enumeration<Object>                                 keys                        = props.keys();
        while (keys.hasMoreElements())
        {
            String                                          key                         = (String)keys.nextElement();
            String                                          value                       = (String)props.get(key);
            
            // Print only certain properties
            if (key.startsWith("log") || key.startsWith("modem.device") || key.startsWith("database"))
            {
                System.out.println(Logger.getFormattedTimestamp() + "CONFIG   " + CLASS + "    * " + Utils.bringToStringLength(key, keyColumnWidth) + ": " + value);
            }
        }
        System.out.println(Logger.getFormattedTimestamp() + "CONFIG   " + CLASS + "    **********************************************************************");
    }
}