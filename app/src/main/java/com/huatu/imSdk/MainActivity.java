package com.huatu.imSdk;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.io.rong.extend.ConversationListAdapterEx;
import io.rong.imkit.io.rong.extend.init.RongHelper;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MainActivity extends AppCompatActivity implements RongIM.ConversationListBehaviorListener{
    private Fragment mConversationListFragment;
    private android.support.v4.app.FragmentTransaction mFragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String token = "WpR/HBZUhSkmNVmHLhTao/MKpI50TuZkmXj0iNyetAOSbhaSbol2YVcjpGEVnOsLvoAkFXwJ+wUIIcNcCUQksg==";
        RongHelper rongHelper=new RongHelper();

        rongHelper.rongConnect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                String s1="";
            }

            @Override
            public void onSuccess(String s) {

                String s1="";

                RongIM.setConversationListBehaviorListener(MainActivity.this);
                initConversationList();

                //开启事务
                mFragmentTransaction = MainActivity.this.getSupportFragmentManager().beginTransaction();
                //设置为默认界面 MainHomeFragment
                mFragmentTransaction.add(R.id.conversationlist, mConversationListFragment).commitAllowingStateLoss(); //开启事务

                //连接成功
                //请注意，此处必须传入 activity 上下文。
//                RouteUtils.routeToConversationListActivity(MainActivity.this, "");

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                String s1="";
            }

        },MainActivity.this);


    }

    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment conversationListFragment = new ConversationListFragment();
            conversationListFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri = Uri.parse("rong://" + MainActivity.this.getApplicationInfo().packageName).buildUpon()
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                    .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//公共服务
                    .build();
            conversationListFragment.setUri(uri);
            mConversationListFragment = conversationListFragment;
            return conversationListFragment;
        } else {
            return mConversationListFragment;
        }
    }

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }
}