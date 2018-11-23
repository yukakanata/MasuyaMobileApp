package com.yusuffahrudin.masuyamobileapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.yusuffahrudin.masuyamobileapp.HomeActivity;
import com.yusuffahrudin.masuyamobileapp.LoginActivity;

import java.util.HashMap;

/**
 * Created by yusuf fahrudin on 22-05-2017.
 */

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int mode = 0;

    private static final String pref_name = "crudpref";
    private static final String is_login = "islogin";
    public static final String kunci_email = "keyemail";
    public static final String level = "level";
    public static final String kota = "kota";
    public static final String kdkota = "kdkota";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(pref_name, mode);
        editor = pref.edit();
    }

    public void createSession(String user, String divisi, String kota, String kdkota){
        editor.putBoolean(is_login, true);
        editor.putString(kunci_email, user);
        editor.putString(level, divisi);
        editor.putString(SessionManager.kota, kota);
        editor.putString(SessionManager.kdkota, kdkota);
        editor.commit();
    }

    public void checkLogin(){
        if (!this.is_login()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else {
            Intent i = new Intent(context, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    private boolean is_login() {
        return pref.getBoolean(is_login, false);
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(pref_name, pref.getString(pref_name, null));
        user.put(kunci_email, pref.getString(kunci_email, null));
        user.put(level, pref.getString(level, null));
        user.put(kota, pref.getString(kota, null));
        user.put(kdkota, pref.getString(kdkota, null));
        return user;
    }
}
