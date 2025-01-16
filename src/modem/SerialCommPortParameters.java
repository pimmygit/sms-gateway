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

import javax.comm.SerialPort;

import utils.ConfigStore;
import utils.Constants;

import java.util.Properties;
import java.io.Serializable;

public class SerialCommPortParameters implements Serializable
{

    private static final long                               serialVersionUID            = -6098681826387628204L;

    private String                                          _modemID;
    private String                                          _portName;
    private int                                             _baudRate;
    private int                                             _flowControlIn;
    private int                                             _flowControlOut;
    private int                                             _databits;
    private int                                             _stopbits;
    private int                                             _parity;
    
    /**
     * Default constructor
     * 
     * @param portName
     */
    public SerialCommPortParameters(String portName)
    {
        _modemID                                                                        = Constants.PROP_PORT_ID;
        _portName                                                                       = portName;
        _baudRate                                                                       = 9600;
        _flowControlIn                                                                  = SerialPort.FLOWCONTROL_RTSCTS_IN;
        _flowControlOut                                                                 = SerialPort.FLOWCONTROL_RTSCTS_OUT;
        _databits                                                                       = SerialPort.DATABITS_8;
        _stopbits                                                                       = SerialPort.STOPBITS_1;
        _parity                                                                         = SerialPort.PARITY_NONE;
    }

    /**
     * Serial port parameters constructor
     * 
     * @param portId            Port id (where we use more than one port at the same time)
     * @param portName          Port name
     * @param baudRate          Baud rate
     * @param flowControlIn     Flow control in
     * @param flowControlOut    Flow control out
     * @param dataBits          Data bits
     * @param stopBits          Stop bits
     * @param parity            Parity
     */
    public SerialCommPortParameters(String portId, String portName, int baudRate, int flowControlIn, int flowControlOut, String dataBits, String stopBits, String parity)
    {
        _modemID                                                                        = portId;
        _portName                                                                       = portName;
        _baudRate                                                                       = baudRate;
        _flowControlIn                                                                  = resolveFlowControl(flowControlIn);
        _flowControlOut                                                                 = resolveFlowControl(flowControlOut);
        _databits                                                                       = resolveDatabits(dataBits);
        _stopbits                                                                       = resolveStopbits(stopBits);
        _parity                                                                         = resolveParity(parity);
    }
  
    public SerialCommPortParameters(ConfigStore props)
    {
        _modemID                                                                        = props.getProperty(Constants.PROP_PORT_ID, Constants.DEF_PORT_ID);
        _portName                                                                       = props.getProperty(Constants.PROP_PORT_NAME, Constants.DEF_PORT_NAME);
        _baudRate                                                                       = props.getProperty(Constants.PROP_PORT_BAUDRATE, Constants.DEF_PORT_BAUDRATE);
        _flowControlIn                                                                  = props.getProperty(Constants.PROP_PORT_FLOWCONTROLIN, Constants.DEF_PORT_FLOWCONTROLIN);
        _flowControlOut                                                                 = props.getProperty(Constants.PROP_PORT_FLOWCONTROLOUT, Constants.DEF_PORT_FLOWCONTROLOUT);
        _databits                                                                       = resolveDatabits(props.getProperty(Constants.PROP_PORT_DATABITS, Constants.DEF_PORT_DATABITS));
        _stopbits                                                                       = resolveStopbits(props.getProperty(Constants.PROP_PORT_STOPBITS, Constants.DEF_PORT_STOPBITS));
        _parity                                                                         = resolveParity(props.getProperty(Constants.PROP_PORT_PARITY, Constants.DEF_PORT_PARITY));
    }
    
    public String getModemID()
    {
        return _modemID;
    }
    
    public void setPortName(String pn)
    {
        _portName = pn;
    }
        
    public String getPortName()
    {
        return _portName;
    }
    
    public void setBaudRate(String br)
    {
        _baudRate = Integer.parseInt(br);
    }
    
