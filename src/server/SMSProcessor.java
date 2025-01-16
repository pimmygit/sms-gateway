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

import java.util.Date;
import java.util.Map;

import log.Logger;
import utils.ConfigStore;
import utils.Constants;

import sms.SMSMessage;

/*
 * Process any SMS messages stored in the SAF Queue
 */
public class SMSProcessor implements Runnable
{
    private static final String                         CLASS                       = SMSProcessor.class.getSimpleName();
    
    private SAFQueue									_queue						= null;
    private SMSActionCacheStore							_actionCache				= SMSActionCacheStore.getInstance();
    
    private ConfigStore                                 _config                     = ConfigStore.getInstance();
    private int											_timeoutStop				= _config.getProperty(Constants.PROP_MESSAGE_CHECK_INTERVAL, Constants.DEF_MESSAGE_CHECK_INTERVAL);
    private Thread                                      _worker                     = null;
    private boolean										_run						= false;
    private boolean										_stopped					= true;
    
    public SMSProcessor(SAFQueue queue)
    {
    	_queue																		= queue;
    	
        if (_worker == null)
        {
        	_run																	= true;
        	_stopped																= false;
            _worker 																= new Thread(this);
            
            this.start();
        }
    }
        
    public void run()
    {
        Logger.write(Logger.DEBUG, CLASS, "Starting SMS Processor.");
        
        SMSMessage										smsMessage					= null;
        SMSAction										smsAction					= null;
        int											    timeSleep					= _config.getProperty(Constants.PROP_MESSAGE_CHECK_INTERVAL, Constants.DEF_MESSAGE_CHECK_INTERVAL);

        while (_run)
        {
            Logger.write(Logger.DEBUG, CLASS, "Checking queue for messages..");
            
            // Check if any messages has arrived and if not we can sleep the specified in the properties interval.
            // If messages has arrived, dont sleep and start processing them immediately. Once the message are processed,
            // loop and check for newly arrived immediately - we dont need to sleep if we keep receiving messages.
            if (_queue.size() > 0)
            {
                Logger.write(Logger.INFO, CLASS, "Found " + _queue.size() + " messages in the queue. Processing..");
                
                while (_queue.size() > 0)
                {
                	smsMessage														= _queue.get();
                	smsAction														= _actionCache.get(smsMessage.getNumber());
                	
                	// Parse the SMS message with the purpose to convert it to a GPS data point.
                	
                	
                	// For the particular SMS Message which we are processing, retrieve the applicable actions
                	// and apply them on the already determined GPS data point.
                	for (Map.Entry<Integer, Date> smsgAction : smsAction.getAll().entrySet())
                	{
                    	Logger.write(Logger.INFO, CLASS, "Applying action [" + smsgAction.getKey() + "] on [" + smsMessage.getNumber() + "]->" + smsMessage.getMessage());
                    	switch (smsgAction.getKey()) {
                    		case 1:	
                    				break;
                    		case 2:
                    				break;
                    		default:
                    				break;
                    				
                    	}
                	}
                }
            } else {
            	// Nothing to process found yet. lets wait a second...
                Logger.write(Logger.DEBUG, CLASS, "Sleeping the specified by property '" + Constants.PROP_MESSAGE_CHECK_INTERVAL + "' interval: " + timeSleep + " seconds.");
            	try { Thread.sleep(1000 * timeSleep); } catch (Exception e) {}
            }
        }
        
        Logger.write(Logger.DEBUG, CLASS, "SMS Processor stopped.");
        _stopped																	= true;
    }
    
    public void start()
    {
    	_run																		= true;
        _timeoutStop																= _config.getProperty(Constants.PROP_SMSG_PROC_TIMEOUT_STOP, Constants.DEF_SMSG_PROC_TIMEOUT_STOP);
        _worker.start();
    }
    
    public void stop()
    {
    	_run																		= false;
    	
    	for (int i=0; i<_timeoutStop; i++)
    	{
    		if (_worker != null)
    		{
    			try { Thread.sleep(1000); } catch (InterruptedException e) {};
    		}
    		else
    		{
    			return;
    		}
    	}
    	
        Logger.write(Logger.MINOR, CLASS, "Timeout while waiting for the thread to stop.");
    }
    
    public boolean isStopped()
    {
    	return _stopped;
    }
}
