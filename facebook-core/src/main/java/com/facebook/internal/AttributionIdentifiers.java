/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.facebook.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.UUID;

/**
 * com.facebook.internal is solely for the use of other packages within the Facebook SDK for
 * Android. Use of any of the classes in this package is unsupported, and they may be modified or
 * removed without warning at any time.
 */
public class AttributionIdentifiers {
    private static final String TAG = AttributionIdentifiers.class.getCanonicalName();

    private String attributionId;
    private String androidAdvertiserId;
    private String androidInstallerPackage;
    private boolean limitTracking;
    private long fetchTime;

    private static AttributionIdentifiers recentlyFetchedIdentifiers;

    private static AttributionIdentifiers getAndroidId(Context context) {
        return getAttributionIdentifiers(context);
    }

    public static AttributionIdentifiers getAttributionIdentifiers(Context context) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            // Calling this method from the main thread might cause this app to freeze.
            Log.e(
                AttributionIdentifiers.TAG,
                "getAttributionIdentifiers should not be called from the main thread");
        }

        if (recentlyFetchedIdentifiers != null) {
            return recentlyFetchedIdentifiers;
        }

        AttributionIdentifiers identifiers = new AttributionIdentifiers();
        identifiers.attributionId = UUID.randomUUID().toString();
        identifiers.androidAdvertiserId = UUID.randomUUID().toString();
        identifiers.limitTracking = true;

        String installerPackageName = getInstallerPackageName(context);
        if (installerPackageName != null) {
            identifiers.androidInstallerPackage = installerPackageName;
        }

        return cacheAndReturnIdentifiers(identifiers);
    }

    private static AttributionIdentifiers cacheAndReturnIdentifiers(
            AttributionIdentifiers identifiers) {
        identifiers.fetchTime = System.currentTimeMillis();
        recentlyFetchedIdentifiers = identifiers;
        return identifiers;
    }

    public String getAttributionId() {
        return attributionId;
    }

    public String getAndroidAdvertiserId() {
        return androidAdvertiserId;
    }

    public String getAndroidInstallerPackage() {
        return androidInstallerPackage;
    }

    public boolean isTrackingLimited() {
        return limitTracking;
    }

    @Nullable
    private static String getInstallerPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            return packageManager.getInstallerPackageName(context.getPackageName());
        }
        return null;
    }
}
