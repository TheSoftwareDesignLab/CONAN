package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.intellij.psi.impl.compiled.ClsClassImpl
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueResponseCodeNotValidated
import com.uniandes.tsdl.ecnlinter.issues.IssueResponseContentNotValidated
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

var rscusesJavaNetConn = false
var rscchecksResponseCode = false
var rscnode : UCallExpression? = null
var reportedrsc = false
class NoResponseCodeValidationDetector: Detector(), Detector.UastScanner{
    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        val types: MutableList<Class<out UElement?>> = ArrayList()
        types.add(UCallExpression::class.java)
        return types
    }

    override fun createUastHandler(context: JavaContext) =
        ResponseCodeNotValidatedDetectorHandler(context)

    override fun afterCheckFile(context: Context) {
        super.afterCheckFile(context)
        if(rscusesJavaNetConn && ! rscchecksResponseCode && rscnode != null && !reportedrsc){
            context.report(IssueResponseCodeNotValidated.ISSUE_RESPONSE_CODE_NOT_VALIDATED, context.getLocation(rscnode!!), "It seems like the response code is not being validated when performing network operations.")
            reportedrsc = true
        }
    }

    class ResponseCodeNotValidatedDetectorHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression) {
            when {
                Helper.nodeIsRetrofitEnqueue(node) -> {
                    node.accept(object : AbstractUastVisitor() {
                        override fun visitMethod(node: UMethod): Boolean {
                            if(node.name=="onResponse"){
                                if(node.uastBody!=null){
                                    if(node.uastBody is UBlockExpression) {
                                        val expressions = (node.uastBody as UBlockExpression).expressions
                                        if(expressions.isNotEmpty()) {
                                            var checkExists = false
                                            expressions.forEach {
                                                if(it is UIfExpression ){
                                                    val cond = it.condition
                                                    cond.accept(object : AbstractUastVisitor() {
                                                        override fun visitCallExpression(node: UCallExpression): Boolean {
                                                            if(node.methodName=="code"&&
                                                                node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("retrofit2.Response") == true){
                                                                checkExists = true
                                                            }
                                                            return super.visitCallExpression(node)
                                                        }
                                                    })
                                                }
                                                if(it is USwitchExpression){
                                                    val cond = it.expression
                                                    cond?.accept(object : AbstractUastVisitor() {
                                                        override fun visitCallExpression(node: UCallExpression): Boolean {
                                                            if((node.methodName=="code")&&
                                                                node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("retrofit2.Response") == true){
                                                                checkExists = true
                                                            }
                                                            return super.visitCallExpression(node)
                                                        }
                                                    })
                                                }
                                            }
                                            if(!checkExists){
                                                context.report(IssueResponseCodeNotValidated.ISSUE_RESPONSE_CODE_NOT_VALIDATED, node, context.getLocation(node), "It seems like the response code is not being validated when performing network operations.")
                                            }
                                        }
                                    }
                                }
                            }
                            return super.visitMethod(node)
                        }
                    })
                }
                Helper.nodeIsVolleyEnqueue(node) -> {
                    node.accept(object: AbstractUastVisitor(){
                        var checkExists = false
                        override fun visitCallExpression(node: UCallExpression): Boolean {
                            if(node.methodName=="parseNetworkResponse" && node.returnType?.canonicalText=="com.android.volley.Response"){
                                node.accept(object : AbstractUastVisitor() {
                                    override fun visitCallExpression(node: UCallExpression): Boolean {
                                        if(node.tryResolveNamed()?.name=="statusCode" && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains("com.android.volley.NetworkResponse")){
                                            checkExists = true
                                        }
                                        return super.visitCallExpression(node)
                                    }

                                    //override fun afterVisitCallExpression(node: UCallExpression) {
                                    //    super.afterVisitCallExpression(node)
                                    //    if(!checkExists){
                                    //        context.report(IssueResponseCodeNotValidated.ISSUE_RESPONSE_CODE_NOT_VALIDATED, node, context.getLocation(node), "The `onResponse()` method does not validate the response status code")
                                    //    }
                                    //}

                                })
                            }
                            return super.visitCallExpression(node)
                        }
                        override fun afterVisitCallExpression(node: UCallExpression) {
                            super.afterVisitCallExpression(node)
                            if(!checkExists && node.methodName !in arrayOf("Listener", "ErrorListener", "StringRequest", "JsonObjectRequest")){
                                context.report(IssueResponseCodeNotValidated.ISSUE_RESPONSE_CODE_NOT_VALIDATED, node, context.getLocation(node), "It seems like the response code is not being validated when performing network operations.")
                            }
                        }
                    })
                }
                Helper.nodeIsOkHttpEnqueue(node) -> {
                    node.accept(object : AbstractUastVisitor() {
                        override fun visitMethod(node: UMethod): Boolean {
                            if(node.name=="onResponse"){
                                if(node.uastBody!=null){
                                    if(node.uastBody is UBlockExpression) {
                                        val expressions = (node.uastBody as UBlockExpression).expressions
                                        if(expressions.isNotEmpty()) {
                                            var checkExists = false
                                            expressions.forEach {
                                                if(it is UIfExpression ){
                                                    val cond = it.condition
                                                    cond.accept(object : AbstractUastVisitor() {
                                                        override fun visitCallExpression(node: UCallExpression): Boolean {
                                                            if((node.methodName=="code" || node.methodName=="isSuccessful" ) &&
                                                                node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("okhttp3.Response") == true){
                                                                checkExists = true
                                                            }
                                                            return super.visitCallExpression(node)
                                                        }
                                                    })
                                                }
                                                if(it is USwitchExpression){
                                                    val cond = it.expression
                                                    cond?.accept(object : AbstractUastVisitor() {
                                                        override fun visitCallExpression(node: UCallExpression): Boolean {
                                                            if((node.methodName=="code" || node.methodName=="isSuccessful" ) &&
                                                                node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("okhttp3.Response") == true){
                                                                checkExists = true
                                                            }
                                                            return super.visitCallExpression(node)
                                                        }
                                                    })
                                                }
                                            }
                                            if(!checkExists){
                                                context.report(IssueResponseCodeNotValidated.ISSUE_RESPONSE_CODE_NOT_VALIDATED, node, context.getLocation(node), "It seems like the response code is not being validated when performing network operations.")
                                            }
                                        }
                                    }
                                }
                            }
                            return super.visitMethod(node)
                        }
                    })
                }
                Helper.nodeIsJavaNetOpenConn(node)->{
                    rscusesJavaNetConn = true
                    rscnode = node
                }
                node.methodName == "getResponseCode"  && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains("java.net")-> {
                    rscchecksResponseCode = true
                }
            }
        }
    }
}