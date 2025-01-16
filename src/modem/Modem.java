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
package modem;

import gnu.io.CommDriver;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import javax.naming.TimeLimitExceededException;

import dblayer.DAO;
import dblayer.DAOProvider;
import log.Logger;
import sms.SMSMessage;
import sms.SMSResponse;
import utils.ConfigStore;
import utils.Constants;

public class Modem implements SerialPortEventListener
{
    private static final String                         CLASS                       = Modem.class.getSimpleName();
    
    private final static String							COMM_DRIVER					= "com.sun.comm.Win32Driver";
    
    private ConfigStore                                 _config                     = ConfigStore.getInstance();
    
    private SerialPort                                  _serialPort                 = null;
    private CommPortIdentifier                          _serialPortID               = null;

    private String                                      _serialPortName             = Constants.DEF_PORT_NAME;
    private int                                         _serialPortWait             = Constants.DEF_PORT_TIMEOUT_OPEN;
    
    private InputStream                                 _inStream                   = null;
    private OutputStream                                _outStream                  = null;

    private boolean                                     _isOpen                     = false;
    
    private String                                      _deviceName                 = null;
    
    private boolean                                     ERROR                       = false;
    private boolean                                     OK                          = false;
    private boolean                                     _sent                        = false;
    private String                                      _dn;
    boolean                                             _responsePending            = false;
    boolean                                             sendMsg                     = false;
    boolean												_testMessage				= false;

    private String                                      atCommand                   = null;
    private SMSResponse                                 smsResponse                 = null;
    private LinkedList<SMSMessage>                      _memMess                    = null;

    public boolean                                      _sending                    = false;
    public int                                          _sending_attempt            = 1;
    public boolean                                      SENT_OK                     = false;

    // AT commands
    private String                                      AT_USER_VALUE_ERRORLEVEL   	= null;
    private String                                      AT_USER_VALUE_SIGNALSTRENGTH= null;
    private String                                      AT_USER_VALUE_GSMSTATUS	   	= null;
    private String                                      AT_USER_VALUE_NETWORKSTATUS	= null;
    private String                                      AT_USER_VALUE_OPERATOR	   	= null;
    private String                                      AT_USER_VALUE_MANUFACTURER	= null;
    private String                                      AT_USER_VALUE_MESSAGEMODE 	= null;
    private String                                      AT_USER_VALUE_SETMESSAGE   	= null;
    private String                                      AT_USER_VALUE_SMSC         	= null;
    private String                                      AT_USER_VALUE_VALIDITY     	= null;
    private String                                      AT_USER_VALUE_SEND         	= null;
    private String                                      AT_USER_VALUE_GET          	= null;
    private String                                      AT_USER_VALUE_REMOVE       	= null;

    public Modem() throws Exception 
    {        
        // Obtain the Serial port to which the modem is connected
        try
        {
        	_serialPortName																= _config.getProperty(Constants.PROP_PORT_NAME, Constants.DEF_PORT_NAME);
        	_serialPortWait																= _config.getProperty(Constants.PROP_PORT_TIMEOUT_OPEN, Constants.DEF_PORT_TIMEOUT_OPEN);
        	
        	this.portIdentify(_serialPortName);
            this.portOpen();
            this.portInit(getSerialPortParameters(_config));
            this.readAtCommands();
        }
        catch (Exception e)
        {
        	this.portClose();
            Logger.write(Logger.CRITICAL, CLASS, "Error starting modem on port: " + _config.getProperty(Constants.PROP_PORT_NAME, Constants.DEF_PORT_NAME));
            throw new Exception ("Error loading port: " + _config.getProperty(Constants.PROP_PORT_NAME, Constants.DEF_PORT_NAME));
        }
    }
    
    /**
     * Run AT Command and return the response as a String with no action taken.
     * This method is for testing purposes.
     * 
     * @param	AT Command
     * @return	response
     */
    public String runAT(String atCommand) {
    	
    	String												response					= "";
    	_testMessage																	= true;
    	
        try
        {
            Logger.write(Logger.INFO, CLASS, "Sending AT Command:" + atCommand);
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
        }
        catch(Exception e)
        {
            Logger.write(Logger.CRITICAL, CLASS, "Failed to run [" + atCommand + "] on modem:" + _serialPort.getName());
        }
    	
    	return response;
    }
    
    /**
     * Find what ports are available on the system and try to find a match with the specified in the properties file port.
     * 
     * @param portName
     * @throws Exception
     */
    private void portIdentify(String portName) throws Exception
    {
    	ArrayList<String>									portList					= new ArrayList<String>();
    	
        // Check if the specified port is available on the system
    	try
    	{
    		portList																	= getAvailablePorts();
    	}
    	catch (Exception e)
    	{
            Logger.write(Logger.MAJOR, CLASS, "Error obtaining the port list! " + e.getMessage());
            throw new Exception(e);
    	}
        
    	if (!portList.contains(portName))
    	{
            Logger.write(Logger.MAJOR, CLASS, "Specified port '" + portName + "' is not available on this system! Please check configuration file: " + _config.getFileConfig());
            throw new Exception("Port '" + portName + "' is not available on the system.");
    	}
    	
        try
        {
            _serialPortID = CommPortIdentifier.getPortIdentifier(portName);
        }
        catch(Exception e)
        {
            throw new Exception("Could not open port or device: " + portName);
        }
    }

