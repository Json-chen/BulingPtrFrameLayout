package in.srain.cube.views.ptr.buling;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import in.srain.cube.views.ptr.R;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

import static in.srain.cube.views.ptr.buling.BulingCoinHeader.DEBUG;

/**
 * 此控件UI周期与BulingCoinHeader同步
 * <p>->onUIRefreshPrepare()
 * <p>-> onUIPositionChange()
 * <p>下拉过程。curRatio < ratioOfHeaderHeightToRefresh 时，右手底部伸出，无标语的金币顶部伸出；反之右手捏住无标语的金币
 * <p>上拉过程。curRatio < ratioOfHeaderHeightToRefresh 时，右手往底部缩回，无标语的金币往缩回；反之右手捏住无标语的金币
 * <p>-> onUIRefreshBegin ()
 * <p>开始刷新。光圈开始转（直到请求返回）
 * <p>-> onUIHandlerHookStart()
 * <p>光圈逐渐透明同时显示带标语的金币，此过程耗时500ms后闪烁星星一次
 * <p>-> onUIRefreshComplete();
 * <p>右手捏着金币缩回底部
 * Created by Robert on 2017/4/6.
 */

public class BulingView extends RelativeLayout {
    private static final String TAG = "BulingCoinView";
    //金币layout(包括光环、金星等)
    private View mLayoutCoin;
    //金币
    private ImageView mCoinView;
    //光环
    private ImageView mHaloView;
    //金星
    private ImageView mBulingView;
    //刷新状态(如：下拉刷新，正在刷新等)
    private ImageView mRefreshView;
    //右手、手指
    private ImageView mRightHandView;
    private ImageView mRightFingerView;
    //金星闪烁动画
    private AnimationDrawable mBulingAnimationDrawable;
    //光环转动动画
    private RotateAnimation mHaloAnimation;
    //偏移变量
    private int leftTranslaX;
    private int leftTranslaY;
    private int rightTranslaX;
    private int rightTranslaY;
    //hook完成回调
    private OnUIHookCompleteCallback mOnHookCompleteCallback;
    //当前比例
    private float curRatio;
    //刷新点比例
    private float ratioOfHeaderHeightToRefresh;

    public BulingView(Context context) {
        this(context, null);
    }

