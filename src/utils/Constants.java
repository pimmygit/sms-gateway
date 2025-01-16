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
package utils;

public interface Constants {
    
    /********************************************************************************
     * Files and directories constants
     ********************************************************************************/
    public static final String                              DIR_CONFIG                  		= "";
    public static final String                              DIR_LOG                     		= "/logs";
    
    public static final String                              FILE_PROPS_SMSGATE          		= "smsgate.cfg";
    public static final String                              FILE_PROPS_MODEM            		= "modem.cfg";
    public static final String                              FILE_LOG_SMSGATE            		= "smsgate.log";
    public static final String                              FILE_LOG_INBOX              		= "inbox.log";
    
    /********************************************************************************
     * SMS Gateway Actions constants.
     ********************************************************************************/
    public static final int                              	ACTION_DB_ARCHIVE                  	= 1;
    public static final int                              	ACTION_PLOT_GOOGLE                  = 11;
    public static final int                              	ACTION_PLOT_BING                  	= 12;
    public static final int                              	ACTION_FORWARD_LATITUDE           	= 101;
    public static final int                              	ACTION_FORWARD_BING                 = 102;

    /********************************************************************************
     * Database constants.
     ********************************************************************************/
    // DB2 Constants.
    public static final String                              DB2_DRIVER                  		= "com.ibm.db2.jcc.DB2Driver";
    public static final String                              DB2_URL_PREFIX              		= "jdbc:db2://";
    public static final String                              DB2_URL_PARAMS              		= "";
    public static final String                              DB2_TEST_QUERY              		= "SELECT 1 FROM SYSIBM.SYSDUMMY1";

    // MySQL Constants.
    public static final String                              MYSQL_DRIVER                		= "com.mysql.jdbc.Driver";
    public static final String                              MYSQL_URL_PREFIX            		= "jdbc:mysql://";
    public static final String                              MYSQL_URL_PARAMS            		= "?characterEncoding=UTF-8";
    public static final String                              MYSQL_TEST_QUERY            		= "SELECT 1";

    // Oracle Constants.
    public static final String                              ORACLE_DRIVER               		= "oracle.jdbc.driver.OracleDriver";
    public static final String                              ORACLE_URL_PREFIX           		= "jdbc:oracle:thin:@";
    public static final String                              ORACLE_URL_PARAMS           		= "";
    public static final String                              ORACLE_TEST_QUERY           		= "SELECT 1 FROM dual";

    // Database table names
    public static final String                              DB_TN_SMSACTION        				= "sms_action";
    public static final String                              DB_TN_SMSMESSAGESENT        		= "sms_message_sent";
    public static final String                              DB_TN_SMSMESSAGERECEIVED    		= "sms_message_received";
    public static final String                              DB_TN_SMSMESSAGETYPE        		= "sms_message_type";

