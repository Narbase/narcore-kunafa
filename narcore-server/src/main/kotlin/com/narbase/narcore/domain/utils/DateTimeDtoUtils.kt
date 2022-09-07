package com.narbase.narcore.domain.utils

import com.narbase.narcore.dto.common.datetime.DateDto
import com.narbase.narcore.dto.common.datetime.DateTimeDto
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.*

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

fun Date.toDto(): DateTimeDto = DateTimeDto(time)

fun DateTime.toDto(): DateTimeDto = DateTimeDto(this.millis)


@Suppress("UnnecessaryVariable")
fun DateTimeDto.toDateTime(timeZone: DateTimeZone = DateTimeZone.UTC): DateTime = DateTime(this.milliSeconds, timeZone)

fun DateTimeDto.toDate(): Date = Date(this.milliSeconds)

fun LocalDate.toDto() = DateDto(toString(dtoDateFormatter))
fun DateDto.toDate() = LocalDate.parse(date.split(' ').first(), dtoDateFormatter)

val dtoDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
val dtoDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

fun DateTimeDto.displayString(): String = dtoDateTimeFormatter.print(milliSeconds)
fun DateDto.displayString(): String = date