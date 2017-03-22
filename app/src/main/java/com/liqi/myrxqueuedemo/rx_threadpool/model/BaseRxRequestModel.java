package com.liqi.myrxqueuedemo.rx_threadpool.model;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;

/**所有要处理的数据基类
 * Created by LiQi on 2017/3/20.
 */

public abstract class BaseRxRequestModel<T> implements Observable.OnSubscribe<T> {
    //是否需要打断线程标识
    private boolean isRunOff;
    //是否已经有线程处理了
    private boolean isRunDispose;
    //错误信息对象
    private Throwable mThrowable;

    /**
     * 设置错误信息
     * @param throwable 错误对象
     */
    protected void setThrowable(Throwable throwable) {
        mThrowable = throwable;
        isRunOff = true;
    }

    public boolean isRunDispose() {
        return isRunDispose;
    }

    public void setRunDispose(boolean runDispose) {
        isRunDispose = runDispose;
    }

    public boolean isRunOff() {
        return isRunOff;
    }

    public void setRunOff(boolean runOff) {
        this.isRunOff = runOff;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {

        T t = run();
        if (!isRunOff())
            subscriber.onNext(t);
        else {
            subscriber.onError(mThrowable);
            Log.e("RxRequestModel_Run", t + "线程切断");
        }
        subscriber.onCompleted();
    }

    /**
     * 执行在子线程的方法
     *
     * @return
     */
    protected abstract T run();
}
