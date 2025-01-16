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

import server.SMSAction;

/**
 * Contains the main interface to the database.
 *
 * @author	kliment@hotmail.co.uk
 */
public interface DAO
{
    /**
     * Saves SMS message. Status reflects successful sending
     * 
     * @param number    Mobile number
     * @param message   SMS Message
     * @param time      Timestamp
     * @param status    Status
     * @throws RuntimeException
     */
    public void saveSentSMS(String number, String message, boolean status)
        throws RuntimeException;

    /**
     * Saves SMS message.
     * 
     * @param number    Mobile number
     * @param message   SMS Message
     * @param time      Timestamp
     * @throws RuntimeException
     */
    public void saveReceivedSMS(String number, String message)
        throws RuntimeException;

    /**
     * Save the SMS Gateway action for the given SMS Number.
     * 
     * @param smsNumber		Mobile number
     * @param action		SMS Gateway action for this mobile number
     * @throws RuntimeException
     */
    public void saveSmsAction(String smsNumber, int action)
            throws RuntimeException;

    /**
     * Save the SMS Gateway action for the given SMS Number.
     * 
     * @param smsNumber		Mobile number
     * @throws RuntimeException
     * @return SMSAction object listing the actions for the given mobile number.
     */
    public SMSAction getSmsAction(String smsNumber)
            throws RuntimeException;

    /**
     * Get the RDBMS specific function or keyword to retrieve the current timestamp.
     * This method should be overridden as necessary in DB specific subclasses.
     * 
     * @return Database current timestamp function/keyword string
     */
    public String getCurrentTimestampFunction();        
}