    /**
     * Returns a list of all serial communication ports available on the system.
     * 
     * @return List of communication ports
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getAvailablePorts() throws Exception
    {
        ArrayList<String>                                   ports                       = new ArrayList<String>();
        StringBuilder                                       portList                    = new StringBuilder();
                
        if( System.getProperty("os.name").indexOf("Windows") != -1 )
        {
        	try
        	{
                Logger.write(Logger.DEBUG, CLASS, "Loading Windows Serial Port driver: " + System.getProperty("os.name"));
        		CommDriver                                  commDriver                  = (CommDriver)Class.forName(COMM_DRIVER).newInstance();
        		commDriver.initialize();
        	}
        	catch (ClassNotFoundException cnfe)
        	{
                Logger.write(Logger.CRITICAL, CLASS, "Unable to find CommDriver: " + COMM_DRIVER);
                throw new ClassNotFoundException("Driver '" + COMM_DRIVER + "' is not found in the java.library.path.");
        	}
        	catch (InstantiationException ie)
        	{
                Logger.write(Logger.CRITICAL, CLASS, "Unable to load CommDriver: " + COMM_DRIVER);
                throw new InstantiationException("Unable to load driver '" + COMM_DRIVER);
        	}
        	catch (IllegalAccessException iae)
        	{
                Logger.write(Logger.CRITICAL, CLASS, "Unable to access CommDriver: " + COMM_DRIVER);
                throw new InstantiationException("Unable to access CommDriver '" + COMM_DRIVER);
        	}

            Logger.write(Logger.DEBUG, CLASS, "Serial Port driver loaded: " + COMM_DRIVER);
        } else {
            Logger.write(Logger.DEBUG, CLASS, "Loading Serial Port driver for O/S: " + System.getProperty("os.name"));
        }
            
        try
        {
            for (Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements() ;)
            {
                String                                      portName                    = ((CommPortIdentifier)e.nextElement()).getName();
                if (portName.startsWith("COM") || portName.startsWith("/dev"))
                {
                    if (!ports.contains(portName))
                    {
                        ports.add(portName);
                        portList.append(portName).append(",");
                    }
                } else {
                	Logger.write(Logger.DEBUG, CLASS, "Discarding unrecognized port: " + portName);
                }
            }
        }
        catch (Exception e)
        {
            Logger.write(Logger.MAJOR, CLASS, "Can't find device driver! Error: " + e.getMessage());
            throw new Exception("Can't find device driver! Error: " + e.getMessage());
        }

        if (portList.length() == 0)
        {
        	throw new Exception("Unable to find any available ports on the system!");
        } else {
        	portList.setLength(Math.max(portList.length() - 1, 0)); //  Remove the last comma (safe for cases where nothing is present).
            Logger.write(Logger.DEBUG, CLASS, "Serial Ports List: " + portList.toString());
        }

        return ports;
    }

    public void portOpen() throws Exception, IOException
    {
        try
        {
            Logger.write(Logger.DEBUG, CLASS, "Opening port: " + _serialPortID.getName());
            _serialPort = (SerialPort)_serialPortID.open("SMS Gateway", _serialPortWait);
            _isOpen                                                                     = true;
            
            Logger.write(Logger.DEBUG, CLASS, "Opening I/O stream for port '"+ _serialPortID.getName() + "'.");
            _inStream                                                                   = _serialPort.getInputStream();
            _outStream                                                                  = _serialPort.getOutputStream();

            Logger.write(Logger.INFO, CLASS, "Port '" + _serialPortID.getName() + "' open.");
        }
        catch(PortInUseException piue)
        {
            this.portClose();
            throw new Exception("Port '" + _serialPortID.getName() + "' is in use by another application.");
        }
        catch (Exception e)
        {
            throw new Exception("Port '" + _serialPortID.getName() + "' is broken.");
        }
    }

    public void portClose()
    {
        try
        {
            _outStream.close();
            _inStream.close();
            Logger.write(Logger.DEBUG, CLASS, "I/O stream for port '"+_serialPortID.getName()+ "' closed.");
        }
        catch(Exception se)
        {
        	Logger.write(Logger.INFO, CLASS, "Failed to close I/O stream for port: " + _serialPortID.getName());
        }

        try
        {
            if (_isOpen)
            {
                _serialPort.close();
                _isOpen = false;
                Logger.write(Logger.INFO, CLASS, _serialPortID.getName() + " closed.");
            }
        }
        catch(Exception pe)
        {
        	Logger.write(Logger.INFO, CLASS, "Failed to close port: " + _serialPortID.getName());
        }
    }

    /**
     * Converts the settings from the properties file to Serial Port parameters
     * @param cs
     * @return SerialCommPortParameters
     */
    private SerialCommPortParameters getSerialPortParameters(ConfigStore cs)
    {
        return new SerialCommPortParameters(
                cs.getProperty(Constants.PROP_PORT_ID, Constants.DEF_PORT_ID),
                cs.getProperty(Constants.PROP_PORT_NAME, Constants.DEF_PORT_NAME), 
                cs.getProperty(Constants.PROP_PORT_BAUDRATE, Constants.DEF_PORT_BAUDRATE), 
                cs.getProperty(Constants.PROP_PORT_FLOWCONTROLIN, Constants.DEF_PORT_FLOWCONTROLIN), 
                cs.getProperty(Constants.PROP_PORT_FLOWCONTROLOUT, Constants.DEF_PORT_FLOWCONTROLOUT), 
                cs.getProperty(Constants.PROP_PORT_DATABITS, Constants.DEF_PORT_DATABITS), 
                cs.getProperty(Constants.PROP_PORT_STOPBITS, Constants.DEF_PORT_STOPBITS),
                cs.getProperty(Constants.PROP_PORT_PARITY, Constants.DEF_PORT_PARITY));
    }

