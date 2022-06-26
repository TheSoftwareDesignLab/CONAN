package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoOnFailureImplemented
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor


class NoOnFailureImplementedDetector : Detector(), Detector.UastScanner {



    override fun getApplicableUastTypes(): List<Class<out UElement?>>
    {
        val types: MutableList<Class<out UElement?>> = ArrayList()
        types.add(UCallExpression::class.java)
        return types
    }

    override fun createUastHandler(context: JavaContext) = NonOFailureImplementedDetectorHandler(context)

    class NonOFailureImplementedDetectorHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression)
        {
           when{
               (Helper.nodeIsRetrofitEnqueue(node) || Helper.nodeIsOkHttpEnqueue(node))->{
                   node.accept(
                       object : AbstractUastVisitor() {
                           override fun visitMethod(node: UMethod): Boolean {
                               if (node is UMethod)
                               {
                                   if (node.name == "onFailure")
                                   {
                                      if(node.uastBody is UBlockExpression)
                                      {
                                          val expressions = (node.uastBody as UBlockExpression).expressions
                                          if(expressions.isEmpty())
                                          {
                                              context.report(IssueNoOnFailureImplemented.ISSUE_NO_ON_FAILURE_IMPLEMENTED_EMPTY, node, context.getLocation(node), "It seems like the network request is not being assessed on failure.")
                                          }
                                      }
                                   }
                               }
                               return super.visitMethod(node)
                           }
                       })
               }
               Helper.nodeIsVolleyEnqueue(node)->{
                   var shouldCheckRefs = (node.valueArguments.first() is UReferenceExpression)
                   node.accept(
                       object : AbstractUastVisitor() {
                           var hasErrorListener = false
                           override fun visitCallExpression(node: UCallExpression): Boolean {
                               if (node.methodName == "ErrorListener" && node.returnType?.canonicalText?.contains("com.android.volley.Response.ErrorListener") == true)
                               {
                                   node.accept(
                                       object : AbstractUastVisitor() {
                                           override fun visitBlockExpression(node: UBlockExpression): Boolean {
                                               if(node.expressions.isNotEmpty()){
                                                   hasErrorListener = true
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
                                               if (node.methodName == "ErrorListener" && node.returnType?.canonicalText?.contains(
                                                       "com.android.volley.Response.ErrorListener"
                                                   ) == true
                                               ) {
                                                   node.accept(
                                                       object : AbstractUastVisitor() {
                                                           override fun visitBlockExpression(node: UBlockExpression): Boolean {
                                                               if (node.expressions.isNotEmpty()) {
                                                                   hasErrorListener = true
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
                               if(!hasErrorListener && !shouldCheckRefs && node.methodName !in arrayOf("Listener", "StringRequest", "JsonObjectRequest", "add")){
                                   context.report(IssueNoOnFailureImplemented.ISSUE_NO_ON_FAILURE_IMPLEMENTED_EMPTY, node, context.getLocation(node), "It seems like the network request is not being assessed on failure.")
                               }
                           }


                           override fun afterVisitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
                               super.afterVisitSimpleNameReferenceExpression(node)
                               if(!hasErrorListener && shouldCheckRefs){
                                   context.report(IssueNoOnFailureImplemented.ISSUE_NO_ON_FAILURE_IMPLEMENTED_EMPTY, node, context.getLocation(node), "It seems like the network request is not being assessed on failure.")
                               }
                           }
                       })
               }
               Helper.nodeIsJavaNetOpenConn(node)->{
                   if (node.uastParent !is UTryExpression){
                       context.report(IssueNoOnFailureImplemented.ISSUE_NO_ON_FAILURE_IMPLEMENTED_EMPTY, node, context.getLocation(node), "It seems like the network request is not being assessed on failure.")
                   }
                   else{
                       var tryExpression = (node.uastParent as UTryExpression)
                       var implementsOnFailure = tryExpression.catchClauses.isEmpty()
                       tryExpression.catchClauses.forEach {
                           if(it.body is UBlockExpression)
                           {
                               val expressions = (it.body as UBlockExpression).expressions
                               if(expressions.isNotEmpty())
                               {
                                   implementsOnFailure = true
                               }
                           }
                       }
                       if(!implementsOnFailure){
                           context.report(IssueNoOnFailureImplemented.ISSUE_NO_ON_FAILURE_IMPLEMENTED_EMPTY, node, context.getLocation(node), "It seems like the network request is not being assessed on failure.")
                       }
                   }
               }
           }
        }
    }
}



