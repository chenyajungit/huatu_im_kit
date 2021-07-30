package com.huatu.imSdk;

import android.app.Application;

import io.rong.imkit.RongIM;
import io.rong.imkit.io.rong.extend.gifMessage.ImGIFMessageItemProvider;
import io.rong.imkit.io.rong.extend.init.RongHelper;

public class SdkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String appKey = "6tnym1br64wq7";
        RongHelper rongHelper=new RongHelper();
        rongHelper.configEsb(SdkApplication.this,"10001004","asdfasdfasd",2,true);
        rongHelper.rongInit(SdkApplication.this, appKey, true, new RongHelper.InitImListener() {
            @Override
            public void doBeforeInit() {

            }

            @Override
            public void doAfterInit() {
                //在此可以设置自定义模板消息,其中文字消息,需要实现OATextMessageItemProvider类
                RongIM.registerMessageTemplate(new OATextDemoMessageItemProvider());
            }
        });

    }
}
