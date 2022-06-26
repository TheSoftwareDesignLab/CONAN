package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.intellij.psi.impl.compiled.ClsClassImpl
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoOnResponseBody
import com.uniandes.tsdl.ecnlinter.issues.IssueResponseCodeNotValidated
import com.uniandes.tsdl.ecnlinter.issues.IssueResponseContentNotValidated
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

var rcusesJavaNetConn = false
var rcchecksResponseContent = false
var rcnode : UCallExpression? = null
var reportedrc = false
class NoResponseContentValidationDetector: Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        val types: MutableList<Class<out UElement?>> = ArrayList()
        types.add(UCallExpression::class.java)
        return types
    }

    override fun createUastHandler(context: JavaContext) =
        ResponseContentNotValidatedDetectorHandler(context)

    override fun afterCheckFile(context: Context) {
        super.afterCheckFile(context)
        if(rcusesJavaNetConn && ! rcchecksResponseContent && rcnode != null && !reportedrc){
            context.report(IssueResponseContentNotValidated.ISSUE_RESPONSE_CONTENT_NOT_VALIDATED, context.getLocation(rcnode!!), "It seems like the response content is not being validated when performing network operations.")
            reportedrc = true
        }
    }
    //override fun afterCheckEachProject(context: Context) {
     //   super.afterCheckEachProject(context)
     //   if(rcusesJavaNetConn && ! rcchecksResponseContent && rcnode != null){
     //       context.report(IssueResponseContentNotValidated.ISSUE_RESPONSE_CONTENT_NOT_VALIDATED, context.getLocation(rcnode!!), "The response content might not be being validated")
     //   }
    //}

    class ResponseContentNotValidatedDetectorHandler(private val context: JavaContext) : UElementHandler() {

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
                                                            if(node.methodName=="body"&&
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
                                                            if(node.methodName=="body"&&
                                                                node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("retrofit2.Response") == true){
                                                                checkExists = true
                                                            }
                                                            return super.visitCallExpression(node)
                                                        }
                                                    })
                                                }
                                            }
                                            if(!checkExists){
                                                context.report(IssueResponseContentNotValidated.ISSUE_RESPONSE_CONTENT_NOT_VALIDATED, node, context.getLocation(node), "It seems like the response content is not being validated when performing network operations.")
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
                    var checkExists = false
                    var shouldCheckRefs = (node.valueArguments.first() is UReferenceExpression)
                    node.accept(
                        object : AbstractUastVisitor() {
                            override fun visitCallExpression(node: UCallExpression): Boolean {
                                if (node.methodName == "Listener" && node.returnType?.canonicalText?.contains("com.android.volley.Response.Listener") == true)
                                {
                                    node.accept(
                                        object : AbstractUastVisitor() {
                                            override fun visitBlockExpression(node: UBlockExpression): Boolean {
                                                if(node.expressions.isNotEmpty()){
                                                    node.expressions.forEach {
                                                        if(it is UIfExpression){
                                                            val cond = it.condition
                                                            cond.accept(object : AbstractUastVisitor() {
                                                                override fun visitCallExpression(node: UCallExpression): Boolean {
                                                                    if(node.methodName=="body" &&
                                                                        node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("com.android.volley.Response") == true){
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
                                                                    if(node.methodName=="body"  &&
                                                                        node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("com.android.volley.Response") == true){
                                                                        checkExists = true
                                                                    }
                                                                    return super.visitCallExpression(node)
                                                                }
                                                            })
                                                        }
                                                    }
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
                                                                    node.expressions.forEach {
                                                                        if(it is UIfExpression){
                                                                            val cond = it.condition
                                                                            cond.accept(object : AbstractUastVisitor() {
                                                                                override fun visitCallExpression(node: UCallExpression): Boolean {
                                                                                    if(node.methodName=="body" &&
                                                                                        node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("com.android.volley.Response") == true){
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
                                                                                    if(node.methodName=="body"  &&
                                                                                        node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("com.android.volley.Response") == true){
                                                                                        checkExists = true
                                                                                    }
                                                                                    return super.visitCallExpression(node)
                                                                                }
                                                                            })
                                                                        }
                                                                    }
                                                                }
                                                                return super.visitBlockExpression(node)
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
                                if(!checkExists && !shouldCheckRefs && node.methodName !in arrayOf("ErrorListener", "StringRequest", "JsonObjectRequest", "add")){
                                    context.report(IssueResponseContentNotValidated.ISSUE_RESPONSE_CONTENT_NOT_VALIDATED, node, context.getLocation(node), "It seems like the response content is not being validated when performing network operations.")
                                }
                            }

                            override fun afterVisitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
                                super.afterVisitSimpleNameReferenceExpression(node)
                                if(!checkExists && shouldCheckRefs){
                                    context.report(IssueResponseContentNotValidated.ISSUE_RESPONSE_CONTENT_NOT_VALIDATED, node, context.getLocation(node), "It seems like the response content is not being validated when performing network operations.")
                                }
                            }
                        })
                }
                Helper.nodeIsOkHttpEnqueue(node) -> {
                    node.accept(object: AbstractUastVisitor(){
                        override fun visitMethod(node: UMethod): Boolean {
                            if(node.name=="onResponse"){
                                if(node.uastBody is UBlockExpression) {
                                    val expressions = (node.uastBody as UBlockExpression).expressions
                                    if(expressions.isNotEmpty()) {
                                        var checkExists = false
                                        expressions.forEach {
                                            if(it is UIfExpression){
                                                val cond = it.condition
                                                cond.accept(object : AbstractUastVisitor() {
                                                    override fun visitCallExpression(node: UCallExpression): Boolean {
                                                        if(node.methodName=="body" &&
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
                                                        if(node.methodName=="body"  &&
                                                            node.resolve()?.hierarchicalMethodSignature?.method?.containingClass?.qualifiedName?.contains("okhttp3.Response") == true){
                                                            checkExists = true
                                                        }
                                                        return super.visitCallExpression(node)
                                                    }
                                                })
                                            }
                                        }
                                        if(!checkExists){
                                            context.report(IssueResponseContentNotValidated.ISSUE_RESPONSE_CONTENT_NOT_VALIDATED, node, context.getLocation(node), "It seems like the response content is not being validated when performing network operations.")
                                        }
                                    }
                                }
                            }
                            return super.visitMethod(node)
                        }
                    })
                }
                Helper.nodeIsJavaNetOpenConn(node)->{
                    rcusesJavaNetConn = true
                    rcnode = node
                }
                (node.methodName == "getResponseMessage" || node.methodName == "getInputStream") && (node.resolve()?.parent as? ClsClassImpl)?.stub?.qualifiedName.toString().contains("java.net")-> {
                    rcchecksResponseContent = true
                }
            }
        }
    }
}