package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoOnResponseBodyDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueNoOnResponseBody {
    private val ID = "NoOnResponseBodyImplementation"
    private val DESCRIPTION = "It seems like the `onResponse()` method is not being implemented. Network Methods with callback parameters require an implementation of the `onResponse` method."
    private val EXPLANATION = """
        
        Some network libraries (Volley, Retrofit and OkHttp) need to define some behavior for the Callback when they receive a response. 
        
                        """
    private val CATEGORY = Helper.CONNECTIVITY
    private val PRIORITY = 10
    private val SEVERITY = Severity.WARNING

    @JvmField
    val ISSUE_NO_ON_RESPONSE_BODY: Issue = Issue.create(
        id = ID,
        briefDescription = DESCRIPTION,
        explanation = EXPLANATION,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoOnResponseBodyDetector::class.java, Scope.JAVA_FILE_SCOPE)
    )
}