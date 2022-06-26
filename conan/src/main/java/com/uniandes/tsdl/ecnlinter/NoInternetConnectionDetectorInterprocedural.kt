package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.interprocedural.CallGraphResult
import com.android.tools.lint.detector.api.interprocedural.buildContextualCallGraph
import com.android.tools.lint.detector.api.interprocedural.searchForContextualPaths
import com.intellij.psi.impl.compiled.ClsClassImpl
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoCheckNetworkConnectionInProject
import com.uniandes.tsdl.ecnlinter.issues.IssueNoInternetConnectionCheck
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.visitor.AbstractUastVisitor

class NoInternetConnectionDetectorInterprocedural : Detector(), SourceCodeScanner {

    override fun isCallGraphRequired(): Boolean { return true }

    override fun analyzeCallGraph(context: Context, callGraph: CallGraphResult) {

        val contextualGraph = callGraph.callGraph.buildContextualCallGraph(callGraph.receiverEval)

        val networkOperationTriggerNodes = contextualGraph.contextualNodes.filter { contextualNode ->
            val element = contextualNode.node.target.element
            if (element is UMethod) {
                var hasInnerCallinNOT = false
                element.accept(
                    object : AbstractUastVisitor() {
                        override fun visitCallExpression(node: UCallExpression): Boolean {
                            if(node is UCallExpression)
                            {
                                if(Helper.nodeIsNetworkRequest(node))
                                {
                                    hasInnerCallinNOT = true
                                }
                            }
                            return super.visitCallExpression(node)
                        }
                    })
                hasInnerCallinNOT
            }
            else {
                false
            }
        }

        val internetCheckNodes = contextualGraph.contextualNodes.filter { contextualNode ->
            val element = contextualNode.node.target.element
            if (element is UMethod) {
                var hasInnerCallinCheck = false
                element.accept(
                    object : AbstractUastVisitor() {
                        override fun visitCallExpression(node: UCallExpression): Boolean {
                            if (node is UCallExpression) {
                                if (node.methodName.equals("hasCapability")
                                    && (node.valueArguments[0].asSourceString().contains("NET_CAPABILITY_INTERNET") ||
                                            node.valueArguments[0].asSourceString().contains("NET_CAPABILITY_VALIDATED"))
                                    && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(
                                        Helper.ANDROID_NET))
                                {
                                    hasInnerCallinCheck = true
                                }
                            }
                            return super.visitCallExpression(node)
                        }
                    })

                hasInnerCallinCheck
            }
            else {
                false
            }
        }

        if (networkOperationTriggerNodes.isNotEmpty())
        {
            if(internetCheckNodes.isNotEmpty())
            {
                val paths = contextualGraph.searchForContextualPaths(networkOperationTriggerNodes, internetCheckNodes)

                if(paths.isEmpty())
                {
                    for ( operation in networkOperationTriggerNodes)
                    {
                        //REPORT ISSUE: It is doing Network Operations, however, there is no network verification within the method
                        val parser = context.client.getUastParser(context.project)
                        val location = parser.createLocation(operation.node.target.element)
                        reportNoInternetVerificationWithinMethod(context, location)
                    }
                }
                else
                {
                    for(operation in networkOperationTriggerNodes)
                    {
                        val validationWithinMethod = contextualGraph.searchForContextualPaths(listOf(operation), internetCheckNodes)
                        if (validationWithinMethod.isEmpty())
                        {
                            val parser = context.client.getUastParser(context.project)
                            val location = parser.createLocation(operation.node.target.element)
                            reportNoInternetVerificationWithinMethod(context, location)
                        }
                    }
                }
            }
            else
            {
                val parser = context.client.getUastParser(context.project)


                for ( operation in networkOperationTriggerNodes)
                {
                    val location = parser.createLocation(operation.node.target.element)

                    //REPORT ISSUE: It is doing Network Operations, however, there is no network verification within the method
                    reportNoInternetVerificationWithinMethod(context, location)
                    //REPORT ISSUE: It is doing Network Operations, however, there is no network verification within the project
                    reportNoInternetVerificationWithinProject(context, location)
                }

            }
        }
    }

    private fun reportNoInternetVerificationWithinProject(context: Context, location: Location) {
        context.report(
            issue = IssueNoInternetConnectionCheck.ISSUE_NO_INTERNET_CONNECTION_CHECK_PROJECT,
            location = location,
            message = "It seems like network operations are being implemented in this project but **Internet connection availability** is not verified **in the project**"
        )
    }

    private fun reportNoInternetVerificationWithinMethod(context: Context, location: Location) {
        context.report(
            issue = IssueNoInternetConnectionCheck.ISSUE_NO_INTERNET_CONNECTION_CHECK_METHOD,
            location = location,
            message = "It seems like network operations are being implemented in this project but **Internet connection availability** is not verified **in the method**"
        )
    }

}