    // Database field names
    public static final String                              DB_FN_ACTION                   		= "action";
    public static final String                              DB_FN_DATETIME              		= "date_time";
    public static final String                              DB_FN_ID                    		= "id";
    public static final String                              DB_FN_MESSAGETEXT           		= "message_text";
    public static final String                              DB_FN_MESSAGETYPE           		= "message_type";
    public static final String                              DB_FN_MOBILENUMBER          		= "mobile_number";
    public static final String                              DB_FN_MULTIPART             		= "multi_part";
    public static final String                              DB_FN_NAME                  		= "name";
    public static final String                              DB_FN_SMSC                  		= "smsc";
    public static final String                              DB_FN_STATUS                		= "status";
    public static final String                              DB_FN_USERID                		= "user_id";

    
    /********************************************************************************
     * GIS constants
     ********************************************************************************/
	//Regular Expression for parsing NW, SE bounds parameter into North, West, South, East co-ordinates
	public static final String								GIS_COORDS_REGEX					=	"^\\(\\(([-+]?\\d{1,2}(?:\\.\\d+)?)[,]\\s?" + 
																									"([-+]?\\d{1,3}(?:\\.\\d+)?)\\)[,]\\s?" +
																									"\\(([-+]?\\d{1,2}(?:\\.\\d+)?)[,]\\s?" + 
																									"([-+]?\\d{1,3}(?:\\.\\d+)?)\\)\\)$";
	//Assign numerical references to map points rather than magic numbers.
	public static final int									GIS_NORTH_REF						= 1;
	public static final int									GIS_WEST_REF						= 2;
	public static final int									GIS_SOUTH_REF						= 3;
	public static final int									GIS_EAST_REF						= 4;
	//Assign constants to the compass / map points.
	public static final String								GIS_NORTH							= "NORTH";
	public static final String								GIS_SOUTH							= "SOUTH";
	public static final String								GIS_WEST							= "WEST";
	public static final String								GIS_EAST							= "EAST";

	
    /********************************************************************************
     * AT Commands used in this application. Values and parameters can be supplied via property
     ********************************************************************************/
    public static final String                              AT_MODEM_CMD_ERRORLEVEL    			= "+CMEE";
    public static final String                              AT_MODEM_CMD_SIGNALSTRENGTH			= "+CSQ";
    public static final String                              AT_MODEM_CMD_GSMSTATUS    			= "+CPAS";
    public static final String                              AT_MODEM_CMD_NETWORKSTATUS    		= "+CREG";
    public static final String                              AT_MODEM_CMD_OPERATOR    			= "+COPS";
    public static final String                              AT_MODEM_CMD_MANUFACTURER  			= "+CGMI";
    public static final String                              AT_MODEM_CMD_MESSAGEMODE   			= "+CMGF";
    public static final String                              AT_MODEM_CMD_SETMESSAGE    			= "+CNMI";
    public static final String                              AT_MODEM_CMD_SMSC					= "+CSCA";
    public static final String                              AT_MODEM_CMD_VALIDITY      			= "+CSMP";
    public static final String                              AT_MODEM_CMD_SEND          			= "+CMGS";
    public static final String                              AT_MODEM_CMD_GET           			= "+CMGL";
    public static final String                              AT_MODEM_CMD_REMOVE       			= "+CMGD";
    public static final String                              AT_MODEM_CMD_NEWMESSAGE    			= "+CMTI";

	
    /********************************************************************************
     * SMS Gateway runtime properties constants
     ********************************************************************************/
    public static final String                              PROP_SMSG_PROCESSING_HALTED			= "smsg.main.processing.halt";
    public static final String                              PROP_SMSG_MAIN_TIMEOUT_STOP			= "smsg.main.timeout.stop";
    public static final String                              PROP_SMSG_PROC_TIMEOUT_STOP			= "smsg.proc.timeout.stop";
    
    /********************************************************************************
     * SMS Message related modem properties constants.
     ********************************************************************************/
    public static final String                              PROP_MESSAGE_CHECK_INTERVAL			= "message.check.interval";
    public static final String                              PROP_MESSAGE_RECEIVE_TIMEOUT		= "message.receive.timeout";
    public static final String                              PROP_MESSAGE_SEND_TIMEOUT   		= "message.send.timeout";
    public static final String                              PROP_MESSAGE_SEND_ATTEMPTS  		= "message.send.attempts";
    public static final String                              PROP_MESSAGE_RESEND_SLEEP   		= "message.resend.sleep";
    public static final String                              PROP_MESSAGE_SAVE_SENT      		= "message.save.sent";
    public static final String                              PROP_MESSAGE_SAVE_FAILED    		= "message.save.failed";

