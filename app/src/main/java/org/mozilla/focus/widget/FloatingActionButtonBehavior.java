/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * A Behavior implementation that will hide/show a FloatingActionButton based on whether an AppBarLayout
 * is visible or not.
 */
@SuppressWarnings("unused") // This behavior is set from xml (fragment_browser.xml)
public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> implements AppBarLayout.OnOffsetChangedListener {
    private static final int ANIMATION_DURATION = 300;

    private AppBarLayout layout;
    private FloatingActionButton button;
    private boolean animating;
    private boolean visible;

    public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {
        super();

        visible = true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (button != child) {
            button = child;
        }

        if (dependency instanceof AppBarLayout && layout != dependency) {
            layout = (AppBarLayout) dependency;
            layout.addOnOffsetChangedListener(this);

            return true;
        }

        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);

        layout.removeOnOffsetChangedListener(this);
        layout = null;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0 && !visible) {
            showButton();
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() && visible) {
            hideButton();
        }
    }

    private void showButton() {
        animate(button, false);
    }

    private void hideButton() {
        animate(button, true);
    }

    private void animate(View child, final boolean hide) {
        animating = true;

        child.animate()
                .scaleX(hide ? 0 : 1)
                .scaleY(hide ? 0 : 1)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animating = false;
                        visible = !hide;
                    }
                })
                .start();
    }
}
