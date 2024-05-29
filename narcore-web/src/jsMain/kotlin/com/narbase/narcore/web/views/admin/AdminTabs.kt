package com.narbase.narcore.web.views.admin

import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.views.basePage.BasePageViewModel

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
enum class AdminTabs(val routeDetails: BasePageViewModel.RouteDetails) {
    Staff(BasePageViewModel.RouteDetails("/users", "Current Staff".localized())),
    InActiveStaff(BasePageViewModel.RouteDetails("/inactive_staff", "Inactive Staff".localized())),
    Roles(BasePageViewModel.RouteDetails("/roles", "Roles".localized())),

    ;

    companion object {
        val subLists = listOf(
            AdminTabSubList("Staff Management".localized(), listOf(Staff, InActiveStaff, Roles)),
        )
    }
}

class AdminTabSubList(
    val title: String,
    val tabs: List<AdminTabs>
)
