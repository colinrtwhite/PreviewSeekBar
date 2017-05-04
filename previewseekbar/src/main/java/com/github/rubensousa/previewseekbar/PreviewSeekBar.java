package com.github.rubensousa.previewseekbar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.TimeBar;

import java.util.ArrayList;
import java.util.List;

/**
 * A TimeBar that should be used inside PreviewSeekBarLayout
 */
public class PreviewSeekBar extends DefaultTimeBar {
    private final List<OnScrubListener> listeners = new ArrayList<>(0);
    private long position, duration;

    public PreviewSeekBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        super.setListener(new OnScrubListener() {
            @Override
            public void onScrubStart(final TimeBar timeBar) {
                for (OnScrubListener listener : listeners) {
                    listener.onScrubStart(timeBar);
                }
            }

            @Override
            public void onScrubMove(final TimeBar timeBar, final long position) {
                PreviewSeekBar.this.position = position;
                for (OnScrubListener listener : listeners) {
                    listener.onScrubMove(timeBar, position);
                }
            }

            @Override
            public void onScrubStop(final TimeBar timeBar, final long position, final boolean cancelled) {
                PreviewSeekBar.this.position = position;
                for (OnScrubListener listener : listeners) {
                    listener.onScrubStop(timeBar, position, cancelled);
                }
            }
        });
    }

    @Override
    public void setDuration(final long duration) {
        super.setDuration(this.duration = duration);
    }

    public long getDuration() {
        return duration;
    }

    public long getPosition() {
        return position;
    }

    @Override
    public void setListener(@NonNull final OnScrubListener listener) {
        clearListeners();
        addListener(listener);
    }

    public void addListener(@NonNull final OnScrubListener listener) {
        listeners.add(listener);
    }

    public void removeListener(@NonNull final OnScrubListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }
}
