package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoActionManageNetworkUsageDetector
import com.uniandes.tsdl.ecnlinter.NoUsingWorkManagerDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueNoManageNetworkUsage {

        private val ID = "NoManageNetworkUsage"
        private val DESCRIPTION = "It seems like data usage management options are not being offered to the user."
        private val EXPLANATION =
            """
                **Manage network usage:**
                
                It has been detected that your application supports network access since you are defining `android.permission.INTERNET` permission in your manifest.
                
                You can implement a preferences activity that gives users explicit control over your app's usage of network resources. For example:

                **1.** You might allow users to upload videos only when the device is connected to a Wi-Fi network.
                
                You can declare the intent filter for the `ACTION_MANAGE_NETWORK_USAGE` action to indicate that your application defines an activity that offers options to control data usage. `ACTION_MANAGE_NETWORK_USAGE` shows settings for managing the network data usage of a specific application. When your app has a settings activity that allows users to control network usage, you should declare this intent filter for that activity.
                
                More information available here: https://developer.android.com/training/basics/network-ops/managing#manage-usage
                
            """
        private val CATEGORY = Helper.CONNECTIVITY
        private val PRIORITY = 10
        private val SEVERITY = Severity.WARNING

        @JvmField
        val ISSUE_NO_MANAGE_NETWORK_USAGE: Issue = Issue.create(
            id = ID,
            briefDescription = DESCRIPTION,
            explanation = EXPLANATION,
            category = CATEGORY,
            priority = PRIORITY,
            severity = SEVERITY,
            implementation = Implementation(NoActionManageNetworkUsageDetector::class.java, Scope.MANIFEST_SCOPE),
            androidSpecific = true
        )
    }