    /**
     * Initialize the ports for serial communication
     * @param portProps
     * @throws Exception
     */
    public void portInit(SerialCommPortParameters portProps) throws Exception
    {
        try
        {
            Logger.write(Logger.DEBUG, CLASS, "Initialising port: " + _serialPortID.getName());
            
            _serialPort.setSerialPortParams(portProps.getBaudRate(),portProps.getDatabits(),portProps.getStopbits(),portProps.getParity());
            _serialPort.setFlowControlMode(portProps.getFlowControlIn());
            _serialPort.enableReceiveTimeout(_config.getProperty(Constants.PROP_PORT_TIMEOUT_RECEIVE, Constants.DEF_PORT_TIMEOUT_RECEIVE));
            _serialPort.notifyOnDataAvailable(true);
            _serialPort.notifyOnBreakInterrupt(true);
            _serialPort.addEventListener(this);

            Logger.write(Logger.INFO, CLASS, "Port '" + _serialPortID.getName() + "' initialised.");
        }
        catch(Exception e)
        {
        	this.portClose();
            Logger.write(Logger.INFO, CLASS, "Exception during port initialization: " + e.getMessage());
            throw new Exception("Error: Unable to initialise serial port: " + portProps.getPortName());
        }
    }
        
    public String getPortName()
    {
        return _serialPortName;
    }

    /**
     * Reading the AT commands from the confguration.
     * Note: ConfigStore may take up to a minute to re-read the modified config file.
     */
    private void readAtCommands()
    {
        //PORTWAIT                                                                  = _config.getProperty(Constants.PROP_PORT_TIMEOUT_OPEN, Constants.DEF_PORT_TIMEOUT_OPEN);
        AT_USER_VALUE_ERRORLEVEL                                                    = _config.getProperty(Constants.PROP_MODEM_CMD_ERRORLEVEL, Constants.DEF_MODEM_CMD_ERRORLEVEL);
        AT_USER_VALUE_SIGNALSTRENGTH                                                = _config.getProperty(Constants.PROP_MODEM_CMD_SIGNALSTRENGTH, Constants.DEF_MODEM_CMD_SIGNALSTRENGTH);
        AT_USER_VALUE_GSMSTATUS                                               	    = _config.getProperty(Constants.PROP_MODEM_CMD_GSMSTATUS, Constants.DEF_MODEM_CMD_GSMSTATUS);
        AT_USER_VALUE_NETWORKSTATUS                                 				= _config.getProperty(Constants.PROP_MODEM_CMD_NETWORKSTATUS, Constants.DEF_MODEM_CMD_NETWORKSTATUS);
        AT_USER_VALUE_OPERATOR                                                  	= _config.getProperty(Constants.PROP_MODEM_CMD_OPERATOR, Constants.DEF_MODEM_CMD_OPERATOR);
        AT_USER_VALUE_MANUFACTURER                                                 	= _config.getProperty(Constants.PROP_MODEM_CMD_MANUFACTURER, Constants.DEF_MODEM_CMD_MANUFACTURER);
        AT_USER_VALUE_MESSAGEMODE                                                 	= _config.getProperty(Constants.PROP_MODEM_CMD_MESSAGEMODE, Constants.DEF_MODEM_CMD_MESSAGEMODE);
        AT_USER_VALUE_SETMESSAGE                                                   	= _config.getProperty(Constants.PROP_MODEM_CMD_SETMESSAGE, Constants.DEF_MODEM_CMD_SETMESSAGE);
        AT_USER_VALUE_SMSC                                                       	= _config.getProperty(Constants.PROP_MODEM_CMD_SMSC, Constants.DEF_MODEM_CMD_SMSC);
        AT_USER_VALUE_VALIDITY                                                     	= _config.getProperty(Constants.PROP_MODEM_CMD_VALIDITY, Constants.DEF_MODEM_CMD_VALIDITY);
        AT_USER_VALUE_SEND                                                      	= _config.getProperty(Constants.PROP_MODEM_CMD_SEND, Constants.DEF_MODEM_CMD_SEND);
        AT_USER_VALUE_GET                                                        	= _config.getProperty(Constants.PROP_MODEM_CMD_GET, Constants.DEF_MODEM_CMD_GET);
        AT_USER_VALUE_REMOVE                                                      	= _config.getProperty(Constants.PROP_MODEM_CMD_REMOVE, Constants.DEF_MODEM_CMD_REMOVE);        
    }
    
