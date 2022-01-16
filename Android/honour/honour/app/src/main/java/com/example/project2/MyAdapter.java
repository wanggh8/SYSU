package com.example.project2;

//import com.example.administrator.shoppinglist.Product;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemViewHolder> {
    ArrayList<Product> products;
    ArrayList<Product> products2;
    int flag;
    Context mmm;
    private static final String DB_NAME= "db_name";
    private static final String TABLE_NAME = "hero";
    private static final String TABLE_NAME1 = "skill";
    public MyDB dbHelper;

    public MyAdapter(ArrayList<Product> products, ArrayList<Product> products2, int flag,Context mmm) {
        this.products = products;
        this.products2 = products2;
        this.flag = flag;
        this.mmm = mmm;
        dbHelper = new MyDB(mmm,DB_NAME,null,1);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final Product product = products.get(position);
        System.out.print(product.getName());
        Bitmap icon = BitmapFactory.decodeFile(product.geticon());
        holder.label.setImageBitmap(icon);
        holder.name.setText((product.getName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//查看详细
                Intent intent = new Intent(view.getContext(), product_detail.class);
                Bundle data = new Bundle();
                data.putInt("productIndex", position);
                data.putString("name", product.getName());
                data.putString("called", product.getCalled());
                data.putString("attack", product.getAttack());
                data.putString("life", product.getLife());
                data.putString("background", product.getpicture());
                data.putString("head_image",product.geticon());
                data.putInt("pos_image",product.getPos_image());
                data.putString("skill1",product.getSkill1());
                data.putString("skill1str",product.getSkill1_str());
                data.putString("skill2",product.getSkill2());
                data.putString("skill2str",product.getSkill2_str());
                data.putString("skill3",product.getSkill3());
                data.putString("skill3str",product.getSkill3_str());
                data.putString("position",product.getPosition());
                data.putString("skill0",product.getSkill0());
                data.putString("skill0str",product.getSkill0_str());
                data.putString("skilleffort",product.getSkill());
                data.putString("difficult",product.getDifficulty());
                data.putInt("flag",flag);
                intent.putExtras(data);
                ((Activity)view.getContext()).startActivityForResult(intent, 0);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (flag == 0) {
                    //Toast.makeText(view.getContext(), "删除" + products.get(position).getName(), Toast.LENGTH_SHORT).show();
                    final int index =  position;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mmm);
                    builder.setTitle("删除")
                            .setMessage("确定删除" + products.get(position).getName() + "?")
                            .setCancelable(true)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    products2.add(products.get(index));
                                    notifyDataSetChanged();
                                    String hero_id = products.get(index).getId();
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    ContentValues cv1 = new ContentValues();
                                    cv1.put("is_add","false");
                                    db.update(TABLE_NAME,cv1,"hero_id = ?",new String[]{hero_id});
                                    db.close();
                                    products.remove(index);
                                    MyAdapter.this.notifyDataSetChanged();
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // do nothing
                                }
                            })
                            .show();
                }
                else if(flag == 1) {
                    //Toast.makeText(view.getContext(), "添加" + products.get(position).getName(), Toast.LENGTH_SHORT).show();
                    final int index =  position;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mmm);
                    builder.setTitle("添加")
                            .setMessage("确定添加" + products.get(position).getName() + "?")
                            .setCancelable(true)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    products2.add(products.get(index));
                                    notifyDataSetChanged();
                                    String hero_id = products.get(index).getId();
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    ContentValues cv1 = new ContentValues();
                                    cv1.put("is_add","true");
                                    db.update(TABLE_NAME,cv1,"hero_id = ?",new String[]{hero_id});
                                    db.close();
                                    products.remove(index);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // do nothing
                                }
                            })
                            .show();
                }
                /*if (flag != 2) {
                    products2.add(products.get(position));
                    //products.remove(position);
                    MyAdapter.this.notifyDataSetChanged();
                }*/
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    public Product getProduct(int pos) {
        return products.get(pos);
    }

//    public void addProduct(Product product) {
//        products.add(product);
//        this.notifyDataSetChanged();
//    }

    class ItemViewHolder extends ViewHolder {

        public ImageView label;
        public TextView name;
        public ItemViewHolder(View itemView) {
            super(itemView);
            this.label = itemView.findViewById(R.id.label);
            this.name = (TextView)itemView.findViewById(R.id.name);
        }
    }
}