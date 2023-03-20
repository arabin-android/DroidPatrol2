package com

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.util.NotNullLazyValue

/**
 * @author Arabin
 * @date 03/20/2023
 * The main component of this plugin
 * */
class DroidEyeComponent : ApplicationComponent {
    override fun initComponent() {
        println("DroidPatrol is On >>>")
        val NOTIFICATION_GROUP: NotNullLazyValue<NotificationGroup> = object : NotNullLazyValue<NotificationGroup>() {
            override fun compute(): NotificationGroup {
                return NotificationGroup(
                    "Analysis Started > ",
                    NotificationDisplayType.STICKY_BALLOON,
                    true
                )
            }
        }
        ApplicationManager.getApplication()
            .invokeLater(
                {
                    Notifications.Bus.notify(
                        NOTIFICATION_GROUP.value
                            .createNotification(
                                "Quote of the day",
                                "Find your data leak during development!!! by Arabin",
                                NotificationType.ERROR,
                                null
                            )
                    )
                },
                ModalityState.NON_MODAL
            )
    }

    override fun disposeComponent() {}
    override fun getComponentName(): String {
        return "DroidEyeComponent"
    }
}