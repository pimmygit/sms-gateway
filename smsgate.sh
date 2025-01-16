#*****************************************************************
#
# SMS Gateway
#
# (C) Copyright Pimmy (Kliment Stefanov). 2016
# kliment@hotmail.co.uk
# All Rights Reserved
#
# THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE
# The copyright notice above does not evidence any
# actual or intended publication of such source code.
#
# RESTRICTED RIGHTS:
#
# This file may have been supplied under a license.
# It may be used, disclosed, and/or copied only as permitted
# under such license agreement. Any copy must contain the
# above copyright notice and this restricted rights notice.
# Use, copying, and/or disclosure of the file is strictly
# prohibited unless otherwise provided in the license agreement.
#
#*****************************************************************
SMSG_HOME=`pwd`
SMSG_LIBS=$SMSG_HOME/lib

RTXT_LIBS=/usr/lib/jni
RTXT_JARS=/usr/share/java

if [ $LD_LIBRARY_PATH ]; then
        export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$RTXT_LIBS
else
        export LD_LIBRARY_PATH=$RTXT_LIBS
fi

java -cp "$RTXT_JARS/RXTXcomm.jar:$SMSG_LIBS/smsgate.jar" server.SMSGateway