package com.toraleap.collimator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("_____AppSearch______onCreate");
        super.onCreate(savedInstanceState);
        String appkey = getIntent().getExtras().getString("app_name").replaceAll(".", "");

        setContentView(R.layout.app_search_activity);
        mEditSearch = (EditText) findViewById(R.id.EditSearch);
        mEditSearch.setText(appkey, TextView.BufferType.EDITABLE);

        //TextView mTextStatus = (TextView) findViewById(R.id.TextStatus);
        //mTextStatus.setText(appkey);
        //initUtils();
        //initViews();
        //Index.deserialization();


        final List<Map<String, Object>> apps = getUserApps(this);

        SimpleAdapter mAdapter = new SimpleAdapter(this,apps,
                R.layout.listitem_apps,
                new String[] {"pkgIcon","pkgName"},
                new int[] {R.id.thumbnail,R.id.filename});
        mListEntries = (ListView) findViewById(R.id.ListEntries);
        mListEntries.setAdapter(mAdapter);
        mListEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    openApp(apps.get(position).get("pkgName").toString());
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        //mListEntries.setOnItemSelectedListener(this);
        System.out.println("Listener Added");

        /*
        mListEntries = (ListView) findViewById(R.id.ListEntries);
        BaseAdapter mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return apps.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout line = new LinearLayout(AppSearchActivity.this);
                line.setOrientation(LinearLayout.HORIZONTAL);
                ImageView image = new ImageView(AppSearchActivity.this);
                image.setImageDrawable((Drawable)apps.get(position).get("pkgIcon"));
                TextView text = new TextView(AppSearchActivity.this);
                text.setText("The " + position + " th");
                text.setTextSize(20);
                text.setTextColor(Color.RED);
                line.addView(image);
                line.addView(text);
                return line;
            }
        };
        mListEntries.setAdapter(mAdapter);*/
    }

    public List<Map<String,Object>> getUserApps(Context context) {
        List<Map<String,Object>> apps = new ArrayList<Map<String,Object>>();
        PackageManager pManager = context.getPackageManager();
        //Obtain all installed app info in the cell phone
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //See if the app is not pre-installed (user installed)
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                Map<String,Object> listItem = new HashMap<String, Object>();
                listItem.put("pkgName",pManager.getApplicationLabel(pak.applicationInfo));

                //listItem.put("pkgInstallTime",)
                //listItem.put("pkgIcon",pManager.getApplicationIcon(pak));
                listItem.put("pkgIcon",pak.applicationInfo.loadIcon(pManager));
                apps.add(listItem);
                //System.out.println(pManager.getApplicationLabel(paklist.get(i).applicationInfo));
            }
        }
        return apps;
    }

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
