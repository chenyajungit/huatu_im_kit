package io.rong.imkit.io.rong.extend.gifMessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import io.rong.eventbus.EventBus;
import io.rong.imkit.R;
import io.rong.imkit.RongBaseNoActionbarActivity;
import io.rong.imkit.RongContext;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.model.Event;
import io.rong.imkit.utilities.KitStorageUtils;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.message.GIFMessage;

public class GIFPreviewActivity extends RongBaseNoActionbarActivity {
    TextView mCountDownView;
    Message currentMessage;
    private static final String TAG = "GIFPreviewActivity";

    public GIFPreviewActivity() {
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.rc_gif_preview);
        this.mCountDownView = (TextView)this.findViewById(R.id.rc_count_down);
        ImageView gifPreview = (ImageView)this.findViewById(R.id.rc_gif_preview);
        this.currentMessage = (Message)this.getIntent().getParcelableExtra("message");
        RongContext.getInstance().getEventBus().register(this);
        if (this.currentMessage != null && this.currentMessage.getContent() != null && this.currentMessage.getContent() instanceof GIFMessage) {
            gifPreview.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    GIFMessage gifMessage = (GIFMessage) GIFPreviewActivity.this.currentMessage.getContent();
                    if (!gifMessage.isDestruct()) {
                        GIFPreviewActivity.this.saveGif(gifMessage);
                    }

                    return false;
                }
            });
            gifPreview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Window window = GIFPreviewActivity.this.getWindow();
                    if (window != null) {
                        window.setFlags(2048, 2048);
                    }

                   GIFPreviewActivity.this.finish();
                }
            });
            GIFMessage gifMessage = (GIFMessage)this.currentMessage.getContent();
            if (gifMessage.isDestruct() && this.currentMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
                DestructManager.getInstance().addListener(this.currentMessage.getUId(), new DestructListener(this.mCountDownView, this.currentMessage.getUId()), "GIFPreviewActivity");
            }

            if (gifMessage.getLocalUri() != null) {
                Glide.with(this).asGif().listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (GIFPreviewActivity.this.currentMessage.getContent().isDestruct() && GIFPreviewActivity.this.currentMessage.getMessageDirection() == Message.MessageDirection.RECEIVE && GIFPreviewActivity.this.currentMessage.getReadTime() <= 0L && !TextUtils.isEmpty(GIFPreviewActivity.this.currentMessage.getUId())) {
                            DestructManager.getInstance().startDestruct(GIFPreviewActivity.this.currentMessage);
                            EventBus.getDefault().post(new Event.changeDestructionReadTimeEvent(GIFPreviewActivity.this.currentMessage));
                        }
                        return false;
                    }
                }).load(gifMessage.getLocalUri().getPath()).into(gifPreview);;


//                Glide.with(this).asGif().addListener(new RequestListener<GifDrawable>() {
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
//                        if (GIFPreviewActivity.this.currentMessage.getContent().isDestruct() &&GIFPreviewActivity.this.currentMessage.getMessageDirection() == Message.MessageDirection.RECEIVE && io.rong.imkit.activity.GIFPreviewActivity.this.currentMessage.getReadTime() <= 0L && !TextUtils.isEmpty(io.rong.imkit.activity.GIFPreviewActivity.this.currentMessage.getUId())) {
//                            DestructManager.getInstance().startDestruct(GIFPreviewActivity.this.currentMessage);
//                            EventBus.getDefault().post(new Event.changeDestructionReadTimeEvent(GIFPreviewActivity.this.currentMessage));
//                        }
//
//                        return false;
//                    }
//                }).load(gifMessage.getLocalUri().getPath()).into(gifPreview);
            }

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }

        } else {
            this.finish();
        }
    }

    private void saveGif(GIFMessage message) {
        String path = message.getLocalUri().getPath();
        final File file = new File(path);
        if (file.exists()) {
            String[] items = new String[]{this.getString(R.string.rc_save_picture)};
            OptionsPopupDialog.newInstance(this, items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
                public void onOptionsItemClicked(int which) {
                    if (which == 0) {
                        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                        if (!PermissionCheckUtil.requestPermissions(GIFPreviewActivity.this, permissions)) {
                            return;
                        }

                        if (file != null && file.exists()) {
                            String name = "rong_" + System.currentTimeMillis() + ".gif";
                            KitStorageUtils.saveMediaToPublicDir(GIFPreviewActivity.this, file, "image");
                            Toast.makeText(GIFPreviewActivity.this, GIFPreviewActivity.this.getString(R.string.rc_save_picture_at), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GIFPreviewActivity.this, GIFPreviewActivity.this.getString(R.string.rc_src_file_not_found), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }).show();
        }
    }

    public void onEventMainThread(Event.MessageDeleteEvent deleteEvent) {
        if (deleteEvent.getMessageIds() != null && this.currentMessage != null) {
            Iterator var2 = deleteEvent.getMessageIds().iterator();

            while(var2.hasNext()) {
                int messageId = (Integer)var2.next();
                if (messageId == this.currentMessage.getMessageId()) {
                    this.finish();
                    break;
                }
            }
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Event.RemoteMessageRecallEvent event) {
        if (this.currentMessage != null) {
            int messageId = this.currentMessage.getMessageId();
            if (messageId == event.getMessageId()) {
                (new AlertDialog.Builder(this, 5)).setMessage(this.getString(R.string.rc_recall_success)).setPositiveButton(this.getString(R.string.rc_dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GIFPreviewActivity.this.finish();
                    }
                }).setCancelable(false).show();
            }

        }
    }

    private static class DestructListener implements RongIMClient.DestructCountDownTimerListener {
        private WeakReference<TextView> mCountDownView;
        private String mMessageId;

        public DestructListener(TextView pCountDownView, String pMessageId) {
            this.mCountDownView = new WeakReference(pCountDownView);
            this.mMessageId = pMessageId;
        }

        public void onTick(long millisUntilFinished, String pMessageId) {
            if (this.mMessageId.equals(pMessageId)) {
                TextView countDownView = (TextView)this.mCountDownView.get();
                if (countDownView != null) {
                    countDownView.setVisibility(View.VISIBLE);
                    countDownView.setText(String.valueOf(Math.max(millisUntilFinished, 1L)));
                }
            }

        }

        public void onStop(String messageId) {
            if (this.mMessageId.equals(messageId)) {
                TextView countDownView = (TextView)this.mCountDownView.get();
                if (countDownView != null) {
                    countDownView.setVisibility(View.GONE);
                }
            }

        }
    }
}
