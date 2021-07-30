package io.rong.imkit.io.rong.extend.messagedetail;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import io.rong.imkit.R;
import io.rong.imkit.io.rong.extend.common.BaseViewHolder;

public class MessageDetailViewHolder extends BaseViewHolder {

    public TextView mTv;
    public ScrollView mScrollView;
    public LinearLayout mLl;

    protected MessageDetailViewHolder(View view) {
        super(view);
        initView();
    }

    private void initView(){
        mTv = $$(R.id.text);
        mScrollView = $$(R.id.scroll_view);
        mLl = $$(R.id.linear_layout);
    }
}
