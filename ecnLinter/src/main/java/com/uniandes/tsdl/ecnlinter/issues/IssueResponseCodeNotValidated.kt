package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoResponseCodeValidationDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueResponseCodeNotValidated {
    private val ID = "NoResponseCodeValidation"
    private val DESCRIPTION = "It seems like the response code is not being validated when performing network operations."
    private val EXPLANATION = """
        When you make an HTTP request, your application should validate the response status code before manipulating it, otherwise it could cause misbehaviour within the app.
        
                        """
    private val CATEGORY = Helper.CONNECTIVITY
    private val PRIORITY = 10
    private val SEVERITY = Severity.WARNING

    @JvmField
    val ISSUE_RESPONSE_CODE_NOT_VALIDATED: Issue = Issue.create(
        id = ID,
        briefDescription = DESCRIPTION,
        explanation = EXPLANATION,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoResponseCodeValidationDetector::class.java, Scope.JAVA_FILE_SCOPE)
    )
}