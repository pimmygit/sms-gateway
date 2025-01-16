@ECHO OFF
@REM #*****************************************************************
@REM #
@REM # SMS Gateway
@REM # 
@REM # (C) Copyright Pimmy (Kliment Stefanov). 2016
@REM # kliment@hotmail.co.uk
@REM # All Rights Reserved
@REM #
@REM # THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE
@REM # The copyright notice above does not evidence any
@REM # actual or intended publication of such source code.
@REM #
@REM # RESTRICTED RIGHTS:
@REM #
@REM # This file may have been supplied under a license.
@REM # It may be used, disclosed, and/or copied only as permitted
@REM # under such license agreement. Any copy must contain the
@REM # above copyright notice and this restricted rights notice.
@REM # Use, copying, and/or disclosure of the file is strictly
@REM # prohibited unless otherwise provided in the license agreement.
@REM #
@REM #*****************************************************************
"C:\Program Files\Java\jdk1.8.0_91\jre\bin\java" -Djava.library.path=./lib -cp ./lib/RXTXcomm.jar;./lib/smsgate.jar server.SMSGateway