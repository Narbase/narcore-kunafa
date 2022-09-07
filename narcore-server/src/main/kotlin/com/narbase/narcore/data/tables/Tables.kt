package com.narbase.narcore.data.tables


import com.narbase.narcore.data.columntypes.array
import com.narbase.narcore.data.columntypes.dateTimeWithoutTimezone
import com.narbase.narcore.data.columntypes.enum
import com.narbase.narcore.data.enums.SmsMessageStatus
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.jodatime.DateColumnType
import org.joda.time.DateTime

interface DeletableTable {
    val isDeleted: Column<Boolean>
}

interface LoggedTable {
    val createdOn: Column<DateTime>
}

fun Table.deletedColumn() = bool("is_deleted").default(false)
fun Table.createdOnColumn() = dateTimeWithoutTimezone("created_on").defaultExpression(CurrentDateTimeAtUtc())

class CurrentDateTimeAtUtc : Function<DateTime>(DateColumnType(false)) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        +"CURRENT_TIMESTAMP at time zone 'UTC'"
    }
}

object ClientsTable : LoggedTable, UUIDTable("clients") {
    val username: Column<String> = text("username").index()
    val passwordHash: Column<String> = text("password_hash")
    val lastLogin = dateTimeWithoutTimezone("last_login").nullable()
    override val createdOn = createdOnColumn()
}

object UsersTable : UUIDTable("staff"), LoggedTable, DeletableTable {
    val clientId = reference("client_id", ClientsTable).uniqueIndex()
    val fullName = text("full_name")
    val callingCode = text("calling_code") // with leading +
    val localPhone = text("local_phone") // without leading zero
    val isInactive = bool("is_inactive").default(false)
    override val isDeleted = deletedColumn()
    override val createdOn = createdOnColumn()
}

object DeviceTokensTable : UUIDTable("device_tokens") {
    val token: Column<String> = text("token").uniqueIndex()
    val clientId = reference("client_id", ClientsTable)
    val createdOn = createdOnColumn()
}

object AppsConfigTable : UUIDTable("app_config") {
    val permissiveUserCode = integer("permissive_user_code")
    val minimumUserCode = integer("minimum_user_code")
}

object SmsRecordTable : LoggedTable, UUIDTable("sms_record") {
    val message = text("message")
    val phones = array("phones")
    val status = enum("status", SmsMessageStatus::class)
    override val createdOn = createdOnColumn()
}
