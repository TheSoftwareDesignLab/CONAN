package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoInternetPermissionDetector
import com.uniandes.tsdl.ecnlinter.NoOnFailureImplementedDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueNoOnFailureImplemented {
        private val ID_EMPTY = "NoOnFailureBodyImplementation"

        private val DESCRIPTION_EMPTY = "It seems like the network request is not being assessed on failure."

        private val EXPLANATION_EMPTY =
            """
                
                In order to avoid misbehaviour when performing network operations it is recommended to handle on failure scenarios.
                
            """

        private val CATEGORY = Helper.CONNECTIVITY
        private val PRIORITY = 10
        private val SEVERITY = Severity.WARNING

        @JvmField
        val ISSUE_NO_ON_FAILURE_IMPLEMENTED_EMPTY: Issue = Issue.create(
            id = ID_EMPTY,
            briefDescription = DESCRIPTION_EMPTY,
            explanation = EXPLANATION_EMPTY,
            category = CATEGORY,
            priority = PRIORITY,
            severity = SEVERITY,
            implementation = Implementation(NoOnFailureImplementedDetector::class.java, Scope.JAVA_FILE_SCOPE)
    )

}