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

/**
 * Created by IntelliJ IDEA.
 * User: Pimmy
 * Date: Nov 7, 2003
 * Time: 9:56:09 AM
 * To change this template use Options | File Templates.
 */
public class SMSResponse
{
	public String _number;
	public String _message;
	public String _smsc;
	public int _validity;

	public static final int OK = 1;
	public static final int SMS_TIMED_OUT = 2;
	public static final int TIMED_OUT_SMS = 3;
	public static final int MAX_TIMED_OUT = 4;
	public static final int ERROR = 5;
	public static final int UNIDENTIFIED = 6;
	public static final int INFO = 10;

	private int status;
	private String errMsg;

	public SMSResponse(String number, String message, String smsc, int validity)
	{
        _number = number;
		_message = message;
		_smsc = smsc;
		_validity = validity;
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

	public void setValidity(int validity)
	{
		_validity = validity;
	}

	public int getValidity()
	{
		return _validity;
	}

	public void setErrorMessage(String e)
	{
	    errMsg = e;
	}

	public String getErrorMessage()
	{
	    return errMsg;
	}

	public void setStatus(int s)
	{
	    status = s;
	}

	public int getStatus()
	{
	    return status;
	}
}
