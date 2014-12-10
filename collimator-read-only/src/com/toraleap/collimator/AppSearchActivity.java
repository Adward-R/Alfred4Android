package com.toraleap.collimator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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

    EditText mEditSearch;
    ListView mListEntries;
    TextView mTextStatus;

    List<Map<String, Object>> apps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("_____AppSearch______onCreate");
        super.onCreate(savedInstanceState);
        String _appkey = getIntent().getExtras().getString("app_name");
        String[] appkey = _appkey.split(" "); //appkey[0] indicates the searching mode
        System.out.println("Mode: "+appkey[0]);

        setContentView(R.layout.app_search_activity);
        mEditSearch = (EditText) findViewById(R.id.EditSearch);
        mEditSearch.setText(_appkey, TextView.BufferType.EDITABLE);
        mEditSearch.setSelection(_appkey.length()-1);

        //initUtils();
        //initViews();
        //Index.deserialization();

        //apps = getUserApps(this,appkey);
        apps = getUserApps(GlobalContext.getInstance(),appkey);

        mTextStatus = (TextView) findViewById(R.id.TextStatus);
        mTextStatus.setText(apps.size()+" applications found.");

        SimpleAdapter mAdapter = new SimpleAdapter(this,apps,
                R.layout.listitem_apps,
                new String[] {"pkgIcon","pkgLabel","pkgName"},
                new int[] {R.id.thumbnail,R.id.filename,R.id.filepath});
        mListEntries = (ListView) findViewById(R.id.ListEntries);
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

        mListEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    openApp(apps.get(position).get("pkgName").toString());
                    //AppSearchActivity.this.finish(); //kill self
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String[] newKey = mEditSearch.getText().toString().split(" ");

                if (newKey[0].equals("a")){
                    //judge if the displayed app-list is changed
                    if (!getUserApps(GlobalContext.getInstance(), newKey).equals(apps)){ //or comparing size?
                        Intent intent = new Intent();
                        intent.setClass(AppSearchActivity.this,AppSearchActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("app_name", mEditSearch.getText().toString());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        AppSearchActivity.this.finish();
                    }
                }
                //multiple "IF"s can be inserted here for extended functions
                else{
                    Intent intent = new Intent();
                    intent.setClass(AppSearchActivity.this,SearchActivity.class);
                    //Bundle bundle = new Bundle();
                    //bundle.putString("app_name", mEditSearch.getText().toString());
                    //intent.putExtras(bundle);
                    startActivity(intent);
                    AppSearchActivity.this.finish();
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
                    if (pkgLabel.toLowerCase().contains(keys[j].toLowerCase())){
                        flag++;
                        break;
                    }
                }

                if (flag>0) {
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
}
