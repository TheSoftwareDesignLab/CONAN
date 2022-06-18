package com.uniandes.tsdl.ecnlinter.helpers

import com.android.SdkConstants
import com.android.tools.lint.checks.PermissionHolder
import com.android.tools.lint.checks.PermissionRequirement
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.utils.XmlUtils
import com.google.common.collect.Sets
import com.intellij.psi.impl.compiled.ClsClassImpl
import org.jetbrains.uast.UCallExpression

object Helper {

    private const val INTERNET_PERMISSION = "android.permission.INTERNET"
    private const val NETWORK_STATE_PERMISSION = "android.permission.ACCESS_NETWORK_STATE"
    const val ANDROID_NET = "android.net"

    const val RETROFIT_PACKAGE = "retrofit2"
    const val OKHTTP_PACKAGE = "okhttp3"
    const val VOLLEY_PACKAGE = "volley"
    const val JAVANET_PACKAGE1 = "java.net.URLConnection"
    const val JAVANET_PACKAGE2 = "java.net.HttpURLConnection"
    const val JAVANET_PACKAGE3 = "java.net.URL"
    const val WEBVIEW_PACKAGE  = "android.webkit.WebView"


    val CONNECTIVITY = Category.create("Connectivity",10)

    private var mPermissions: PermissionHolder? = null

    fun getPermissions(context: Context): PermissionHolder {
        if (mPermissions == null) {
            val permissions = Sets.newHashSetWithExpectedSize<String>(30)
            val revocable = Sets.newHashSetWithExpectedSize<String>(4)
            val mainProject = context.mainProject
            val mergedManifest = mainProject.mergedManifest
            if (mergedManifest != null) {
                for (element in XmlUtils.getSubTags(mergedManifest.documentElement)) {
                    val nodeName = element.nodeName
                    if (SdkConstants.TAG_USES_PERMISSION == nodeName ||
                        SdkConstants.TAG_USES_PERMISSION_SDK_23 == nodeName ||
                        SdkConstants.TAG_USES_PERMISSION_SDK_M == nodeName
                    ) {
                        val name = element.getAttributeNS(
                            SdkConstants.ANDROID_URI,
                            SdkConstants.ATTR_NAME
                        )
                        if (!name.isEmpty()) {
                            permissions.add(name)
                        }
                    } else if (nodeName == SdkConstants.TAG_PERMISSION) {
                        val protectionLevel = element.getAttributeNS(
                            SdkConstants.ANDROID_URI,
                            PermissionRequirement.ATTR_PROTECTION_LEVEL
                        )
                        if (PermissionRequirement.VALUE_DANGEROUS == protectionLevel) {
                            val name = element.getAttributeNS(
                                SdkConstants.ANDROID_URI,
                                SdkConstants.ATTR_NAME
                            )
                            if (!name.isEmpty()) {
                                revocable.add(name)
                            }
                        }
                    }
                }
            }
            val minSdkVersion = mainProject.minSdkVersion
            val targetSdkVersion = mainProject.targetSdkVersion
            mPermissions = PermissionHolder.SetPermissionLookup(
                permissions,
                revocable,
                minSdkVersion,
                targetSdkVersion
            )
        }
        return mPermissions!!
    }

    fun hasInternetPermission(context: Context): Boolean {
        return getPermissions(context).hasPermission(INTERNET_PERMISSION)
    }

    fun hasNetworkStatePermission(context: Context): Boolean {
        return getPermissions(context).hasPermission(NETWORK_STATE_PERMISSION)
    }

    fun nodeIsRetrofitEnqueue(node: UCallExpression): Boolean
    {
        return node.methodName.equals("enqueue") && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(RETROFIT_PACKAGE)
    }

    fun nodeIsRetrofitExecute(node: UCallExpression): Boolean
    {
        return node.methodName.equals("execute") && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(RETROFIT_PACKAGE)
    }

    fun nodeIsOkHttpEnqueue(node: UCallExpression): Boolean
    {
        return node.methodName.equals("enqueue") && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(OKHTTP_PACKAGE)
    }

    fun nodeIsOkHttpExecute(node: UCallExpression): Boolean
    {
        return node.methodName.equals("execute") && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(OKHTTP_PACKAGE)
    }

    fun nodeIsVolleyEnqueue(node: UCallExpression): Boolean
    {
        return node.methodName.equals("add") && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(VOLLEY_PACKAGE)
    }

    fun nodeIsJavaNetOpenConn(node: UCallExpression): Boolean
    {
        return node.methodName.equals("openConnection") && (
                (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(JAVANET_PACKAGE1)
                        ||(node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(JAVANET_PACKAGE2)
                        || (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString() == JAVANET_PACKAGE3
                )
    }

    fun nodeIsWebView(node: UCallExpression): Boolean
    {
        return node.methodName.equals("loadUrl") && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(WEBVIEW_PACKAGE) && node.valueArguments[0].asSourceString().contains("http")
    }

    fun nodeIsNetworkRequest(node: UCallExpression) : Boolean
    {
        return nodeIsRetrofitEnqueue(node) || nodeIsRetrofitExecute(node) || nodeIsOkHttpEnqueue(node) || nodeIsOkHttpExecute(node) || nodeIsVolleyEnqueue(node) || nodeIsJavaNetOpenConn(node)
    }
}