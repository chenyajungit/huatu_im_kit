package io.rong.imkit.io.rong.extend.init;

import android.content.Context;
import android.net.Uri;

import io.rong.imkit.RongIM;
import io.rong.imkit.io.rong.extend.common.SharedPreferencesUtil;
import io.rong.imkit.io.rong.extend.gifMessage.ImGIFMessageItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

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






}
