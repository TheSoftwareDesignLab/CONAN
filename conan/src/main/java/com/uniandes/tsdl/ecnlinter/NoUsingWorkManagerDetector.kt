package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoInternetPermission
import com.uniandes.tsdl.ecnlinter.issues.IssueNoUsingWorkManager
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement

private const val JOB = "android.app.job.JobScheduler"
private const val GCM = "com.google.android.gms.gcm.GcmNetworkManager"
private const val FIREBASE = "com.firebase.jobdispatcher"

class NoUsingWorkManagerDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        val types: MutableList<Class<out UElement?>> = ArrayList()
        types.add(UImportStatement::class.java)
        return types
    }

    override fun createUastHandler(context: JavaContext) = NoUsingWorkManagerDetectorHandler(context)

    class NoUsingWorkManagerDetectorHandler(private val context: JavaContext) : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val listOfImports = listOf(JOB, GCM, FIREBASE)
            node.importReference?.let { importReference ->
                val import = importReference.asSourceString()
                val foundLibrary = listOfImports.find { item -> import.startsWith(item) }
                if (foundLibrary != null) {
                    return context.report(
                        IssueNoUsingWorkManager.ISSUE_NO_USING_WORK_MANAGER,
                        node,
                        context.getLocation(node),
                        "It seems like this project is employing a non-suggested library to schedule reliable and asynchronous tasks, the usage of `WorkManager` is recommended."
                    )
                }
            }
        }
    }
}