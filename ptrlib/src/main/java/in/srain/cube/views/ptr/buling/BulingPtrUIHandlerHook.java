package in.srain.cube.views.ptr.buling;

import in.srain.cube.views.ptr.PtrUIHandlerHook;

/**
 * 下拉刷新关闭header的hook，可以再这里处理关闭header之前的事情
 * Created by Robert on 2017/4/11.
 */

public class BulingPtrUIHandlerHook extends PtrUIHandlerHook {
    public interface OnPtrUIHandlerHookCallback{
        void onPtrUIHandlerHookStart();
    }

    private OnPtrUIHandlerHookCallback handlerHookCallback;

    public BulingPtrUIHandlerHook(OnPtrUIHandlerHookCallback handlerHookCallback) {
        this.handlerHookCallback = handlerHookCallback;
    }

    @Override
    public void run() {
        if (handlerHookCallback != null) {
            handlerHookCallback.onPtrUIHandlerHookStart();
        }
    }
}
