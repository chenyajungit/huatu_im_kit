package io.rong.imkit.io.rong.extend.textMessage;

import android.view.MotionEvent;
import android.view.View;

/**
 * 双击点击监听
 * author:lienlin
 * date:2018/9/26
 */
public class OnDoubleClickListener implements View.OnTouchListener{

    private int count = 0;//点击次数
    private long firstClick = 0;//第一次点击时间
    private long secondClick = 0;//第二次点击时间
    private final int totalTime = 500;//两次点击时间间隔，单位毫秒
    private int currentView;

    private DoubleClickCallback mCallback;
    public interface DoubleClickCallback {
        void onDoubleClick(View view);
    }
    public OnDoubleClickListener(DoubleClickCallback callback) {
        super();
        this.mCallback = callback;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (1 == count) {
                firstClick = System.currentTimeMillis();//记录第一次点击时间
                currentView=view.getId();
            }else if (2 == count) {
                if (currentView==view.getId()){
                    secondClick = System.currentTimeMillis();//记录第二次点击时间
                    if (secondClick - firstClick < totalTime&&currentView==view.getId()) {//判断二次点击时间间隔是否在设定的间隔时间之内
                        if (mCallback != null) {
                            mCallback.onDoubleClick(view);
                        }
                        count = 0;
                        firstClick = 0;
                        secondClick = 0;
                        return true;
                    } else {
                        firstClick = secondClick;
                        count = 1;
                    }
                    secondClick = 0;
                }else {
                    firstClick = System.currentTimeMillis();//记录第一次点击时间
                    currentView=view.getId();
                    count=1;
                }

            }
        }
        return false;
    }
}
