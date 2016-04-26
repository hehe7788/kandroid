package me.keeganlee.kandroid.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import me.keeganlee.kandroid.R;
import me.keeganlee.kandroid.adapter.RecyclerAdapter;
import me.keeganlee.kandroid.core.ActionCallbackListener;
import me.keeganlee.kandroid.core.ErrorEvent;
import me.keeganlee.kandroid.model.CouponBO;

public class CouponRecycleListActivity extends KBaseActivity implements SwipeRefreshLayout.OnRefreshListener,
RecyclerAdapter.OnRecyclerViewListener{
    private static final String TAG = "CouponRecycle";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private int lastVisiblePosition;
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
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        getData();

        adapter = new RecyclerAdapter(this);
        adapter.setOnRecyclerViewListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e(TAG, "lastVisiblePosition and adapter.getItemCount()" + lastVisiblePosition + "  " + adapter.getItemCount());

                //在没有删除项的情况下，滚动到底时lastVisiblePosition + 1 = adapter.getItemCount()
                //删除了某些项后，滚动到底时lastVisiblePosition + 1 > adapter.getItemCount()
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisiblePosition + 1 >= adapter.getItemCount()) {
                    currentPage++;
                    getData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiblePosition =  layoutManager.findLastVisibleItemPosition();
            }
        });
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
                if (errorEvent == ErrorEvent.PAGE_UP_FLOW) {
                    currentPage--;
                }
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


    @Override
    public void onItemClick(int position) {
        Log.e(TAG, ((CouponBO) adapter.getItem(position)).getName());
    }

    @Override
    public boolean onItemLongClick(int position) {
        Log.e(TAG + "long", ((CouponBO) adapter.getItem(position)).getName());
        adapter.removeItem(position);
        return true;
    }
}
