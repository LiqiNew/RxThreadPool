package com.liqi.myrxqueuedemo.rx_threadpool.interchange;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.liqi.myrxqueuedemo.rx_threadpool.interfa.OnIsRequestListener;
import com.liqi.myrxqueuedemo.rx_threadpool.interfa.OnRxMessageGetListener;
import com.liqi.myrxqueuedemo.rx_threadpool.model.BaseRxRequestModel;
import com.liqi.myrxqueuedemo.rx_threadpool.model.RxRequestModel;
import com.liqi.myrxqueuedemo.rx_threadpool.thread.RxThreadDispatch;
import com.liqi.myrxqueuedemo.rx_threadpool.utils.RxThreadPoolUtisl;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 把数据源中的数据转换成rxJava线程的中转站对象
 * Created by LiQi on 2017/3/20.
 */

public class RxThreadInterchange implements RxThreadDispatch.OnRunDataDisListener {
    //Rx线程池并发数量处理值
    private static final int RUNSIZE = 4;
    private static RxThreadInterchange mRxThreadInterchange;
    private RxThreadDispatch mRxThreadDispatch;
    private OnRxMessageGetListener mOnRxMessageDisListener;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RxRequestModel baseRxRequestModel = (RxRequestModel) msg.obj;
            runRequest(baseRxRequestModel);
        }
    };

    private RxThreadInterchange() {

    }

    public static RxThreadInterchange getRxThreadInterchange() {
        return mRxThreadInterchange = null == mRxThreadInterchange ? new RxThreadInterchange() : mRxThreadInterchange;
    }

    /**
     * 转换运行
     *
     * @param onRxMessageGetListener 数据源对内暴露接口
     * @param <T>
     */
    public <T extends BaseRxRequestModel> void start(@NonNull OnRxMessageGetListener<T> onRxMessageGetListener) {
        if (null == mOnRxMessageDisListener) {
            selectOkState(onRxMessageGetListener);
        } else {
            if (mOnRxMessageDisListener != onRxMessageGetListener) {
                selectOkState(onRxMessageGetListener);
            }
        }
        if (null == mRxThreadDispatch || !mRxThreadDispatch.isAlive()) {

            runToNo();
            //开启中转线程
            mRxThreadDispatch = new RxThreadDispatch(RUNSIZE, mOnRxMessageDisListener.getList());
            mRxThreadDispatch.setOnRunDataDisListener(this);
            mRxThreadDispatch.setDaemon(true);
            mRxThreadDispatch.start();
        } else {
            mRxThreadDispatch.setRunSize(false);
            synchronized (this) {
                start();
            }
        }
    }

    /**
     * 唤醒中转线程起来做事了
     */
    private void start() {
        if (mRxThreadDispatch.isAlive() && !mRxThreadDispatch.isRunState())
            RxThreadPoolUtisl.threadNotify(RxThreadInterchange.this);
    }

    /**
     * 判断当前是已经获取数据源对象
     *
     * @param onRxMessageDisListener
     */
    private void selectOkState(@NonNull OnRxMessageGetListener onRxMessageDisListener) {
        mOnRxMessageDisListener = onRxMessageDisListener;
        runToNo();
    }

    /**
     * 把当前中转线程打死
     */
    private void runToNo() {
        if (null != mRxThreadDispatch) {
            mRxThreadDispatch.setRunTag(false);
            mRxThreadDispatch = null;
        }
    }

    /**
     * 中转线程创建rxJava线程
     * @param baseRxRequestModel 要处理的对象
     * @param <T>
     */
    private <T> void runRequest(RxRequestModel<T> baseRxRequestModel) {
        if (null != baseRxRequestModel) {
            final OnIsRequestListener<T> onIsRequestListener = baseRxRequestModel.getOnIsRequestListener();
            Observable.create(baseRxRequestModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<T>() {
                        @Override
                        public void onCompleted() {
                            messageListDalete();

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(T t) {
                            if (null != onIsRequestListener)
                                onIsRequestListener.getT(t);
                        }
                    });
        }
    }

    /**
     * 当数据源里面的数据长度达到一个指定的值的时候，干掉指定值长度的数据。然后继续浪(运行)
     */
    private void messageListDalete() {
        synchronized (this) {
            int size = mOnRxMessageDisListener.size();
            if (size >= 3) {
                for (int i = 0; i < size; i++) {
                    BaseRxRequestModel baseRxRequestModel = mOnRxMessageDisListener.get(i);
                    if (baseRxRequestModel.isRunDispose())
                        mOnRxMessageDisListener.delete(i);
                }
            }
            mRxThreadDispatch.addRunSize();
            start();
        }
    }

    @Override
    public void getRunData(BaseRxRequestModel runData) {
        Message message = mHandler.obtainMessage();
        message.obj = runData;
        mHandler.sendMessage(message);
    }

    @Override
    public void waitThread() {
       // Log.e("运行总长度", runSize + "<<<<<");
        RxThreadPoolUtisl.threadWait(this);
    }
}
