/**
 * Copyright 2013 SyncWise International SARL
 * All rights reserved.
 * 
 * The text, images, audio and video files herein are the copyrighted works of SyncWise International SARL.
 * Reproduction in whole or in part, whether on paper, on the Internet, on CD-ROM, or any other medium,
 * including utilization in machines capable of reproduction or without the express permission of SyncWise
 * International SARL is prohibited.
 * */

package me.SyncWise.Android.Widgets.DSLV;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Checkable;

/**
 * Lightweight ViewGroup that wraps list items obtained from user's
 * ListAdapter. ItemView expects a single child that has a definite
 * height (i.e. the child's layout height is not MATCH_PARENT).
 * The width of
 * ItemView will always match the width of its child (that is,
 * the width MeasureSpec given to ItemView is passed directly
 * to the child, and the ItemView measured width is set to the
 * child's measured width). The height of ItemView can be anything;
 * the 
 * 
 *
 * The purpose of this class is to optimize slide
 * shuffle animations.
 */
public class DragSortItemView extends ViewGroup implements Checkable {

    private int mGravity = Gravity.TOP;

    public DragSortItemView(Context context) {
        super(context);

        // always init with standard ListView layout params
        setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        //setClipChildren(true);
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public int getGravity() {
        return mGravity;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final View child = getChildAt(0);

        if (child == null) {
            return;
        }

        if (mGravity == Gravity.TOP) {
            child.layout(0, 0, getMeasuredWidth(), child.getMeasuredHeight());
        } else {
            child.layout(0, getMeasuredHeight() - child.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        }
    }

    /**
     * 
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final View child = getChildAt(0);
        if (child == null) {
            setMeasuredDimension(0, width);
            return;
        }

        if (child.isLayoutRequested()) {
            // Always let child be as tall as it wants.
            measureChild(child, widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            ViewGroup.LayoutParams lp = getLayoutParams();

            if (lp.height > 0) {
                height = lp.height;
            } else {
                height = child.getMeasuredHeight();
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean isChecked() {
        View child = getChildAt(0);
        if (child instanceof Checkable)
            return ((Checkable) child).isChecked();
        else
            return false;
    }

    @Override
    public void setChecked(boolean checked) {
        View child = getChildAt(0);
        if (child instanceof Checkable)
            ((Checkable) child).setChecked(checked);
    }

    @Override
    public void toggle() {
        View child = getChildAt(0);
        if (child instanceof Checkable)
            ((Checkable) child).toggle();
    }
}
