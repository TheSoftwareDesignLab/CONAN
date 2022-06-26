package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.uniandes.tsdl.ecnlinter.issues.IssueNoMoreThanOneConstructor
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UastCallKind

private const val OKHTTP = "okhttp3.OkHttpClient"

private val okHttpClientConstructors = mutableSetOf<OkHttpContextLocation>()

data class OkHttpContextLocation(val context: JavaContext, val location: Location)

class NoSingleInstanceOkhttpDetector : Detector(), Detector.UastScanner {

    override fun afterCheckRootProject(context: Context)
    {
        if(okHttpClientConstructors.size > 1)
        {
            for(callContextLocation in okHttpClientConstructors)
            {
                reportMoreThanOneConstructor(callContextLocation.context, callContextLocation.location)
            }
        }
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? = NoSingleInstanceOkhttpHandler(context)

    class NoSingleInstanceOkhttpHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression)
        {
            if(node.getExpressionType()?.canonicalText == OKHTTP && node.kind == UastCallKind.CONSTRUCTOR_CALL)
            {
                okHttpClientConstructors.add(OkHttpContextLocation(context, context.getLocation(node)))
            }
        }
    }

    private fun reportMoreThanOneConstructor(context: JavaContext, location: Location) {
        context.report(
            issue = IssueNoMoreThanOneConstructor.ISSUE_NO_MORE_ONE_CONST_OKHTTP,
            location = location,
            message = "It seems like this project is employing more than one `OkHttpClient` instance."
        )
    }
}