/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uniandes.tsdl.ecnlinter

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.uniandes.tsdl.ecnlinter.issues.*
import com.uniandes.tsdl.ecnlinter.issues.retrofit.IssueUsageSyncExecute

/*
 * The list of issues that will be checked when running <code>lint</code>.
 */
@Suppress("UnstableApiUsage")
class IssueRegistry : IssueRegistry() {
    override val issues = listOf(
        IssueUsageSyncExecute.ISSUE_USAGE_SYNC_EXEC,
        IssueNoUsingWorkManager.ISSUE_NO_USING_WORK_MANAGER,
        IssueNoInternetPermission.ISSUE_NO_INTERNET_PERMISSION,
        IssueNoManageNetworkUsage.ISSUE_NO_MANAGE_NETWORK_USAGE,
        IssueNoMoreThanOneConstructor.ISSUE_NO_MORE_ONE_CONST_OKHTTP,
        IssueNoNetworkStatePermission.ISSUE_NO_NETWORK_STATE_PERMISSION,
        IssueNoOnFailureImplemented.ISSUE_NO_ON_FAILURE_IMPLEMENTED_EMPTY,
        IssueNoCheckNetworkTypeInProject.ISSUE_NO_CHECK_NETWORK_TYPE_IN_METHOD_SCOPE,
        IssueNoCheckNetworkTypeInProject.ISSUE_NO_CHECK_NETWORK_TYPE_IN_PROJECT_SCOPE,
        IssueNoCheckNetworkConnectionInProject.ISSUE_NO_CHECK_NETWORK_CONNECTION_IN_METHOD_SCOPE,
        IssueNoCheckNetworkConnectionInProject.ISSUE_NO_CHECK_NETWORK_CONNECTION_IN_PROJECT_SCOPE,
        IssueNoInternetConnectionCheck.ISSUE_NO_INTERNET_CONNECTION_CHECK_METHOD,
        IssueNoInternetConnectionCheck.ISSUE_NO_INTERNET_CONNECTION_CHECK_PROJECT,
        IssueResponseContentNotValidated.ISSUE_RESPONSE_CONTENT_NOT_VALIDATED,
        IssueResponseCodeNotValidated.ISSUE_RESPONSE_CODE_NOT_VALIDATED,
        IssueNoOnResponseBody.ISSUE_NO_ON_RESPONSE_BODY
        )

    override val api: Int
        get() = CURRENT_API

    override val minApi: Int
        get() = 6 // works with Studio 4.1 or later; see com.android.tools.lint.detector.api.Api / ApiKt

//     Requires lint API 30.0+; if you're still building for something
//     older, just remove this property.
//    override val vendor: Vendor = Vendor(
//        vendorName = "**SEART** (SoftwarE Analytics Research Team) & **TSDL** (The Software Design Lab)",
//    )
}
