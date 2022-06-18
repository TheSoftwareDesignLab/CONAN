package com.uniandes.tsdl.ecnlinter.issues

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.uniandes.tsdl.ecnlinter.NoUsingWorkManagerDetector
import com.uniandes.tsdl.ecnlinter.helpers.Helper

object IssueNoUsingWorkManager {

        private val ID = "NoUsingWorkManager"
        private val DESCRIPTION = "It seems like this project is employing a non-suggested library to schedule reliable and asynchronous tasks, the usage of `WorkManager` is recommended."
        private val EXPLANATION = """
            
                    **Unifying Background Task Scheduling on Android:**
            
                    `WorkManager` is an API that makes it easy to schedule reliable, asynchronous tasks that are expected to run even if the app exits or the device restarts. The WorkManager API is a suitable and recommended replacement for all previous Android background scheduling APIs, including FirebaseJobDispatcher, GcmNetworkManager, and Job Scheduler. WorkManager incorporates the features of its predecessors in a modern, consistent API that works back to API level 14 while also being conscious of battery life.
                    
                    Under the hood WorkManager uses an underlying job dispatching service based on the following criteria:
                    
                    **Resources:**
                    
                    https://developer.android.com/topic/libraries/architecture/workmanager
                    https://android-developers.googleblog.com/2019/11/unifying-background-task-scheduling-on.html
                    
                    **Migration guides:**
                    
                    **Migrating from Firebase JobDispatcher to WorkManager:** 
                    https://developer.android.com/topic/libraries/architecture/workmanager/migrating-fb
                    
                    **Migrating from GCMNetworkManager to WorkManager:**
                    https://developer.android.com/topic/libraries/architecture/workmanager/migrating-gcm
                    
                    """
        private val CATEGORY = Helper.CONNECTIVITY
        private val PRIORITY = 10
        private val SEVERITY = Severity.WARNING

        @JvmField
        val ISSUE_NO_USING_WORK_MANAGER: Issue = Issue.create(
            id = ID,
            briefDescription = DESCRIPTION,
            explanation = EXPLANATION,
            category = CATEGORY,
            priority = PRIORITY,
            severity = SEVERITY,
            implementation = Implementation(
                NoUsingWorkManagerDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }


