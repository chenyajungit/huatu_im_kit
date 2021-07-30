package io.rong.imkit.io.rong.extend.textMessage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.R;
import io.rong.imkit.io.rong.extend.common.BaseViewHolder;
import io.rong.imkit.io.rong.extend.common.TemplateButtonViewholder;

/**
 * Created by cyj
 */

public class TemplateButtonAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    //只有一个时展示
    private final  static int JUST_ONE = 1;
    //有多个时,展示二行的形式
    private final static int TWO_BUTTON = 2;
    //有多个时,只有一行的那一个展示
    private final static int ONE_MORE_BUTTON = 3;
    private Context mContext;
    private ItemClickListener itemClickListener;



    //算下一行有几个按钮
    private List<List<ButtonEntity>> buttonLists=new ArrayList<>();
    public TemplateButtonAdapter(Context context){
        mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TemplateButtonViewholder baseViewHolder = null;
        View view;
        switch (viewType) {
            case JUST_ONE:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_template_layout, parent, false);
                baseViewHolder = new TemplateButtonViewholder(view);
                baseViewHolder.setType(1);
                break;
            case TWO_BUTTON:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_template_layout, parent, false);
                baseViewHolder = new TemplateButtonViewholder(view);
                baseViewHolder.setType(2);
                break;
            case ONE_MORE_BUTTON:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_template_layout, parent, false);
                baseViewHolder = new TemplateButtonViewholder(view);
                baseViewHolder.setType(3);
                break;
        }
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        if (holder instanceof TemplateButtonViewholder){
            TemplateButtonViewholder templateButtonViewholder = (TemplateButtonViewholder)holder;
            List<ButtonEntity> tempS=buttonLists.get(position);
            int showType=templateButtonViewholder.getShowType();
            switch (showType){
                case 1:
                    templateButtonViewholder.details_1_tv.setText(tempS.get(0).showText);
                    templateButtonViewholder.details_1_tv.setTag(tempS.get(0));
                    templateButtonViewholder.details_1_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickAction( view);
                        }
                    });
                    break;
                case 2:
                    templateButtonViewholder.frist_button_tv.setText(tempS.get(0).showText);
                    templateButtonViewholder.second_button_tv.setText(tempS.get(1).showText);
                    templateButtonViewholder.frist_button_tv.setTag(tempS.get(0));
                    templateButtonViewholder.frist_button_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickAction( view);

                        }
                    });
                    templateButtonViewholder.second_button_tv.setTag(tempS.get(1));
                    templateButtonViewholder.second_button_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickAction( view);

                        }
                    });
                    break;
                case 3:
                    templateButtonViewholder.one_button_tv.setText(tempS.get(0).showText);
                    templateButtonViewholder.one_button_tv.setTag(tempS.get(0));
                    templateButtonViewholder.one_button_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickAction( view);
                        }
                    });
                    break;

            }








        }

    }

    private void clickAction(View view){
        ButtonEntity buttonEntity=(ButtonEntity)view.getTag();
        if(itemClickListener!=null){
            itemClickListener.clickItem(buttonEntity);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        try{
            return buttonLists.size();
        }catch (Exception ex){
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        //当只有一个时,是展示一个
        int size=buttonLists.size();
        if (size==1&&buttonLists.get(0).size()==1){
            return JUST_ONE;
        }else {
           List<ButtonEntity> strings=buttonLists.get(position);
           if(strings.size()>1){
               return TWO_BUTTON;
           }else {
               return ONE_MORE_BUTTON;
           }

        }
    }

    public interface ItemClickListener{
        //点击的是哪一行的 哪个位置
        public void clickItem(ButtonEntity buttonEntity);
    }
    public void setButtonLists(List<List<ButtonEntity>> buttonLists) {
        this.buttonLists = buttonLists;
        notifyDataSetChanged();
    }

}
