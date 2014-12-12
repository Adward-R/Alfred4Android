package com.toraleap.collimator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adward on 14/12/8.
 */
public final class AppSearchActivity extends Activity {

    int mode;
    EditText mEditSearch;
    ListView mListEntries;
    TextView mTextStatus;
    SimpleAdapter mAdapter;

    List<Map<String, Object>> apps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("_____AppSearch______onCreate");
        super.onCreate(savedInstanceState);
        String _appkey = getIntent().getExtras().getString("searchKey");
        String[] appkey = _appkey.split(" "); //appkey[0] indicates the searching mode

        setContentView(R.layout.app_search_activity);
        mEditSearch = (EditText) findViewById(R.id.EditSearch);
        mEditSearch.setText(_appkey, TextView.BufferType.EDITABLE);
        mEditSearch.setSelection(_appkey.length());
        mEditSearch.requestFocus();
        mTextStatus = (TextView) findViewById(R.id.TextStatus);
        mListEntries = (ListView) findViewById(R.id.ListEntries);

        if (appkey[0].equals("a")){
            mode = 1;//App Search Mode
        }
        else if (appkey[0].equals("c")){
            mode = 2;//Contact Search Mode
        }
        //EXTENDED FUNCTIONS MUST ADD "ELSE-IF" CLAUSE HERE
        else{
            mode = 0;
        }

