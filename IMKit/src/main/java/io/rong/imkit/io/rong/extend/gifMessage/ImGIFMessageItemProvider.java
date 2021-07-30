package io.rong.imkit.io.rong.extend.gifMessage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import io.rong.imkit.R;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.integer;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongIM;
import io.rong.imkit.destruct.DestructManager;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.CircleProgressView;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.IRongCallback.IDownloadMediaMessageCallback;
import io.rong.imlib.RongIMClient.DestructCountDownTimerListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.GIFMessage;

@ProviderTag(
        messageContent = GIFMessage.class,
        showProgress = false,
        showReadState = true
)
public class ImGIFMessageItemProvider extends MessageProvider<GIFMessage> {
    private static final String TAG = "ImGIFMessageItemProvider";

    public ImGIFMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(layout.rc_item_gif_message, (ViewGroup)null);
        ViewHolder holder = new ViewHolder();
        holder.img = (AsyncImageView)view.findViewById(id.rc_img);
        holder.preProgress = (ProgressBar)view.findViewById(id.rc_pre_progress);
        holder.loadingProgress = (CircleProgressView)view.findViewById(id.rc_gif_progress);
        holder.startDownLoad = (ImageView)view.findViewById(id.rc_start_download);
        holder.downLoadFailed = (ImageView)view.findViewById(id.rc_download_failed);
        holder.length = (TextView)view.findViewById(id.rc_length);
        holder.fireView = (FrameLayout)view.findViewById(id.rc_destruct_click);
        holder.sendFire = (FrameLayout)view.findViewById(id.fl_send_fire);
        holder.receiverFire = (FrameLayout)view.findViewById(id.fl_receiver_fire);
        holder.receiverFireImg = (ImageView)view.findViewById(id.iv_receiver_fire);
        holder.receiverFireText = (TextView)view.findViewById(id.tv_receiver_fire);
        holder.clickHint = (TextView)view.findViewById(id.rc_destruct_click_hint);
        view.setTag(holder);
        return view;
    }

    public void onItemClick(View view, int position, GIFMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if (holder.startDownLoad.getVisibility() == View.VISIBLE) {
            holder.startDownLoad.setVisibility(  View.GONE);
            if (this.checkPermission(view.getContext())) {
                this.downLoad(message.getMessage(), holder);
            } else {
                holder.downLoadFailed.setVisibility(View.VISIBLE);
                holder.length.setVisibility(View.VISIBLE);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
                Toast.makeText(view.getContext(), string.rc_ac_file_download_request_permission, Toast.LENGTH_SHORT).show();
            }
        } else if (holder.downLoadFailed.getVisibility() == View.VISIBLE) {
            holder.downLoadFailed.setVisibility(  View.GONE);
            if (this.checkPermission(view.getContext())) {
                this.downLoad(message.getMessage(), holder);
            } else {
                holder.downLoadFailed.setVisibility(View.VISIBLE);
                holder.length.setVisibility(View.VISIBLE);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
                Toast.makeText(view.getContext(), string.rc_ac_file_download_request_permission, Toast.LENGTH_SHORT).show();
            }
        } else if (holder.preProgress.getVisibility() !=View.VISIBLE && holder.loadingProgress.getVisibility() != View.VISIBLE && content != null) {
            Context context=view.getContext();
            Intent intent = new Intent(context,GIFPreviewActivity.class);
//            intent.setPackage(view.getContext().getPackageName());
            intent.putExtra("message", message.getMessage());
            context.startActivity(intent);
        }

    }

    public void bindView(View v, int position, GIFMessage content, UIMessage message) {
        ViewHolder holder = (ViewHolder)v.getTag();
        holder.startDownLoad.setVisibility(  View.GONE);
        holder.downLoadFailed.setVisibility(  View.GONE);
        holder.preProgress.setVisibility(  View.GONE);
        holder.loadingProgress.setVisibility(  View.GONE);
        holder.length.setVisibility(  View.GONE);
        int[] paramsValue = this.getParamsValue(v.getContext(), content.getWidth(), content.getHeight());
        holder.img.setLayoutParam(paramsValue[0], paramsValue[1]);
        holder.img.setImageDrawable(v.getContext().getResources().getDrawable(drawable.def_gif_bg));
        int progress = message.getProgress();
        if (message.getMessageDirection() == MessageDirection.SEND) {
            SentStatus status = message.getSentStatus();
            if (progress > 0 && progress < 100) {
                holder.loadingProgress.setProgress(progress, true);
                holder.loadingProgress.setVisibility(View.VISIBLE);
                holder.preProgress.setVisibility(  View.GONE);
            } else if (status.equals(SentStatus.SENDING)) {
                holder.loadingProgress.setVisibility(  View.GONE);
            } else if (progress == -1) {
                holder.loadingProgress.setVisibility(  View.GONE);
                holder.preProgress.setVisibility(  View.GONE);
                holder.downLoadFailed.setVisibility(View.VISIBLE);
                holder.length.setVisibility(View.VISIBLE);
            } else {
                holder.loadingProgress.setVisibility(  View.GONE);
                holder.preProgress.setVisibility(  View.GONE);
            }
        } else if (message.getReceivedStatus().isDownload()) {
            if (progress > 0 && progress < 100) {
                holder.loadingProgress.setProgress(progress, true);
                holder.loadingProgress.setVisibility(View.VISIBLE);
                holder.preProgress.setVisibility(  View.GONE);
                holder.startDownLoad.setVisibility(  View.GONE);
            } else if (progress == 100) {
                holder.loadingProgress.setVisibility(  View.GONE);
                holder.preProgress.setVisibility(  View.GONE);
                holder.length.setVisibility(  View.GONE);
                holder.startDownLoad.setVisibility(  View.GONE);
            } else if (progress == -1) {
                holder.loadingProgress.setVisibility(  View.GONE);
                holder.preProgress.setVisibility(  View.GONE);
                holder.downLoadFailed.setVisibility(View.VISIBLE);
                holder.length.setVisibility(View.VISIBLE);
                holder.startDownLoad.setVisibility(  View.GONE);
            } else {
                holder.loadingProgress.setVisibility(  View.GONE);
                holder.preProgress.setVisibility(View.VISIBLE);
                holder.length.setVisibility(View.VISIBLE);
                holder.startDownLoad.setVisibility(  View.GONE);
            }
        } else {
            holder.loadingProgress.setVisibility(  View.GONE);
            holder.preProgress.setVisibility(  View.GONE);
            holder.length.setVisibility(  View.GONE);
            holder.startDownLoad.setVisibility(  View.GONE);
            if (progress == -1) {
                holder.downLoadFailed.setVisibility(View.VISIBLE);
                holder.length.setVisibility(View.VISIBLE);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
            }
        }

        if (content.isDestruct()) {
            Drawable drawable;
            if (message.getMessageDirection() == MessageDirection.SEND) {
                holder.sendFire.setVisibility(View.VISIBLE);
                holder.receiverFire.setVisibility(  View.GONE);
                holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
                drawable = v.getContext().getResources().getDrawable(R.drawable.rc_fire_sender_album);
                drawable.setBounds(0, 0, RongUtils.dip2px(40.0F), RongUtils.dip2px(34.0F));
                holder.clickHint.setCompoundDrawables((Drawable)null, drawable, (Drawable)null, (Drawable)null);
                holder.clickHint.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.sendFire.setVisibility(  View.GONE);
                holder.receiverFire.setVisibility(View.VISIBLE);
                holder.fireView.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
                drawable = v.getContext().getResources().getDrawable(R.drawable.rc_fire_receiver_album);
                drawable.setBounds(0, 0, RongUtils.dip2px(40.0F), RongUtils.dip2px(34.0F));
                holder.clickHint.setCompoundDrawables((Drawable)null, drawable, (Drawable)null, (Drawable)null);
                holder.clickHint.setTextColor(Color.parseColor("#F4B50B"));
                DestructManager.getInstance().addListener(message.getUId(), new DestructListener(holder, message), "GIFMessageItemProvider");
                if (message.getMessage().getReadTime() > 0L) {
                    holder.receiverFireText.setVisibility(View.VISIBLE);
                    holder.receiverFireImg.setVisibility(  View.GONE);
                    String unFinishTime;
                    if (TextUtils.isEmpty(message.getUnDestructTime())) {
                        unFinishTime = DestructManager.getInstance().getUnFinishTime(message.getUId());
                    } else {
                        unFinishTime = message.getUnDestructTime();
                    }

                    holder.receiverFireText.setText(unFinishTime);
                    DestructManager.getInstance().startDestruct(message.getMessage());
                } else {
                    holder.receiverFireText.setVisibility(  View.GONE);
                    holder.receiverFireImg.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.receiverFire.setVisibility(  View.GONE);
            holder.sendFire.setVisibility(  View.GONE);
        }

        if (content.getLocalPath() != null) {
            if (content.isDestruct()) {
                holder.fireView.setVisibility(View.VISIBLE);
                holder.img.setVisibility(  View.GONE);
            } else {
                holder.fireView.setVisibility(  View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                this.loadGif(v, content.getLocalUri(), holder);
            }
        } else {
            int size = v.getContext().getResources().getInteger(integer.rc_gifmsg_auto_download_size);
            if (content.getGifDataSize() <= (long)(size * 1024)) {
                if (this.checkPermission(v.getContext())) {
                    if (!message.getReceivedStatus().isDownload()) {
                        message.getReceivedStatus().setDownload();
                        this.downLoad(message.getMessage(), holder);
                    }
                } else if (progress != -1) {
                    holder.startDownLoad.setVisibility(View.VISIBLE);
                    holder.length.setVisibility(View.VISIBLE);
                    holder.length.setText(this.formatSize(content.getGifDataSize()));
                }
            } else if (progress > 0 && progress < 100) {
                holder.startDownLoad.setVisibility(  View.GONE);
                holder.length.setVisibility(View.VISIBLE);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
            } else if (progress != -1) {
                holder.startDownLoad.setVisibility(View.VISIBLE);
                holder.preProgress.setVisibility(  View.GONE);
                holder.loadingProgress.setVisibility(  View.GONE);
                holder.downLoadFailed.setVisibility(  View.GONE);
                holder.length.setVisibility(View.VISIBLE);
                holder.length.setText(this.formatSize(content.getGifDataSize()));
            }
        }

    }

    public Spannable getContentSummary(GIFMessage data) {
        return null;
    }

    public Spannable getContentSummary(Context context, GIFMessage data) {
        return data.isDestruct() ? new SpannableString(context.getString(string.rc_message_content_burn)) : new SpannableString(context.getString(string.rc_message_content_image));
    }

    private void downLoad(Message downloadMsg, ViewHolder holder) {
        holder.preProgress.setVisibility(View.VISIBLE);
        RongIM.getInstance().downloadMediaMessage(downloadMsg, (IDownloadMediaMessageCallback)null);
    }

    private void loadGif(View v, Uri uri, ViewHolder holder) {
        Glide.with(v.getContext()).asGif().load(uri.getPath()).into(holder.img);
    }

    private String formatSize(long length) {
        float size;
        if (length > 1048576L) {
            size = (float)Math.round((float)length / 1048576.0F * 100.0F) / 100.0F;
            return size + "M";
        } else if (length > 1024L) {
            size = (float)Math.round((float)length / 1024.0F * 100.0F) / 100.0F;
            return size + "KB";
        } else {
            return length + "B";
        }
    }

    private int[] getParamsValue(Context context, int width, int height) {
        int maxWidth = dip2px(context, 120.0F);
        int minValue = dip2px(context, 80.0F);
        float scale;
        int finalWidth;
        int finalHeight;
        if (width > maxWidth) {
            finalWidth = maxWidth;
            scale = (float)width / (float)maxWidth;
            finalHeight = Math.round((float)height / scale);
            if (finalHeight < minValue) {
                finalHeight = minValue;
            }
        } else if (width < minValue) {
            finalWidth = minValue;
            scale = (float)width / (float)minValue;
            finalHeight = Math.round((float)height * scale);
            if (finalHeight < minValue) {
                finalHeight = minValue;
            }
        } else {
            finalWidth = Math.round((float)height);
            finalHeight = Math.round((float)width);
        }

        int[] params = new int[]{finalWidth, finalHeight};
        return params;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    private boolean checkPermission(Context context) {
        String[] permission = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
        return PermissionCheckUtil.checkPermissions(context, permission);
    }

    private static class DestructListener implements DestructCountDownTimerListener {
        private WeakReference<ViewHolder> mHolder;
        private UIMessage mUIMessage;

        public DestructListener(ViewHolder pHolder, UIMessage pUIMessage) {
            this.mHolder = new WeakReference(pHolder);
            this.mUIMessage = pUIMessage;
        }

        public void onTick(long millisUntilFinished, String pMessageId) {
            if (this.mUIMessage.getUId().equals(pMessageId)) {
               ViewHolder viewHolder = (ViewHolder)this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(View.VISIBLE);
                    viewHolder.receiverFireImg.setVisibility(  View.GONE);
                    String unDestructTime = String.valueOf(Math.max(millisUntilFinished, 1L));
                    viewHolder.receiverFireText.setText(unDestructTime);
                    this.mUIMessage.setUnDestructTime(unDestructTime);
                }
            }

        }

        public void onStop(String messageId) {
            if (this.mUIMessage.getUId().equals(messageId)) {
                ViewHolder viewHolder = (ViewHolder)this.mHolder.get();
                if (viewHolder != null) {
                    viewHolder.receiverFireText.setVisibility(  View.GONE);
                    viewHolder.receiverFireImg.setVisibility(View.VISIBLE);
                    this.mUIMessage.setUnDestructTime((String)null);
                }
            }

        }
    }

    private static class ViewHolder {
        AsyncImageView img;
        ProgressBar preProgress;
        CircleProgressView loadingProgress;
        ImageView startDownLoad;
        ImageView downLoadFailed;
        TextView length;
        FrameLayout fireView;
        FrameLayout sendFire;
        FrameLayout receiverFire;
        ImageView receiverFireImg;
        TextView receiverFireText;
        TextView clickHint;

        private ViewHolder() {
        }
    }
}
