package dev.undefinedteam.gensh1n.gui;

import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import dev.undefinedteam.gensh1n.gui.frags.MainGuiFragment;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.Utils;
import icyllis.arc3d.core.Color;
import icyllis.modernui.R;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.core.Context;
import icyllis.modernui.graphics.Canvas;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.material.MaterialCheckBox;
import icyllis.modernui.mc.ui.ThemeControl;
import icyllis.modernui.util.ColorStateList;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.MotionEvent;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;

import java.util.*;

import static icyllis.modernui.view.ViewGroup.LayoutParams.*;
import static icyllis.modernui.widget.RelativeLayout.*;

public class ModulesViewPager extends ViewPager {
    private static final int MODULES_PAGE = 0;
    private static final int SETTINGS_PAGE = 1;
    private static final int SETTINGS_VIEW_ID = 199 * 2;

    private final MainGuiFragment.ModulesFragment parent;

    private Category category;
    private static Module select;

    private String searchText = "", lastSearch = "0";

    public Category getCategory() {
        return category;
    }

    public void setSearch(String text) {
        if (text == null) text = "";
        this.searchText = text;
        if (!searchText.equals(lastSearch)) {
            if (this.getAdapter() != null) {
                ((MainPagerAdapter) this.getAdapter()).modulesPage(getContext());
            }
            lastSearch = searchText;
        }
    }

    public void setCategory(Category category) {
        if (category.hashCode() != this.category.hashCode()) {
            this.category = category;
            this.setAdapter(this.new MainPagerAdapter(category));
        }
    }

    private void updateTitle() {
        if (getCurrentItem() == 0) {
            parent.mTitleRight.setText(category.title + ">");
        } else if (getCurrentItem() == 1 && select != null) {

            parent.mTitleRight.setText("...>" + select.title + '>');
        }

        parent.mTitleRight.postDelayed(this::updateTitle, 100);
    }

    public ModulesViewPager(MainGuiFragment.ModulesFragment parent, Category category, Context context) {
        super(context);
        this.category = category;
        this.parent = parent;

        setAdapter(this.new MainPagerAdapter(category));
        setFocusableInTouchMode(true);
        setKeyboardNavigationCluster(true);
        setEdgeEffectColor(ThemeControl.THEME_COLOR);

        if (parent != null) {
            parent.mTitleRight.post(this::updateTitle);
        }

        {
            var indicator = new LinearPagerIndicator(getContext());
            indicator.setPager(this);
            indicator.setLineWidth(dp(4));
            indicator.setLineColor(ThemeControl.THEME_COLOR);
            var lp = new ViewPager.LayoutParams();
            lp.height = dp(30);
            lp.isDecor = true;
            lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            addView(indicator, lp);
        }

        var lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        setLayoutParams(lp);
    }

    private class MainPagerAdapter extends PagerAdapter {

        private final Category category;
        private LinearLayout mSettings;
        private LinearLayout mModules;

        public MainPagerAdapter(Category category) {
            this.category = category;
        }

        @Override
        public int getCount() {
            return 2;
        }

        private final Map<Setting<?>, View> settingViews = new HashMap<>();

        private ViewGroup loadSettings(Context ctx, Module current) {
            var base = new LinearLayout(ctx);
            base.setOrientation(LinearLayout.VERTICAL);
            base.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            for (SettingGroup group : current.settings.groups) {
                var content = new LinearLayout(ctx);
                content.setOrientation(LinearLayout.VERTICAL);
                content.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                var mModSettings = new LinearLayout(ctx);

                {
                    MaterialCheckBox title = new MaterialCheckBox(ctx);
                    {
                        title.setTextColor(Color.BLACK);
                        title.setText(group.name);
                        title.setTextSize(10);
                        title.setMinWidth(base.dp(250));
                        title.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1);
                            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                            title.setLayoutParams(params);
                            title.setBackground(new Drawable() {
                                @Override
                                public void draw(Canvas canvas) {
                                    Paint paint = Paint.obtain();
                                    paint.setColor(new java.awt.Color(245, 245, 245).getRGB());
                                    canvas.drawRoundRect(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom, 4, Gravity.TOP, paint);
                                    paint.recycle();
                                }
                            });
                        }

                        title.setChecked(true);
                        title.setOnCheckedChangeListener((__, checked) -> {
                            if (checked) {
                                content.post(() -> content.addView(mModSettings));
                            } else {
                                content.post(() -> content.removeView(mModSettings));
                            }
                        });

                    }

                    content.addView(title, 0);
                }

