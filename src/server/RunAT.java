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

import log.Logger;
import modem.Modem;
import modem.SerialCommPortParameters;
import utils.ConfigStore;
import utils.Constants;

public class RunAT {

    private final static String                             CLASS                       = SMSGateway.class.getSimpleName();
    
    Modem                                               	_modem                      = null;

    public void main(String[] args) {
    	
    	String												atCommand					= args[0];
        // Set the Logger
        try
        {
            Logger.setLog(ConfigStore.getInstance().getFileLog());
            Logger.setLogOutput(ConfigStore.getInstance().getProperty("log.output", Constants.DEF_LOG_OUTPUT));
            Logger.setLogLevel(ConfigStore.getInstance().getProperty("log.level", Constants.DEF_LOG_LEVEL));
        }
        catch(Exception exit_log)
        {
            System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + 
                    "     Error setting Logger: file[" + ConfigStore.getInstance().getFileLog() + 
                    "], output[" + ConfigStore.getInstance().getProperty("log.output", Constants.DEF_LOG_OUTPUT) + 
                    "], level[" + ConfigStore.getInstance().getProperty("log.level", Constants.DEF_LOG_LEVEL) + "].");
            System.out.println(Logger.getFormattedTimestamp() + "CRITICAL " + CLASS + "     Exit.");
            System.exit(-1);
        }

        // Setting the connection for sending SMS Messages
        //lanConnection = Boolean.valueOf(prps.getProperty("LAN", "true")).booleanValue();

        Logger.write(Logger.DEBUG, CLASS, "");
        Logger.write(Logger.DEBUG, CLASS, "Starting application.");
        Logger.write(Logger.DEBUG, CLASS, "Home directory set to: " + ConfigStore.getInstance().getDirHome());
                
        try
        {
            _modem                                                                   	= new Modem();
            _modem.modemInit(new SerialCommPortParameters(ConfigStore.getInstance()));
        }
        catch (Exception e)
        {
            Logger.write(Logger.CRITICAL, CLASS, "Failed to start the modem: " + e.toString());
            _modem.portClose();
            System.exit(1);
        }

        _modem.runAT(atCommand);
    }
}
