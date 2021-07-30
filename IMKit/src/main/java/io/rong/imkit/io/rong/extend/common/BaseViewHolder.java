package io.rong.imkit.io.rong.extend.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by cyj
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public View rootView = null;

    protected BaseViewHolder(View view) {
        super(view);
        this.rootView = view;

    }


    protected <T extends View> T $$(int viewId) {
        return (T) this.rootView.findViewById(viewId);
    }

    public View getRootView() {
        return rootView;
    }



}
