package com.example.imageswitch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List images = new ArrayList(  );
    ImageSwitcher mSwitcher;
    int position;
    float downX;

    ViewGroup group;
    ImageView[] tips;
    boolean isRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwitcher = findViewById(R.id.imgSwitch);
        group = findViewById(R.id.viewGroup);

        initData();
        mSwitcher.setFactory(imgFactor);
        mSwitcher.setOnTouchListener(touchListen);

        initPointer();

        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while(isRunning){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int cur_item=position;
                            cur_item=(cur_item+1)%images.size();
                            mSwitcher.setImageResource( (Integer) images.get(cur_item));
                            setTips(cur_item);
                            position=cur_item;
                        }
                    });
                }
            }
        }).start();


    }

    private void initData() {
        images.add(R.drawable.t1);
        images.add(R.drawable.t2);
        images.add(R.drawable.t3);
    }

    private ViewSwitcher.ViewFactory imgFactor=new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource((Integer) images.get(position));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
    };

    private View.OnTouchListener touchListen=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float lastX = motionEvent.getX();
                    if (lastX > downX){
                        if (position > 0){
                            mSwitcher.setInAnimation( AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_in_left));
                            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_out_right));
                            position --;
                            mSwitcher.setImageResource( (Integer) images.get( position % images.size() ) );
                            setTips(position);
                        }else {
                            Toast.makeText(getApplication(), "已经是第一张", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if(position < images.size() - 1){
                            mSwitcher.setInAnimation( AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_in_left));
                            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), android.R.anim.slide_out_right));
                            position ++ ;
                            mSwitcher.setImageResource( (Integer) images.get( position % images.size() ) );
                            setTips(position);
                        }else{
                            Toast.makeText(getApplication(), "到了最后一张", Toast.LENGTH_SHORT).show();
                        }
                    }
            }
            return true;
        }
    };


    private void initPointer() {
        tips = new ImageView[3];

        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(85, 85));
            imageView.setPadding(20, 0, 20, 0);
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            }
            group.addView(tips[i]);
        }
    }

    void setTips(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i==selectItems){
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
        }
    }


}