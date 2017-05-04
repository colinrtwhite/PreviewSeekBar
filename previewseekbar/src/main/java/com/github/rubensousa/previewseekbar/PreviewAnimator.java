package com.github.rubensousa.previewseekbar;


import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

abstract class PreviewAnimator {
    static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    static final int MORPH_REVEAL_DURATION = 250;
    static final int MORPH_MOVE_DURATION = 200;
    static final int UNMORPH_MOVE_DURATION = 200;
    static final int UNMORPH_UNREVEAL_DURATION = 250;

    PreviewSeekBar previewSeekBar;
    PreviewSeekBarLayout previewSeekBarLayout;
    View previewView;
    View frameView;
    View morphView;

    public PreviewAnimator(PreviewSeekBarLayout previewSeekBarLayout) {
        this.previewSeekBarLayout = previewSeekBarLayout;
        this.previewSeekBar = previewSeekBarLayout.getSeekBar();
        this.previewView = previewSeekBarLayout.getPreviewFrameLayout();
        this.morphView = previewSeekBarLayout.getMorphView();
        this.frameView = previewSeekBarLayout.getFrameView();
    }

    public void move() {
        previewView.setX(getPreviewX());
        morphView.setX(getPreviewCenterX(morphView.getWidth()));
    }

    public abstract void show();

    public abstract void hide();

    float getWidthOffset(long progress) {
        return (float) progress / previewSeekBar.getDuration();
    }

    float getPreviewCenterX(int width) {
        return (previewSeekBarLayout.getWidth() - previewView.getWidth())
                * getWidthOffset(previewSeekBar.getPosition()) + previewView.getWidth() / 2f
                - width / 2f;
    }

    float getPreviewX() {
        return ((float) (previewSeekBarLayout.getWidth() - previewView.getWidth()))
                * getWidthOffset(previewSeekBar.getPosition());
    }

    float getHideY() {
        return previewSeekBar.getY() + 0;
    }

    float getShowY(){
       return (int) (previewView.getY() + previewView.getHeight() / 2f);
    }
}
