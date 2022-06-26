package com.uniandes.tsdl.ecnlinter

import com.android.SdkConstants.*
import com.android.tools.lint.detector.api.*
import com.android.utils.XmlUtils.getFirstSubTagByName
import com.android.utils.XmlUtils.getSubTagsByName
import com.uniandes.tsdl.ecnlinter.helpers.Helper
import com.uniandes.tsdl.ecnlinter.issues.IssueNoManageNetworkUsage
import org.w3c.dom.Element;

private val elementLocation = mutableSetOf<ElementLocation>()

data class ElementLocation(val context: Context, val location: Location)

private var isThereManNetUse = false


class NoActionManageNetworkUsageDetector : Detector(), Detector.XmlScanner , XmlScanner {

    override fun getApplicableElements(): Collection<String>? {
        return listOf(TAG_ACTIVITY)
    }
    override fun visitElement(context: XmlContext, element: Element)
    {
            val filter: Element? = getFirstSubTagByName(element, TAG_INTENT_FILTER)
            if (filter != null) {
                for (action: Element in getSubTagsByName(filter, TAG_ACTION))
                {
                    val actionName: String = action.getAttributeNS(ANDROID_URI, ATTR_NAME)
                    elementLocation.add(ElementLocation(context,context.getLocation(element)))
                    if(actionName == "android.intent.action.MANAGE_NETWORK_USAGE")
                    {
                        isThereManNetUse = true
                        break
                    }
                }
            }
    }

    override fun afterCheckFile(context: Context)
    {
        if(!isThereManNetUse && Helper.hasInternetPermission(context))
        {
            context.report(
                issue = IssueNoManageNetworkUsage.ISSUE_NO_MANAGE_NETWORK_USAGE,
                location = elementLocation.first().location,
                message = "It seems like manage data usage options are not being offered to the user."
            )
        }
    }
}


