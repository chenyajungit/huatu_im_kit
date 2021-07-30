package io.rong.imkit.io.rong.extend.messagedetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.rong.imkit.R;


public class MessageDetailActivity extends AppCompatActivity {

    private MessageDetailViewHolder mMessageDetailViewHolder;



    @Override
    protected void onCreate(Bundle bundle) {
        CharSequence text = getIntent().getCharSequenceExtra("text");
        super.onCreate(bundle);

        setContentView(R.layout.activity_message_detail);
        mMessageDetailViewHolder = new MessageDetailViewHolder(this.getWindow().getDecorView().findViewById(android.R.id.content));
        mMessageDetailViewHolder.mTv.setText(text);
        mMessageDetailViewHolder.mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMessageDetailViewHolder.mLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
