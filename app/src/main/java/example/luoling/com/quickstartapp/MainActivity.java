package example.luoling.com.quickstartapp;


import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewStub;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private SplashFragment splashFragment;
    private ViewStub viewStub;

    /**
     * 等待开机广告视图展示完成和初始化完成两个信号
     * _latch.await();
     * _latch.countDown();
     **/
    private CountDownLatch _latch = new CountDownLatch(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashFragment = new SplashFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame,splashFragment);
        transaction.commit();

        viewStub = findViewById(R.id.content_viewstub);
        //1.判断当窗体加载完毕的时候，立马在加载真正的布局进来。
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        viewStub.inflate();
                    }
                });
            }
        });

        //2.判断当窗体加载完毕的时候执行，延迟一段时间做动画。
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                //开启延迟加载，也可以不用延迟执行
                mHandler.postDelayed(new DelayRunnable(MainActivity.this,splashFragment),2000);
            }
        });

        //3.同时进行异步加载数据

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static class DelayRunnable implements  Runnable{

        private WeakReference<Context> contextRef;
        private WeakReference<SplashFragment> fragmentRef;

        public DelayRunnable(Context context,SplashFragment fragment){
            contextRef = new WeakReference<Context>(context);
            fragmentRef = new WeakReference<SplashFragment>(fragment);
        }

        @Override
        public void run() {
            if (contextRef != null){
                SplashFragment splashFragment = fragmentRef.get();
                if (splashFragment == null){
                    return;
                }
                FragmentActivity activity = (FragmentActivity) contextRef.get();
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.remove(splashFragment);
                transaction.commit();
            }
        }
    }

}
