package com.huatu.imSdk;

import android.view.View;
import android.widget.Toast;

import io.rong.imkit.io.rong.extend.textMessage.OATextMessageItemProvider;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.message.TextMessage;

@ProviderTag(
        messageContent = TextMessage.class,
        showReadState = true
)
public  class OATextDemoMessageItemProvider extends OATextMessageItemProvider {
    private static final String TAG = "OATextMessageItemProvider";


    @Override
    public void textViewMessageListener(View view) {

    }

    @Override
    public void templateClickListener(int type, int hrefType, String hrefContent) {
        Toast.makeText(mContext,hrefContent,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clickPdfListener(String pdfUrl) {

    }

    @Override
    public void commonListener(String webUrl) {

    }

    @Override
    public void commonTextMessageLongClickListener(View view, Integer messageId) {

    }

    @Override
    public void commonTextMessageClickListener(View view, Integer messageId) {

    }

    @Override
    public void onItemClick(View view, int i, TextMessage textMessage, UIMessage uiMessage) {

    }
}
