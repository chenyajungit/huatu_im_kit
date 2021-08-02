package io.rong.imkit.io.rong.extend.textMessage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.rong.imkit.R;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM.ConversationBehaviorListener;
import io.rong.imkit.RongIM.ConversationClickListener;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.io.rong.extend.ConversationActivity;
import io.rong.imkit.io.rong.extend.common.LinkMovementMethodEx;
import io.rong.imkit.io.rong.extend.common.NoUnderlineSpan;
import io.rong.imkit.io.rong.extend.common.SharedPreferencesUtil;
import io.rong.imkit.io.rong.extend.messagedetail.MessageDetailActivity;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AutoLinkTextView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.MD5;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import static io.rong.imkit.io.rong.extend.ConversationActivity.CURRENT_CHAT_ID;
import static io.rong.imkit.io.rong.extend.ConversationActivity.CURRENT_TARGETID;
import static io.rong.imkit.io.rong.extend.ConversationActivity.RONG_IM_USER_ID;

@ProviderTag(
        messageContent = TextMessage.class,
        showReadState = true
)
public abstract class OATextMessageItemProvider extends IContainerItemProvider.MessageProvider<TextMessage> {
    private static final String TAG = "OATextMessageItemProvider";
    public Context mContext;
    private String targeId="";
    private int messageId;
    private Message currentMessage;

    //副标题
    public static final int SUB_TITLE_TYPE=1;
    //表单项
    public static final int FORM_TYPE=2;
    //按钮项
    public static final int BUTTON_TYPE=3;


    //将文字消息类型的View直接传出去,这样方便二次开发
    public abstract void textViewMessageListener(View view);
    /**
     * @param type        点击的表单项内容 1副标题  2表单项  3按钮项
     * @param hrefType    链接类型 1 H5  2 原生
     * @param hrefContent 链接参数
     */
    public abstract void templateClickListener(int type, int hrefType, String hrefContent) ;

    /**
     *
     * @param pdfUrl pdf的url
     */
    public abstract void clickPdfListener(String pdfUrl) ;

    /**
     *
     * @param webUrl 网页链接
     */
    public abstract void commonListener(String webUrl) ;

    /**
     *
     * @param view 长按对应的View
     * @param messageId 长按对应的消息id
     */
    public abstract void commonTextMessageLongClickListener(View view,Integer messageId) ;

    /**
     *
     * @param view 点击对应的View
     * @param messageId 对应的消息id
     */
    public abstract void commonTextMessageClickListener(View view,Integer messageId) ;

    public View newView(Context context, ViewGroup group) {
        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.oa_conversation_item_custom_message, null);
        //有特殊需求可自行处理
        textViewMessageListener(view);

        ViewHolder holder = new ViewHolder();
        holder.message = view.findViewById(R.id.oa_conversition_item_autolink_tv);
        holder.content1Recycleview=view.findViewById(R.id.content_1_recycleview);
        holder.publicServices1Ll=view.findViewById(R.id.public_services_1_ll);
        holder.clickLl=view.findViewById(R.id.click_ll);
        holder.line1=view.findViewById(R.id.line_1);
        holder.details1Tv=view.findViewById(R.id.details_1_tv);
        holder.mettingLl=view.findViewById(R.id.metting_ll);
        holder.mettingTv=view.findViewById(R.id.metting_tv);
        holder.meetingLittleTitleTv=view.findViewById(R.id.meeting_little_title_tv);
        holder.title0Tv=view.findViewById(R.id.title_0_tv);
        holder.title1Tv=view.findViewById(R.id.title_1_tv);
        holder.mettingRecycleview=view.findViewById(R.id.metting_recycleview);
        holder.mettingForwardTv=view.findViewById(R.id.metting_forward_tv);
        holder.mettingAddTv=view.findViewById(R.id.metting_add_tv);
        //问答
        holder.publicServices2Ll=view.findViewById(R.id.public_services_2_ll);
        holder.q2Rv=view.findViewById(R.id.q2_rv);

        holder.buSettingContentTv=view.findViewById(R.id.bu_setting_content_tv);
        holder.buSettingRl=view.findViewById(R.id.bu_setting_ll);
        holder.changeBuTv=view.findViewById(R.id.change_bu_tv);

