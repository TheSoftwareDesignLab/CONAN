package com.uniandes.tsdl.ecnlinter.issues.retrofit

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoExecuteUsageInRetrofitDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueUsageSyncExecute {
    private val ID = "AvoidSync"
    private val DESCRIPTION = "It seems like synchronous operations are being implemented, avoid such approach."
    private val EXPLANATION =
        """
        Using the `.execute()` method on a call object will perform a synchronous request which is not recommended and its usage is discouraged
        
        In **synchronous operations**, tasks are performed one at a time and only when one is completed, the following is unblocked.

        In **asynchronous operations**, you can move to another task before the previous one finishes, allowing you to deal with multiple requests simultaneously.
           
        Moreover, it is less likely to perform network operations on the main thread. Android requires you to perform network operations on a thread other than the main UI thread; a `NetworkOnMainThreadException` is thrown otherwise.
        
        **Resources:**
        
        **Retrofit:** https://square.github.io/retrofit/
        https://developer.android.com/training/basics/network-ops/connecting
        
        """
    private val CATEGORY = Helper.CONNECTIVITY
    private val PRIORITY = 10
    private val SEVERITY = Severity.WARNING

    @JvmField
    val ISSUE_USAGE_SYNC_EXEC: Issue = Issue.create(
        id = ID,
        briefDescription = DESCRIPTION,
        explanation = EXPLANATION,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoExecuteUsageInRetrofitDetector::class.java, Scope.JAVA_FILE_SCOPE)
    )
}