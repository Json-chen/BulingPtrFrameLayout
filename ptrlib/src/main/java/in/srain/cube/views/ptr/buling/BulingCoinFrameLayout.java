package in.srain.cube.views.ptr.buling;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;


public class BulingCoinFrameLayout extends PtrFrameLayout implements BulingCoinHeader.OnUIRefreshStatusListener {

    private BlUIRefreshStatusListener mListener;

    public interface BlUIRefreshStatusListener {
        void onBlRefreshStatusChange(int statues);
    }

    public void setBlUIRefreshStatusListener(BlUIRefreshStatusListener listener) {
        this.mListener = listener;
    }

    private BulingCoinHeader buLingCoinHeader;

    public BulingCoinFrameLayout(Context context) {
        super(context);
        initView();
    }

    public BulingCoinFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public BulingCoinFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        buLingCoinHeader = new BulingCoinHeader(getContext());
        buLingCoinHeader.setUIRefreshStatusListener(this);
        setHeaderView(buLingCoinHeader);
        addPtrUIHandler(buLingCoinHeader);
    }

    public BulingCoinHeader getHeader() {
        return buLingCoinHeader;
    }

    @Override
    public void onUIRefreshStatusChange(int statues) {
        if (mListener != null) {
            mListener.onBlRefreshStatusChange(statues);
        }
    }
}
