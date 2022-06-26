package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.NoConnectedNetwrokDetectedInterprocedural

object IssueNoCheckNetworkConnectionInProject {

    private val ID_IN_PROJECT_SCOPE = "NoCheckNetworkConnectionInProject"
    private val ID_IN_METHOD_SCOPE = "NoCheckNetworkConnectionInMethod"

    private val DESCRIPTION_PROJECT_SCOPE = "It seems like network operations are being implemented in this project but **network availability** is not verified **in the project**"
    private val DESCRIPTION_METHOD_SCOPE = "It seems like network operations are being implemented in this method but **network availability** is not verified **in the method**"


    private val EXPLANATION_PROJECT_SCOPE = """
        
                    In order to retrieve information from Internet is recommended to verify the **network availability** before performing a network operation, however, there is no such verification **within the project**
                    
                    **Note:** Before you perform network operations, it's good practice to check the state of network connectivity. Among other things, this could prevent your app from inadvertently using the wrong radio. If a network connection is unavailable, your application should respond gracefully. To check the network connection, you typically use the following class:

                    **`ConnectivityManager:`** Answers queries about the state of network connectivity. It also notifies applications when network connectivity changes.
        
                    """
    private val EXPLANATION_METHOD_SCOPE = """
        
                    In order to retrieve information from Internet is recommended to verify the **network availability** before performing a network operation, however, there is no such verification **within the method**
                    
                    **Note:** Before you perform network operations, it's good practice to check the state of network connectivity. Among other things, this could prevent your app from inadvertently using the wrong radio. If a network connection is unavailable, your application should respond gracefully. To check the network connection, you typically use the following class:

                    **ConnectivityManager:** Answers queries about the state of network connectivity. It also notifies applications when network connectivity changes.
                    
                    """

    private val CATEGORY = Helper.CONNECTIVITY
    private val PRIORITY = 10
    private val SEVERITY = Severity.WARNING

    @JvmField
    val ISSUE_NO_CHECK_NETWORK_CONNECTION_IN_PROJECT_SCOPE: Issue = Issue.create(
        id = ID_IN_PROJECT_SCOPE,
        briefDescription = DESCRIPTION_PROJECT_SCOPE,
        explanation = EXPLANATION_PROJECT_SCOPE,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoConnectedNetwrokDetectedInterprocedural::class.java, Scope.JAVA_FILE_SCOPE)
    )

    @JvmField
    val ISSUE_NO_CHECK_NETWORK_CONNECTION_IN_METHOD_SCOPE: Issue = Issue.create(
        id = ID_IN_METHOD_SCOPE,
        briefDescription = DESCRIPTION_METHOD_SCOPE,
        explanation = EXPLANATION_METHOD_SCOPE,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoConnectedNetwrokDetectedInterprocedural::class.java, Scope.JAVA_FILE_SCOPE)
    )
}