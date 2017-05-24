package in.srain.cube.views.ptr;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.MonkeyHeader.UIRefreshStatusListener;

public class MonkeyFrameLayout extends PtrFrameLayout implements UIRefreshStatusListener {

    private MonkeyUIRefreshStatusListener mListener;

    public interface MonkeyUIRefreshStatusListener {
        void onMonkeyRefreshCallBack(int statues);
    }

    public void setMonkeyUIRefreshStatusListener(MonkeyUIRefreshStatusListener listener) {
        this.mListener = listener;
    }

    private MonkeyHeader monkeyHeader;

    public MonkeyFrameLayout(Context context) {
        super(context);
        initView();
    }

    public MonkeyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public MonkeyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        monkeyHeader = new MonkeyHeader(getContext());
        monkeyHeader.setUIRefreshStatusListener(this);
        setHeaderView(monkeyHeader);
        addPtrUIHandler(monkeyHeader);
    }

    public MonkeyHeader getHeader() {
        return monkeyHeader;
    }

    @Override
    public void onRefreshCallBack(int statues) {
        if(mListener != null) {
            mListener.onMonkeyRefreshCallBack(statues);
        }
    }
}
