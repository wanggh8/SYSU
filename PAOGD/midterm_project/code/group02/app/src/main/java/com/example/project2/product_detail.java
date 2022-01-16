package com.example.project2;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class product_detail extends AppCompatActivity {
    private Adapter adapter;
    HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    RelativeLayout image_panel;
    TextView name;
    TextView called;
    EditText edt_name;
    EditText edt_called;
    ListView operationListView;
    FloatingActionButton fab;
    ImageView back;
    ImageButton posButton;
    String newname;
    String newcalled;
    String myname;
    int productIndex;
    int index;
    int flagg;
    boolean isclick = false;
    private List<Map<String, String>> datas=new ArrayList<Map<String, String>>();
    SimpleAdapter simpleAdapter;
    SimpleAdapter simpleAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        image_panel = (RelativeLayout) findViewById(R.id.image_panel);
        name = (TextView)findViewById(R.id.name);
        called = (TextView)findViewById(R.id.called);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_called = (EditText) findViewById(R.id.edt_called);

        //product_image = (ImageView)findViewById(R.id.product_image);
        operationListView = (ListView)findViewById(R.id.operationListView);
        back = (ImageView)findViewById(R.id.back);

        posButton = (ImageButton)findViewById(R.id.pos_button);

        int wid = getResources().getDisplayMetrics().widthPixels;
        int hei = getResources().getDisplayMetrics().heightPixels/3;
        image_panel.setLayoutParams(new ConstraintLayout.LayoutParams(wid,hei));

        final Bundle product = getIntent().getExtras();
        flagg = product.getInt("flag",0);
        String t_name = product.getString("called");
        Log.i("thename",t_name);
        String n_name = t_name ;
        for (int i = 0; i < t_name.length(); i++) {
            if(t_name.substring(i, i + 1).equals("|")){
                n_name = t_name.substring(0,i).trim();
                break;
            }
        }
        Log.i("anothername",n_name);


        name.setText(product.getString("name"));
        called.setText(n_name);
        edt_name.setText(product.getString("name"));
        edt_called.setText(product.getString("called"));
        Bitmap background = BitmapFactory.decodeFile(product.getString("background"));
        Drawable bd= new BitmapDrawable(getResources(),background);
        image_panel.setBackground(bd);
        productIndex = product.getInt("productIndex");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        posButton.setImageResource(product.getInt("pos_image"));
        myname = product.getString("name");
        newname = product.getString("name");
        newcalled = product.getString("called");

        //String []opArr =  {"位置：", "技能1：", "技能2：", "技能3：","生存能力","攻击能力"};
        Map map1=new HashMap();
        map1.put("attribute","被动");
        map1.put("value",product.getString("skill0"));
        Map map2=new HashMap();
        map2.put("attribute","技能1");
        map2.put("value",product.getString("skill1"));
        Map map3=new HashMap();
        map3.put("attribute","技能2");
        map3.put("value",product.getString("skill2"));
        Map map4=new HashMap();
        map4.put("attribute","技能3");
        map4.put("value",product.getString("skill3"));
        Map map5=new HashMap();
        map5.put("attribute","生存能力");
        map5.put("value",product.getString("life"));
        Map map6=new HashMap();
        map6.put("attribute","攻击能力");
        map6.put("value",product.getString("attack"));
        Map map7=new HashMap();
        map7.put("attribute","技能效果");
        map7.put("value",product.getString("skilleffort"));
        Map map8=new HashMap();
        map8.put("attribute","上手难度");
        map8.put("value",product.getString("difficult"));
        datas.add(map1);
        datas.add(map2);
        datas.add(map3);
        datas.add(map4);
        datas.add(map5);
        datas.add(map6);
        datas.add(map7);
        datas.add(map8);
        simpleAdapter=new SimpleAdapter(this,datas,R.layout.product_detail_operation,new String[]{"attribute","value"},new int[]{R.id.attribute,R.id.value});
        simpleAdapter2=new SimpleAdapter(product_detail.this,datas,R.layout.edt_product_detail_operation,new String[]{"attribute","value"},new int[]{R.id.edt_attribute,R.id.edt_value});
        operationListView.setAdapter(simpleAdapter);

        operationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0 && i!=1 && i!=2 && i!=3) return;
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(product_detail.this);
                alertDialog.setTitle("技能详情");
                alertDialog.setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                String mes = "";
                if(i==0) mes = product.getString("skill0str");
                if(i==1) mes = product.getString("skill1str");
                if(i==2) mes = product.getString("skill2str");
                if(i==3) mes = product.getString("skill3str");
                alertDialog.setMessage(mes);
                alertDialog.show();

            }
        });

        fab = (FloatingActionButton)findViewById(R.id.edit);
        final AlertDialog.Builder builder = new AlertDialog.Builder(product_detail.this);
        builder.setTitle("提示");
        builder.setMessage("是否保存设置？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newname = edt_name.getText().toString();
                newcalled = edt_called.getText().toString();
                Log.i("here", operationListView.getItemAtPosition(0).toString());

                /*
                datas.set(0, (HashMap<String,String>)operationListView.getItemAtPosition(0));
                datas.set(1, (HashMap<String,String>)operationListView.getItemAtPosition(1));
                datas.set(2, (HashMap<String,String>)operationListView.getItemAtPosition(2));
                datas.set(3, (HashMap<String,String>)operationListView.getItemAtPosition(3));
                datas.set(4, (HashMap<String,String>)operationListView.getItemAtPosition(4));
                datas.set(5, (HashMap<String,String>)operationListView.getItemAtPosition(5));
                */
                if (hashMap.size() > 0) {
                    for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
                        datas.get(entry.getKey()).put("value", entry.getValue());
                    }
                }
                simpleAdapter.notifyDataSetChanged();
                hashMap.clear();
                Intent intent = new Intent(product_detail.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", productIndex);
                bundle.putString("name", newname);
                bundle.putString("called", newcalled);
                bundle.putString(datas.get(0).get("attribute"), datas.get(0).get("value"));
                bundle.putString(datas.get(1).get("attribute"), datas.get(1).get("value"));
                bundle.putString(datas.get(2).get("attribute"), datas.get(2).get("value"));
                bundle.putString(datas.get(3).get("attribute"), datas.get(3).get("value"));
                bundle.putString(datas.get(4).get("attribute"), datas.get(4).get("value"));
                bundle.putString(datas.get(5).get("attribute"), datas.get(5).get("value"));
                bundle.putString(datas.get(6).get("attribute"), datas.get(6).get("value"));
                bundle.putString(datas.get(7).get("attribute"), datas.get(7).get("value"));
                intent.putExtras(bundle);
                setResult(1,intent);
                operationListView.setAdapter(simpleAdapter);
                fab.setImageResource(R.drawable.edit);
                name.setVisibility(View.VISIBLE);
                called.setVisibility(View.VISIBLE);
                edt_name.setVisibility(View.INVISIBLE);
                edt_called.setVisibility(View.INVISIBLE);
                name.setText(newname);
                called.setText(newcalled);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                operationListView.setAdapter(simpleAdapter);
                fab.setImageResource(R.drawable.edit);
                name.setVisibility(View.VISIBLE);
                called.setVisibility(View.VISIBLE);
                edt_name.setVisibility(View.INVISIBLE);
                edt_called.setVisibility(View.INVISIBLE);
            }
        });
        if(flagg!=0) fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isclick) {
                    builder.show();
                } else {
                    edt_name.setVisibility(View.VISIBLE);
                    edt_called.setVisibility(View.VISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    called.setVisibility(View.INVISIBLE);
//                    Map map1=new HashMap();
//                    map1.put("attribute","位置");
//                    map1.put("value","王八");
//                    datas.set(1, map1);
                    adapter = new Adapter();
                    operationListView.setAdapter(adapter);
                    fab.setImageResource(R.drawable.more);
                }
                isclick = !isclick;
            }
        });

    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            String str1 = datas.get(position).get("attribute");
            String str2 = datas.get(position).get("value");
            convertView = LayoutInflater.from(getApplication()).inflate(R.layout.edt_product_detail_operation, null);


            final TextView text = (TextView) convertView.findViewById(R.id.edt_attribute);
            text.setText(str1);
            final EditText editText = (EditText)convertView.findViewById(R.id.edt_value);
            editText.setText(str2);

            //为editText设置TextChangedListener，每次改变的值设置到hashMap
            //我们要拿到里面的值根据position拿值
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count,int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //将editText中改变的值设置的HashMap中
                    hashMap.put(position, s.toString());
                }
            });

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        index = position;
                    }
                    return false;
                }

            });

            editText.clearFocus();

            if (index != -1 && index == position) {
// 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                editText.requestFocus();
            }
            editText.setSelection(editText .getText().length());
            //如果hashMap不为空，就设置的editText
            if(hashMap.get(position) != null){
                //editText.setText(hashMap.get(position));
            }


            return convertView;
        }

    }



}