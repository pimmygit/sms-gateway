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
package log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import utils.Utils;

public class Logger
{
    /* Log levels */
    public final static int         DEBUG                   = 0;
    public final static int         INFO                    = 1;
    public final static int         CONFIG                  = 2;
    public final static int         MINOR                   = 3;
    public final static int         MAJOR                   = 4;
    public final static int         CRITICAL                = 5;

    
    /* Log sink types */
    public static int               STDOUT                  = 0;
    public static int               FILE                    = 1;

    private final static int        LOG_CLASS_STRING_LENGTH = 15;

    private static int              _logLevel               = DEBUG;
    private static int              _logOutput              = FILE;

    private static final String[]   _days                   = {"Sun","Mon","Tue","Wed","Thr","Fri","Sat"};

    private static PrintWriter      _writer                 = null;
    
    public static synchronized void setLogLevel(String level)
    {
        int log_level;
        
        if(level.equalsIgnoreCase("debug"))
            log_level=Logger.DEBUG;
        else if (level.equalsIgnoreCase("info"))
            log_level=Logger.INFO;
        else if (level.equalsIgnoreCase("config"))
            log_level=Logger.CONFIG;
        else if (level.equalsIgnoreCase("minor"))
            log_level=Logger.MINOR;
        else if (level.equalsIgnoreCase("major"))
            log_level=Logger.MAJOR;
        else if (level.equalsIgnoreCase("critical"))
            log_level=Logger.CRITICAL;
        else
            log_level=Logger.MAJOR;

        
        if( log_level<DEBUG || log_level>CRITICAL)
        {
            throw new IllegalArgumentException("LogLevel out of range");
        }
        
        _logLevel = log_level;
    }

    public static synchronized void setLogOutput(String output)
    {
       int log_output;
       
       if(output.equalsIgnoreCase("stdout"))
           log_output=Logger.STDOUT;
       else if (output.equalsIgnoreCase("file"))
           log_output=Logger.FILE;
       else
           log_output=Logger.STDOUT;

       if( log_output!=STDOUT && log_output!=FILE)
        {
            throw new IllegalArgumentException("Unknown log output");
        }
        else
        {
            _logOutput = log_output;
        }
    }

    public static synchronized void setLog(String file) throws IOException
    {
        setLog(new File(file));
    }

    public static synchronized void setLog(File file) throws IOException
    {
        if(_writer!=null)
        {
            _writer                                         = new PrintWriter(new BufferedWriter(new FileWriter(file.getPath(),true)));
        }
    }

    /**
     * Writes message via the Log writer.
     * 
     * @param logLevel
     * @param className
     * @param message
     */
    public static void write(int logLevel, String className, String message)
    {
        // Check if we should print the message depending on the log level setting
        if(logLevel < _logLevel)
        {
            return;
        }
        
        // Format the message string
        String formattedMessage = getFormattedTimestamp() + logLevelToString(logLevel) + Utils.bringToStringLength(className, LOG_CLASS_STRING_LENGTH) + message;
        
        if(_logOutput==STDOUT)
        {
            System.out.println(formattedMessage);
        }
        else
        {
            synchronized(_writer)
            {
                _writer.println(formattedMessage);
                _writer.flush();
            }
        }
    }

    
    @SuppressWarnings("unused")
    public static final String getFormattedTimestamp()
    {
        GregorianCalendar       cal                 = new GregorianCalendar();
        
        cal.setTime(new Date(System.currentTimeMillis()));
        
        
        String[]                    tmp                     = new String[6];
        String                      hour                    = tmp[0]                    = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String                      minute                  = tmp[1]                    = Integer.toString(cal.get(Calendar.MINUTE));
        String                      second                  = tmp[2]                    = Integer.toString(cal.get(Calendar.SECOND));
        String                      millisecond             = tmp[3]                    = Integer.toString(cal.get(Calendar.MILLISECOND));
        String                      day                     = tmp[4]                    = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        String                      month                   = tmp[5]                    = Integer.toString(cal.get(Calendar.MONTH) + 1);
        String                      year                                                = Integer.toString(cal.get(Calendar.YEAR));
        StringBuffer                timestamp               = new StringBuffer();

        for(int i=0;i<6;i++)
        {
            if(tmp[i].length()==1)
            {
                tmp[i]="0"+tmp[i];
            }
        }
        
        StringBuffer                buffer                  = new StringBuffer();
        buffer.append("[");
        buffer.append(tmp[0]);
        buffer.append(":");
        buffer.append(tmp[1]);
        buffer.append(":");
        buffer.append(tmp[2]);
        buffer.append(".");
        buffer.append(tmp[3]);
        buffer.append("  ");
        buffer.append(_days[cal.get(Calendar.DAY_OF_WEEK)-1]);
        buffer.append(' ');
        buffer.append(tmp[4]);
        buffer.append("/");
        buffer.append(tmp[5]);
        buffer.append("/");
        buffer.append(year);
        buffer.append("]");
        buffer.append(" ");
        
        return buffer.toString();
    }
    
    
    /**
     * Formats the log level string to be constant length by appending white spaces
     * 
     * @param logLevel
     * @return
     */
    private static String logLevelToString(int logLevel) {
        
        switch (logLevel) {
            case DEBUG:     return "DEBUG    ";
            case INFO:      return "INFO     ";
            case CONFIG:    return "CONFIG   ";
            case MINOR:     return "MINOR    ";
            case MAJOR:     return "MAJOR    ";
            case CRITICAL:  return "CRITICAL ";
            default:        return "         ";
        }
    }
}