    /********************************************************************************
     * Database connection configuration properties constants.
     ********************************************************************************/
    public static final String                              PROP_DB_URL                 		= "database.jdbc.url";
    public static final String                              PROP_DB_TYPE                		= "database.type";
    public static final String                              PROP_DB_HOST                		= "database.host";
    public static final String                              PROP_DB_PORT                		= "database.port";
    public static final String                              PROP_DB_NAME                		= "database.name";
    public static final String                              PROP_DB_USERNAME            		= "database.username";
    public static final String                              PROP_DB_PASSWORD            		= "database.password";
    public static final String                              PROP_DEFAULT_FETCH_SIZE     		= "database.connection.defaultFetchSize";
    public static final String                              CONNECTION_PROPERTIES_FILE  		= "database.connection.propertiesFile";

    /********************************************************************************
     * Serial Port properties constants.
     ********************************************************************************/
    public static final String                              PROP_PORT_ID                		= "port.id";
    public static final String                              PROP_PORT_NAME              		= "port.name";
    public static final String                              PROP_PORT_TIMEOUT_OPEN      		= "port.timeout.open";
    public static final String                              PROP_PORT_TIMEOUT_RECEIVE   		= "port.timeout.receive";
    public static final String                              PROP_PORT_BAUDRATE          		= "port.baudrate";
    public static final String                              PROP_PORT_DATABITS          		= "port.databits";
    public static final String                              PROP_PORT_STOPBITS          		= "port.stopbits";
    public static final String                              PROP_PORT_PARITY            		= "port.parity";
    public static final String                              PROP_PORT_FLOWCONTROLIN     		= "port.flowcontrolin";
    public static final String                              PROP_PORT_FLOWCONTROLOUT    		= "port.flowcontrolout";

    /********************************************************************************
     * Modem configuration properties constants.
     ********************************************************************************/
    public static final String                              PROP_MODEM_CMD_ERRORLEVEL   		= "modem.command.errorlevel";
    public static final String                              PROP_MODEM_CMD_SIGNALSTRENGTH		= "modem.command.signalstrength";
    public static final String                              PROP_MODEM_CMD_GSMSTATUS    		= "modem.command.gsmstatus";
    public static final String                              PROP_MODEM_CMD_NETWORKSTATUS    	= "modem.command.networkstatus";
    public static final String                              PROP_MODEM_CMD_OPERATOR    			= "modem.command.operator";
    public static final String                              PROP_MODEM_CMD_MANUFACTURER 		= "modem.command.manufacturer";
    public static final String                              PROP_MODEM_CMD_MESSAGEMODE  		= "modem.command.messagemode";
    public static final String                              PROP_MODEM_CMD_SETMESSAGE   		= "modem.command.setmessage";
    public static final String                              PROP_MODEM_CMD_SMSC         		= "modem.command.smsc";
    public static final String                              PROP_MODEM_CMD_VALIDITY     		= "modem.command.validity";
    public static final String                              PROP_MODEM_CMD_SEND         		= "modem.command.send";
    public static final String                              PROP_MODEM_CMD_GET          		= "modem.command.get";
    public static final String                              PROP_MODEM_CMD_REMOVE       		= "modem.command.remove";
    
    /********************************************************************************
     * Mobile network configuration properties constants.
     ********************************************************************************/
    public static final String                              PROP_PROVIDER_COUNTRY_CODE  		= "provider.country.code";
    public static final String                              PROP_PROVIDER_SMSC          		= "provider.smsc";
    public static final String                              PROP_PROVIDER_SMS_VALIDITY  		= "provider.sms.validity";
    public static final String                              PROP_PROVIDER_SMS_STATUS    		= "provider.sms.status";

    /********************************************************************************
     * Store and Forward properties constants.
     ********************************************************************************/
    public static final String                              PROP_QUEUE_THREAD_HALTED			= "smsg.halt";
    public static final String                              PROP_QUEUE_MAX_SIZE         		= "saf.size";
    public static final String                              PROP_QUEUE_MAX_AGE          		= "saf.age";
    
    
    /********************************************************************************
     * Default values (all times are in seconds)
     ********************************************************************************/
    public static final boolean								DEF_SMSG_PROCESSING_HALTED			= false;
    public static final int									DEF_SMSG_MAIN_TIMEOUT_STOP			= 10;
    public static final int									DEF_SMSG_PROC_TIMEOUT_STOP			= 5;

