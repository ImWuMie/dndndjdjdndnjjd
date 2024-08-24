package dev.undefinedteam.gensh1n.gui.overlay;

import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.app.Activity;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.view.WindowManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class DialogManager {
    public static DialogManager INSTANCE;

    static final Marker MARKER = MarkerManager.getMarker("Dialog");
    private final WindowManager mWindowManager;

    private ViewGroup mLayout;

    private ObjectAnimator showAnimator, hiddenAnimator;

    public DialogManager(Activity activity) {
        mWindowManager = activity.getWindowManager();
        INSTANCE = this;
    }

    public DialogManager setLayout(ViewGroup viewGroup) {
        this.mLayout = viewGroup;
        this.showAnimator = ObjectAnimator.ofFloat(viewGroup, ViewAnimators.ALPHA_255, 0, 255);
        this.hiddenAnimator = ObjectAnimator.ofFloat(viewGroup, ViewAnimators.ALPHA_255, 255, 0);

        this.showAnimator.setInterpolator(TimeInterpolator.DECELERATE);
        this.hiddenAnimator.setInterpolator(TimeInterpolator.DECELERATE);

        this.hiddenAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                DialogManager.this.mLayout = null;
            }
        });

        this.mLayout.setAlpha(0);
        return this;
    }

    public void show() {
        if (hiddenAnimator != null && this.hiddenAnimator.isRunning()) {
            this.hiddenAnimator.cancel();
        } else if (hiddenAnimator == null) return;

        if (this.mLayout != null) {
            var params = new WindowManager.LayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            params.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;

            mWindowManager.addView(this.mLayout, params);
            showAnimator.start();
        }
    }

    public void hidden() {
        if (showAnimator != null && showAnimator.isRunning()) {
            this.showAnimator.cancel();
        } else if (showAnimator == null) {
            this.mLayout = null;
            return;
        }

        hiddenAnimator.start();
    }

    public boolean isShowing() {
        return this.mLayout != null;
    }
}
