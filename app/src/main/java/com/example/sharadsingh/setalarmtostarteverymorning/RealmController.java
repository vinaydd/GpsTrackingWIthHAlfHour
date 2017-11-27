package com.example.sharadsingh.setalarmtostarteverymorning;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.example.sharadsingh.setalarmtostarteverymorning.receiver.VimayModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public void refresh() {
        realm.setAutoRefresh(true);
    }

    //User Detail Information Data to save realm start
    public void clearAllUserData() {
        realm.beginTransaction();
        realm.delete(VimayModel.class);
        realm.commitTransaction();
    }

    public VimayModel getAllUserDetail() {
        return realm.where(VimayModel.class).findFirst();
    }

    public RealmResults<VimayModel> getBooksUser() {
        return realm.where(VimayModel.class).findAll();
    }

    public VimayModel getUserDetailThroughId(String id) {
        return realm.where(VimayModel.class).equalTo("id", id).findFirst();
    }


    public RealmResults<VimayModel> getUserDetailWithQuery() {
        return realm.where(VimayModel.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();
    }
}