                {
                    mModSettings.setOrientation(LinearLayout.VERTICAL);
                    mModSettings.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                    mModSettings.setMinimumHeight(1);
                    settingViews.clear();
                    int id = 0;
                    for (Setting<?> setting : group.settings) {
                        View view = setting.createView(ctx, mModSettings);
                        if (view != null) {
                            view.setId(id + (114 * 514));
                            settingViews.put(setting, view);
                            mModSettings.addView(view);
                            view.post(() -> setting.checkVisible0(ctx, view));
                            id++;
                        }
                    }

                    content.addView(mModSettings, 1);
                }

                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    params.setMargins(base.dp(5), base.dp(5), base.dp(5), base.dp(5));
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    content.setLayoutParams(params);

                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setCornerRadius(base.dp(4));
                    drawable.setColor(new java.awt.Color(230, 230, 230, 220).getRGB());
                    drawable.setInnerRadius(base.dp(2));
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setStroke(base.dp(1), new java.awt.Color(244, 244, 244).getRGB());
                    content.setBackground(drawable);
                }

                base.addView(content);
            }

            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                params.setMargins(base.dp(1), base.dp(1), base.dp(1), base.dp(1));
                params.gravity = CENTER_HORIZONTAL;
                base.setLayoutParams(params);
            }
            return base;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            var context = container.getContext();
            var sv = new ScrollView(context);
            if (position == 1) {
                sv.addView(settingsPage(context), MATCH_PARENT, WRAP_CONTENT);
            } else {
                sv.addView(modulesPage(context), MATCH_PARENT, WRAP_CONTENT);

                var animator = ObjectAnimator.ofFloat(sv,
                    ViewAnimators.ALPHA_255, 0, 255);
                animator.setInterpolator(TimeInterpolator.DECELERATE);

                sv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                               int oldTop, int oldRight, int oldBottom) {
                        animator.start();
                        v.removeOnLayoutChangeListener(this);
                    }
                });
            }
            sv.setEdgeEffectColor(ThemeControl.THEME_COLOR);

            var params = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1);
            var dp6 = sv.dp(6);
            params.setMargins(dp6, dp6, dp6, dp6);
            container.addView(sv, params);

            return sv;
        }

        private static final ColorStateList MODULE_LAYOUT_COLOR = new ColorStateList(
            new int[][]{
                new int[]{R.attr.state_checked},
                StateSet.get(StateSet.VIEW_STATE_HOVERED),
                StateSet.WILD_CARD,
            },
            new int[]{
                new java.awt.Color(255, 255, 255, 255).getRGB(), // selected
                new java.awt.Color(0xFFE0E0E0).getRGB(), // hovered
                new java.awt.Color(0xFFB4B4B4).getRGB()} // other
        );

        private LinearLayout modulesPage(Context context) {
            if (mModules == null) {
                mModules = new LinearLayout(context);
                mModules.setOrientation(LinearLayout.VERTICAL);
                mModules.setHorizontalGravity(Gravity.LEFT);
            }

            if (mModules != null) mModules.removeAllViews();

            {
                List<Module> moduleArray = new ArrayList<>(Modules.get().getModulesByCategory(this.category));
                if (!searchText.isEmpty()) {
                    moduleArray.sort(Comparator.comparingInt(m -> -Utils.searchInWords(m.title, searchText)));
                }

                for (Module module : moduleArray) {
                    RelativeLayout base = new RelativeLayout(context);
                    base.setVerticalGravity(Gravity.CENTER_VERTICAL);

                    int dp5 = base.dp(5);
                    {
                        LinearLayout nameAndDesc = new LinearLayout(context);
                        nameAndDesc.setOrientation(LinearLayout.HORIZONTAL);
                        nameAndDesc.setVerticalGravity(Gravity.CENTER_VERTICAL);
                        TextView name = new TextView(context);
                        name.setTextColor(Color.BLACK);
                        name.setTextIsSelectable(false);
                        name.setText(module.title);
                        name.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                        name.setTextSize(18);
                        nameAndDesc.addView(name, WRAP_CONTENT, WRAP_CONTENT);

                        TextView desc = new TextView(context);
                        desc.setTextColor(Color.BLACK);
                        desc.setTextIsSelectable(false);
                        desc.setText(module.description);
                        desc.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
                        desc.setTextSize(10);
                        {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                            params.setMargins(base.dp(5), 0, 0, 0);
                            desc.setLayoutParams(params);
                        }
                        nameAndDesc.addView(desc);

                        {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, base.dp(30));
                            params.addRule(START_OF);
                            params.addRule(ALIGN_START);
                            params.addRule(ALIGN_PARENT_START);
                            params.addRule(ALIGN_PARENT_LEFT);
                            params.addRule(ALIGN_LEFT);
                            params.addRule(CENTER_VERTICAL);
                            params.setMargins(dp5, dp5, dp5, dp5);
                            nameAndDesc.setLayoutParams(params);
                        }
                        base.addView(nameAndDesc);
                    }

                    {
                        SwitchButton toggleButton = new SwitchButton(context);
                        toggleButton.setChecked(module.isActive());
                        toggleButton.setOnCheckedChangeListener((__, checked) -> {
                            if (checked && !module.isActive()) {
                                module.toggle();
                            } else if (!checked && module.isActive()) {
                                module.toggle();
                            }
                        });


                        base.setOnTouchListener((__, e) -> {
                            if (e.isButtonPressed(MotionEvent.BUTTON_SECONDARY)) {
                                select = module;
                                this.mSettings.removeViewAt(0);
                                settingsPage(context);
                                ModulesViewPager.this.setCurrentItem(SETTINGS_PAGE, true);
                                return true;
                            }
                            return false;
                        });


                        {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(base.dp(50), base.dp(20));
                            params.addRule(END_OF);
                            params.addRule(ALIGN_END);
                            params.addRule(ALIGN_PARENT_END);
                            params.addRule(ALIGN_PARENT_RIGHT);
                            params.addRule(ALIGN_RIGHT);
                            params.addRule(CENTER_VERTICAL);
                            params.setMargins(dp5, dp5, dp5, dp5);
                            toggleButton.setLayoutParams(params);
                        }
                        base.addView(toggleButton);
                    }

                    {
                        ShapeDrawable drawable = new ShapeDrawable();
                        drawable.setColor(Color.WHITE);
                        drawable.setCornerRadius(4f);
                        drawable.setShape(ShapeDrawable.RECTANGLE);
                        base.setBackground(drawable);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, base.dp(45));
                        params.setMargins(dp5, dp5, dp5, dp5);
                        base.setLayoutParams(params);
                    }
                    mModules.addView(base);
                }
            }
            if (mModules == null) {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setColor(new java.awt.Color(242, 242, 242, 242).getRGB());
                drawable.setCornerRadius(5f);
                drawable.setShape(ShapeDrawable.RECTANGLE);
                mModules.setBackground(drawable);
            }

            return mModules;
        }

        private LinearLayout settingsPage(Context context) {
            if (mSettings == null)
                this.mSettings = new LinearLayout(context);
            {
                Module currentModule = select;
                if (currentModule == null) {
                    var base = new LinearLayout(context);
                    base.setOrientation(LinearLayout.VERTICAL);
                    base.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                    base.setVerticalGravity(Gravity.CENTER_VERTICAL);
                    TextView view = new TextView(context);
                    {
                        view.setText("No Module Select");
                        view.setTextColor(Color.BLACK);
                        view.setTextSize(40);
                        view.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    }
                    base.setId(SETTINGS_VIEW_ID);
                    base.addView(view, MATCH_PARENT, MATCH_PARENT);
                    this.mSettings.addView(base, 0);
                } else {
                    this.mSettings.addView(loadSettings(context, currentModule), 0);
                }
            }
            return mSettings;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
