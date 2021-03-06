package com.example.wang.myapplication;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.view.View;
import android.widget.SimpleAdapter;
import android.support.design.widget.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.AdapterView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

public class MainActivity extends AppCompatActivity {
    boolean click = true;
    List<Map<String, Object>> collectthings;
    SimpleAdapter mySimpleAdapter;
    ListView collect;
    FloatingActionButton fButton;
    RecyclerView recyclerView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getString("Collect") != null) {
            if (extras.getString("Collect").equals("true")) {
                collect.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                fButton.setImageDrawable(getResources().getDrawable(R.mipmap.mainpage));
                click = !click;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Map<String, Object> temp = new LinkedHashMap<>();
        temp.put("cycle", event.getKind());
        temp.put("name", event.getName());
        this.collectthings.add(temp);
        mySimpleAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        collect = (ListView) findViewById(R.id.collect);
        fButton = (FloatingActionButton) findViewById(R.id.fButton);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        TextView tv = (TextView) findViewById(R.id.appwidget_text);
        final List<Map<String, Object>> data = new ArrayList<>();
        collectthings = new ArrayList<>();
        Map<String, Object> title = new LinkedHashMap<>();
        title.put("cycle", "*");
        title.put("name", "?????????");
        collectthings.add(title);

        /* ?????????????????? */
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click) {
                    collect.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    fButton.setImageDrawable(getResources().getDrawable(R.mipmap.mainpage));
                } else {
                    collect.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    fButton.setImageDrawable(getResources().getDrawable(R.mipmap.collect));
                }
                click = !click;
            }
        });

        /* ??????????????? */
        final List<Collection> goodslist = new ArrayList<Collection>() {
            {
                add(new Collection("??????", "???", "??????", "?????????", "#BB4C3B"));
                add(new Collection("??????????????????", "???", "??????", "?????????C", "#C48D30"));
                add(new Collection("??????", "???", "??????", "???", "#4469B0"));
                add(new Collection("??????", "???", "??????", "?????????", "#20A17B"));
                add(new Collection("?????????", "???", "??????", "????????????", "#BB4C3B"));
                add(new Collection("??????", "???", "??????", "????????????", "#4469B0"));
                add(new Collection("?????????", "???", "??????", "????????????", "#20A17B"));
                add(new Collection("??????", "???", "??????", "????????????", "#BB4C3B"));
                add(new Collection("??????", "???", "???", "????????????????????????", "#C48D30"));
            }

        };
        for (int i = 0; i < goodslist.size(); i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("cycle", goodslist.get(i).getCycle());
            temp.put("name", goodslist.get(i).getName());
            data.add(temp);
        }

        Random random = new Random();
        int n = random.nextInt(goodslist.size());
        Intent intentBroadcast = new Intent("com.example.wang.myapplication.MyStaticFilter"); // ??????Intent
        // IntentFilter filter = new IntentFilter();
        // filter.addAction(ACTION_BOOT_COMPLETED);
        ComponentName componentName = new ComponentName(this.getPackageName(),
                "com.example.wang.myapplication.StaticReceiver");
        intentBroadcast.setComponent(componentName);
        Collection temp1 = goodslist.get(n);
        intentBroadcast.putExtra("Adgoods", temp1);
        sendBroadcast(intentBroadcast);
        Intent widgetIntentBroadcast = new Intent();
        //Bundle bundle1 = new Bundle();
        widgetIntentBroadcast.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        //bundle1.putString("Adgoods",temp1.getName());
        widgetIntentBroadcast.putExtra("Adgoods",temp1);
        sendBroadcast(widgetIntentBroadcast);

        /* RecycleView???????????? */
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyRecyclerViewAdapter myAdapter = new MyRecyclerViewAdapter<Collection>(MainActivity.this, R.layout.goods,
                goodslist) {
            @Override
            public void convert(MyViewHolder holder, Collection s) {
                TextView name = holder.getView(R.id.name);
                name.setText(s.getName());
                TextView cycle = holder.getView(R.id.cycle);
                cycle.setText(s.getCycle());
            }
        };

        myAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Collection temp = goodslist.get(position);
                intent.putExtra("Collection", temp);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onLongClick(int position) {
                Toast.makeText(getApplication(), "??????" + goodslist.get(position).getName(), Toast.LENGTH_SHORT).show();
                data.remove(position);
                goodslist.remove(position);
                myAdapter.notifyDataSetChanged();
            }
        });

        /* ?????????????????? */
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(myAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter((scaleInAnimationAdapter));
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());

        /* collect???????????? */
        mySimpleAdapter = new SimpleAdapter(this, collectthings, R.layout.goods, new String[] { "cycle", "name" },
                new int[] { R.id.cycle, R.id.name });
        collect.setAdapter(mySimpleAdapter);

        collect.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    int pos = 0;
                    final Iterator iter = collectthings.get(i).keySet().iterator();
                    iter.next();
                    String key = iter.next().toString();
                    String name = collectthings.get(i).get(key).toString();

                    for (pos = 0; pos < goodslist.size(); pos++) {
                        if (name.equals(goodslist.get(pos).getName())) {
                            break;
                        }
                    }
                    if (pos < goodslist.size()) {
                        Collection temp = goodslist.get(pos);
                        intent.putExtra("Collection", temp);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplication(), "?????????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /* collect???????????? */
        collect.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position > 0) {
                    final Iterator iter = collectthings.get(position).keySet().iterator();
                    Toast.makeText(getApplication(), "??????" + collectthings.get(position).get(iter.next().toString()),
                            Toast.LENGTH_SHORT).show();
                    collectthings.remove(position);
                    mySimpleAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