    /**
     * Initialize the modem
     * 
     * @param mp Serial Port Parameters
     * @throws Exception
     */
    public void modemInit(SerialCommPortParameters mp) throws Exception
    {
        ERROR                                                                       = false;
        Logger.write(Logger.DEBUG, CLASS, "Initialising device on port:" + this.getPortName());
        try
        {
            _dn                                                                     = null;
            _responsePending                                                        = true;
            atCommand                                                               = new String("ATE0\r\n");
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
            //LogManager.write("Setting Error Report on " +  portName,LogHandle.ALL,LogManager.DEBUG);
            _responsePending                                                        = true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_ERRORLEVEL + AT_USER_VALUE_ERRORLEVEL + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Setting error level:" + atCommand);
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
        }
        catch(IOException ioe)
        {
            throw new Exception("Cannot set port IO stream: " + ioe.getLocalizedMessage());
        }
        catch (Exception e)
        {
            throw new Exception("Cannot set error report: " + e.getLocalizedMessage());
        }

        if ((!OK) && (_dn == null))
        {
            throw new Exception("No response from device");
        }
        if ((!OK) && (!ERROR))
        {
            throw new Exception("Unrecognised response from device");
        }
        Logger.write(Logger.INFO, CLASS, "Device on [" + this.getPortName()+ "] initialised.");

        try
        {
            _deviceName                                                             = " ";
            _dn                                                                     = null;
            _responsePending                                                    	= true;
            
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_MANUFACTURER + AT_USER_VALUE_MANUFACTURER+"\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Determining the manufacturer on " + this.getPortName() + ": " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
                        
            if(_dn != null && _dn.trim().matches("(?is).*OK.*"))
            {
                _deviceName                                                         = _dn.substring(0, _dn.indexOf((char)13));
            }
            Logger.write(Logger.INFO, CLASS, "Device: " + _deviceName);
        }
        catch(IOException e)
        {
            throw new Exception("Cannot GET modem MANUFACTURER info.");
        }
        catch(Exception e)
        {
            throw new Exception("Unknown device name.");
        }

        checkSignalStrenght();
        
        try
        {
            _dn                                                                     = null;
            _responsePending                                                    	= true;
            
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_GSMSTATUS + AT_USER_VALUE_GSMSTATUS + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Determining GSM status of modem on " + this.getPortName() + ": " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();

        	// GSM number
        	if (_dn.startsWith(Constants.AT_MODEM_CMD_GSMSTATUS))
            {
                Logger.write(Logger.INFO, CLASS, "GSM Status: " + _dn.substring(7, _dn.indexOf("OK")).trim());
            }
        }
        catch(IOException e)
        {
            throw new Exception("Cannot GET GSM status.");
        }
        catch(Exception e)
        {
            throw new Exception("Unknown GSM status.");
        }

        try
        {
            _dn                                                                     = null;
            _responsePending                                                    	= true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_NETWORKSTATUS + AT_USER_VALUE_NETWORKSTATUS + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Quering Network for status: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();

            _dn                                                                     = null;
            _responsePending                                                    	= true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_NETWORKSTATUS + "?" + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Determining Network status: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();

        	// GSM number
        	if (_dn.startsWith(Constants.AT_MODEM_CMD_NETWORKSTATUS))
            {
                Logger.write(Logger.INFO, CLASS, "Network Status: " + _dn.substring(7, _dn.indexOf("OK")).trim());
            }
        }
        catch(IOException e)
        {
            throw new Exception("Cannot GET Network status.");
        }
        catch(Exception e)
        {
            throw new Exception("Unknown Network status.");
        }

        try
        {
            _dn                                                                     = null;
            _responsePending                                                    	= true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_NETWORKSTATUS + AT_USER_VALUE_NETWORKSTATUS + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Quering Network operators: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();

            _dn                                                                     = null;
            _responsePending                                                    	= true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_NETWORKSTATUS + "?" + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Determining Network operator: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();

        	// GSM number
        	if (_dn.startsWith(Constants.AT_MODEM_CMD_NETWORKSTATUS))
            {
                Logger.write(Logger.INFO, CLASS, "Network operator: " + _dn.substring(7, _dn.indexOf("OK")).trim());
            }
        }
        catch(IOException e)
        {
            throw new Exception("Cannot GET Network operator.");
        }
        catch(Exception e)
        {
            throw new Exception("Unknown Network operator.");
        }

        try
        {
            _responsePending                                                        = true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_MESSAGEMODE + AT_USER_VALUE_MESSAGEMODE + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Setting message to text mode: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
        }
        catch (Exception e)
        {
            throw new Exception ("Cannot set message to 'text mode'.");
        }
        Logger.write(Logger.INFO, CLASS, "Modem '" + this.getPortName().substring(8) + "' initialized.");   
        
        removeAllSMS();
    }
    
    public SerialPort getPort()
    {
    	return _serialPort;
    }
    
