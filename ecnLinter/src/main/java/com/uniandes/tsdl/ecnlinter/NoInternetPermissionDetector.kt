package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoInternetPermission
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement

class NoInternetPermissionDetector : Detector(), Detector.UastScanner {

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
            if (Helper.nodeIsNetworkRequest(node) && !Helper.hasInternetPermission(context))
            {
                return context.report(IssueNoInternetPermission.ISSUE_NO_INTERNET_PERMISSION, node, context.getLocation(node), "It seems like network operations are being implemented, such operations need explicit permission in the manifest.")
            }
        }

    }
}



