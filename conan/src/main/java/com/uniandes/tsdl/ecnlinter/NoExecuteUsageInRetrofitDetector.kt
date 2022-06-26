package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.retrofit.IssueUsageSyncExecute
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

class NoExecuteUsageInRetrofitDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement?>>
    {
        val types: MutableList<Class<out UElement?>> = ArrayList()
        types.add(UCallExpression::class.java)
        return types
    }

    override fun createUastHandler(context: JavaContext) = NoInternetPermissionDetectorHandler(context)

    class NoInternetPermissionDetectorHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression)
        {
            when{
                Helper.nodeIsRetrofitExecute(node)->{
                    context.report(IssueUsageSyncExecute.ISSUE_USAGE_SYNC_EXEC, node, context.getLocation(node), "It seems like synchronous operations are being implemented, avoid such approach.")
                }
                Helper.nodeIsOkHttpExecute(node)->{
                    context.report(IssueUsageSyncExecute.ISSUE_USAGE_SYNC_EXEC, node, context.getLocation(node), "It seems like synchronous operations are being implemented, avoid such approach.")
                }
                Helper.nodeIsJavaNetOpenConn(node)->{
                    context.report(IssueUsageSyncExecute.ISSUE_USAGE_SYNC_EXEC, node, context.getLocation(node), "It seems like synchronous operations are being implemented, avoid such approach.")
                }
            }
        }
    }
}



