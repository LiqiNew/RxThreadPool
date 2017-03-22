package com.liqi.myrxqueuedemo.rx_threadpool.model;

import android.util.Log;

import com.liqi.myrxqueuedemo.rx_threadpool.interfa.OnIsRequestListener;

/**测试要处理数据模型
 * Created by LiQi on 2017/3/20.
 */

public class RxRequestModel<T> extends BaseRxRequestModel<T> {
    private T mContent;
    //标识
    private Object mSign;
    //线程处理完毕后回调接口
    private OnIsRequestListener<T> mOnIsRequestListener;

    /**
     * 判断此标识是否是当前对象
     * @param sign  标识
     * @return
     */
    public boolean isCancel(Object sign) {
        if (mSign == sign) {
            setRunOff(true);
            return true;
        }
        return false;
    }

    public void setSign(Object sign) {
        mSign = sign;
    }

    private T getContent() {
        return mContent;
    }

    public void setContent(T content) {
        mContent = content;
    }

    public OnIsRequestListener<T> getOnIsRequestListener() {
        return mOnIsRequestListener;
    }

    public void setOnIsRequestListener(OnIsRequestListener<T> onIsRequestListener) {
        mOnIsRequestListener = onIsRequestListener;
    }

    @Override
    protected T run() {
        try {
            Log.e("执行了几次", mContent + "线程执行了");
            //模拟耗时操作(实际上应该在此处做打断耗时操作的方法)
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (T) (getContent() + "，RxJava线程已经处理完毕");
    }
}
