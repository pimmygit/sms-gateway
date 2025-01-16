/******************************************************************
 *
 * SMS Gateway
 * 
 * (C) Copyright Pimmy (Kliment Stefanov). 2015  
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import log.Logger;
import sms.SMSMessage;
import utils.ConfigStore;
import utils.Constants;

public class SAFQueue implements Runnable
{
    private static final String                         CLASS                       = SAFQueue.class.getSimpleName();

    private Queue<SMSMessage>                          	_queue                      = null;
    private ConfigStore                                 _config                     = ConfigStore.getInstance();

    private int                                         _limit                      = 0;
    private long                                        _maxAge                     = 0;
    
    private Thread                                      _worker                     = null;
    
    public SAFQueue()
    {
        _queue                                                                      = new LinkedList<SMSMessage>();
        _limit                                                                      = _config.getProperty(Constants.PROP_QUEUE_MAX_SIZE, Constants.DEF_QUEUE_MAX_SIZE);
        _maxAge                                                                     = _config.getProperty(Constants.PROP_QUEUE_MAX_AGE, Constants.DEF_QUEUE_MAX_AGE);        
        
        // Create the thread
        if (_worker == null)
        {
            _worker = new Thread(this);
        }
    }
    
    public void add(SMSMessage sms)
    {
        // Check if we are exceeding the maximum size of the queue and if necessary remove the oldest element
        if (_queue.size() >= _limit)
        {
        	SMSMessage									discardedSMS				= _queue.remove();
            Logger.write(Logger.INFO, CLASS, "Maximum queue size of " + _limit + " elements reached. Discarded oldest message received at " + discardedSMS.getTime() + " from " + discardedSMS.getNumber());
            Logger.write(Logger.DEBUG, CLASS, "Message content: " + discardedSMS.getMessage());
        }
        // Add the new element to the queue
        _queue.add(sms);
        
        Logger.write(Logger.DEBUG, CLASS, "Queue has " + _queue.size() + " elements after adding message received at " + sms.getTime() + " from " + sms.getNumber());
        
        if (!_worker.isAlive())
        {
        	_worker.start();
        }
    }
    
    public void addAll(List<SMSMessage> messageList)
    {
    	// First check if we would exceed the maximum queue size if we add those.
    	// If necessary free up some room to add the new messages.
    	int												newQueueSize				= _queue.size() + messageList.size();
    	int												howManyToRemove				= 0;
    	
    	if (newQueueSize >= _limit)
    	{
    		howManyToRemove															= newQueueSize - _limit;
    		
    		while (howManyToRemove > 0)
    		{
    			_queue.remove();
    		}
    	}
    	
    	_queue.addAll(messageList);
    }
    
    public void run()
    {
        Logger.write(Logger.DEBUG, CLASS, "Starting age checking thread.");

        while (_queue.size() > 0)
        {
            long										oldest						= ( (System.currentTimeMillis() - _queue.peek().getTime().getTime()) / 1000);
            int											queueSize					= _queue.size();
            
            Logger.write(Logger.DEBUG, CLASS, "Checking for timed out messages: maxAge[" + _maxAge + "], oldest[" + oldest + "]");

            if (oldest > _maxAge)
            {
                Logger.write(Logger.INFO, CLASS, "Discarding aged message from the queue: maxAge[" + _maxAge + "], oldest[" + oldest + "] -> " + _queue.poll());
                Logger.write(Logger.DEBUG, CLASS, "Queue size changed from " + queueSize + " to " + _queue.size() + ".");
            } else {
            	// Nothing to discard found yet. lets wait a second...
            	try { Thread.sleep(1000); } catch (Exception e) {}
            }
        }
        
        Logger.write(Logger.DEBUG, CLASS, "Stopping age checking thread.");
        try { Thread.sleep(10); } catch (Exception e) {};
    }
    
    public SMSMessage get()
    {
        SMSMessage										sms                     	= _queue.poll();
        Logger.write(Logger.DEBUG, CLASS, "Queue has " + _queue.size() + " elements after returning message received at " + sms.getTime() + " from " + sms.getNumber());
        return sms;
    }
    
    public int size()
    {
        return _queue.size();
    }
}























































