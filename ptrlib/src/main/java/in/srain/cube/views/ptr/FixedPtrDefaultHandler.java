package in.srain.cube.views.ptr;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * 解决Listview和ptr的滑动冲突，支持PtrFrameLayout的子视图是ListView的情况
 *
 * Created by Villey on 2016/11/17.
 */

public abstract class FixedPtrDefaultHandler implements PtrHandler {


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header, -1);
    }

    /**
     * Default implement for check can perform pull to refresh
     *
     * @param frame
     * @param content
     * @param header
     * @return
     */
    public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header,
                                                      int internalListViewId) {
        if (!(content instanceof ViewGroup)) {
            return true;
        }

        ViewGroup viewGroup = (ViewGroup) content;
        if (viewGroup.getChildCount() == 0) {
            return true;
        }

        if (viewGroup instanceof AbsListView) {
            AbsListView listView = (AbsListView) viewGroup;
            if (canListViewScrollUp(listView)) {
                return false;
            }
        }

        View childListView = viewGroup.findViewById(internalListViewId);
        if(childListView != null && childListView instanceof AbsListView) {
            AbsListView listView = (AbsListView) childListView;
            if (canListViewScrollUp(listView)) {
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= 14) {
            return !content.canScrollVertically(-1);
        } else {
            if (viewGroup instanceof ScrollView || viewGroup instanceof AbsListView) {
                return viewGroup.getScrollY() == 0;
            }
        }

        View child = viewGroup.getChildAt(0);
        ViewGroup.LayoutParams glp = child.getLayoutParams();
        int top = child.getTop();
        if (glp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) glp;
            return top == mlp.topMargin + viewGroup.getPaddingTop();
        } else {
            return top == viewGroup.getPaddingTop();
        }
    }

    private static boolean canListViewScrollUp(AbsListView absListView){
        return absListView.getChildCount() > 0
                && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                .getTop() < absListView.getPaddingTop());
    }
}