        //EXTENDED FUNCTIONS MUST ADD "CASE" CLAUSE HERE
        switch (mode){
            case 1:{
                //apps = getUserApps(this,appkey);
                apps = getUserApps(GlobalContext.getInstance(),appkey);
                mTextStatus.setText(apps.size()+" applications found.");

                mAdapter = new SimpleAdapter(this,apps,
                        R.layout.listitem_apps,
                        new String[] {"pkgIcon","pkgLabel","pkgName"},
                        new int[] {R.id.thumbnail,R.id.filename,R.id.filepath});
                mListEntries.setAdapter(mAdapter);
                mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view instanceof ImageView && data instanceof Drawable) {
                            ImageView iv = (ImageView) view;
                            iv.setImageDrawable((Drawable) data);
                            return true;
                        } else return false;
                    }
                });
                break;
            }
            case 2:{
                apps = getUserContacts(appkey);
                mTextStatus.setText(apps.size()+" contacts found.");

                mAdapter = new SimpleAdapter(this,apps,
                        R.layout.listitem_apps,
                        new String[] {/*"contactPhoto",*/"contactName","contactPhoneNum"},
                        new int[] {/*R.id.thumbnail,*/R.id.filename,R.id.filepath});
                mListEntries.setAdapter(mAdapter);
                mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view instanceof ImageView && data instanceof Drawable) {
                            ImageView iv = (ImageView) view;
                            iv.setImageDrawable((Drawable) data);
                            return true;
                        } else return false;
                    }
                });
                break;
            }
            default:; //how to get back to normal search mode?
        }




        mListEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //EXTENDED FUNCTIONS MUST ADD "CASE" CLAUSE HERE
                switch (mode){
                    case 1: {
                        try {
                            openApp(apps.get(position).get("pkgName").toString());
                            //AppSearchActivity.this.finish(); //kill self
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 2: {
                        //make phone call or send messages
                        break;
                    }
                }
            }
        });

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String[] newKey = mEditSearch.getText().toString().split(" ");


                //When no search keyword is offered, the result should be all user installed apps (seems already is?)
                if (newKey[0].equals("a")){
                    //judge if the displayed app-list is changed
                    int flag=0;
                    List<Map<String, Object>> newapps = getUserApps(GlobalContext.getInstance(), newKey);
                    if (newapps.size()!=apps.size()){
                        flag=1;
                    }
                    else{
                        for (int i=0;i<apps.size();i++){
                            if (!newapps.get(i).get("pkgName").toString().equals(apps.get(i).get("pkgName").toString())){
                                flag=1;
                                break;
                            }
                        }
                    }
                    if (true){//if (flag>0){ //if the adapter must be refreshed
                        SimpleAdapter newAdapter = new SimpleAdapter(GlobalContext.getInstance(),newapps,
                                R.layout.listitem_apps,
                                new String[] {"pkgIcon","pkgLabel","pkgName"},
                                new int[] {R.id.thumbnail,R.id.filename,R.id.filepath});
                        mListEntries.setAdapter(newAdapter);
                        newAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            public boolean setViewValue(View view, Object data, String textRepresentation) {
                                if (view instanceof ImageView && data instanceof Drawable) {
                                    ImageView iv = (ImageView) view;
                                    iv.setImageDrawable((Drawable) data);
                                    return true;
                                }
                                else {
                                    return false;
                                }
                            }
                        });
                        mTextStatus.setText(newapps.size()+" applications found.");
                    }
                }
                else if (newKey[0].equals("c")){
                    List<Map<String, Object>> newcontacts = getUserContacts(newKey);
                    SimpleAdapter newAdapter = new SimpleAdapter(GlobalContext.getInstance(),newcontacts,
                            R.layout.listitem_apps,
                            new String[] {/*"contactPhoto",*/"contactName","contactPhoneNum"},
                            new int[] {/*R.id.thumbnail,*/R.id.filename,R.id.filepath});
                    mListEntries.setAdapter(newAdapter);
                    newAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof ImageView && data instanceof Drawable) {
                                ImageView iv = (ImageView) view;
                                iv.setImageDrawable((Drawable) data);
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                    });
                    mTextStatus.setText(newcontacts.size()+" contacts found.");
                }
                //multiple "IF"s can be inserted here for extended functions
                else{/*
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),SearchActivity.class);//
                    //Bundle bundle = new Bundle();
                    //bundle.putString("searchKey", mEditSearch.getText().toString());
                    //intent.putExtras(bundle);
                    startActivity(intent);
                    //AppSearchActivity.this.finish();*/
                    try {
                        openApp(getPackageName());
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    //put in a new .java will be better
    public List<Map<String,Object>> getUserApps(Context context,String[] keys) {
        List<Map<String,Object>> apps = new ArrayList<Map<String,Object>>();
        PackageManager pManager = context.getPackageManager();
        //Obtain all installed app info in the cell phone
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //See if the app is not pre-installed (user installed)
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                String pkgLabel = pManager.getApplicationLabel(pak.applicationInfo).toString();
                int flag = 0;
                for (int j=1;j<keys.length;j++){
                    if (!pkgLabel.toLowerCase().contains(keys[j].toLowerCase())){
                        flag++;
                        break;
                    }
                }

                if (flag==0) {
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    listItem.put("pkgName", pak.packageName);
                    listItem.put("pkgLabel", pkgLabel);
                    //listItem.put("pkgInstallTime",)
                    //listItem.put("pkgIcon",pManager.getApplicationIcon(pak));
                    listItem.put("pkgIcon", pak.applicationInfo.loadIcon(pManager));
                    apps.add(listItem);
                    //System.out.println(pManager.getApplicationLabel(paklist.get(i).applicationInfo));
                }
            }
        }
        return apps;
    }

    //Cannot open several APPs, such as 微信电话本...
    private void openApp(String packageName) throws PackageManager.NameNotFoundException {
        PackageInfo pi = getPackageManager().getPackageInfo(packageName, 0);

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null ) {
            String pkgName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(pkgName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    public List<Map<String,Object>> getUserContacts(String[] keys) {
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[] { "_id" }, null, null, null);
        List<Map<String,Object>> contacts = new ArrayList<Map<String,Object>>();

        while (cursor.moveToNext()) {
            Map<String,Object> contact = new HashMap<String, Object>();
            int contactID = cursor.getInt(0);
            contact.put("contactID",contactID);
            uri = Uri.parse("content://com.android.contacts/contacts/"
                    + contactID + "/data");
            Cursor cursor1 = resolver.query(uri, new String[] { "mimetype",
                    "data1", "data2" }, null, null, null);
            int flag1=0,flag2=0; //indicates if the item should be put into the result list

            while (cursor1.moveToNext()){
                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));

                if ("vnd.android.cursor.item/name".equals(mimeType)) { // Is name
                    //Judge if the search key suits the name of contact
                    if (data1==null){
                        continue;
                    }
                    for (int j=1;j<keys.length;j++){
                        if (!data1.toLowerCase().contains(keys[j].toLowerCase())){
                            flag1++;
                            break;
                        }
                    }
                    contact.put("contactName",data1);
                } /*
                else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { // Is Email
                    if (data1==null){
                        continue;
                    }
                    if (!TextUtils.isEmpty(data1)) {
                        contact.put("contactMail",data1);
                    }
                } */
                else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { // Is Phone Number
                    if (data1==null){
                        continue;
                    }
                    String phoneNum = data1.replaceAll("[- +]", "");
                    for (int j=1;j<keys.length;j++){
                        if (!phoneNum.contains(keys[j])){
                            flag2++;
                            break;
                        }
                    }
                    //System.out.println("~~"+data1+"~~");
                    contact.put("contactPhoneNum",phoneNum);
                }/*
                else if ("vnd.android.cursor.item/photo".equals(mimeType)){
                    if (data1==null){
                        continue;
                    }
                    System.out.println("__PHOTO GET");
                    contact.put("contactPhoto",data1);
                }*/

            }
            if (flag1==0||flag2==0) {
                contacts.add(contact);
            }
            cursor1.close();
        }
        cursor.close();
        return contacts;
    }
}