    public int getBaudRate()
    {
        return _baudRate;
    }
  
    private int resolveFlowControl(int flowControl) 
    {
	    if (flowControl == 8) 
	    {
	    	// Xon/Xoff
	        return SerialPort.FLOWCONTROL_XONXOFF_OUT;
	    }
	    else if (flowControl == 4) 
	    {
	    	// Xon/Xoff Local
	        return SerialPort.FLOWCONTROL_XONXOFF_IN;
	    }
	    else if (flowControl == 1) 
	    {
	    	// RTS/CTS In
	        return SerialPort.FLOWCONTROL_RTSCTS_IN;
	    }
	    else if (flowControl == 2) 
	    {
	    	// RTS/CTS Out
	        return SerialPort.FLOWCONTROL_RTSCTS_OUT;
	    }
	    else if (flowControl == 0)
	    {
	    	// None
	        return SerialPort.FLOWCONTROL_NONE;
	    }
	    return 0;
    }
      
    public void setFlowControlIn(int fc)
    {
        _flowControlIn = fc;
    }
    
    public int getFlowControlIn()
    {
        return _flowControlIn;
    }
    
    public void setFlowControlOut(int fc)
    {
        _flowControlOut = fc;
    }

    public int getFlowControlOut()
    {
        return _flowControlOut;
    }
 
    private int resolveDatabits(String databits) 
    {        
	    if (databits.equals("8")) 
	    {
	        return SerialPort.DATABITS_8;
	    }
	    else if (databits.equals("6")) 
	    {
	        return SerialPort.DATABITS_6;
	    }
	    else if (databits.equals("7")) 
	    {
	        return SerialPort.DATABITS_7;
	    }
	    else if (databits.equals("5")) 
	    {
	        return SerialPort.DATABITS_5;
	    }
	    else 
	    {
	        return SerialPort.DATABITS_8;
	    }
    }
 
    public int getDatabits() 
    {
        return _databits;
    }

    public int resolveStopbits(String stopbits) 
    {
	    if (stopbits.equals("1.5")) 
	    {
	        return SerialPort.STOPBITS_1_5;
	    }
	    else if (stopbits.equals("2")) 
	    {
	        return SerialPort.STOPBITS_2;
	    }
	    else
	    {
	        return SerialPort.STOPBITS_1;
	    }
    }

    public int getStopbits() 
    {
	    return _stopbits;
    }

    public int resolveParity(String parity) 
    {
	    if (parity.equals("Even")) 
	    {
	        return SerialPort.PARITY_EVEN;
	    }
	    else if (parity.equals("Odd")) 
	    {
	        return SerialPort.PARITY_ODD;
	    }
	    else if (parity.equals("Mark")) 
	    {
	        return SerialPort.PARITY_MARK;
	    }
	    else if (parity.equals("Space")) 
	    {
	        return SerialPort.PARITY_SPACE;
	    }
	    else 
	    {
	        return SerialPort.PARITY_NONE;
	    }    
    }

    public int getParity() 
    {
	    return _parity;
    }
    
    public Properties getProperties()
    {
	    Properties props = new Properties();
	    props.setProperty(Constants.PROP_PORT_ID, _modemID);
	    props.setProperty(Constants.PROP_PORT_NAME, _portName);
	    props.setProperty(Constants.PROP_PORT_BAUDRATE,Integer.toString(_baudRate));
	    props.setProperty(Constants.PROP_PORT_FLOWCONTROLIN,Integer.toString(_flowControlIn));
	    props.setProperty(Constants.PROP_PORT_FLOWCONTROLOUT,Integer.toString(_flowControlOut));
	    props.setProperty(Constants.PROP_PORT_DATABITS,Integer.toString(_databits));
	    props.setProperty(Constants.PROP_PORT_STOPBITS,Integer.toString(_stopbits));
	    props.setProperty(Constants.PROP_PORT_PARITY,Integer.toString(_parity));
	    return props;
    }
}
