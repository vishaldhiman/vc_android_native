package com.vchoose.Vchoose.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.vchoose.Vchoose.R;


public class MyCustomProgressDialog extends ProgressDialog {
  private AnimationDrawable animation;

  public static ProgressDialog ctor(Context context) {
    MyCustomProgressDialog dialog = new MyCustomProgressDialog(context, android.R.style.Theme_Holo_Dialog);
    //dialog.setIndeterminate(true);
    //dialog.setCancelable(false);

    return dialog;
  }

  public MyCustomProgressDialog(Context context) {
    super(context);
  }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    public MyCustomProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.view_custom_progress_dialog);
    //this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

    ImageView la = (ImageView) findViewById(R.id.animation);
    la.setBackgroundResource(R.drawable.custom_progress_dialog_animation);
    animation = (AnimationDrawable) la.getBackground();
  }

  @Override
  public void show() {
    super.show();
    animation.start();
  }

  @Override
  public void dismiss() {
    super.dismiss();
    animation.stop();
  }
}
