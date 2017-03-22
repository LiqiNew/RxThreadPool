package com.liqi.myrxqueuedemo.rx_threadpool;


import android.support.annotation.NonNull;

import com.liqi.myrxqueuedemo.rx_threadpool.interfa.OnRxMessageGetListener;
import com.liqi.myrxqueuedemo.rx_threadpool.interfa.OnRxMessageSetListener;
import com.liqi.myrxqueuedemo.rx_threadpool.model.RxRequestModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源共享对象
 * Created by LiQi on 2017/3/20.
 */

public class RxMessageSource implements OnRxMessageSetListener<RxRequestModel>, OnRxMessageGetListener<RxRequestModel> {
    private static RxMessageSource mRxMessageSource;
    //信息源集合
    private final List<RxRequestModel> MMESSAGELIST = new ArrayList<>();

    private RxMessageSource() {

    }

    public static RxMessageSource getRxMessageSource() {
        return mRxMessageSource = null == mRxMessageSource ? new RxMessageSource() : mRxMessageSource;
    }

    @Override
    public RxMessageSource add(@NonNull RxRequestModel parameter, Object sign) {
        parameter.setSign(sign);
        MMESSAGELIST.add(parameter);
        return mRxMessageSource;
    }

    @Override
    public List<RxRequestModel> getList() {
        return MMESSAGELIST;
    }

    @Override
    public RxRequestModel get(int index) {
        if (size() - 1 >= index) {
            return MMESSAGELIST.get(index);
        }
        return new RxRequestModel();
    }

    @Override
    public void delete(int indxe) {
        if (size() - 1 >= indxe)
            MMESSAGELIST.remove(indxe);
    }

    @Override
    public int size() {
        return MMESSAGELIST.size();
    }

    private void deleteAll() {
        if (!MMESSAGELIST.isEmpty()) {
            MMESSAGELIST.clear();
        }
    }


    @Override
    public RxMessageSource cancel(Object sign) {
        if (!MMESSAGELIST.isEmpty()) {
            for (int i = 0; i < MMESSAGELIST.size(); i++) {
                RxRequestModel requestModel = MMESSAGELIST.get(i);
                if (requestModel.isCancel(sign))
                    MMESSAGELIST.remove(i);
            }
        }
        return mRxMessageSource;
    }

    @Override
    public RxMessageSource cancelAll() {
        if (!MMESSAGELIST.isEmpty()) {
            for (int i = 0; i < size(); i++) {
                RxRequestModel rxRequestModel = MMESSAGELIST.get(i);
                rxRequestModel.setRunOff(true);
            }
        }
        deleteAll();
        return mRxMessageSource;
    }

    @Override
    public RxMessageSource add(@NonNull RxRequestModel parameter) {
        MMESSAGELIST.add(parameter);
        return mRxMessageSource;
    }
}
