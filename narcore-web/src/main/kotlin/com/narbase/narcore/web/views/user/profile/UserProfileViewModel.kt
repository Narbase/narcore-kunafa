package com.narbase.narcore.web.views.user.profile

import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.dto.domain.user.profile.GetProfileDto
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.basicNetworkCall
import com.narbase.narcore.web.utils.BasicUiState

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
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