    public static final int                              	DEF_MESSAGE_CHECK_INTERVAL			= 5;
    public static final boolean                             DEF_MESSAGE_SAVE_SENT      			= true;
    public static final boolean                             DEF_MESSAGE_SAVE_FAILED    			= true;
    public static final int                                 DEF_MESSAGE_SEND_TIMEOUT   			= 10;
    public static final int                                 DEF_MESSAGE_SEND_ATTEMPTS  			= 3;
    public static final int                                 DEF_MESSAGE_RESEND_SLEEP   			= 10;
    public static final int                                 DEF_MESSAGE_RECEIVE_TIMEOUT			= 3;
    public static final int                                 DEF_MESSAGE_RECEIVE_SLEEP  			= 10;
    
    public static final String                              DEF_LOG_OUTPUT              		= "STDOUT";
    public static final String                              DEF_LOG_LEVEL              			= "CONFIG";
    public static final int                                 DEF_LOG_SIZE                		= 10;
    
    public static final String                              DEF_DB_TYPE                 		= "MySQL";
    public static final String                              DEF_DB_HOST                 		= "localhost";
    public static final String                              DEF_DB_PORT                			= "3306";
    public static final String                              DEF_DB_NAME                 		= "smsgate";
    public static final String                              DEF_DB_USERNAME             		= "root";
    public static final String                              DEF_DB_PASSWORD             		= "";

    public static final String                              DEF_PORT_ID                 		= "default";
    public static final String                              DEF_PORT_NAME               		= "COM1";
    public static final int                                 DEF_PORT_TIMEOUT_OPEN       		= 60;
    public static final int                                 DEF_PORT_TIMEOUT_RECEIVE    		= 30;
    public static final int                              	DEF_PORT_BAUDRATE           		= 9600;
    public static final String                             	DEF_PORT_DATABITS           		= "8";
    public static final String                          	DEF_PORT_STOPBITS           		= "1";
    public static final String                              DEF_PORT_PARITY             		= "0";
    public static final int                              	DEF_PORT_FLOWCONTROLIN      		= 0;
    public static final int                              	DEF_PORT_FLOWCONTROLOUT     		= 0;

    public static final String                              DEF_MODEM_CMD_ERRORLEVEL    		= "=1";
    public static final String                              DEF_MODEM_CMD_SIGNALSTRENGTH		= "";
    public static final String                              DEF_MODEM_CMD_GSMSTATUS    			= "";
    public static final String                              DEF_MODEM_CMD_NETWORKSTATUS  		= "=1";
    public static final String                              DEF_MODEM_CMD_OPERATOR    			= "";
    public static final String                              DEF_MODEM_CMD_MANUFACTURER  		= "";
    public static final String                              DEF_MODEM_CMD_MESSAGEMODE   		= "=1";
    public static final String                              DEF_MODEM_CMD_SETMESSAGE    		= "=1,2,2,1,0";
    public static final String                              DEF_MODEM_CMD_SMSC					= "";
    public static final String                              DEF_MODEM_CMD_VALIDITY      		= "";
    public static final String                              DEF_MODEM_CMD_SEND          		= "";
    public static final String                              DEF_MODEM_CMD_GET           		= "=\"ALL\"";
    public static final String                              DEF_MODEM_CMD_REMOVE       			= "=1";
    
    public static final int                                 DEF_PROVIDER_COUNTRY_CODE   		= 44;
    public static final int                                 DEF_PROVIDER_SMS_VALIDITY   		= 255;
    public static final boolean                             DEF_PROVIDER_SMS_STATUS     		= false;

    public static final boolean								DEF_QUEUE_HALTED					= false;
    public static final int                                 DEF_QUEUE_MAX_SIZE          		= 1000000;
    public static final long                                DEF_QUEUE_MAX_AGE           		= 604800;

}