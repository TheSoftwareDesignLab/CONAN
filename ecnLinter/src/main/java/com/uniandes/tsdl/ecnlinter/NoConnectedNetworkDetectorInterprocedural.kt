package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.interprocedural.*
import com.intellij.psi.impl.compiled.ClsClassImpl
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoCheckNetworkConnectionInProject
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.visitor.AbstractUastVisitor

class NoConnectedNetwrokDetectedInterprocedural : Detector(), SourceCodeScanner {

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

        val networkCheckNodes = contextualGraph.contextualNodes.filter { contextualNode ->
            val element = contextualNode.node.target.element
            if (element is UMethod) {
                var hasInnerCallinCheck = false
                element.accept(
                    object : AbstractUastVisitor() {
                        override fun visitCallExpression(node: UCallExpression): Boolean {
                            if (node is UCallExpression) {
                                //println((node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString())
                                if ((node.methodName.equals("isConnected") || node.methodName.equals("onAvailable")) && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains(Helper.ANDROID_NET))
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
            if(networkCheckNodes.isNotEmpty())
            {
                val paths = contextualGraph.searchForContextualPaths(networkOperationTriggerNodes, networkCheckNodes)

                if(paths.isEmpty())
                {
                    for ( operation in networkOperationTriggerNodes)
                    {
                        //REPORT ISSUE: It is doing Network Operations, however, there is no network verification within the method
                        val parser = context.client.getUastParser(context.project)
                        val location = parser.createLocation(operation.node.target.element)
                        reportNoNetworkVerificationWithinMethod(context, location)
                    }
                }
                else
                {
                    for(operation in networkOperationTriggerNodes)
                    {
                        val validationWithinMethod = contextualGraph.searchForContextualPaths(listOf(operation), networkCheckNodes)
                        if (validationWithinMethod.isEmpty())
                        {
                            val parser = context.client.getUastParser(context.project)
                            val location = parser.createLocation(operation.node.target.element)
                            reportNoNetworkVerificationWithinMethod(context, location)
                        }
                    }
                }
            }
            else
            {
                for ( operation in networkOperationTriggerNodes)
                {
                    val parser = context.client.getUastParser(context.project)
                    val location = parser.createLocation(operation.node.target.element)

                    //REPORT ISSUE: It is doing Network Operations, however, there is no network verification within the method
                    reportNoNetworkVerificationWithinMethod(context, location)
                    //REPORT ISSUE: It is doing Network Operations, however, there is no network verification within the project
                    reportNoNetworkVerificationWithinProject(context, location)
                }
            }
        }
    }

    private fun reportNoNetworkVerificationWithinProject(context: Context, location: Location) {
        context.report(
            issue = IssueNoCheckNetworkConnectionInProject.ISSUE_NO_CHECK_NETWORK_CONNECTION_IN_PROJECT_SCOPE,
            location = location,
            message = "It seems like network operations are being implemented in this project but **network connection availability** is not verified **in the project**"
        )
    }

    private fun reportNoNetworkVerificationWithinMethod(context: Context, location: Location) {
        context.report(
            issue = IssueNoCheckNetworkConnectionInProject.ISSUE_NO_CHECK_NETWORK_CONNECTION_IN_METHOD_SCOPE,
            location = location,
            message = "It seems like network operations are being implemented in this method but **network connection availability** is not verified **in the method**"
        )
    }

}
