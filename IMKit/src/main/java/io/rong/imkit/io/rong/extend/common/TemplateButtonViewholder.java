package io.rong.imkit.io.rong.extend.common;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.rong.imkit.R;

/**
 * Created by cyj
 */

public class TemplateButtonViewholder extends BaseViewHolder{
    public LinearLayout just_one_ll,two_button_ll,one_button_ll;
    public TextView  details_1_tv,frist_button_tv,second_button_tv,one_button_tv;
    //1 只有一个时  2 有多个时展示二个   3  有多个时展示一个
    private int showType=-1;
    public TemplateButtonViewholder(View view) {
        super(view);
        initView();
    }

    private void initView() {
        just_one_ll = $$(R.id.just_one_ll);
        details_1_tv = $$(R.id.details_1_tv);

        two_button_ll = $$(R.id.two_button_ll);
        frist_button_tv= $$(R.id.frist_button_tv);
        second_button_tv= $$(R.id.second_button_tv);

        one_button_ll = $$(R.id.one_button_ll);
        one_button_tv= $$(R.id.one_button_tv);

    }

    public void setType(int type){
        showType=type;
        switch (type){
            case 1:
                just_one_ll.setVisibility(View.VISIBLE);
                two_button_ll.setVisibility(View.GONE);
                one_button_ll.setVisibility(View.GONE);
                break;
            case 2:
                just_one_ll.setVisibility(View.GONE);
                two_button_ll.setVisibility(View.VISIBLE);
                one_button_ll.setVisibility(View.GONE);

                break;
            case 3:
                just_one_ll.setVisibility(View.GONE);
                two_button_ll.setVisibility(View.GONE);
                one_button_ll.setVisibility(View.VISIBLE);

                break;

        }


    }
    public int getShowType() {
        return showType;
    }



}
