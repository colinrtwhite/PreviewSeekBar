package com.github.rubensousa.previewseekbar;


import android.os.Build;
import android.view.View;

import com.google.android.exoplayer2.ui.TimeBar;

class PreviewDelegate implements TimeBar.OnScrubListener {

    private PreviewSeekBarLayout previewSeekBarLayout;
    private PreviewAnimator animator;
    private boolean showing;
    private boolean startTouch;
    private boolean setup;

    public PreviewDelegate(PreviewSeekBarLayout previewSeekBarLayout) {
        this.previewSeekBarLayout = previewSeekBarLayout;
    }

    public void setup() {
        previewSeekBarLayout.getPreviewFrameLayout().setVisibility(View.INVISIBLE);
        previewSeekBarLayout.getMorphView().setVisibility(View.INVISIBLE);
        previewSeekBarLayout.getFrameView().setVisibility(View.INVISIBLE);
        previewSeekBarLayout.getSeekBar().addListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.animator = new PreviewAnimatorLollipopImpl(previewSeekBarLayout);
        } else {
            this.animator = new PreviewAnimatorImpl(previewSeekBarLayout);
        }
        setup = true;
    }

    public boolean isShowing() {
        return showing;
    }

    public void show() {
        if (!showing && setup) {
            animator.show();
            showing = true;
        }
    }

    public void hide() {
        if (showing) {
            animator.hide();
            showing = false;
        }
    }

    @Override
    public void onScrubStart(final TimeBar timeBar) {
        startTouch = true;
    }

    @Override
    public void onScrubMove(final TimeBar timeBar, final long position) {
        if (setup) {
            animator.move();
            if (!showing && !startTouch) {
                animator.show();
                showing = true;
            }
        }
        startTouch = false;
    }

    @Override
    public void onScrubStop(final TimeBar timeBar, final long position, final boolean cancelled) {
        if (showing) {
            animator.hide();
        }
        showing = false;
        startTouch = false;
    }
}
