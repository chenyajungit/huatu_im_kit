package io.rong.imkit.io.rong.extend;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.json.JSONObject;

import java.util.Base64;
import java.util.Locale;
import java.util.Random;

import io.rong.imkit.R;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.io.rong.extend.common.SharedPreferencesUtil;
import io.rong.imkit.io.rong.extend.common.StringUtils;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cyj
 */

public class ConversationActivity extends AppCompatActivity implements RongIM.ConversationClickListener, RongIMClient.OnReceiveMessageListener {
    private  final String TAG = "ConversationActivity";
    private String title;
    /**
     * 对方id
     */
    private String mTargetId;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    public static  Conversation.ConversationType CURRENT_CONVERSATION_TYPE;
    public static String CURRENT_TARGETID;

    public ConversationFragment conversationFragment;
    private android.support.v4.app.FragmentTransaction mFragmentTransaction;
    public static final String RONG_IM_USER_ID="RONG_IM_USER_ID";
    //ESB的id和secret
    public static final String ESB_APP_ID="ESB_APP_ID";
    public static final String ESB_APP_SECRET="ESB_APP_SECRET";
    //每两次推送欢迎语之间,间隔的分钟数。需要推送时，此字段必填，默认为5分钟
    public static final String INTERVAL="INTERVAL";
    //是否在查询的同时，直接推送欢迎语
    public static final String PUSHFLAG="PUSHFLAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntentData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_conversation_layout);
        //获取欢迎语
        pushWelcomeMsg();
        initControl();
    }

    private void getIntentData() {
        title = this.getIntent().getData().getQueryParameter("title");
        mTargetId = this.getIntent().getData().getQueryParameter("targetId");
        mConversationType = Conversation.ConversationType.valueOf(this.getIntent().getData()
                .getLastPathSegment().toUpperCase(Locale.US));
        CURRENT_CONVERSATION_TYPE=mConversationType;
        CURRENT_TARGETID=mTargetId;

    }

    private void initControl(){
        conversationFragment=new ConversationFragment();
        //开启事务
        mFragmentTransaction = ConversationActivity.this.getSupportFragmentManager().beginTransaction();
        //设置为默认界面 MainHomeFragment
        mFragmentTransaction.add(R.id.conversation, conversationFragment).commitAllowingStateLoss(); //开启事务
        requestMyPermissions();

    }

    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d(TAG, "requestMyPermissions: 有读SD权限");
        }
    }

    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        return false;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo, String s) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        return false;
    }



    @Override
    public boolean onMessageLinkClick(Context context, String s, Message message) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
    //获取欢迎语
    private void pushWelcomeMsg(){

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    String appId="";
                    String secret="";
                    Integer interval=null;
                    Boolean pushFlag=null;
                    SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(ConversationActivity.this);
                    appId=(String) sharedPreferencesUtil.getSharedPreference(ESB_APP_ID,"");
                    secret=(String) sharedPreferencesUtil.getSharedPreference(ESB_APP_SECRET,"");
                    Object  intervalObj=sharedPreferencesUtil.getSharedPreference(INTERVAL,"");
                    if(intervalObj!=null){
                       String  intervalStr=intervalObj.toString();
                        interval=Integer.parseInt(intervalStr);
                    }


                    Object  pushflagObj=sharedPreferencesUtil.getSharedPreference(PUSHFLAG,"");
                    if(pushflagObj!=null){
                        String  pushflagStr=pushflagObj.toString();
                        pushFlag=Boolean.parseBoolean(pushflagStr);
                    }



                    if(StringUtils.isEmpty(appId)||StringUtils.isEmpty(secret)){
                        ConversationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConversationActivity.this,"未配置ESB,请配置后使用!",Toast.LENGTH_SHORT).show();
                            }
                        });

                        return;
                    }
                    String connectUrl="http://dev-esb.huatu.com:8082/ronghub/officialAccount/pushWelcomeMsg.json";
                    //从UTC 1970年1月1日午夜开始经过的毫秒数,业务系统自己生成。生产环境，仅允许业务系统服务器与ESB服务器有一分钟的时间差
                    long timestamp = System.currentTimeMillis();
                    Random random = new Random();
                    int noStr = random.nextInt(99999999)+1;
                    // TODO: 2021/7/26 这个appId与 secret需要后台提供,不同的地方
                    Base64.Encoder encoder = Base64.getEncoder();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("appId=").append(appId)
                            .append("&noStr=").append(noStr)
                            .append("&secret=").append(secret)
                            .append("&timestamp=").append(timestamp);
                    //生成sign
                    String sign =encoder.encodeToString(stringBuilder.toString().getBytes()).toUpperCase();
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("officialAccountId",mTargetId);

                    jsonObject.put("userId",sharedPreferencesUtil.getSharedPreference(RONG_IM_USER_ID,""));
                    if(pushFlag!=null){
                        jsonObject.put("interval",interval);
                        jsonObject.put("pushFlag",pushFlag);
                    }
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
                    Request request = new Request.Builder()
                            .addHeader("appId",appId)
                            .addHeader( "noStr",noStr+"")
                            .addHeader( "timestamp",timestamp+"")
                            .addHeader( "sign",sign+"")
                            .url(connectUrl)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    if(response.code()!=200){
                        final String data = response.body().string();
                        ConversationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConversationActivity.this,data,Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

//                    String data = response.body().string();
//                    if(requestDataInterface!=null){
//                        requestDataInterface.success(data);
//                    }
//                    String string="";
                    String string="";
                }catch (final Exception e){

                    ConversationActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConversationActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    String string="";
//                    if(requestDataInterface!=null){
//                        requestDataInterface.success(e.getMessage());
//                    }
                }
            }
        }).start();
    }
    @Override
    public boolean onReceived(Message message, int i) {
        return false;
    }
}
