package com.androstock.smsapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.androstock.wx.RxUtil;
import com.androstock.wx.WxStore;
import com.lifeofcoding.cacheutlislibrary.CacheUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSION_KEY = 1;
    ArrayList<HashMap<String, String>> smsList = new ArrayList<HashMap<String, String>>();
    static MainActivity inst;
    InboxAdapter adapter;
    ListView listView;
    private TextView netDesc;
    FloatingActionButton fab_new;
    ProgressBar loader;

    DisponsablePresenter mDisponsablePresenter = new DisponsablePresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CacheUtils.configureCache(this);

        listView = (ListView) findViewById(R.id.listView);
        loader = (ProgressBar) findViewById(R.id.loader);
        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);
        netDesc = findViewById(R.id.net_desc);

        listView.setEmptyView(loader);


        fab_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewSmsActivity.class));
            }
        });

        adapter = new InboxAdapter(MainActivity.this, smsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                Intent intent = new Intent(MainActivity.this, Chat.class);
                intent.putExtra("name", smsList.get(+position).get(Function.KEY_NAME));
                intent.putExtra("address", smsList.get(+position).get(Function.KEY_PHONE));
                intent.putExtra("thread_id", smsList.get(+position).get(Function.KEY_THREAD_ID));
                startActivity(intent);
            }
        });

    }


    private boolean isRefreshIng = false;

    private void refreshSms() {
        if (isRefreshIng) {
            return;
        }
        isRefreshIng = true;
        Observable.create(new ObservableOnSubscribe<ArrayList<HashMap<String, String>>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<HashMap<String, String>>> e) throws Exception {
                e.onNext(getSms());
            }
        }).compose(RxUtil.<ArrayList<HashMap<String, String>>>getTransformer2()).subscribe(new Observer<ArrayList<HashMap<String, String>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisponsablePresenter.addDisponsable(d);
            }

            @Override
            public void onNext(ArrayList<HashMap<String, String>> hashMaps) {
                smsList.clear();
                smsList.addAll(hashMaps);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                isRefreshIng = false;
            }

            @Override
            public void onComplete() {
                isRefreshIng = false;
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshSms();
                } else {
                    Toast.makeText(MainActivity.this, "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_PHONE_STATE};
        if (!Function.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        } else {
            refreshSms();
            setDefaultSMSApp();
            WxStore.checkNet(checkNet);
        }
    }

    private WxStore.CallBack<String> checkNet = new WxStore.CallBack<String>() {
        @Override
        public void onSucc(String s) {
            netDesc.setVisibility(View.GONE);
        }

        @Override
        public void onFail(Throwable t) {
            netDesc.setVisibility(View.VISIBLE);
        }
    };


    class InboxAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<HashMap<String, String>> data;

        public InboxAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
            activity = a;
            data = d;
        }


        public int getCount() {
            return data == null ? 0 : data.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            InboxViewHolder holder = null;
            if (convertView == null) {
                holder = new InboxViewHolder();
                convertView = LayoutInflater.from(activity).inflate(
                        R.layout.conversation_list_item, parent, false);

                holder.inbox_thumb = (ImageView) convertView.findViewById(R.id.inbox_thumb);
                holder.inbox_user = (TextView) convertView.findViewById(R.id.inbox_user);
                holder.inbox_msg = (TextView) convertView.findViewById(R.id.inbox_msg);
                holder.inbox_date = (TextView) convertView.findViewById(R.id.inbox_date);

                convertView.setTag(holder);
            } else {
                holder = (InboxViewHolder) convertView.getTag();
            }
            holder.inbox_thumb.setId(position);
            holder.inbox_user.setId(position);
            holder.inbox_msg.setId(position);
            holder.inbox_date.setId(position);

            HashMap<String, String> song = new HashMap<String, String>();
            song = data.get(position);
            try {
                holder.inbox_user.setText(song.get(Function.KEY_NAME));
                holder.inbox_msg.setText(song.get(Function.KEY_MSG));
                holder.inbox_date.setText(song.get(Function.KEY_TIME));

                String firstLetter = String.valueOf(song.get(Function.KEY_NAME).charAt(0));
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(getItem(position));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(firstLetter, color);
                holder.inbox_thumb.setImageDrawable(drawable);
            } catch (Exception e) {
            }
            return convertView;
        }
    }


    class InboxViewHolder {
        ImageView inbox_thumb;
        TextView inbox_user, inbox_msg, inbox_date;
    }


    //设为默认短息app
    protected void setDefaultSMSApp() {
        String defaultSmsApp = null;
        String currentPn = getPackageName();//获取当前程序包名
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
        }
        if (!defaultSmsApp.equals(currentPn)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentPn);
            startActivity(intent);
        }
    }


    public ArrayList<HashMap<String, String>> getSms() {
        ArrayList<HashMap<String, String>> smsList = new ArrayList<HashMap<String, String>>();
        Uri uriInbox = Uri.parse("content://sms/inbox");

        Cursor inbox = getContentResolver().query(uriInbox, null, "address IS NOT NULL) GROUP BY (thread_id", null, null); // 2nd null = "address IS NOT NULL) GROUP BY (address"
        Uri uriSent = Uri.parse("content://sms/sent");
        Cursor sent = getContentResolver().query(uriSent, null, "address IS NOT NULL) GROUP BY (thread_id", null, null); // 2nd null = "address IS NOT NULL) GROUP BY (address"
        Cursor c = new MergeCursor(new Cursor[]{inbox, sent}); // Attaching inbox and sent sms


        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String name = null;
                String phone = "";
                String _id = c.getString(c.getColumnIndexOrThrow("_id"));
                String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id"));
                String msg = c.getString(c.getColumnIndexOrThrow("body"));
                String type = c.getString(c.getColumnIndexOrThrow("type"));
                String timestamp = c.getString(c.getColumnIndexOrThrow("date"));
                phone = c.getString(c.getColumnIndexOrThrow("address"));


                //读取联系人姓名，暂不处理
                name = phone;
                //name = Function.getContactbyPhoneNumber(getApplicationContext(), c.getString(c.getColumnIndexOrThrow("address")));


                SmsBody smsBody = new SmsBody(_id, thread_id, name, phone, msg, type, timestamp, Function.converToTime(timestamp));
                smsList.add(Function.mappingInbox(_id, thread_id, name, phone, msg, type, timestamp, Function.converToTime(timestamp)));
                c.moveToNext();
            }
        }
        c.close();

        Collections.sort(smsList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging sms by timestamp decending
        ArrayList<HashMap<String, String>> purified = Function.removeDuplicates(smsList); // Removing duplicates from inbox & sent
        smsList.clear();
        smsList.addAll(purified);

        // Updating cache data
        try {
            Function.createCachedFile(MainActivity.this, "smsapp", smsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsList;

    }

}


