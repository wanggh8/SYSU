package com.example.wang.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.view.View;
import android.widget.SimpleAdapter;
import android.support.design.widget.FloatingActionButton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {
    boolean click = true;
    FloatingActionButton fButton;
    ListView collect;
    List<Map<String, Object>> collectthings;
    SimpleAdapter mySimpleAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == 1) {
            Map<String, Object> t = (Map<String, Object>) intent.getExtras().get("favoratethings");
            this.collectthings.add(t);
            mySimpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        collect = (ListView) findViewById(R.id.collect);
        fButton = (FloatingActionButton) findViewById(R.id.fButton);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final List<Map<String, Object>> data = new ArrayList<>();
        collectthings = new ArrayList<>();
        Map<String, Object> title = new LinkedHashMap<>();
        title.put("cycle", "*");
        title.put("name", "收藏夹");
        collectthings.add(title);

        /* 收藏按钮侦听 */
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

        /* 初始化商品 */
        final List<Collection> goodslist = new ArrayList<Collection>() {
            {
                add(new Collection("大豆", "粮", "粮食", "蛋白质", "#BB4C3B"));
                add(new Collection("十字花科蔬菜", "蔬", "蔬菜", "维生素C", "#C48D30"));
                add(new Collection("牛奶", "饮", "饮品", "钙", "#4469B0"));
                add(new Collection("海鱼", "肉", "肉食", "蛋白质", "#20A17B"));
                add(new Collection("菌菇类", "蔬", "蔬菜", "微量元素", "#BB4C3B"));
                add(new Collection("番茄", "蔬", "蔬菜", "番茄红素", "#4469B0"));
                add(new Collection("胡萝卜", "蔬", "蔬菜", "胡萝卜素", "#20A17B"));
                add(new Collection("荞麦", "粮", "粮食", "膳食纤维", "#BB4C3B"));
                add(new Collection("鸡蛋", "杂", "杂", "几乎所有营养物质", "#C48D30")); }
            

        };
        for (int i = 0; i < goodslist.size(); i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("cycle", goodslist.get(i).getCycle());
            temp.put("name", goodslist.get(i).getName());
            data.add(temp);
        }

        /* RecycleView按钮侦听 */
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
                Toast.makeText(getApplication(), "删除" + goodslist.get(position).getName(), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < collectthings.size(); i++) {
                    if (data.get(position).equals(collectthings.get(i))){
                        collectthings.remove(i);
                        break;
                    }
                }
                data.remove(position);
                goodslist.remove(position);
                myAdapter.notifyDataSetChanged();
            }
        });

        /* 设置动画效果 */
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(myAdapter);
        scaleInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter((scaleInAnimationAdapter));
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());

        /* collect单击侦听 */
        mySimpleAdapter = new SimpleAdapter(this, collectthings, R.layout.goods, new String[] { "cycle", "name" },
                new int[] { R.id.cycle, R.id.name });
        collect.setAdapter(mySimpleAdapter);

        collect.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    int pos = 0;
                    final Iterator iter = collectthings.get(i).keySet().iterator();
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
                        Toast.makeText(getApplication(), "该食物已被删除", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /* collect长按侦听 */
        collect.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position > 0) {
                    final Iterator iter = collectthings.get(position).keySet().iterator();
                    Toast.makeText(getApplication(), "删除" + collectthings.get(position).get(iter.next().toString()), 
                            Toast.LENGTH_SHORT).show();
                    collectthings.remove(position);
                    mySimpleAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }
}
