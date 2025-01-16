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

/*
 * A collection of constants used by Precision applications to identify
 * the provider type of the database being used e.g. MySQL or Oracle.
 */
public abstract interface DatabaseTypes
{
    public static final String                          DBTYPE_DB2                      = "db2";
    public static final String                          DBTYPE_MYSQL                    = "mysql";
    public static final String                          DBTYPE_ORACLE                   = "oracle";
//    public static final String                          DBTYPE_POSTGRESQL               = "postgresql";
}