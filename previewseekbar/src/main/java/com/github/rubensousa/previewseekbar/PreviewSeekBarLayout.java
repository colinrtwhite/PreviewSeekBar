package com.github.rubensousa.previewseekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ui.TimeBar;

public class PreviewSeekBarLayout extends RelativeLayout implements TimeBar.OnScrubListener {

    private PreviewDelegate delegate;
    private PreviewSeekBar seekBar;
    private FrameLayout previewFrameLayout;
    private View morphView;
    private View frameView;
    private boolean firstLayout = true;
    private int tintColor;
    private PreviewLoader loader;

    public PreviewSeekBarLayout(Context context) {
        super(context);
        init(context);
    }

    public PreviewSeekBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreviewSeekBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        TypedValue outValue = new TypedValue();

        getContext().getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        tintColor = ContextCompat.getColor(context, outValue.resourceId);

        // Create morph view
        morphView = new View(getContext());
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);

        // Create frame view for the circular reveal
        frameView = new View(getContext());
        delegate = new PreviewDelegate(this);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getWidth() > 0 && getHeight() > 0 && firstLayout) {
            // Check if we have the proper views
            if (!checkChilds()) {
                throw new IllegalStateException("You need to add a PreviewSeekBar and a FrameLayout as direct childs");
            }

            // Set proper seek bar margins
            setupSeekbarMargins();

            // Setup colors for the morph view and frame view
            setTintColor(tintColor);

            delegate.setup();

            if (loader != null) {
                setup(loader);
            }

            // Setup morph view
            int size = getResources().getDimensionPixelSize(R.dimen.previewseekbar_indicator_width);
            addView(morphView, new ViewGroup.LayoutParams(size, size));

            // Add frame view to the preview layout
            FrameLayout.LayoutParams frameLayoutParams
                    = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayoutParams.gravity = Gravity.CENTER;
            previewFrameLayout.addView(frameView, frameLayoutParams);
            firstLayout = false;
        }
    }

    public void setup(PreviewLoader loader) {
        this.loader = loader;
        if (this.loader != null && seekBar != null) {
            seekBar.addListener(this);
        }
    }

    public boolean isShowingPreview() {
        return delegate.isShowing();
    }

    public void showPreview() {
        delegate.show();
    }

    public void hidePreview() {
        delegate.hide();
    }

    public FrameLayout getPreviewFrameLayout() {
        return previewFrameLayout;
    }

    public PreviewSeekBar getSeekBar() {
        return seekBar;
    }

    View getFrameView() {
        return frameView;
    }

    View getMorphView() {
        return morphView;
    }

    public void setTintColor(@ColorInt int color) {
        tintColor = color;
        Drawable drawable = DrawableCompat.wrap(morphView.getBackground());
        DrawableCompat.setTint(drawable, color);
        morphView.setBackground(drawable);
        frameView.setBackgroundColor(color);
    }

    public void setTintColorResource(@ColorRes int color) {
        setTintColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * Align seekbar thumb with the frame layout center
     */
    private void setupSeekbarMargins() {
        LayoutParams layoutParams = (LayoutParams) seekBar.getLayoutParams();

        int halfWidth = (previewFrameLayout.getWidth() - getResources().getDimensionPixelSize(R.dimen.scrubber_default_size)) / 2;
        layoutParams.rightMargin = halfWidth;
        layoutParams.leftMargin = halfWidth;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginStart(layoutParams.leftMargin);
            layoutParams.setMarginEnd(layoutParams.rightMargin);
        }

        seekBar.setLayoutParams(layoutParams);
        requestLayout();
        invalidate();
    }

    private boolean checkChilds() {
        int childs = getChildCount();

        if (childs < 2) {
            return false;
        }

        boolean hasSeekbar = false;
        boolean hasFrameLayout = false;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child instanceof PreviewSeekBar) {
                hasSeekbar = true;
                seekBar = (PreviewSeekBar) child;
            } else if (child instanceof FrameLayout) {
                previewFrameLayout = (FrameLayout) child;
                hasFrameLayout = true;
            }

            if (hasSeekbar && hasFrameLayout) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onScrubStart(final TimeBar timeBar) {
    }

    @Override
    public void onScrubMove(final TimeBar timeBar, final long position) {
        if (loader != null) {
            loader.loadPreview(position, seekBar.getDuration());
        }
        showPreview();
    }

    @Override
    public void onScrubStop(final TimeBar timeBar, final long position, final boolean cancelled) {
        hidePreview();
    }
}
