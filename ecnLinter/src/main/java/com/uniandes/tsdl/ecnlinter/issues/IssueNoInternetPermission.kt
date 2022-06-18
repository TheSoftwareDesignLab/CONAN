package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoInternetPermissionDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueNoInternetPermission {
        private val ID = "NoInternetPermission"
        private val DESCRIPTION = "It seems like network operations are being implemented, such operations need explicit permission in the manifest."
        private val EXPLANATION =
            """
            To perform network operations in your application, your manifest must include the following permission: 
            `<uses-permission android:name=\"android.permission.INTERNET\"/>`
            
            More information available here: https://developer.android.com/training/basics/network-ops/connecting
            
            """
        private val CATEGORY = Helper.CONNECTIVITY
        private val PRIORITY = 10
        private val SEVERITY = Severity.WARNING

        @JvmField
        val ISSUE_NO_INTERNET_PERMISSION: Issue = Issue.create(
            id = ID,
            briefDescription = DESCRIPTION,
            explanation = EXPLANATION,
            category = CATEGORY,
            priority = PRIORITY,
            severity = SEVERITY,
            implementation = Implementation(NoInternetPermissionDetector::class.java, Scope.JAVA_FILE_SCOPE)
    )
}