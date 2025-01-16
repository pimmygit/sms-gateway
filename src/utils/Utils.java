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

public class Utils {

    /**
     * Formats the passed string to be exact length by appending white spaces
     * 
     * @param string
     * @param length
     * @return
     */
    public static String bringToStringLength(String string, int length) {
        
        StringBuilder               finalString             = new StringBuilder(length);

        finalString.append(string);
        
        while (finalString.length() < length) {
            finalString.append(" ");
        }
        
        return finalString.toString();
    }
}