    public BulingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BulingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
    }

    private void initData() {
        leftTranslaX = PtrLocalDisplay.designedDP2px(15);
        leftTranslaY = PtrLocalDisplay.designedDP2px(35);
        rightTranslaX = PtrLocalDisplay.designedDP2px(15);
        rightTranslaY = PtrLocalDisplay.designedDP2px(25);
    }

    public void setUIHookCompleteCallback(OnUIHookCompleteCallback onHookCompleteCallback) {
        this.mOnHookCompleteCallback = onHookCompleteCallback;
    }

    public interface OnUIHookCompleteCallback {
        void onUIHookComplete();
    }

    private void initView() {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.buling_view, this);
        mLayoutCoin = header.findViewById(R.id.ly_buling_coin);
        mCoinView = (ImageView) header.findViewById(R.id.iv_coin);
        mHaloView = (ImageView) header.findViewById(R.id.bg_halo);
        mBulingView = (ImageView) header.findViewById(R.id.bg_buling);
        mRefreshView = (ImageView) header.findViewById(R.id.iv_refresh);
        mRightHandView = (ImageView) header.findViewById(R.id.iv_right_hand);
        mRightFingerView = (ImageView) header.findViewById(R.id.iv_right_finger);
    }

    /**
     * 显示下拉刷新/松手刷新
     *
     * @param isPullToRefresh
     */
    public void setRefreshStatusImg(boolean isPullToRefresh) {
        if (isPullToRefresh) {
            mRefreshView.setImageResource(R.drawable.header_pull_down_refresh);
        } else {
            mRefreshView.setImageResource(R.drawable.header_release_refresh);
        }
    }

    /**
     * 在onUIRefreshComplete之前调用，实际是执行一个Runnable
     */
    public void onPtrUIHandlerHookStart() {
        //请求返回->光圈渐变消失同时显示带标语的金币（0.5m）->金星闪烁
        ValueAnimator shadeAnimator = ValueAnimator.ofFloat(0, 1f);
        shadeAnimator.setDuration(500);
        shadeAnimator.setInterpolator(new LinearInterpolator());
        shadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float shadeValues = (float) animation.getAnimatedValue();
                mHaloView.setAlpha((int) (255 * (1 - shadeValues)));
                mCoinView.setImageResource(R.drawable.header_coin_fouce);
            }
        });
        shadeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mHaloAnimation != null) {
                    mHaloAnimation.cancel();
                }
                mHaloView.setAlpha(255);
                mHaloView.clearAnimation();
                mHaloView.setVisibility(GONE);
                startBulingAnimation();
            }
        });
        shadeAnimator.start();
    }

    //松手或者刷新完成后view还原后调用
    public void onUIReset() {
        resetOtherView();
        resetRightView();
    }

    //存在手势动作并currentPos>0开始调用
    public void onUIRefreshPrepare() {
        resetOtherView();
        resetRightView();
    }

    //刷新时调用
    public void onUIRefreshBegin() {
        mRefreshView.setImageResource(R.drawable.header_refreshing);
        startHaloAnimation();
    }

    //刷新完成
    public void onUIRefreshComplete() {
        rightHandOut();
    }

    //位置存在变化时都会调用
    public void onUIPositionChange(PtrIndicator ptrIndicator) {
        curRatio = ptrIndicator.getCurrentPercent();
        printLogCat("[curRatio] =" + curRatio);
        int currentPos = ptrIndicator.getCurrentPosY();
        printLogCat("[currentPos] =" + currentPos);
        int lastPos = ptrIndicator.getLastPosY();
        //刷新点百分值。该比例在BulingCoinHeader.onUIRefreshPrepare中设置
        ratioOfHeaderHeightToRefresh = ptrIndicator.getRatioOfHeaderToHeightRefresh();
        //刷新点高度值。
        int offsetToRefresh = (int) (ratioOfHeaderHeightToRefresh * ptrIndicator.getHeaderHeight());
        if (currentPos < offsetToRefresh && curRatio < ratioOfHeaderHeightToRefresh) {
            coinIn();
            rightHandIn();
        } else if (lastPos < offsetToRefresh) {
            //猛下拉，则忽略金币下降过程直接到右手握着金币的状态
            fixViewByFLingView();
        }
        setRefreshStatusImg(currentPos < offsetToRefresh);
    }

    /**
     * 金币进
     */
    private void coinIn() {
        mLayoutCoin.setTranslationX(leftTranslaX * curRatio);
        mLayoutCoin.setTranslationY(leftTranslaY * curRatio);
    }

    /**
     * 右手进
     */
    private void rightHandIn() {
        mRightHandView.setTranslationX(-rightTranslaX * curRatio);
        mRightHandView.setTranslationY(-rightTranslaY * curRatio);

        mRightFingerView.setTranslationX(-rightTranslaX * curRatio);
        mRightFingerView.setTranslationY(-rightTranslaY * curRatio);
    }

    /**
     * 右手缩回
     */
    private void rightHandOut() {
        final ValueAnimator mRightFadeOutAnimator = ValueAnimator.ofFloat(0, ratioOfHeaderHeightToRefresh);
        mRightFadeOutAnimator.setDuration(200);
        mRightFadeOutAnimator.setInterpolator(new LinearInterpolator());
        mRightFadeOutAnimator.setRepeatCount(0);
        mRightFadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                mLayoutCoin.scrollTo((int) (-rightTranslaX * values), (int) (-rightTranslaY * values));
                mRightHandView.scrollTo((int) (-rightTranslaX * values), (int) (-rightTranslaY * values));
                mRightFingerView.scrollTo((int) (-rightTranslaX * values), (int) (-rightTranslaY * values));
            }
        });
        mRightFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    mRightFadeOutAnimator.cancel();
                } catch (AndroidRuntimeException exception) {
                    mRightFadeOutAnimator.removeAllListeners();
                }
            }
        });
        if (mRightFadeOutAnimator != null) {
            mRightFadeOutAnimator.start();
        }
    }


    /**
     * 金星闪烁
     */
    private void startBulingAnimation() {
        if (mBulingView == null) {
            return;
        }
        mBulingView.setVisibility(VISIBLE);
        mBulingAnimationDrawable = (AnimationDrawable) mBulingView.getDrawable();
        if (mBulingAnimationDrawable != null) {
            mBulingAnimationDrawable.start();
            //帧动画，共三帧，一帧150
            mBulingView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBulingAnimationDrawable.stop();
                    if (mOnHookCompleteCallback != null) {
                        mOnHookCompleteCallback.onUIHookComplete();
                    }
                }
            }, 450);
        }
    }

    /**
     * 光圈旋转
     */
    private void startHaloAnimation() {
        if (mHaloView == null) {
            return;
        }
        //创建旋转动画
        mHaloView.setVisibility(VISIBLE);
        mHaloAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mHaloAnimation.setDuration(300); // 设置动画时间
        mHaloAnimation.setRepeatCount(Animation.INFINITE);//动画的重复次数
        mHaloAnimation.setInterpolator(new LinearInterpolator()); // 设置插入器
        mHaloView.startAnimation(mHaloAnimation);
    }

    private void resetOtherView() {
        mRefreshView.setImageResource(R.drawable.header_pull_down_refresh);
        mCoinView.setImageResource(R.drawable.header_coin_normal);
        mBulingView.setVisibility(GONE);
        mHaloView.setVisibility(GONE);
    }

    /**
     * 每次平移后都需要还原位置，不然下次会从上次位置开始
     */
    private void resetRightView() {
        mLayoutCoin.scrollTo(0, 0);
        mRightHandView.scrollTo(0, 0);
        mRightFingerView.scrollTo(0, 0);
    }

    private void fixViewByFLingView() {
        mLayoutCoin.setTranslationX(leftTranslaX * ratioOfHeaderHeightToRefresh);
        mLayoutCoin.setTranslationY(leftTranslaY * ratioOfHeaderHeightToRefresh);

        mRightHandView.setTranslationX(-rightTranslaX * ratioOfHeaderHeightToRefresh);
        mRightHandView.setTranslationY(-rightTranslaY * ratioOfHeaderHeightToRefresh);

        mRightFingerView.setTranslationX(-rightTranslaX * ratioOfHeaderHeightToRefresh);
        mRightFingerView.setTranslationY(-rightTranslaY * ratioOfHeaderHeightToRefresh);
    }

    private void printLogCat(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
