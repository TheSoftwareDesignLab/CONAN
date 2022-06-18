package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoNetworkStatePermission
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UImportStatement

private const val CONNMAN = "android.net.ConnectivityManager"
private const val NETINFO = "android.net.NetworkInfo"

class NoNetworkStatePermissionDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        val types: MutableList<Class<out UElement?>> = ArrayList()
        types.add(UImportStatement::class.java)
        return types
    }

    override fun createUastHandler(context: JavaContext) = NoNetworkStatePermissionHandler(context)


    class NoNetworkStatePermissionHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitImportStatement(node: UImportStatement) {
            val listOfImports = listOf(CONNMAN, NETINFO)
            node.importReference?.let { importReference ->
                val import = importReference.asSourceString()
                val foundLibrary = listOfImports.find { item -> import.startsWith(item) }
                if (foundLibrary!=null && !Helper.hasNetworkStatePermission(context)) {
                    return context.report(
                        IssueNoNetworkStatePermission.ISSUE_NO_NETWORK_STATE_PERMISSION, node, context.getLocation(node),
                        "It seems like network information is being checked, such operation needs explicit permission in the manifest"
                    )
                }
            }
        }
    }
}