package com.example.password_keeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.example.password_keeper.MyApplication;
import com.example.password_keeper.R;
import com.example.password_keeper.adapters.KeyListAdapter;
import com.example.password_keeper.beans.Item;
import com.example.password_keeper.constants.Constants;
import com.example.password_keeper.greendao.ItemDao;
import com.example.password_keeper.storage.MySP;
import com.example.password_keeper.utils.LoadUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面和菜单界面
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.category_self)
    LinearLayout mCategorySelf;

    @BindView(R.id.category_comm)
    LinearLayout mCategoryComm;

    @BindView(R.id.category_net)
    LinearLayout mCategoryNet;

    @BindView(R.id.category_work)
    LinearLayout mCategoryWork;

    @BindView(R.id.category_other)
    LinearLayout mCategoryOther;

    @BindView(R.id.category_layout)
    LinearLayout mCategoryLayout;

    @BindView(R.id.items_list)
    RecyclerView mItemsList;

    @BindView(R.id.progress)
    ProgressBar pro;

    private MySP mMySP;

    private Item[] mKeyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定黄油刀
        ButterKnife.bind(this);
        mMySP = new MySP(this).getmMySP();
        mMySP.save("is_first_load", false);
        //通过枚举的CategoryId填充对应目录下的全部条目
        mKeyItems = LoadUtils.loadSelfKeyItemsByCategoryId(Constants.SELF_ID);
        mItemsList.setLayoutManager(new LinearLayoutManager(this));
        mItemsList.setAdapter(new KeyListAdapter(this, mKeyItems, Constants.SELF_ID));
    }

    //加载顶层按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //顶层按钮的条目的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.menu_dev){
            Intent intent = new Intent(MainActivity.this, DevActivity.class);
            startActivity(intent);
            return true;
        }
        else if(itemId == R.id.menu_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        //同步选项的点击事件
        else if(itemId == R.id.menu_syn) {
            pro.setVisibility(View.VISIBLE);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMySP = new MySP(this).getmMySP();
        mMySP.save("is_first_load", false);
        //通过枚举的CategoryId填充对应目录下的全部条目
        mKeyItems = LoadUtils.loadSelfKeyItemsByCategoryId(Constants.SELF_ID);
        mItemsList.setLayoutManager(new LinearLayoutManager(this));
        mItemsList.setAdapter(new KeyListAdapter(this, mKeyItems, Constants.SELF_ID));
        ItemDao itemDao = MyApplication.getInstance().getDaoSession().getItemDao();
        List<Item> itemList = itemDao.queryBuilder().where(ItemDao.Properties.CategoryId.eq(5)).list();
    }

    //使用黄油刀设置各个按钮的点击事件
    @OnClick({R.id.category_self, R.id.category_comm, R.id.category_net, R.id.category_work, R.id.category_other, R.id.add_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.category_self:
                mKeyItems = LoadUtils.loadSelfKeyItemsByCategoryId(Constants.SELF_ID);
                mItemsList.setAdapter(new KeyListAdapter(this, mKeyItems, Constants.SELF_ID));
                break;
            case R.id.category_comm:
                mKeyItems = LoadUtils.loadSelfKeyItemsByCategoryId(Constants.COMM_ID);
                mItemsList.setAdapter(new KeyListAdapter(this, mKeyItems, Constants.COMM_ID));
                break;
            case R.id.category_net:
                mKeyItems = LoadUtils.loadSelfKeyItemsByCategoryId(Constants.NET_ID);
                mItemsList.setAdapter(new KeyListAdapter(this, mKeyItems, Constants.NET_ID));
                break;
            case R.id.category_work:
                mKeyItems = LoadUtils.loadSelfKeyItemsByCategoryId(Constants.WORK_ID);
                mItemsList.setAdapter(new KeyListAdapter(this, mKeyItems, Constants.WORK_ID));
                break;
            case R.id.category_other:
                mKeyItems = LoadUtils.loadSelfKeyItemsByCategoryId(Constants.OTHER_ID);
                mItemsList.setAdapter(new KeyListAdapter(this, mKeyItems, Constants.OTHER_ID));
                break;
            case R.id.add_button:
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.DETAIL_ACTIVITY_MODE, Constants.DETAIL_ACTIVITY_ADD);
                intent.putExtra(Constants.DETAIL_ACTIVITY_MODE_DATA, bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
