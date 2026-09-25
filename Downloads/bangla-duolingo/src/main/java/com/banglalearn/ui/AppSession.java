package com.banglalearn.ui;

import com.banglalearn.db.UserProfile;

/** Holds the profile picked at the profile-picker screen for the rest of the session. */
public final class AppSession {

    private static UserProfile currentProfile;

    private AppSession() {
    }

    public static UserProfile currentProfile() {
        return currentProfile;
    }

    public static void setCurrentProfile(UserProfile profile) {
        currentProfile = profile;
    }
}
