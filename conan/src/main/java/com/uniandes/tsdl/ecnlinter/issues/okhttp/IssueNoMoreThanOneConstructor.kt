package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.NoSingleInstanceOkhttpDetector

object IssueNoMoreThanOneConstructor {

    private val ID = "NoMoreThanOneOkhttpConstructor"
    private val DESCRIPTION = "It seems like this project is employing more than one `OkHttpClient` instance."
    private val EXPLANATION = """
                    `OkHttp` performs best when you create a single `OkHttpClient` instance and reuse it for all of your `HTTP` calls. This is because each client holds its own connection pool and thread pools. Reusing connections and threads reduces latency and saves memory. Conversely, creating a client for each request wastes resources on idle pools.
                    
                    More information available here: https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/
                    
                    """
    private val CATEGORY = Helper.CONNECTIVITY
    private val PRIORITY = 10
    private val SEVERITY = Severity.WARNING

    @JvmField
    val ISSUE_NO_MORE_ONE_CONST_OKHTTP: Issue = Issue.create(
        id = ID,
        briefDescription = DESCRIPTION,
        explanation = EXPLANATION,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoSingleInstanceOkhttpDetector::class.java, Scope.JAVA_FILE_SCOPE)
    )


}