        holder.totalLlV1=view.findViewById(R.id.total_ll_v1);
        holder.totalLlV2=view.findViewById(R.id.total_ll_v2);

        holder.left_weight_view=view.findViewById(R.id.left_weight_view);
        holder.right_weight_view=view.findViewById(R.id.right_weight_view);
        holder.text_content_ll=view.findViewById(R.id.text_content_ll);


        holder.actionButtonRl=view.findViewById(R.id.action_button_rl);


        holder.mainTitleTv=view.findViewById(R.id.main_title_tv);
        holder.subtitleTv=view.findViewById(R.id.subtitle_tv);
        holder.contentTv=view.findViewById(R.id.content_tv);

        view.setTag(holder);
        return view;
    }

    public Spannable getContentSummary(TextMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, TextMessage data) {
        if (data == null) {
            return null;
        } else {
            String content = data.getContent();
            if (content != null) {

                String string= getTitleShow(content);

                return new SpannableString(AndroidEmoji.ensure(string));
            } else {
                return null;
            }
        }
    }

//    public void onItemClick(View view, int position, TextMessage content, UIMessage message) {
//        MessageContent messageContentTemp = message.getContent();
//        if (messageContentTemp != null && messageContentTemp instanceof TextMessage) {
//            TextMessage textMessage = (TextMessage) messageContentTemp;
//            String contentMessage = textMessage.getContent();
//
//        }
//    }


    public void bindView(final View v, int position, TextMessage content, final UIMessage data) {
        ViewHolder holder = (ViewHolder) v.getTag();
        JSONObject jsonObject=content.getJSONUserInfo();
        messageId=data.getMessageId();
        currentMessage=data.getMessage();
        targeId=data.getTargetId();
        holder.totalLlV2.setVisibility(View.GONE);
        holder.totalLlV1.setVisibility(View.VISIBLE);
        try {
            if(jsonObject!=null){
                String contentStr=jsonObject.getString("portrait");
                if(contentStr==null||"".equals(contentStr)){

                    comminMessage(  v,  position,  content,   data, holder);
                }else {
                    JSONObject msgObject=new JSONObject(contentStr);
                    String version=msgObject.optString("version");
                    if(version!=null&&"v2".equals(version)){
                        String firstHead=msgObject.optString("firstHead");
                        if(firstHead==null||"".equals(firstHead)||"null".equals(firstHead)){
                            holder.mainTitleTv.setVisibility(View.GONE);
                        }else {
                            holder.mainTitleTv.setVisibility(View.VISIBLE);
                            holder.mainTitleTv.setText(firstHead);
                        }
                        JSONObject secondHead=msgObject.optJSONObject("secondHead");
                        if(secondHead!=null){
                            String secondHeadContent=secondHead.optString("secondHeadContent");
                            if(secondHeadContent==null||"".equals(secondHeadContent)){
                                holder.subtitleTv.setVisibility(View.GONE);
                            }else {
                                holder.subtitleTv.setVisibility(View.VISIBLE);
                                if(secondHead.getBoolean("includeHref")){
                                    SpannableStringBuilder spannableStringBuilder= v2DataAnalysis(secondHeadContent,1);
                                    //添加这句话，否则点击不生效
                                    holder.subtitleTv.setMovementMethod(LinkMovementMethod.getInstance());
                                    holder.subtitleTv.setText(spannableStringBuilder);
                                }else {
                                    holder.subtitleTv.setText(secondHeadContent);
                                }
                            }
                        }else {
                            holder.subtitleTv.setVisibility(View.GONE);
                        }


                        JSONObject form=msgObject.optJSONObject("form");
                        if(form!=null){
                            String formContent=form.optString("formContent");
                            if(formContent==null||"".equals(formContent)){
                                holder.contentTv.setVisibility(View.GONE);
                            }else {
                                holder.contentTv.setVisibility(View.VISIBLE);
                                if(secondHead.getBoolean("includeHref")){
                                    SpannableStringBuilder spannableStringBuilder= v2DataAnalysis(formContent,2);
                                    //添加这句话，否则点击不生效
                                    holder.contentTv.setMovementMethod(LinkMovementMethod.getInstance());
                                    holder.contentTv.setText(spannableStringBuilder);
                                }else {
                                    holder.contentTv.setText(formContent);
                                }
                            }
                        }else {
                            holder.contentTv.setVisibility(View.GONE);
                        }
                        holder.totalLlV2.setVisibility(View.VISIBLE);
                        holder.totalLlV1.setVisibility(View.GONE);
                        List<ButtonEntity> buttonEntities=new ArrayList<>();
                        JSONArray jsonArray=msgObject.getJSONArray("buttonList");
                        int size=jsonArray.length();
                        for(int i=0;i<size;i++){
                            JSONObject jsonObject1=jsonArray.optJSONObject(i);
                            ButtonEntity buttonEntity=new ButtonEntity();
                            buttonEntity.hrefType=jsonObject1.optString("hrefType");
                            buttonEntity.url=jsonObject1.optString("url");
                            buttonEntity.showText=jsonObject1.optString("showText");
                            buttonEntity.secret=jsonObject1.optString("secret");
                            buttonEntities.add(buttonEntity);
                        }
                        List<List<ButtonEntity>> lists=new ArrayList<>();
                        if(buttonEntities.size()==1){
                            List<ButtonEntity> strings=new ArrayList<>();
                            strings.add(buttonEntities.get(0));
                            lists.add(strings);
                        }else {
                            List<ButtonEntity> temps=null;
                            int buttonEntitiesSize=buttonEntities.size();
                            int index=1;
                            for(ButtonEntity buttonEntity:buttonEntities){
                                if(temps==null){
                                    temps=new ArrayList<>();
                                }
                                if(temps.size()<2){
                                    temps.add(buttonEntity);
                                }
                                if(index==buttonEntitiesSize||temps.size()==2){
                                    //如果是最后一个 或者 加入了二个按钮的话 则将其加入list
                                    List<ButtonEntity> temp1s=new ArrayList<>();
                                    for(ButtonEntity s:temps){
                                        temp1s.add(s);
                                    }
                                    lists.add(temp1s);
                                    temps=null;
                                }
                                index++;
                            }
                        }
                        TemplateButtonAdapter templateButtonAdapter=new TemplateButtonAdapter(mContext);
                        holder.actionButtonRl.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
                        holder.actionButtonRl.setAdapter(templateButtonAdapter);
                        templateButtonAdapter.setButtonLists(lists);
                        templateButtonAdapter.setItemClickListener(new TemplateButtonAdapter.ItemClickListener() {
                            @Override
                            public void clickItem(ButtonEntity buttonEntity) {
//                                String s="";

                                newVersionClickAction(buttonEntity.hrefType, buttonEntity.url, buttonEntity.showText,3,buttonEntity.secret);

                            }

                        });




                    }else {
                        comminMessage(  v,  position,  content,   data, holder);
                    }
                }
            }else {
                comminMessage(  v,  position,  content,   data, holder);
            }
        } catch (JSONException e) {
            comminMessage(  v,  position,  content,   data, holder);
        }
    }

    private SpannableStringBuilder v2DataAnalysis(String originalStr, final int type){
        try{
            String[] fristArr=originalStr.split("<esbhref>");
            int length=fristArr.length;
            //慢慢往里面加东西
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            for(int i=0;i<length;i++){
                //单个数据
                String tempStr=fristArr[i];
                //包含的话
                if(tempStr!=null){
                    if(tempStr.contains("</esbhref>")){
                        String[] endArr=tempStr.split("</esbhref>");
                        if(endArr!=null){
                            int size=endArr.length;
                            for(int i1=0;i1<size;i1++){
                                switch (i1){
                                    case 0:
                                        final JSONObject jsonObject=new JSONObject(endArr[0]);
                                        int startIndex=spannableStringBuilder.length();
                                        String showText=jsonObject.optString("showText");
                                        int endIndex=startIndex+showText.length();
                                        spannableStringBuilder.append(jsonObject.optString("showText"));
                                        spannableStringBuilder.setSpan(new NoUnderlineSpan(){
                                            @Override
                                            public void onClick(@NonNull View view) {
                                                String hrefType=jsonObject.optString("hrefType");
                                                String url=jsonObject.optString("url");
                                                String showText=jsonObject.optString("showText");
                                                String secret=jsonObject.optString("secret");
                                                newVersionClickAction(hrefType, url, showText,type,secret);

                                            }
                                        }, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#4A88FB")), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);


                                        break;
                                    case 1:
                                        spannableStringBuilder.append(endArr[1]);
                                        break;
                                }

                            }


                        }


                        //包含了尾部信息
//                         spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#0099ff")), firstIndex, firstIndex + filterStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                    }else {
                        //未包含尾部信息,则直接添加
                        spannableStringBuilder.append(tempStr);
                    }
                }

            }


            return spannableStringBuilder;

        }catch (Exception e){
            return null;
        }





    }



    //调用服务器接口从而获取问题答案
    public void requestService(final String userId, final String officialAccountCode, final String timestamp, final String random, final String secret, final String originalUrl){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                String md5= MD5.encrypt(userId+officialAccountCode+timestamp+random+secret);
                String url =originalUrl;
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .addHeader("userId",userId)
                        .addHeader("officialAccountCode",officialAccountCode)
                        .addHeader("timestamp",timestamp)
                        .addHeader("random8",random)
                        .addHeader("secret",secret)
                        .addHeader("token",md5)
                        .url(url).get().build();
                OkHttpClient okHttpClient = new OkHttpClient();
                final Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String s="";
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        String s="";
                    }
                });
            }
        }).start();
    }









    private void comminMessage(final View v, int position, final TextMessage content, final UIMessage data, ViewHolder holder){
        holder.message.setTag(R.id.tag_object,data.getMessage());
        holder.message.setTag(R.id.im_ui_message,data);
        holder.text_content_ll.setVisibility(View.VISIBLE);
        holder.mettingLl.setVisibility(View.GONE);
        holder.publicServices1Ll.setVisibility(View.GONE);
        holder.publicServices2Ll.setVisibility(View.GONE);
        holder.buSettingRl.setVisibility(View.GONE);

        if (data.getMessageDirection() == MessageDirection.SEND) {
            holder.left_weight_view.setVisibility(View.VISIBLE);
            holder.right_weight_view.setVisibility(View.GONE);
            holder.message.setTextColor(Color.parseColor("#FFFFFFFF"));
            holder.message.setBackgroundResource(R.drawable.oa_im_rc_ic_bubble_right);
        } else {
            holder.left_weight_view.setVisibility(View.GONE);
            holder.right_weight_view.setVisibility(View.VISIBLE);
            holder.message.setTextColor(Color.parseColor("#FF373D46"));
            holder.message.setBackgroundResource(R.drawable.oa_im_rc_ic_bubble_left);
        }
        final AutoLinkTextView textView = holder.message;
        textView.setText("");
        if (data.getTextMessageContent() != null) {
            int len = data.getTextMessageContent().length();
            if (v.getHandler() != null && len > 500) {
                v.getHandler().postDelayed(new Runnable() {
                    public void run() {
                        textOperation(textView,  content,   data, textView);
                    }
                }, 50L);
            } else {
                textOperation(textView, content,   data,  textView);
            }
        }

        holder.message.setMovementMethod(null);
        holder.message.setLinkTextColor(mContext.getResources().getColor(R.color.lightskyblue));
        holder.message.setMovementMethod(new LinkMovementMethodEx(new LinkMovementMethodEx.LinkClickListener() {
            @Override
            public boolean onLinkClick(String mURL) {
                ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
                ConversationClickListener clickListener = RongContext.getInstance().getConversationClickListener();
                boolean result = false;
                if (listener != null) {
                    result = listener.onMessageLinkClick(v.getContext(), mURL);
                } else if (clickListener != null) {
                    result = clickListener.onMessageLinkClick(v.getContext(), mURL, data.getMessage());
                }
                if (listener == null && clickListener == null || !result) {
                    String url = mURL.toLowerCase();
                    String end = url.substring(url.lastIndexOf(".") + 1, url.length()).toLowerCase();
                    if(!url.contains("http")){
                        url="http://"+url;
                    }
                    if(end.equals("pdf")){
//                        Intent intent2 = new Intent(mContext, PdfViewActivity.class);
//                        intent2.putExtra("url",url);
//                        intent2.putExtra("title","pdf文件");
//                        mContext.startActivity(intent2);
                        clickPdfListener(url);
                        //打开pdf
                        return true;
                    }else {
//                        Intent intent1 = new Intent(mContext, NewOaWebViewForRongYunActivity.class);
//                        intent1.putExtra("url", mURL);
//                        mContext.startActivity(intent1);
                        commonListener(mURL);
                        //点击链接后跳转

                        return true;
                    }
                }
                return result;


            }
        }));


        textView.stripUnderlines();

        textView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick(View view) {
                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putExtra("text", textView.getText());
                mContext.startActivity(intent);
            }
        }));
        holder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setTag(R.id.new_oa_tag_position,messageId);
                //长按之后处理
                commonTextMessageLongClickListener(v,messageId);
                return true;
            }
        });

        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击之后
                commonTextMessageClickListener(v,messageId);
            }
        });

    }

    private String  getTitleShow(String strMsg){
        if(strMsg!=null&&strMsg.contains("#*￥$#")) {
            //表示包含了待解析的数据
            String[] strArr = strMsg.split("\\#\\*\\￥\\$\\#");
            if(strArr.length>0){
                if(strArr.length==1){
                    return strMsg;
                }else {
                    return strArr[0];
                }
            }else {
                return "";
            }
        }else {
            if (strMsg != null) {
                if (strMsg.length() > 100) {
                    strMsg = strMsg.substring(0, 100);
                }
                return strMsg;
            } else {
                return "";
            }
        }

    }




    private void textOperation(View v, TextMessage content, UIMessage data, TextView textView){
        MessageContent content1 = data.getContent();
        if (content instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) content1;
            String clickContent=textMessage.getContent();
            if(clickContent!=null){
                    SpannableStringBuilder spannable = new SpannableStringBuilder(clickContent);
                    SpannableStringBuilder spannable1 = AndroidEmoji.replaceEmojiWithText(spannable);
                    AndroidEmoji.ensure(spannable1);
                    textView.setText(spannable1);
            }
        }
    }



    private void newVersionClickAction(String hrefType,String url,String showText,int type,String secret){
        switch (hrefType){
            case "1":
                //H5
                templateClickListener(type,Integer.parseInt(hrefType),url);
                break;
            case "2":
                //原生
                templateClickListener(type,Integer.parseInt(hrefType),url);
                break;

            case "3":
                Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + showText));//跳转到拨号界面，同时传递电话号码
                mContext.startActivity(dialIntent);
                break;
            case "4":
                StringBuilder str=new StringBuilder();//定义变长字符串
                Random random=new Random();
                //随机生成数字，并添加到字符串
                for(int i=0;i<8;i++){
                    str.append(random.nextInt(10));
                }
                SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(mContext);
                requestService( (String) sharedPreferencesUtil.getSharedPreference(RONG_IM_USER_ID,""),(String) sharedPreferencesUtil.getSharedPreference(CURRENT_CHAT_ID,""),System.currentTimeMillis()+"",str.toString(),secret,url);
                break;
        }


    }



    private static class ViewHolder {
        LinearLayout totalLlV1;
        LinearLayout totalLlV2;
        TextView mainTitleTv;
        TextView subtitleTv;
        TextView contentTv;

        AutoLinkTextView message;
        View left_weight_view,right_weight_view;
        LinearLayout text_content_ll;
        LinearLayout publicServices1Ll;
        RecyclerView content1Recycleview;
        TextView details1Tv;
        LinearLayout mettingLl;
        TextView mettingTv;
        TextView meetingLittleTitleTv;
        LinearLayout clickLl;
        View line1;
        RecyclerView mettingRecycleview;
        TextView mettingForwardTv;
        TextView mettingAddTv;
        LinearLayout publicServices2Ll;
        RecyclerView q2Rv;
        TextView title0Tv, title1Tv;

        RelativeLayout buSettingRl;
        TextView buSettingContentTv;
        TextView changeBuTv;

        RecyclerView actionButtonRl;

        private ViewHolder() {
        }
    }

}