    public void setModemONLine(String smsc)throws Exception
    {
        ERROR                                                                       = false;
        try
        {
            _responsePending                                                        = true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_SETMESSAGE + AT_USER_VALUE_SETMESSAGE + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Setting modem '"+this.getPortName().substring(8)+"' online: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
        }
        catch(IOException e)
        {
            throw new Exception("Cannot set modem online");
        }


        if ((smsc.trim()).length() > 10 )
        {
            Logger.write(Logger.DEBUG, CLASS, "Setting SMSC to ["+smsc+"].");
            try
            {
                _responsePending                                                    = true;
                atCommand                                                           = this.AT_USER_VALUE_SMSC + "=\""+smsc+"\"\r\n";
                Logger.write(Logger.DEBUG, CLASS, "Setting SMSC: " + atCommand);            
               _serialPort.getOutputStream().write(atCommand.getBytes());
                sleep();
            }
            catch(IOException e)
            {
                throw new Exception("Cannot set SMSC");
            }
        }
        Logger.write(Logger.INFO, CLASS, "Modem '" + _deviceName + "' is online.");        
    }

    /**
     * Setting relative validity index period by applying PDU format
     * 
     * @throws Exception
     */
    public void setValidity() throws Exception
    {
        Logger.write(Logger.DEBUG, CLASS, "Modem Manager - Configuring SMS Message properties.");
        int                                             vdtIndex                    = _config.getProperty(Constants.PROP_PROVIDER_SMS_VALIDITY, Constants.DEF_PROVIDER_SMS_VALIDITY);
        int                                             status;

        if (_config.getProperty(Constants.PROP_PROVIDER_SMS_STATUS, Constants.DEF_PROVIDER_SMS_STATUS))
        {
            status                                                                  = 49;
        }
        else
        {
            status                                                                  = 17;
        }

        try
        {
            _responsePending                                                        = true;
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_VALIDITY + AT_USER_VALUE_VALIDITY + "=\"" + status + "," + vdtIndex + ",0,0\"\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Setting validity: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
        }
        catch (IOException e)
        {
            throw new Exception("Cannot set the SMS vdtIndex period.");
        }
        
        if (_dn!=null)
        {
            Logger.write(Logger.DEBUG, CLASS, "Device '" + _deviceName + "' ready.");
        }
        else
        {
            Logger.write(Logger.DEBUG, CLASS, "Device on port '"+this.getPortName()+"' ready.");
        }
    }

//    public void setModemOFFLine()throws Exception
//    {
//        try
//        {
//            atCommand = MESSAGEMODE + "\r\n";
//            outputStream.write(atCommand.getBytes());
//            sleep();
//        }
//        catch(IOException e)
//       {
//           throw new Exception("Cannot set modem off line");
//        }
//    }

