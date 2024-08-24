package dev.undefinedteam.gensh1n.gui.animators;

import icyllis.modernui.util.FloatProperty;
import icyllis.modernui.view.View;

public class ViewAnimators {
    public static final FloatProperty<View> ALPHA_100 = new FloatProperty<>("alpha100") {
        @Override
        public void setValue(View object, float value) {
            object.setAlpha(value / 100f);
        }

        @Override
        public Float get(View object) {
            return object.getAlpha() * 100f;
        }
    };

    public static final FloatProperty<View> ALPHA_255 = new FloatProperty<>("alpha255") {
        @Override
        public void setValue(View object, float value) {
            object.setAlpha(value / 255f);
        }

        @Override
        public Float get(View object) {
            return object.getAlpha() * 255f;
        }
    };

    public static final FloatProperty<View> SCALE_XY = new FloatProperty<>("scale_xy") {
        @Override
        public void setValue(View object, float value) {
            object.setScaleX(value);
            object.setScaleY(value);
        }

        @Override
        public Float get(View object) {
            return Math.max(object.getScaleX(),object.getScaleY());
        }
    };
}
