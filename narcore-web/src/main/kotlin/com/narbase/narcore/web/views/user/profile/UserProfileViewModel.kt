package com.narbase.narcore.web.views.user.profile

import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.dto.domain.user.profile.GetProfileDto
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.basicNetworkCall
import com.narbase.narcore.web.utils.BasicUiState

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/01/30.
 */
class UserProfileViewModel {
    val getProfileUiState = Observable<BasicUiState>()
    val updateProfileUiState = Observable<BasicUiState>()

    var loadedProfile: GetProfileDto.UserProfile? = null

    fun getProfile() {

        basicNetworkCall(getProfileUiState) {
            val response = ServerCaller.getUserProfiles()
            loadedProfile = response.data.profile
        }
    }

    fun updateProfile(
        fullName: String,
        callingCode: String,
        localPhone: String
    ) {
        basicNetworkCall(updateProfileUiState) {
            val dto = UpdateUserProfileDto.RequestDto(fullName, callingCode, localPhone)
            ServerCaller.updateUserProfile(dto)
        }
    }

}