    public boolean sendMessage(SMSResponse sms) throws Exception
    {
        smsResponse                                                                 = sms;
        try
        {
            long                                        timeToSend                  = System.currentTimeMillis();
            _sending                                                                = true;
            _sent                                                                    = false;
            ERROR                                                                   = false;
            
            _responsePending                                                        = true;
            atCommand                                                               = new String("AT" + Constants.AT_MODEM_CMD_SEND + AT_USER_VALUE_SEND + "=\"" + sms.getNumber() + "\"\r\n");
            Logger.write(Logger.DEBUG, CLASS, "Sending message: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
            
            atCommand                                                               = sms.getMessage();
            _serialPort.getOutputStream().write(atCommand.getBytes());
            
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {}
            
            char CTRL_Z                                                             = 26;
            atCommand                                                               = CTRL_Z + "\r";
            _serialPort.getOutputStream().write(atCommand.getBytes());

            int timeout                                                             = 0;
            while (!getSENT_OK())
            {
                if (timeout <= _config.getProperty(Constants.PROP_MESSAGE_SEND_TIMEOUT, Constants.DEF_MESSAGE_SEND_TIMEOUT))
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){}
                    timeout++;
                }
                else
                {
                    _sending=false;
                    
                    if (_sending_attempt < _config.getProperty(Constants.PROP_MESSAGE_SEND_ATTEMPTS, Constants.DEF_MESSAGE_SEND_ATTEMPTS))
                    {
                        _sending_attempt++;
                        Logger.write(Logger.MINOR, CLASS, "Acknowledge not received within " + _config.getProperty(Constants.PROP_MESSAGE_SEND_TIMEOUT, Constants.DEF_MESSAGE_SEND_TIMEOUT) + " seconds. Executing attempt: " + _sending_attempt);
                        sendMessage(sms);
                    }
                    else if (_config.getProperty(Constants.PROP_MESSAGE_SAVE_FAILED, Constants.DEF_MESSAGE_SAVE_FAILED))
                    {
                        _sending_attempt = 0;
                        Logger.write(Logger.MINOR, CLASS, "All " + _config.getProperty(Constants.PROP_MESSAGE_SEND_TIMEOUT, Constants.DEF_MESSAGE_SEND_TIMEOUT) + " attempts to sent the message failed. Giving up and saving in 'Failed' items.");
                        saveMessage(false);
                        return false;
                    }
                }
            }
            
            if (_config.getProperty(Constants.PROP_MESSAGE_SAVE_SENT, Constants.DEF_MESSAGE_SAVE_SENT))
            {
                Logger.write(Logger.DEBUG, CLASS, "Message to " + sms.getNumber() + " sent on " + _sending_attempt + " attempts for total of " + (System.currentTimeMillis() - timeToSend) + " ms. Saving in 'Sent' items.");
                saveMessage(true);
            }
            _sending=false;
            return true;
        }
        catch(Exception e)
        {
            Logger.write(Logger.MAJOR, CLASS, "Error: Port or Device not available.");
            return false;
        }
    }

    private void saveMessage(boolean status)
    {
        try
        {
            DAO                                             dao                         = DAOProvider.getDAO();
            dao.saveSentSMS(smsResponse.getNumber(), smsResponse.getMessage(), status);
            Logger.write(Logger.INFO, CLASS, "Saved SMS Message from: " + smsResponse.getNumber());
        }
        catch (Exception e)
        {
            Logger.write(Logger.MAJOR, CLASS, "Failed to save SMS Message from: " + smsResponse.getNumber());
        }
    }

    public List<SMSMessage> getSIMMessages()
    {
        _memMess                                                                        = new LinkedList<SMSMessage>();

        checkSignalStrenght();
        
        try
        {
            _responsePending                                                        	= true;
            atCommand                                                                   = "AT" + Constants.AT_MODEM_CMD_GET + AT_USER_VALUE_GET +"\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Reading SIM memory for new messages: " + atCommand);
            _serialPort.getOutputStream().write(atCommand.getBytes());

            // Gives specified time from either start of receiving (if no messages arrive at all), or
            // since the last message has arrived, ensuring all messages from the SIM have been collected.
            try
            {
                int                                             lastCheckSize           = -1;
                for (int t=0; t < _config.getProperty(Constants.PROP_MESSAGE_RECEIVE_TIMEOUT, Constants.DEF_MESSAGE_RECEIVE_TIMEOUT); t++)
                {
                    if (_memMess.size() != lastCheckSize)
                    {
                        lastCheckSize = _memMess.size();
                        t = 0;
                    } else {
                        Thread.sleep(1000);
                    }
                }
            }
            catch (InterruptedException e)
            {
                Logger.write(Logger.MINOR, CLASS, "Exception during waiting for message checking to complete.");
            }

            if (_memMess.size() == 1)
            {
                Logger.write(Logger.DEBUG, CLASS, "Found " + _memMess.size() + " message on SIM");
            }
            else
            {
                Logger.write(Logger.DEBUG, CLASS, "Found " + _memMess.size() + " messages on SIM");
            }
        }
        catch(IOException e)
        {
            Logger.write(Logger.MAJOR, CLASS, "Cannot read SIM memory.");
        }
        
        return _memMess;
    }

    public void removeAllSMS() throws Exception
    {
        Logger.write(Logger.DEBUG, CLASS, "Removing ALL SMS Messages from SIM memory.");
        try
        {
            _responsePending                                                        	= true;
            atCommand                                                                   = "AT" + Constants.AT_MODEM_CMD_REMOVE + AT_USER_VALUE_REMOVE + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Removing all messages from modem: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
        }
        catch(Exception e)
        {
            throw new Exception("Cannot remove SMS Messages.");
        }

    }

    public void removeSMS(int index)
    {
        Logger.write(Logger.DEBUG, CLASS, "Removing SMS Messages with index: " + index);
        try
        {
            _responsePending                                                        	= true;
            atCommand                                                                   = "AT" + Constants.AT_MODEM_CMD_REMOVE + "="+index+",3\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Removing message from modem: " + atCommand);            
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();
        }
        catch(Exception e)
        {
            Logger.write(Logger.MINOR, CLASS, "Cannot remove SMS Message with index " +index);
            //throw new Exception("Cannot remove SMS Message.");
        }

    }

    public void serialEvent(SerialPortEvent evt)
    {
        Logger.write(Logger.DEBUG, CLASS, "Event received.. ");
        String                                          str1                            = null;
        int												retries							= 3;
        
        switch(evt.getEventType())
        {
            case SerialPortEvent.DATA_AVAILABLE:
                int                                     ch                              = 0;
                StringBuffer                            buffer                          = new StringBuffer();
                while (ch != -1 || retries > 0)
                {
                    try
                    {
                        ch                                                              = _serialPort.getInputStream().read();
                        if (ch == -1 )
                        {
                        	if (retries-- > 0)
                        	{
                        		Thread.sleep(100);
                                Logger.write(Logger.DEBUG, CLASS, "Check for continuosly arriving message: " +retries+ "/3.");
                        	}
                        	else
                        	{
                        		break;
                        	}
                        } else {
                        	buffer.append((char)ch);
                        }
                    }
                    catch(Exception e)
                    {
                        Logger.write(Logger.MAJOR, CLASS, "Error reading input: "+e);
                        break;
                    }
                }
                str1                                                                    = (buffer.toString()).trim();
                Logger.write(Logger.DEBUG, CLASS, "Response:\n" +str1.trim()+ "[END]");
                
                if (!_testMessage) {
                	parseBuffer(str1);
                }
        }
    }

    private void parseBuffer(String s)
    {
        _dn                                                                             = s;
        _responsePending                                                        		= false;
        
        // Status OK. The check used to do this as well [ || s.trim().matches("(?is).*OK.*")]
        if (s.trim().startsWith("OK") || s.trim().matches("(?is).*OK.*") || s.trim().startsWith("ATE"))
        {
            Logger.write(Logger.DEBUG, CLASS, "Command processed successfully: " + s);
        	OK																			= true;
        }
        
        else if (s.startsWith("OK") && _sending)
        {
            Logger.write(Logger.DEBUG, CLASS, "Message sent:" + s);
            setSENT_OK(true);
            OK                                                                          = true;
            return;
        }
        
        else if (s.trim().matches(">"))
        {
            sendMsg                                                                     = true;
        }
        
        // Message sent successfully to the SMSC
        else if(s.startsWith(Constants.AT_MODEM_CMD_SEND))
        {
            smsAcknowledge();
            setSENT_OK(true);
            _sent                                                                        = true;
            return;
        }
                
        // Indication that new message has arrived
        else if(s.startsWith(Constants.AT_MODEM_CMD_GET))
        {
            Logger.write(Logger.DEBUG, CLASS, "New Message received with CMTI: " + s);
            
            // It is possible that we get more than one message at a time. Each message starts with "+CMGL: "
            // which we can use to separate them into individual ones
        	String[] messages = s.split("\\" + Constants.AT_MODEM_CMD_GET + ": ");
        	
            Logger.write(Logger.DEBUG, CLASS, "Number of message: " + messages.length);

        	for (int i=0; i<messages.length; i++)
        	{
                if (messages[i] != null && !"".equals(messages[i].trim()))
                {
                	SMSMessage 							newMessage 						= new SMSMessage(messages[i]);
                    _memMess.add(newMessage);
                    
                    // We've received the message from the modem, hence we remove it from there.
                    this.removeSMS(newMessage.getIndex());
                }
        	}

        	return;
        }
        
        // Direct output of the short message
        else if(s.startsWith("+CMT:"))
        {
            Logger.write(Logger.DEBUG, CLASS, "New Message received with CMT: " + s);
        	SMSMessage									newMesage						= new SMSMessage(s);
            _memMess.add(newMesage);
            return;
        }
        
        // Status from the recipient for successfully received message
        else if(s.startsWith("+CDS"))
        {
            statusReceived(s);
            return;
        }
        
        // Direct output of the cell broadcast message
        else if(s.startsWith("+CBM:"))
        {
            OK                                                                          = true;
            return;
        }
        
        //List of supported <stats>
        //0 ”REC UNREAD”: received unread messages (default)
        //1 ”REC READ”: received read messages
        //2 ”STO UNSENT”: stored unsent messages
        //3 ”STO SENT”: stored sent messages
        //4 ”ALL”: all messages
        else if(s.startsWith(Constants.AT_MODEM_CMD_SEND))
        {
            Logger.write(Logger.DEBUG, CLASS, "Received SMS Message from modem: " + s);

        	SMSMessage									newMesage						= new SMSMessage(s);
            _memMess.add(newMesage);

            //Some times there are messages on the actual modem, not on the SIM
            //This will retrieve them as separate messages. Normally this shouldn't happen.
            //
            //String[] pieces = s.split("\\+CMGL: ");
            //for (int i=0; i<pieces.length;i++)
            //{
            //    memMess.add(pieces[i]);
            //    LogManager.write("\n***** Start No. " +i+ " *****\n" +pieces[i]+ "\n***** Stop No. " +i+ " ****", LogHandle.ALL,LogManager.DEBUG);
            //}


            //Determining the message index, then deleting the message from the SIM.
            //String indx = s;
            //int in[]= new int[9];
            //in[0]=indx.indexOf(":");
            //in[1]=indx.indexOf(",", in[0]+1); // index
            //in[7]=indx.indexOf("#",in[6]+1);
            //in[8]=indx.indexOf("#",in[7]+1);
            //indx = indx.substring(in[0]+1,in[1]);
            //removeSMS(indx);

            return;
        }
        
        // List of supported services
        else if(s.startsWith("+CSMS:"))
        {
            OK                                                                          = true;
            return;
        }

        else if(s.startsWith(Constants.AT_MODEM_CMD_VALIDITY))
        {
            Logger.write(Logger.DEBUG, CLASS, "Message: " + s);
            OK                                                                          = true;
            return;
        }
        
        // Error response
        else if (s.startsWith("+CMS ERROR:") || s.startsWith("+CME ERROR:") || s.indexOf("ERROR") != -1)
        {
            //LogManager.write("Error string: " +s,LogHandle.ALL,LogManager.DEBUG);
            parseSMSError(s);
            OK                                                                          = false;
            ERROR                                                                       = true;
            return;
        }

        else if (s.indexOf("OK") != -1)
        {
            OK                                                                          = true;
        }

        else
        {
            Logger.write(Logger.MINOR, CLASS, "Unknown response from modem: " + s);
        }
    }

    private void smsAcknowledge()
    {
        Logger.write(Logger.DEBUG, CLASS, "Message sent.");
        long                                            p                               = System.currentTimeMillis();
        Date                                            date                            = new Date(p);
        String                                          time                            = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)+ ", " +
                                                                                            DateFormat.getTimeInstance(DateFormat.LONG).format(date);
        smsResponse.setStatus(SMSResponse.INFO);
        smsResponse.setErrorMessage("SMS acknowledged at " + time);
        //LogManager.write("Modem Manager - SMS acknowledged at " +time, LogHandle.ALL,LogManager.DEBUG);
    }

    private void statusReceived(String msg)
    {
        Logger.write(Logger.DEBUG, CLASS, "Status Received: " + msg);
    }

    private void parseSMSError(String str)
    {
        ERROR                                                                           = false;
        String                                          e                               = "Unknown error...";
        try
        {
            // Check if error
            int                                         x                               = str.indexOf("ERROR:");
            int                                         z                               = str.indexOf('+', x-5);
            int                                         y                               = 0;
            if ( z != -1)
            {
                y                                                                       = str.indexOf('\n', x);
                if ( y == -1 )
                    e                                                                   = (str.substring(z).trim());
                else
                    e                                                                   = (str.substring(z, y)).trim();
            }
            e                                                                           = ModemErrorCodes.getStringValue(e);
            smsResponse.setStatus(SMSResponse.ERROR);
            if (e.equals(null))
            {
                e                                                                       = "Error port ";
            }
            smsResponse.setErrorMessage(e + " " + this.getPortName() + " device " + _deviceName);
            Logger.write(Logger.DEBUG, CLASS, "Error: " + e);

        }
        catch(Exception ex)
        {
            Logger.write(Logger.MAJOR, CLASS, "Unknown Error: " + e);
            return;
        }
    }

    private void sleep() throws TimeLimitExceededException
    {
        int												dataReceiveTimeout				= _config.getProperty(Constants.PROP_PORT_TIMEOUT_RECEIVE, Constants.DEF_PORT_TIMEOUT_RECEIVE) * 100;
        
        for (int t=0; t < dataReceiveTimeout; t++)
        {
        	try { Thread.sleep(10); } catch (InterruptedException e){}

            if(!_responsePending || sendMsg)
            {
                _responsePending                                                        = false;
                sendMsg                                                                 = false;
                Logger.write(Logger.DEBUG, CLASS, "Modem responded in " + (t * 10) + " ms.");
                return;
            }
        }
        Logger.write(Logger.MINOR, CLASS, "Modem failed to respond within the given by '" + Constants.PROP_PORT_TIMEOUT_RECEIVE + "' time: " + 
        		_config.getProperty(Constants.PROP_PORT_TIMEOUT_RECEIVE, Constants.DEF_PORT_TIMEOUT_RECEIVE));
        throw new TimeLimitExceededException("Timeout waiting for modem to respond.");
    }
    
    private void checkSignalStrenght()
    {
        try
        {
            String		signalStrengthStr											= "";
            int			signalStrengthInt											= 0;
            _dn                                                                     = null;
            _responsePending                                                    	= true;
            
            atCommand                                                               = "AT" + Constants.AT_MODEM_CMD_SIGNALSTRENGTH + AT_USER_VALUE_SIGNALSTRENGTH + "\r\n";
            Logger.write(Logger.DEBUG, CLASS, "Determining the signal strength on '" + this.getPortName() + "': " + atCommand);
            _serialPort.getOutputStream().write(atCommand.getBytes());
            sleep();

            try
            {
            	signalStrengthStr = _dn.substring(6, _dn.indexOf("OK")).trim();
                Logger.write(Logger.MAJOR, CLASS, "Raw Signal strength: " + signalStrengthStr);
            	signalStrengthInt = Integer.valueOf(signalStrengthStr.substring(0, 2));
            }
            catch (NumberFormatException nfe)
            {
                Logger.write(Logger.MAJOR, CLASS, "Unable to determine Signal strength!");
                return;
            }
                        
            if (signalStrengthInt < 1)
            {
                Logger.write(Logger.MAJOR, CLASS, "Signal strength (" + signalStrengthStr + "): ______");
                return;
            }
            if (signalStrengthInt == 1)
            {
                Logger.write(Logger.MINOR, CLASS, "Signal strength(" + signalStrengthStr + "): *_____");
                return;
            }
            if (signalStrengthInt < 10)
            {
                Logger.write(Logger.MINOR, CLASS, "Signal strength(" + signalStrengthStr + "): _*____");
                return;
            }
            if (signalStrengthInt < 15)
            {
                Logger.write(Logger.INFO, CLASS, "Signal strength(" + signalStrengthStr + "): __*___");
                return;
            }
            if (signalStrengthInt < 20)
            {
                Logger.write(Logger.INFO, CLASS, "Signal strength(" + signalStrengthStr + "): ___*__");
                return;
            }
            if (signalStrengthInt < 30)
            {
                Logger.write(Logger.INFO, CLASS, "Signal strength(" + signalStrengthStr + "): ____*_");
                return;
            }
            if (signalStrengthInt < 99)
            {
                Logger.write(Logger.INFO, CLASS, "Signal strength(" + signalStrengthStr + "): _____*");
                return;
            }
            if (signalStrengthInt == 99)
            {
                Logger.write(Logger.MAJOR, CLASS, "Signal strength(" + signalStrengthStr + "): UNKNOWN");
                return;
            }
        }
        catch(IOException e)
        {
            Logger.write(Logger.MAJOR, CLASS, "Failed to determine Signal strength!");
        }
        catch(Exception e)
        {
            Logger.write(Logger.MAJOR, CLASS, "Failed to determine Signal strength!");
        }

    }

    public void setSending(boolean snd)
    {
        _sending = snd;
    }

    public boolean getSeding()
    {
        return _sending;
    }

    public void setSENT_OK(boolean sent)
    {
        SENT_OK = sent;
    }

    public boolean getSENT_OK()
    {
        return SENT_OK;
    }
}
