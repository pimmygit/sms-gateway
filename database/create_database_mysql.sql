CREATE DATABASE IF NOT EXISTS smsgate;

USE smsgate;

#DROP TABLE sms_message_received;
#DROP TABLE sms_message_sent;
#DROP TABLE sms_message_type;

#
# Name: sms_message_sent
# Desc: Contains SMS messages sent to users.
# Last: 01.09.2014
#
CREATE TABLE IF NOT EXISTS sms_message_sent(
user_id				INTEGER NOT NULL,							# ID of the user who sent the message
mobile_number			VARCHAR(20) NOT NULL,							# Mobile number to which the SMS was sent
message_type			INTEGER NOT NULL,							# Type of the SMS message (config, regular, etc.)
multi_part			BOOLEAN NOT NULL DEFAULT FALSE,						# Is it part of a multi-part message
message_text			VARCHAR(160),								# Content of the SMS message
status				BOOLEAN NOT NULL DEFAULT FALSE,						# Has the message been sent successfully
date_time			TIMESTAMP DEFAULT CURRENT_TIMESTAMP					# Timestamp of when the SMS was sent
);

#
# Name: sms_message_sent
# Desc: Contains SMS messages sent to users.
# Last: 01.09.2014
#
CREATE TABLE IF NOT EXISTS sms_message_received(
mobile_number			VARCHAR(20) NOT NULL,							# Mobile number from which the SMS was received
message_text			VARCHAR(160),								# Content of the SMS message
date_time			TIMESTAMP DEFAULT CURRENT_TIMESTAMP					# Timestamp of when the SMS was received
);

#
# Name: sms_message_type
# Desc: Contains list of SMS message types
# Last: 01.09.2014
#
CREATE TABLE IF NOT EXISTS sms_message_type(
id				INTEGER NOT NULL,							# Type ID
name				VARCHAR(10) NOT NULL							# Type name
);

#
# Name: sms_message_type
# Desc: Contains list of SMS message types
# Last: 01.09.2014
#
CREATE TABLE IF NOT EXISTS sms_action(
mobile_number			VARCHAR(20) NOT NULL,							# Mobile phone number
action				INT(11) NOT NULL,							# User defined action (what to do with the SMS?)
date_time			TIMESTAMP DEFAULT CURRENT_TIMESTAMP					# Timestamp of when the SMS Action was last modified
);
