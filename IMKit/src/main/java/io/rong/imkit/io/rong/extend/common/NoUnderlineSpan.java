package io.rong.imkit.io.rong.extend.common;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * 无下划线的Span
 * Author: msdx (645079761@qq.com)
 * Time: 14-9-4 上午10:43
 */
public class NoUnderlineSpan extends ClickableSpan {


    private ClickListener clickListener;

    @Override
    public void onClick(@NonNull View widget) {
      if(clickListener!=null){
          clickListener.spanClick();
      }


    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(false);
    }

    public interface ClickListener{
        public void spanClick();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}