/*
 * Copyright (C) 2016. Keegan小钢（http://keeganlee.me）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.keeganlee.kandroid.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import me.keeganlee.kandroid.R;
import me.keeganlee.kandroid.model.CouponBO;
import me.keeganlee.kandroid.util.CouponPriceUtil;

/**
 * Created by feng on 2016/4/26.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CouponViewHolder> {
    private static final String TAG = "RecyclerAdapter";

    private Context context;

    public List<CouponBO> itemList = new ArrayList<CouponBO>();

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 判断数据是否为空
     *
     * @return 为空返回true，不为空返回false
     */
    public boolean isEmpty() {
        return itemList.isEmpty();
    }

    /**
     * 在原有的数据上添加新数据
     *
     * @param itemList
     */
    public void addItems(List<CouponBO> itemList) {
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    /**
     * 设置为新的数据，旧数据会被清空
     *
     * @param itemList
     */
    public void setItems(List<CouponBO> itemList) {
        this.itemList.clear();
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    /**
     * 清空数据
     */
    public void clearItems() {
        itemList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.e(TAG, "onCreateViewHolder, i: " + i);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_coupon,null);

        return new CouponViewHolder(view);
    }

    /**
     *滑动RecyclerView，onBindViewHolder就会不停的调用
     * 如果每个item有点击事件的话，应判断已避免重建点击事件对象if(mOnItemClickListener != null)
     */
    @Override
    public void onBindViewHolder(CouponViewHolder viewHolder, int i) {
        Log.e(TAG, "onBindViewHolder, i: " + i + ", viewHolder: " + viewHolder);
        viewHolder.position = i;
        CouponBO coupon = itemList.get(i);
        Log.e(TAG, "titleText:" + coupon.getName() + " infoText:" + coupon.getIntroduce());

        viewHolder.titleText.setText(coupon.getName());
        viewHolder.infoText.setText(coupon.getIntroduce());
        SpannableString priceString;
        // 根据不同的券类型展示不同的价格显示方式
        switch (coupon.getModelType()) {
            default:
            case CouponBO.TYPE_CASH:
                priceString = CouponPriceUtil.getCashPrice(context, coupon.getFaceValue(), coupon.getEstimateAmount());
                break;
            case CouponBO.TYPE_DEBIT:
                priceString = CouponPriceUtil.getVoucherPrice(context, coupon.getDebitAmount(), coupon.getMiniAmount());
                break;
            case CouponBO.TYPE_DISCOUNT:
                priceString = CouponPriceUtil.getDiscountPrice(context, coupon.getDiscount(), coupon.getMiniAmount());
                break;
        }
        viewHolder.priceText.setText(priceString);

    }


    //viewholder
    class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView infoText;
        TextView priceText;
        int position;

        public CouponViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.text_item_title);
            infoText = (TextView) itemView.findViewById(R.id.text_item_info);
            priceText = (TextView) itemView.findViewById(R.id.text_item_price);
        }
    }

}
