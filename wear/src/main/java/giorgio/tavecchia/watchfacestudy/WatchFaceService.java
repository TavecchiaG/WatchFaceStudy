package giorgio.tavecchia.watchfacestudy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import giorgio.tavecchia.watchfacestudy.R;

/**
 * Created by tavec on 05/09/2017.
 */

public class WatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine(){
        return new WatchFaceEngine();
    }

    private class WatchFaceEngine extends Engine{

        private Typeface WATCH_TEXT_TYPEFACE = Typeface.create(Typeface.SERIF, Typeface.NORMAL);

        private static final int MSG_UPDATE_TIME_ID=42;
        private long mUpdateRateMs=1000;

        private Time mDisplayTime;
        private Paint mBackgroundColorPaint;
        private Paint mTextColorPaint;

        private boolean mHasTimeZoneReceiverBeenRegistered = false;
        private boolean mIsInMinuteMode;
        private boolean mIsLowBitAmbient;

        private float mXOffset;
        private float mYOffset;

        private int mBackgroundColor = Color.parseColor("black");
        private int mTextColor = Color.parseColor("red");

        /*
            Used when user changes his timezone to update the time
         */
        final BroadcastReceiver mTimeZoneBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mDisplayTime.clear(intent.getStringExtra("time-zone"));
                mDisplayTime.setToNow();
            }
        };

        /*
            Update the watchface every second if not in ambient mode
         */
        private final Handler mTimeHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case MSG_UPDATE_TIME_ID:{
                        invalidate();
                        if(isVisible() && !isInAmbientMode()){
                            long currentTimeMillis = System.currentTimeMillis();
                            long delay = mUpdateRateMs - (currentTimeMillis % mUpdateRateMs);
                            mTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME_ID,delay);
                        }
                        break;
                    }
                }
            }
        };


        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFaceService.this)
            .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
            .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
            .setShowSystemUiTime(false)
            .build());

            mDisplayTime=new Time();
            initBackground();
            initDisplayText();
        }


        /*
            Utility
         */
        private void initBackground() {
            mBackgroundColorPaint = new Paint();
            mBackgroundColorPaint.setColor( mBackgroundColor );
        }

        private void initDisplayText() {
            mTextColorPaint = new Paint();
            mTextColorPaint.setColor( mTextColor );
            mTextColorPaint.setTypeface( WATCH_TEXT_TYPEFACE );
            mTextColorPaint.setAntiAlias( true );
            mTextColorPaint.setTextSize( getResources().getDimension( R.dimen.text_size ) );
        }
    }

}
