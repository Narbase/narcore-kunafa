package com.narbase.narcore.dto.common.datetime


/***

current:
#### Db is considered UTC
- Time in API will be Long
- UI will need to get clinic timezone to display it correctly ( Daylight saving time).
- Sms sending and the like? can be done directly.
- Any function that depends on the day (today schedule, statistics?) needs to use the clinic time zone.


Outdated:
The clinic sends the datetime according to its time zone (e.g. gmt+2). The server converts the dto from the clinic timezone
to the server time zone (e.g. gmt+1). When storing the datetime in the db, the server converts it to UTC and then stores
it in the db without the timezone info (TIMESTAMP WITHOUT TIMEZONE).
When the server retrieves the info from the db, the server converts it from UTC to the server timezone using DateTime
class.

Issues to keep in mind:
- All data transfers are done with the clinic timezone
- All processing in server is done using the server timezone
- All datetime fields stored in the db are stored as UTC without explcit timezone.

 ***/

class DateTimeDto(val milliSeconds: Long)

/**
 * In format: yyyy-MM-dd
 */

class DateDto(val date: String)
