package com.narbase.narcore.web.views.basePage

import com.narbase.narcore.web.utils.notifications.NotificationsController


class BasePageViewModel {

    fun onViewCreated() {
        NotificationsController.connect()
    }

    data class RouteDetails(var href: String, val title: String, val image: String? = null)

}
