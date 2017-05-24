package in.srain.cube.views.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class MonkeyHeader extends FrameLayout implements PtrUIHandler {

    private LinearLayout layout_pullDown;

    private TextView textInfo;

    private ImageView imgRefersh;
    private UIRefreshStatusListener mListener;
    public static final int REFRESH_STATUS_PREPARE = 0;
    public static final int REFRESH_STATUS_BEGIN = 1;
    public static final int REFRESH_STATUS_COMPLETE = 2;


    public interface UIRefreshStatusListener {
        void onRefreshCallBack(int statues);
    }

    public void setUIRefreshStatusListener(UIRefreshStatusListener listener) {
        this.mListener = listener;
    }

    public MonkeyHeader(Context context) {
        super(context);
        init();
    }

    public MonkeyHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MonkeyHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 初始化View
        View header = LayoutInflater.from(getContext()).inflate(R.layout.monkey_header, this);

        layout_pullDown = (LinearLayout) header.findViewById(R.id.layout_pull_down);

        textInfo = (TextView) header.findViewById(R.id.text_info);

        imgRefersh = (ImageView) header.findViewById(R.id.img_refersh);

    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        layout_pullDown.setVisibility(View.VISIBLE);
        if (frame.isPullToRefresh()) {
            textInfo.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        } else {
            textInfo.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        }
        imgRefersh.setBackgroundDrawable(null);
        imgRefersh.setBackgroundResource(R.drawable.icon_down);

        if (mListener != null) {
            mListener.onRefreshCallBack(REFRESH_STATUS_PREPARE);
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

        layout_pullDown.setVisibility(View.GONE);

        if (mListener != null) {
            mListener.onRefreshCallBack(REFRESH_STATUS_BEGIN);
        }
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        if (mListener != null) {
            mListener.onRefreshCallBack(REFRESH_STATUS_COMPLETE);
        }
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
                                   PtrIndicator ptrIndicator) {

        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromBottomUnderTouch(frame);
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                crossRotateLineFromTopUnderTouch(frame);

            }
        }
    }

    private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
        textInfo.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            textInfo.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        } else {
            textInfo.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
        }
        imgRefersh.setBackgroundDrawable(null);
        imgRefersh.setBackgroundResource(R.drawable.icon_down);
    }

    private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
        if (!frame.isPullToRefresh()) {
            textInfo.setVisibility(VISIBLE);
            textInfo.setText(R.string.cube_ptr_release_to_refresh);
            imgRefersh.setBackgroundDrawable(null);
            imgRefersh.setBackgroundResource(R.drawable.icon_up);
        }
    }

}
