package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoInternetConnectionDetectorInterprocedural
import com.uniandes.tsdl.ecnlinter.NoInternetPermissionDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueNoInternetConnectionCheck {

    private val ID_PROJECT_SCOPE = "NoCheckInternetConnectionInProject"
    private val ID_METHOD_SCOPE = "NoCheckInternetConnectionInMethod"


    private val DESCRIPTION_SCOPE = "It seems like network operations are being implemented in this project but the **Internet availability** is not verified **in the project**"
    private val EXPLANATION_SCOPE =
        """
            In order to retrieve information from Internet is recommended to verify the **Internet availability** before performing a network operation, however, there is no such verification **within the project**
                    
            More information available here: 
            - https://developer.android.com/training/basics/network-ops/reading-network-state
            - https://developer.android.com/reference/android/net/NetworkCapabilities
            
        """
    private val DESCRIPTION_METHOD = "It seems like network operations are being implemented in this method but the **Internet availability** is not verified **in the method**"
    private val EXPLANATION_METHOD =
        """
            In order to retrieve information from Internet is recommended to verify the **Internet availability** before performing a network operation, however, there is no such verification **within the method**
                    
            More information available here: 
            - https://developer.android.com/training/basics/network-ops/reading-network-state
            - https://developer.android.com/reference/android/net/NetworkCapabilities
            
        """
    private val CATEGORY = Helper.CONNECTIVITY
    private val PRIORITY = 10
    private val SEVERITY = Severity.WARNING

    @JvmField
    val ISSUE_NO_INTERNET_CONNECTION_CHECK_METHOD: Issue = Issue.create(
        id = ID_METHOD_SCOPE,
        briefDescription = DESCRIPTION_METHOD,
        explanation = EXPLANATION_METHOD,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoInternetConnectionDetectorInterprocedural::class.java, Scope.JAVA_FILE_SCOPE)
    )

    @JvmField
    val ISSUE_NO_INTERNET_CONNECTION_CHECK_PROJECT: Issue = Issue.create(
        id = ID_PROJECT_SCOPE,
        briefDescription = DESCRIPTION_SCOPE,
        explanation = EXPLANATION_SCOPE,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoInternetConnectionDetectorInterprocedural::class.java, Scope.JAVA_FILE_SCOPE)
    )
}