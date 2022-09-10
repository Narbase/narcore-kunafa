package com.narbase.narcore.datetime

import com.narbase.narcore.datetime.DateTimeTestUtils.connectToDb
import com.narbase.narcore.datetime.DateTimeTestUtils.createDateTimeTestTable
import com.narbase.narcore.datetime.DateTimeTestUtils.dropDateTimeTestTable
import com.narbase.narcore.datetime.DateTimeTestUtils.resetDateTimeTestTable
import com.narbase.narcore.domain.utils.toDateTime
import com.narbase.narcore.domain.utils.toDto
import com.narbase.narcore.dto.common.datetime.DateTimeDto
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateTimeTests {

    @BeforeAll
    fun setupAll() {
        connectToDb()
        createDateTimeTestTable()
    }

    @BeforeEach
    fun setup() {
        resetDateTimeTestTable()
    }

    @AfterAll
    fun tearDown() {
        dropDateTimeTestTable()
    }

    @Test
    fun dateTimeMillis_shouldBeConvertedToEquivalentBrowserParse_whenSameStringIsUsed() {
        val browserInstant =
            1620656640000 //result of running Date.parse('2021-05-10T16:24:00') in browser (which is +2)
        val dateTime = DateTime.parse("2021-05-10T16:24:00")
        assertEquals(browserInstant, dateTime.millis)
    }

    @Test
    fun dateTimeDto_shouldBeConvertedToEquivalentUTCDateTime_whenToDateTimeIsCalled() {
        val dateTime = DateTime.parse("2021-05-10T16:24:00").millis
        val dto = DateTimeDto(dateTime)
        assertEquals(DateTime.parse("2021-05-10T14:24:00+00:00").millis, dto.toDateTime().millis)
    }

    @Test
    fun dateTimeDto_shouldHaveTheSameValue_whenStoredAndRetrievedFromDb() {
        val dateTime = DateTime.parse("2021-05-10T16:24:00").millis
        val dto = DateTimeDto(dateTime)
        val retrievedDto = transaction {
            DateTimeTestUtils.DateTimeTestTable.insert {
                it[createdOn] = dto.toDateTime()
            }
            DateTimeTestUtils.DateTimeTestTable.selectAll().limit(1)
                .first()[DateTimeTestUtils.DateTimeTestTable.createdOn].toDto()
        }
        assertEquals(retrievedDto.milliSeconds, dto.milliSeconds)
    }

    @Test
    fun clinicStartOfTheDay_shouldMatchStartOfTheDayInClinicTimeZone_whenDayRelatedQueriesAreRun() {
        val dateTime = DateTime.parse("2021-05-11T00:24:00").millis
        val dto = DateTimeDto(dateTime)
        val retrievedDto = transaction {
            DateTimeTestUtils.DateTimeTestTable.insert {
                it[createdOn] = dto.toDateTime()
            }
            DateTimeTestUtils.DateTimeTestTable.selectAll().limit(1)
                .first()[DateTimeTestUtils.DateTimeTestTable.createdOn].toDto()

//            DateTimeTestUtils.DateTimeTestTable.select {
//                DateTimeTestUtils.DateTimeTestTable.createdOn.isOnDateAtTimeZone(Da)
//            }.limit(1).first()[DateTimeTestUtils.DateTimeTestTable.createdOn].toDto()
        }
        assertEquals(retrievedDto.milliSeconds, dto.milliSeconds)
    }

}