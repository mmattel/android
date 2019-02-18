/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
 * Copyright (C) 2019 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.shares.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.owncloud.android.Resource
import com.owncloud.android.lib.resources.shares.ShareType
import com.owncloud.android.shares.datasources.LocalSharesDataSource
import com.owncloud.android.shares.datasources.RemoteSharesDataSource
import com.owncloud.android.shares.db.OCShare
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class OCShareRepository(
    private val localSharesDataSource: LocalSharesDataSource,
    private val remoteShareSharesDataSource: RemoteSharesDataSource
) : ShareRepository {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    val sharesForFile: MutableLiveData<Resource<List<OCShare>>> = MutableLiveData();

    override fun loadSharesForFile(
        filePath: String,
        accountName: String,
        shareTypes: List<ShareType>,
        reshares: Boolean,
        subfiles: Boolean
    ) {

        scope.launch { // All operations below will be executed in Main Thread (UI) except the server and database ones

            val remoteOperationResult = withContext(Dispatchers.IO) {
                remoteShareSharesDataSource.getSharesForAFile(filePath, false, false)
            }

            if (remoteOperationResult.isSuccess) {
                val localShares = remoteOperationResult.data.shares.map { remoteShare -> OCShare(remoteShare) }

                withContext(Dispatchers.IO) {
                    for (localShare in localShares) {
                        localShare.accountOwner = accountName
                        localSharesDataSource.insert(localShare)
                    }
                }

                sharesForFile.postValue(
                    Resource.success(
                        withContext(Dispatchers.IO) {
                            localSharesDataSource.getSharesForFile(
                                filePath, accountName, shareTypes.map { shareType -> shareType.value }
                            )
                        }
                    )
                )
            } else {
                sharesForFile.postValue(
                    Resource.error(
                        remoteOperationResult.logMessage,
                        withContext(Dispatchers.IO) {
                            localSharesDataSource.getSharesForFile(
                                filePath, accountName, shareTypes.map { shareType -> shareType.value }
                            )
                        }
                    )
                )
            }
        }
    }

    override fun getSharesForFileAsLiveData(): LiveData<Resource<List<OCShare>>> {
        return sharesForFile
    }
}
