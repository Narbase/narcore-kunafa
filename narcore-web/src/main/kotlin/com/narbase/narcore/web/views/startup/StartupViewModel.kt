package com.narbase.narcore.web.views.startup

import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.dto.domain.user.profile.GetProfileDto
import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.basicNetworkCall
import com.narbase.narcore.web.storage.CurrentUserProfile
import com.narbase.narcore.web.storage.SessionInfo
import com.narbase.narcore.web.storage.StorageManager
import com.narbase.narcore.web.utils.BasicUiState

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/02/18.
 */

class StartupViewModel {
    val getConfigUiState = Observable<BasicUiState>()


    fun getConfig() {
        if (StorageManager.isUserLoggedIn().not()) {
            SessionInfo.currentUser = null
            getConfigUiState.value = BasicUiState.Loaded
            return
        }
        basicNetworkCall(getConfigUiState) {
            val profile = ServerCaller.getUserProfiles().data.profile
            SessionInfo.currentUser = profile.toCurrentUserProfile()
            // You can check app version or subscription here as well
        }
    }

    private fun GetProfileDto.UserProfile.toCurrentUserProfile() =
        CurrentUserProfile(
            clientId,
            userId,
            fullName,
            username,
            callingCode,
            localPhone,
            privileges.map { Privilege.valueOf(it) })
}
