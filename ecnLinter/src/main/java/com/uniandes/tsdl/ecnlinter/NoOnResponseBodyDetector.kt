package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoOnFailureImplemented
import com.uniandes.tsdl.ecnlinter.issues.IssueNoOnResponseBody
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

class NoOnResponseBodyDetector: Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        val types: MutableList<Class<out UElement?>> = ArrayList()
        types.add(UCallExpression::class.java)
        return types
    }

    override fun createUastHandler(context: JavaContext) =
        EmptyOnResponseDetectorHandler(context)


    class EmptyOnResponseDetectorHandler(private val context: JavaContext) :
        UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            if (Helper.nodeIsRetrofitEnqueue(node) || Helper.nodeIsOkHttpEnqueue(node)) {
                node.accept(object : AbstractUastVisitor() {
                    override fun visitMethod(node: UMethod): Boolean {
                        if (node.name == "onResponse") {
                            if (node.uastBody == null) {
                                //no implementa onresponse
                                context.report(
                                    IssueNoOnResponseBody.ISSUE_NO_ON_RESPONSE_BODY,
                                    node,
                                    context.getLocation(node),
                                    "It seems like the `onResponse()` method is not being implemented. Network Methods with callback parameters require an implementation of the `onResponse` method."
                                )
                            } else {
                                if (node.uastBody is UBlockExpression) {
                                    val expressions =
                                        (node.uastBody as UBlockExpression).expressions
                                    if (expressions.isEmpty()) {
                                        context.report(
                                            IssueNoOnResponseBody.ISSUE_NO_ON_RESPONSE_BODY,
                                            node,
                                            context.getLocation(node),
                                            "It seems like the `onResponse()` method is not being implemented. Network Methods with callback parameters require an implementation of the `onResponse` method."
                                        )
                                    }
                                }
                            }
                        }
                        return super.visitMethod(node)
                    }
                })
            }
            else if(Helper.nodeIsVolleyEnqueue(node)){
                var shouldCheckRefs = (node.valueArguments.first() is UReferenceExpression)
                node.accept(
                    object : AbstractUastVisitor() {
                        var hasListener = false
                        override fun visitCallExpression(node: UCallExpression): Boolean {
                            if (node.methodName == "Listener" && node.returnType?.canonicalText?.contains("com.android.volley.Response.Listener") == true)
                            {
                                node.accept(
                                    object : AbstractUastVisitor() {
                                        override fun visitBlockExpression(node: UBlockExpression): Boolean {
                                            if(node.expressions.isNotEmpty()){
                                                hasListener = true
                                            }
                                            return super.visitBlockExpression(node)
                                        }
                                    }
                                )
                            }
                            return super.visitCallExpression(node)
                        }

                        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression): Boolean {
                            if(shouldCheckRefs) {
                                (node.getContainingDeclaration() as UMethod).accept(
                                    object : AbstractUastVisitor() {
                                        override fun visitCallExpression(node: UCallExpression): Boolean {
                                            if (node.methodName == "Listener" && node.returnType?.canonicalText?.contains(
                                                    "com.android.volley.Response.Listener"
                                                ) == true
                                            ) {
                                                node.accept(
                                                    object : AbstractUastVisitor() {
                                                        override fun visitBlockExpression(node: UBlockExpression): Boolean {
                                                            if (node.expressions.isNotEmpty()) {
                                                                hasListener = true
                                                            }
                                                            return super.visitBlockExpression(
                                                                node
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                            return super.visitCallExpression(node)
                                        }
                                    }
                                )
                            }
                            return super.visitSimpleNameReferenceExpression(node)
                        }

                        override fun afterVisitCallExpression(node: UCallExpression) {
                            super.afterVisitCallExpression(node)
                            //arrayOf contains the name of the possible call expressions that are called before finishing the general visit
                            if(!hasListener && !shouldCheckRefs && node.methodName !in arrayOf("ErrorListener", "StringRequest", "JsonObjectRequest", "add")){
                                context.report(
                                    IssueNoOnResponseBody.ISSUE_NO_ON_RESPONSE_BODY,
                                    node,
                                    context.getLocation(node),
                                    "It seems like the `onResponse()` method is not being implemented. Network Methods with callback parameters require an implementation of the `onResponse` method."
                                )
                            }
                        }

                        override fun afterVisitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
                            super.afterVisitSimpleNameReferenceExpression(node)
                            if(!hasListener && shouldCheckRefs){
                                context.report(
                                    IssueNoOnResponseBody.ISSUE_NO_ON_RESPONSE_BODY,
                                    node,
                                    context.getLocation(node),
                                    "It seems like the `onResponse()` method is not being implemented. Network Methods with callback parameters require an implementation of the `onResponse` method."
                                )
                            }
                        }
                    })
            }
        }
    }
}
