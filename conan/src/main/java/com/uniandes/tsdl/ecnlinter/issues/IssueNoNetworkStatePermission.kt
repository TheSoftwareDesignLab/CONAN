package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoNetworkStatePermissionDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueNoNetworkStatePermission {

        private val ID = "NoNetworkStatePermission"
        private val DESCRIPTION = "It seems like network information is being checked, such operation needs explicit permission in the manifest."
        private val EXPLANATION =
            """
                
             To access information about the state of network connection, your manifest must include the following permission: 
            `<uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\"/>`
            
            More information available here: https://developer.android.com/training/basics/network-ops/connecting

            """
        private val CATEGORY = Helper.CONNECTIVITY
        private val PRIORITY = 10
        private val SEVERITY = Severity.WARNING

        @JvmField
        val ISSUE_NO_NETWORK_STATE_PERMISSION: Issue = Issue.create(
            id = ID,
            briefDescription = DESCRIPTION,
            explanation = EXPLANATION,
            category = CATEGORY,
            priority = PRIORITY,
            severity = SEVERITY,
            implementation = Implementation(NoNetworkStatePermissionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
