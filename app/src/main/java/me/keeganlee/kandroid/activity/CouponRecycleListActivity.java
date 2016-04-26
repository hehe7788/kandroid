package me.keeganlee.kandroid.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import me.keeganlee.kandroid.R;
import me.keeganlee.kandroid.adapter.CouponListAdapter;
import me.keeganlee.kandroid.core.ActionCallbackListener;
import me.keeganlee.kandroid.model.CouponBO;

public class CouponRecycleListActivity extends KBaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_recycle_list);

        initViews();
    }

    private void initViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.list_view);

        //使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        recyclerView.setHasFixedSize(true);

        //设置recyclerView方向
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        getData();

        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        this.appAction.listCoupon(currentPage, new ActionCallbackListener<List<CouponBO>>() {
            @Override
            public void onSuccess(List<CouponBO> data) {
                if (!data.isEmpty()) {
                    if (currentPage == 1) { // 第一页
                        adapter.setItems(data);
                    } else { // 分页数据
                        adapter.addItems(data);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        // 需要重置当前页为第一页，并且清掉数据
        currentPage = 1;
        adapter.clearItems();
        getData();
    }


}
