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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import utils.ConfigStore;

import log.Logger;

/**
 * Reads the SMS message and matches the format to particular manufacturer
 * in order to provide the respective parser
 * 
 * @author pimmy
 *
 */
public class SMSParser
{
    private static final String                         CLASS                       = SMSParser.class.getSimpleName();

    public SMSParser()
    {
        
    }
    
    private void parseSMS(String msg)
    {
        Logger.write(Logger.DEBUG, CLASS, "SMS Message text: [" +msg+"].");

        String                                          indx                            = null;
        String                                          token                           = null;
        String                                          number                          = null;
        String                                          messTime                        = null;
        String                                          recvTime                        = null;
        String                                          message                         = null;

        try
        {
            //Determining the message index
            indx = msg;
            int in[]= new int[9];
            in[0]                                                                       = indx.indexOf(":");
            in[1]                                                                       = indx.indexOf(",", in[0]+1); // index
            in[7]                                                                       = indx.indexOf("#",in[6]+1);
            in[8]                                                                       = indx.indexOf("#",in[7]+1);
            indx                                                                        = indx.substring(in[0]+1,in[1]);

            StringTokenizer strTok = new StringTokenizer(msg, "\"");
            while (strTok.hasMoreElements())
            {
                token                                                                   = strTok.nextToken();

                if (token.startsWith("+"))
                {
                    if (Character.isDigit(token.charAt(1)))
                    {
                        //LogManager.write("Token1: " +token, LogHandle.ALL,LogManager.DEBUG);
                        number                                                          = token;
                    }
                }
                else if (token.startsWith("0")||token.startsWith("1")||token.startsWith("2")||token.startsWith("3"))
                {
                    //LogManager.write("Token2: " +token, LogHandle.ALL,LogManager.DEBUG);
                    messTime                                                            = token;
                }
                //else if (Character.isLetter(token.charAt(1)))
                else if (!token.startsWith(","))
                {
                    //LogManager.write("Token3: " +token, LogHandle.ALL,LogManager.DEBUG);
                    message                                                             = token.trim();

                    //This is to remove the ending "\r\n\r\nOK" from the message
                    if (message.endsWith("OK"))
                        message                                                         = message.substring(0, message.indexOf("\r\n"));

                }
                token = null;
            }

            long p  = System.currentTimeMillis();
            Date date = new Date(p);
            recvTime = DateFormat.getDateInstance(DateFormat.SHORT).format(date)+ ", " +DateFormat.getTimeInstance(DateFormat.LONG).format(date);

            recvTime = recvTime.replaceAll(":", "/");
            messTime = messTime.replaceAll(":", "/");
            messTime = messTime.replaceAll(",", ", ");
            String tmp1 = messTime.substring(6,8).concat(messTime.substring(2));
            String tmp2 = tmp1.substring(0,6).concat(messTime.substring(0,2));
            messTime = tmp2.concat(messTime.substring(8));

            Properties smsMessage = new Properties();
            smsMessage.setProperty("TimeSent", messTime);
            smsMessage.setProperty("Timestamp", recvTime);
            smsMessage.setProperty("Number", number);
            smsMessage.setProperty("Message", message);
            smsMessage.setProperty("New", "true");

            messTime = messTime.replaceAll("/", "");
            messTime = messTime.replaceAll(", ", "_");
            messTime = messTime.substring(0, messTime.indexOf("+"));

            //LogManager.write("Number: " +number, LogHandle.ALL,LogManager.DEBUG);
            //LogManager.write("MessTime: " +messTime, LogHandle.ALL,LogManager.DEBUG);
            //LogManager.write("TimeNow: " +recvTime, LogHandle.ALL,LogManager.DEBUG);
            //LogManager.write("Message: " +message.trim(), LogHandle.ALL,LogManager.DEBUG);

            // The path has to be made from the messTime
            String path = ConfigStore.getInstance().getFileLog() + messTime+ ".sms";

            try
            {
                FileOutputStream stream=new FileOutputStream(path);
                smsMessage.store(new PrintStream(stream), "SMS Message. Warning - Do not edit this file!!!");
                stream.close();
                Logger.write(Logger.DEBUG, CLASS, "SMS Message [" +indx+ "] saved in " + path);
            }
            catch (Exception e)
            {
                Logger.write(Logger.MINOR, CLASS, "Saving SMS Message [" +indx+ "] to ["+path+"] failed.");
                //e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        catch (Exception e)
        {
            Logger.write(Logger.DEBUG, CLASS, "Token of message [" +indx+ "] is NULL. Invalid/Empty Message - ignored and not saved.");
        }

    }
}
