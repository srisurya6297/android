/*
 *
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2019 Tobias Kaminsky
 * Copyright (C) 2019 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.nextcloud.client;

import android.Manifest;
import android.app.Activity;

import com.facebook.testing.screenshot.Screenshot;
import com.owncloud.android.AbstractIT;
import com.owncloud.android.R;
import com.owncloud.android.lib.resources.files.CreateFolderRemoteOperation;
import com.owncloud.android.lib.resources.files.SearchRemoteOperation;
import com.owncloud.android.lib.resources.shares.CreateShareRemoteOperation;
import com.owncloud.android.lib.resources.shares.OCShare;
import com.owncloud.android.lib.resources.shares.ShareType;
import com.owncloud.android.ui.activity.FileDisplayActivity;
import com.owncloud.android.ui.events.SearchEvent;

import org.greenrobot.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;


public class FileDisplayActivityIT extends AbstractIT {
    @Rule public IntentsTestRule<FileDisplayActivity> activityRule = new IntentsTestRule<>(FileDisplayActivity.class,
                                                                                           true,
                                                                                           false);

    @Rule
    public final GrantPermissionRule permissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Test
    public void open() throws InterruptedException {
        Activity sut = activityRule.launchActivity(null);

        Thread.sleep(3000);

        Screenshot.snapActivity(sut).record();
    }

    @Test
    public void drawer() {
        Activity sut = activityRule.launchActivity(null);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        Screenshot.snapActivity(sut).record();
    }

    @Test
    public void showShares() throws InterruptedException {
        assertTrue(new CreateFolderRemoteOperation("/shareToAdmin/", true).execute(client).isSuccess());
        assertTrue(new CreateFolderRemoteOperation("/shareToGroup/", true).execute(client).isSuccess());
        assertTrue(new CreateFolderRemoteOperation("/shareViaLink/", true).execute(client).isSuccess());
        assertTrue(new CreateFolderRemoteOperation("/noShare/", true).execute(client).isSuccess());

        // share folder to user "admin"
        assertTrue(new CreateShareRemoteOperation("/shareToAdmin/",
                                                  ShareType.USER,
                                                  "admin",
                                                  false,
                                                  "",
                                                  OCShare.MAXIMUM_PERMISSIONS_FOR_FOLDER)
                       .execute(client).isSuccess());

        // share folder via public link
        assertTrue(new CreateShareRemoteOperation("/shareViaLink/",
                                                  ShareType.PUBLIC_LINK,
                                                  "",
                                                  true,
                                                  "",
                                                  OCShare.READ_PERMISSION_FLAG)
                       .execute(client).isSuccess());

        // share folder to group
        Assert.assertTrue(new CreateShareRemoteOperation("/shareToGroup/",
                                                         ShareType.GROUP,
                                                         "users",
                                                         false,
                                                         "",
                                                         OCShare.DEFAULT_PERMISSION)
                              .execute(client).isSuccess());

        Activity sut = activityRule.launchActivity(null);
        Thread.sleep(2000);

        EventBus.getDefault().post(new SearchEvent("",
                                                   SearchRemoteOperation.SearchType.SHARED_FILTER,
                                                   SearchEvent.UnsetType.UNSET_BOTTOM_NAV_BAR));

        Thread.sleep(2000);

        Screenshot.snapActivity(sut).record();
    }

    @Test
    public void showAccounts() {
        Activity sut = activityRule.launchActivity(null);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_active_user)).perform(click());

        Screenshot.snapActivity(sut).record();
    }
}
