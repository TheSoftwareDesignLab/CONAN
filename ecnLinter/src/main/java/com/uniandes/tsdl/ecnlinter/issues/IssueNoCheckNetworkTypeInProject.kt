package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.NoConnectedNetwrokDetectedInterprocedural
import com.uniandes.tsdl.ecnlinter.NoNetworkTypeInterprocedural

object IssueNoCheckNetworkTypeInProject {

    private val ID_IN_PROJECT_SCOPE = "NoCheckNetworkTypeInProject"
    private val ID_IN_METHOD_SCOPE = "NoCheckNetworkTypeInMethod"

    private val DESCRIPTION_PROJECT_SCOPE = "It seems like network operations are being implemented in this project but the **network type** is not verified **in the project**"
    private val DESCRIPTION_METHOD_SCOPE = "It seems like network operations are being implemented in this method but the **network type** is not verified **in the method**"


    private val EXPLANATION_PROJECT_SCOPE = """
        
                    In order to retrieve information from Internet is recommended to verify the **network type** before performing a network operation, however, there is no such verification **within the project**
                    
                    A device can have various types of network connections. For the full list of possible network types, see https://developer.android.com/reference/android/net/ConnectivityManager
                    
                    **Note:** `Wi-Fi` is typically faster. Also, mobile data is often metered, which can get expensive. A common strategy for apps is to only fetch large data if a Wi-Fi network is available.
                    
                    More information available here: 
                    https://developer.android.com/training/basics/network-ops/managing
                    https://developer.android.com/training/efficient-downloads/connectivity_patterns
                    
                    """
    private val EXPLANATION_METHOD_SCOPE = """
        
                    In order to retrieve information from Internet is recommended to verify the **network type** before performing a network operation, however, there is no such verification **within the method**
                    
                    A device can have various types of network connections. For the full list of possible network types, see https://developer.android.com/reference/android/net/ConnectivityManager
                    
                    **Note:** `Wi-Fi` is typically faster. Also, mobile data is often metered, which can get expensive. A common strategy for apps is to only fetch large data if a Wi-Fi network is available.
                    
                    More information available here: 
                    https://developer.android.com/training/basics/network-ops/managing
                    https://developer.android.com/training/efficient-downloads/connectivity_patterns
                    
                    """

    private val CATEGORY = Helper.CONNECTIVITY
    private val PRIORITY = 10
    private val SEVERITY = Severity.WARNING

    @JvmField
    val ISSUE_NO_CHECK_NETWORK_TYPE_IN_PROJECT_SCOPE: Issue = Issue.create(
        id = ID_IN_PROJECT_SCOPE,
        briefDescription = DESCRIPTION_PROJECT_SCOPE,
        explanation = EXPLANATION_PROJECT_SCOPE,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoNetworkTypeInterprocedural::class.java, Scope.JAVA_FILE_SCOPE)
    )

    @JvmField
    val ISSUE_NO_CHECK_NETWORK_TYPE_IN_METHOD_SCOPE: Issue = Issue.create(
        id = ID_IN_METHOD_SCOPE,
        briefDescription = DESCRIPTION_METHOD_SCOPE,
        explanation = EXPLANATION_METHOD_SCOPE,
        category = CATEGORY,
        priority = PRIORITY,
        severity = SEVERITY,
        implementation = Implementation(NoNetworkTypeInterprocedural::class.java, Scope.JAVA_FILE_SCOPE)
    )
}