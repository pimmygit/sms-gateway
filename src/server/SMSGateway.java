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

import java.util.LinkedList;
import java.util.List;

import log.Logger;
import modem.Modem;
import modem.SerialCommPortParameters;
import sms.SMSMessage;
import utils.ConfigStore;
import utils.Constants;

public class SMSGateway implements Constants, Runnable {
    
    private final static String                             CLASS                       = SMSGateway.class.getSimpleName();
    
    private ConfigStore                                 	_config                     = ConfigStore.getInstance();

    Modem                                               	_modem                      = null;
    SMSProcessor											_processor					= null;
    SAFQueue												_queue						= null;
    
    // These options to halt the threads are mainly for debugging purposes. Properties to manage the threads are not revealed in the property files.
    private boolean											_safHalted					= _config.getProperty(Constants.PROP_QUEUE_THREAD_HALTED, Constants.DEF_QUEUE_HALTED);
    private boolean											_processingHalted			= _config.getProperty(Constants.PROP_SMSG_PROCESSING_HALTED, Constants.DEF_SMSG_PROCESSING_HALTED);

    private int												_timeoutStop				= _config.getProperty(Constants.PROP_MESSAGE_CHECK_INTERVAL, Constants.DEF_MESSAGE_CHECK_INTERVAL);
    private Thread                                      	_worker                     = null;
    private boolean											_run						= false;

    // Create the Main Frame for the SMS GUI
    public SMSGateway()
    {
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
        
        /*
         * Test the SAF Queue
         * --------------------
        for (int i=0; i < 10; i++)
        {
            safQueue.add("Test message " + i);
        }
		*/
        
        try
        {
            _modem                                                                   = new Modem();
            _modem.modemInit(new SerialCommPortParameters(ConfigStore.getInstance()));
        }
        catch (Exception e)
        {
            Logger.write(Logger.CRITICAL, CLASS, "Failed to start the modem: " + e.toString());
            _modem.portClose();
            System.exit(1);
        }
        
        if (!_safHalted) {
            _queue																	= new SAFQueue();
        }
        if (!_processingHalted) {
        	_processor																= new SMSProcessor(_queue);
        }
        
        // Handle Ctrl+C program termination
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    Logger.write(Logger.INFO, CLASS, "Shutting down..");
                    _modem.portClose();
                    Logger.write(Logger.INFO, CLASS, "Bye!");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        if (_worker == null)
        {
        	_run																	= true;
            _worker 																= new Thread(this);
            _worker.start();
        }
    }

    public void run()
    {
        Logger.write(Logger.INFO, CLASS, "Starting periodic SMS checking.");
        
        int											    timeSleep					= 0;
        List<SMSMessage>								simMessages					= new LinkedList<SMSMessage>();
                
        while (_run)
        {
            Logger.write(Logger.INFO, CLASS, "Checking for newly arrived messages on modem: " + _modem.getPortName());
            
            _timeoutStop															= _config.getProperty(Constants.PROP_SMSG_MAIN_TIMEOUT_STOP, Constants.DEF_SMSG_MAIN_TIMEOUT_STOP);
            _processingHalted														= _config.getProperty(Constants.PROP_SMSG_PROCESSING_HALTED, Constants.DEF_SMSG_PROCESSING_HALTED);
            timeSleep																= _config.getProperty(Constants.PROP_MESSAGE_CHECK_INTERVAL, Constants.DEF_MESSAGE_CHECK_INTERVAL);
            simMessages																= _modem.getSIMMessages();
            
            Logger.write(Logger.INFO, CLASS, "Found " + simMessages.size() + " messages from the modem.");

            // Check if any messages has arrived and if not we can sleep the specified in the properties interval.
            // If messages has arrived, dont sleep, but check immediately for more - we might have a burst of them.
            // TODO: Implement flood sending - block numbers that are trying to flood the gateway.
            if (simMessages.size() > 0)
            {
            	if (!_safHalted) {
            		_queue.addAll(simMessages);
            	} else {
                    Logger.write(Logger.INFO, CLASS, "SAF Queue has been halted. Ignoring all messages received from the mmodem.");
            	}
                
                // Wake up the processor if it is sleeping
                if (!_processingHalted && _processor.isStopped())
                {
                	_processor.start();
                } else {
                    Logger.write(Logger.INFO, CLASS, "SMS Processing has been halted. Queue contains " + _queue.size() + " messages.");
                }
                
                try { Thread.sleep(10); } catch (InterruptedException e) {};		// Give the modem a chance to receive another message before the next check.
            } else {
            	// Nothing to discard found yet. lets wait a second...
                Logger.write(Logger.DEBUG, CLASS, "Sleeping the specified by property '" + Constants.PROP_MESSAGE_CHECK_INTERVAL + "' interval: " + timeSleep + " seconds.");
            	try { Thread.sleep(1000 * timeSleep); } catch (Exception e) {}
            }
        }
        
        _processor																	= null;
        _worker 																	= null;
        Logger.write(Logger.INFO, CLASS, "SMS periodic check stopped.");
    }
    
    public void stop()
    {
    	_run																		= false;
    	_processor.stop();
    	
    	for (int i=0; i<_timeoutStop; i++)
    	{
    		if (!_processor.isStopped() && _worker != null)
    		{
    			try { Thread.sleep(1000); } catch (InterruptedException e) {};
    		} else {
    			return;
    		}
    	}
    	
        Logger.write(Logger.MINOR, CLASS, "Timeout wile waiting for the thread to stop on modem: " + _modem.getPortName());
    }
   
    public static void main(String[] args)
    {
        new SMSGateway();
    }
}
