package com.narbase.narcore.web.views.admin

import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.views.basePage.BasePageViewModel

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/01/19.
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
