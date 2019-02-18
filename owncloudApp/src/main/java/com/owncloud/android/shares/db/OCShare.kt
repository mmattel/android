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

package com.owncloud.android.shares.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.ContentValues
import android.database.Cursor
import com.owncloud.android.db.ProviderMeta.ProviderTableMeta
import com.owncloud.android.lib.resources.shares.RemoteShare

/**
 * Represents one record of the Shares table.
 */
@Entity(tableName = ProviderTableMeta.OCSHARES_TABLE_NAME)
data class OCShare(
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_FILE_SOURCE)
    val fileSource: Long?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_ITEM_SOURCE)
    val itemSource: Long?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_SHARE_TYPE)
    val shareType: Int?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_SHARE_WITH)
    val shareWith: String?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_PATH)
    val path: String?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_PERMISSIONS)
    val permissions: Int?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_SHARED_DATE)
    val sharedDate: Long?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_EXPIRATION_DATE)
    val expirationDate: Long?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_TOKEN)
    val token: String?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_SHARE_WITH_DISPLAY_NAME)
    val sharedWithDisplayName: String?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_IS_DIRECTORY)
    val isFolder: Boolean?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_USER_ID)
    val userId: Long?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_ID_REMOTE_SHARED)
    val remoteId: Long?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_ACCOUNT_OWNER)
    var accountOwner: String?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_NAME)
    val name: String?,
    @ColumnInfo(name = ProviderTableMeta.OCSHARES_URL)
    val shareLink: String?
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    constructor(remoteShare: RemoteShare) : this(
        remoteShare.fileSource,
        remoteShare.itemSource,
        remoteShare.shareType.value,
        remoteShare.shareWith,
        remoteShare.path,
        remoteShare.permissions,
        remoteShare.sharedDate,
        remoteShare.expirationDate,
        remoteShare.token,
        remoteShare.sharedWithDisplayName,
        remoteShare.isFolder,
        remoteShare.userId,
        remoteShare.remoteId,
        "",
        remoteShare.name,
        remoteShare.shareLink
    )

    constructor(cursor: Cursor) : this(
        cursor.getLong(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_FILE_SOURCE)),
        cursor.getLong(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_ITEM_SOURCE)),
        cursor.getInt(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_SHARE_TYPE)),
        cursor.getString(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_SHARE_WITH)),
        cursor.getString(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_PATH)),
        cursor.getInt(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_PERMISSIONS)),
        cursor.getLong(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_SHARED_DATE)),
        cursor.getLong(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_EXPIRATION_DATE)),
        cursor.getString(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_TOKEN)),
        cursor.getString(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_SHARE_WITH_DISPLAY_NAME)),
        cursor.getInt(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_IS_DIRECTORY)) == 1,
        cursor.getLong(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_USER_ID)),
        cursor.getLong(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_ID_REMOTE_SHARED)),
        cursor.getString(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_ACCOUNT_OWNER)),
        cursor.getString(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_NAME)),
        cursor.getString(cursor.getColumnIndex(ProviderTableMeta.OCSHARES_URL))
    )

    companion object {

        /**
         * Generated - should be refreshed every time the class changes!!
         */
        private const val serialVersionUID = 4124975224281327921L

        private val TAG = OCShare::class.java.simpleName

        const val DEFAULT_PERMISSION = -1
        const val READ_PERMISSION_FLAG = 1
        const val UPDATE_PERMISSION_FLAG = 2
        const val CREATE_PERMISSION_FLAG = 4
        const val DELETE_PERMISSION_FLAG = 8
        const val SHARE_PERMISSION_FLAG = 16
        const val MAXIMUM_PERMISSIONS_FOR_FILE = READ_PERMISSION_FLAG +
                UPDATE_PERMISSION_FLAG +
                SHARE_PERMISSION_FLAG
        const val MAXIMUM_PERMISSIONS_FOR_FOLDER = MAXIMUM_PERMISSIONS_FOR_FILE +
                CREATE_PERMISSION_FLAG +
                DELETE_PERMISSION_FLAG
        const val FEDERATED_PERMISSIONS_FOR_FILE_UP_TO_OC9 = READ_PERMISSION_FLAG + UPDATE_PERMISSION_FLAG
        const val FEDERATED_PERMISSIONS_FOR_FILE_AFTER_OC9 = READ_PERMISSION_FLAG +
                UPDATE_PERMISSION_FLAG +
                SHARE_PERMISSION_FLAG
        const val FEDERATED_PERMISSIONS_FOR_FOLDER_UP_TO_OC9 = READ_PERMISSION_FLAG +
                UPDATE_PERMISSION_FLAG +
                CREATE_PERMISSION_FLAG +
                DELETE_PERMISSION_FLAG
        const val FEDERATED_PERMISSIONS_FOR_FOLDER_AFTER_OC9 =
            FEDERATED_PERMISSIONS_FOR_FOLDER_UP_TO_OC9 + SHARE_PERMISSION_FLAG

        fun fromContentValues(values: ContentValues): OCShare {
            return OCShare(
                values.getAsLong(ProviderTableMeta.OCSHARES_FILE_SOURCE),
                values.getAsLong(ProviderTableMeta.OCSHARES_ITEM_SOURCE),
                values.getAsInteger(ProviderTableMeta.OCSHARES_SHARE_TYPE),
                values.getAsString(ProviderTableMeta.OCSHARES_SHARE_WITH),
                values.getAsString(ProviderTableMeta.OCSHARES_PATH),
                values.getAsInteger(ProviderTableMeta.OCSHARES_PERMISSIONS),
                values.getAsLong(ProviderTableMeta.OCSHARES_SHARED_DATE),
                values.getAsLong(ProviderTableMeta.OCSHARES_EXPIRATION_DATE),
                values.getAsString(ProviderTableMeta.OCSHARES_TOKEN),
                values.getAsString(ProviderTableMeta.OCSHARES_SHARE_WITH_DISPLAY_NAME),
                values.getAsBoolean(ProviderTableMeta.OCSHARES_IS_DIRECTORY),
                values.getAsLong(ProviderTableMeta.OCSHARES_USER_ID),
                values.getAsLong(ProviderTableMeta.OCSHARES_ID_REMOTE_SHARED),
                values.getAsString(ProviderTableMeta.OCSHARES_ACCOUNT_OWNER),
                values.getAsString(ProviderTableMeta.OCSHARES_NAME),
                values.getAsString(ProviderTableMeta.OCSHARES_URL)
            )
        }
    }
}
