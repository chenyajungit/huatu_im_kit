package io.rong.imkit.io.rong.extend.init;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Base64;
import java.util.Random;

import io.rong.imkit.RongIM;
import io.rong.imkit.io.rong.extend.ConversationActivity;
import io.rong.imkit.io.rong.extend.common.SharedPreferencesUtil;
import io.rong.imkit.io.rong.extend.common.StringUtils;
import io.rong.imkit.io.rong.extend.gifMessage.ImGIFMessageItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static io.rong.imkit.io.rong.extend.ConversationActivity.CURRENT_CHAT_ID;
import static io.rong.imkit.io.rong.extend.ConversationActivity.ESB_APP_ID;
import static io.rong.imkit.io.rong.extend.ConversationActivity.ESB_APP_SECRET;
import static io.rong.imkit.io.rong.extend.ConversationActivity.ESB_PRODUCTION_ENVIRONMENT;
import static io.rong.imkit.io.rong.extend.ConversationActivity.INTERVAL;
import static io.rong.imkit.io.rong.extend.ConversationActivity.PUSHFLAG;
import static io.rong.imkit.io.rong.extend.ConversationActivity.RONG_IM_USER_ID;

public class RongHelper {

    public void rongInit(final Context context, final String appKey, final boolean enablePush, final InitImListener initImListener) {

        RongIM.initAsync(context, appKey, enablePush, new RongIM.AsyncInitListener() {
            @Override
            public void doBeforeInit() {
                if(initImListener!=null)
                initImListener.doBeforeInit();
            }

            @Override
            public void doAfterInit() {
                RongIM.registerMessageTemplate(new ImGIFMessageItemProvider());
                if(initImListener!=null)
                initImListener.doAfterInit();
            }
        });
    }

    /**
     *
     * @param context
     * @param esbAppId  esb的appId与secret,用来进行模板管理的,可联系南京esb相关开发老师进行配置
     * @param esbSecret esb的appId与secret,用来进行模板管理的,可联系南京esb相关开发老师进行配置
     * @param inteval   每两次推送欢迎语之间,间隔的分钟数。需要推送时，此字段必填
     * @param pushFlag  是否在查询的同时，直接推送欢迎语
     * @param isESBProductionEnvironment  是否是esb生产环境,是则为true,测试环境则为false
     */
    public void configEsb(Context context,String esbAppId,String esbSecret,Integer inteval,Boolean pushFlag,boolean isESBProductionEnvironment) {
        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(context);
        sharedPreferencesUtil.put(ESB_APP_ID,esbAppId);
        sharedPreferencesUtil.put(ESB_APP_SECRET,esbSecret);
        if(inteval!=null)
        sharedPreferencesUtil.put(INTERVAL,inteval+"");
        if(pushFlag!=null)
        sharedPreferencesUtil.put(PUSHFLAG,pushFlag+"");
        sharedPreferencesUtil.put(ESB_PRODUCTION_ENVIRONMENT,isESBProductionEnvironment+"");
    }

    public interface InitImListener{
        public void doBeforeInit();
        public void doAfterInit();

    }

    //融云IM登出
    public static void logout() {
           RongIM.getInstance().logout();
    }

    /**
     * 刷新用户缓存数据。
     * @param userId
     * @param userName
     * @param userAvatar
     */
    public static void refreshUserData(String userId,String userName,String userAvatar){

        RongIM.getInstance().refreshUserInfoCache(new UserInfo(userId, userName, Uri.parse(userAvatar)));
    }

    /**
     * 刷新群组缓存数据。
     * @param groupId
     * @param groupName
     * @param groupPortrait
     */
    public static void refreshGroupInfoCache(String groupId,String groupName,String groupPortrait){

        RongIM.getInstance().refreshGroupInfoCache(new Group(groupId, groupName, Uri.parse(groupPortrait)));
    }

        public void rongConnect(String token, final RongIMClient.ConnectCallback connectCallback, final Context context){

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
//                    String s1="";
                    connectCallback.onTokenIncorrect();
                }

                @Override
                public void onSuccess(String s) {

                    SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(context);
                    sharedPreferencesUtil.put(RONG_IM_USER_ID,s);
                    connectCallback.onSuccess(s);

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
//                    String s1="";
                    connectCallback.onError(errorCode);
                }

            });

    }


    //不使用默认的消息展示页面,就需实现该方法
    public static void setConversationData(final String mTargetId, final Context context){
        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(context);
        sharedPreferencesUtil.put(CURRENT_CHAT_ID,mTargetId);
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    try {
                        String appId="";
                        String secret="";
                        Integer interval=null;
                        Boolean pushFlag=null;
                        boolean isProduct=false;
                        SharedPreferencesUtil sharedPreferencesUtil=new SharedPreferencesUtil(context);
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

                        Object  isProductObj=sharedPreferencesUtil.getSharedPreference(ESB_PRODUCTION_ENVIRONMENT,"");
                        if(isProductObj!=null){
                            String  isProductStr=isProductObj.toString();
                            isProduct=Boolean.parseBoolean(isProductStr);
                        }
                        String tempStr="";
                        if(!isProduct){
                            tempStr="http://dev-esb.huatu.com:8082";
                        }else {
                            tempStr="https://esb.huatu.com";
                        }



                        if(StringUtils.isEmpty(appId)|| StringUtils.isEmpty(secret)){
                            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"未配置ESB,请配置后使用!",Toast.LENGTH_SHORT).show();
                                }
                            });

                            return;
                        }
                        String connectUrl=tempStr+"/ronghub/officialAccount/pushWelcomeMsg.json";
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
                        jsonObject.put("officialAccountCode",mTargetId);

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
                            ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,data,Toast.LENGTH_SHORT).show();
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
                        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
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





}
