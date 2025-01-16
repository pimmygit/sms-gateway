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
package sms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import dblayer.DAO;
import dblayer.DAOProvider;
import log.Logger;
import utils.Constants;


public class SMSMessage
{
    private static final String                         	CLASS                       = SMSMessage.class.getSimpleName();
    
    private int												_index						= 0;
    private String                                          _number                     = null;
    private String                                          _message                    = null;
    private String                                          _smsc                       = null;
    private Timestamp                                       _time                       = null;

    private String                                         	_unread                     = null;
    private boolean                                         _sent                       = false;
    private boolean                                         _status                     = false;

    private DAO                                             dao                         = DAOProvider.getDAO();
    
    /**
     * SMS Message
     * 
     * @param number
     * @param message
     * @param smsc
     * @param time
     * @param unread
     * @param sent
     * @param status
     */
    public SMSMessage(int index, String number, String message, String smsc, Timestamp time, String unread, boolean sent, boolean status)
    {
    	_index																			= index;
        _number                                                                         = number;
        _message                                                                        = message;
        _smsc                                                                           = smsc;
        _time                                                                           = time;
        _unread                                                                         = unread;
        _sent                                                                           = sent;
        _status                                                                         = status;
    }

    /**
     * SMS Message
     * 
     * Processes the raw text from the modem
     * @param message
     */
    public SMSMessage(String rawMessage)
    {
        Logger.write(Logger.DEBUG, CLASS, "SMS Message text: [" +rawMessage+"].");
    	
        String strDate;
        String strTime;
        
        _sent																			= false;
        
        if (rawMessage == null || "".equals(rawMessage.trim()))
        {
        	Logger.write(Logger.MAJOR, CLASS, "SMS message is empty.");
        }
        
        // msg[0] -> Message header
        // msg[1] -> Message content
        String[] msg = rawMessage.split("\\r?\\n");
        
        if (msg.length != 2)
        {
            Logger.write(Logger.MAJOR, CLASS, "Invalid SMS message format detected: " + msg.length);
            return;
        }
        
        // Remove all quotes from header: 3,"REC READ","+447590266231",,"16/08/28,15:56:28+04"
        msg[0] = msg[0].replace("\"", "");
        
        Logger.write(Logger.DEBUG, CLASS, "Stripped string: " + msg[0]);

        String[] msgHeader = msg[0].split(",");
        
        try
        {
        	_index = Integer.valueOf(msgHeader[0]);
            Logger.write(Logger.DEBUG, CLASS, "SMS Message index: [" +_index+"].");
        }
        catch (NumberFormatException nfe)
        {
        	_index = 0;
            Logger.write(Logger.MINOR, CLASS, "Failed to determine message index in the modem. Setting to zero.");
        }
        
        _unread = msgHeader[1];
        Logger.write(Logger.DEBUG, CLASS, "SMS Message state: [" +_unread+"].");
        _number = msgHeader[2];
        Logger.write(Logger.DEBUG, CLASS, "SMS Message number: [" +_number+"].");
        
        strDate = msgHeader[4];
        strTime = msgHeader[5];
        
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd hh:mm:ssX");
            Date parsedDate = dateFormat.parse(strDate + " " + strTime);
            _time = new java.sql.Timestamp(parsedDate.getTime());
        }
        catch(Exception e)
        {
            _time = new java.sql.Timestamp(System.currentTimeMillis());
            Logger.write(Logger.MINOR, CLASS, "Failed to determine the message timestamp. Setting to NOW.");
        }
        
        Logger.write(Logger.DEBUG, CLASS, "SMS Message date: [" +_time.toString()+"].");

        _message = msg[1];
        
        Logger.write(Logger.DEBUG, CLASS, "SMS Message message: [" +_message+"].");
    }

    public SMSMessage() { }

    public boolean storeMessage()
    {
        // Determine if we are storing sent or received message
        if (_sent)
        {
            // Saving sent message.
            dao.saveSentSMS(_number, _message, _status);
        } else {
            // Saving received message (all new messages are saved as 'unread'.
            dao.saveReceivedSMS(_number, _message);
        }
        return true;
    }
    
    public int getIndex()
    {
    	return _index;
    }
    
    public void setNumber(String nmb)
    {
        _number = nmb;
    }

    public String getNumber()
    {
        return _number;
    }

    public void setMessage(String msg)
    {
        _message = msg;
    }

    public String getMessage()
    {
        return _message;
    }

    public void setSMSC(String smsc)
    {
        _smsc = smsc;
    }

    public String getSMSC()
    {
        return _smsc;
    }

    public Timestamp getTime()
    {
        return _time;
    }

    public void setTime(Timestamp time)
    {
        _time = time;
    }
    public void setUnread(String unread)
    {
        _unread = unread;
    }

    public String isUnread()
    {
        return _unread;
    }

    public boolean isSent()
    {
        return _sent;
    }

    public void setSent(boolean sent)
    {
        _sent = sent;
    }

    public boolean getStatus()
    {
        return _status;
    }

    public void setStatus(boolean status)
    {
        _status = status;
    }
}