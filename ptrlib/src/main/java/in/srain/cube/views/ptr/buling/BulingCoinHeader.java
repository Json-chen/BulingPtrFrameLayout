package in.srain.cube.views.ptr.buling;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.R;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class BulingCoinHeader extends FrameLayout implements PtrUIHandler, BulingPtrUIHandlerHook.OnPtrUIHandlerHookCallback, BulingView.OnUIHookCompleteCallback {
    private final static String TAG = "ptr";
    public static final boolean DEBUG = true;
    private OnUIRefreshStatusListener mListener;
    //下拉view
    private BulingView mBulingView;
    private BulingPtrUIHandlerHook handlerHook;
    public static final int REFRESH_STATUS_RESET = 0;
    public static final int REFRESH_STATUS_PREPARE = 1;
    public static final int REFRESH_STATUS_BEGIN = 2;
    public static final int REFRESH_STATUS_COMPLETE = 3;
    private PtrFrameLayout ptrFrame;

    public interface OnUIRefreshStatusListener {
        void onUIRefreshStatusChange(int statues);
    }

    public void setUIRefreshStatusListener(OnUIRefreshStatusListener listener) {
        this.mListener = listener;
    }

    public BulingCoinHeader(Context context) {
        this(context, null);
    }

    public BulingCoinHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BulingCoinHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 初始化View
        View header = LayoutInflater.from(getContext()).inflate(R.layout.buling_header, this);
        mBulingView = (BulingView) header.findViewById(R.id.buling);
        mBulingView.setUIHookCompleteCallback(this);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        printLogCat("onUIReset ...");
        mBulingView.onUIReset();
        if (mListener != null) {
            mListener.onUIRefreshStatusChange(REFRESH_STATUS_RESET);
        }
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        printLogCat("onUIRefreshPrepare ...");
        if (frame != null) {
            ptrFrame = frame;
            handlerHook = new BulingPtrUIHandlerHook(this);
            frame.setRefreshCompleteHook(handlerHook);
            //header加载时间，让它多转几下
            frame.setLoadingMinTime(1000);
            //header关闭时间
            frame.setDurationToCloseHeader(300);
            //当前值为169/220
            frame.setRatioOfHeaderHeightToRefresh(1.3f);
        }
        mBulingView.onUIRefreshPrepare();
        if (mListener != null) {
            mListener.onUIRefreshStatusChange(REFRESH_STATUS_PREPARE);
        }
    }


    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        printLogCat("onUIRefreshBegin ...");
        mBulingView.onUIRefreshBegin();
        if (mListener != null) {
            mListener.onUIRefreshStatusChange(REFRESH_STATUS_BEGIN);
        }
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        printLogCat("onUIRefreshComplete ...");
        mBulingView.onUIRefreshComplete();
        if (mListener != null) {
            mListener.onUIRefreshStatusChange(REFRESH_STATUS_COMPLETE);
        }
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
                                   PtrIndicator ptrIndicator) {
        printLogCat("onUIPositionChange ...");
        if (isUnderTouch) {
            mBulingView.onUIPositionChange(ptrIndicator);
        }
    }

    @Override
    public void onPtrUIHandlerHookStart() {
        printLogCat("onPtrUIHandlerHookStart ...");
        if (mBulingView != null) {
            mBulingView.onPtrUIHandlerHookStart();
        } else if (mListener != null && ptrFrame != null) {
            ptrFrame.refreshComplete();
            mListener.onUIRefreshStatusChange(REFRESH_STATUS_COMPLETE);
        }
    }

    @Override
    public void onUIHookComplete() {
        if (handlerHook != null) {
            handlerHook.resume();
        }
    }


    private void printLogCat(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

}
