package dev.undefinedteam.gensh1n.gui.frags;

import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.gui.ModulesViewPager;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ParamsBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.gui.overlay.MusicSpectrum;
import dev.undefinedteam.gensh1n.gui.renders.ParticlesRender;
import dev.undefinedteam.gensh1n.gui.utils.BlockView;
import dev.undefinedteam.gensh1n.gui.utils.BlockViewGroup;
import dev.undefinedteam.gensh1n.music.GMusic;
import dev.undefinedteam.gensh1n.music.objs.SearchMusicObj;
import dev.undefinedteam.gensh1n.music.objs.music.SearchPageObj;
import dev.undefinedteam.gensh1n.system.hud.gui.HudEditorFragment;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.fragment.FragmentContainerView;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.graphics.*;
import icyllis.modernui.graphics.drawable.Drawable;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.material.MaterialRadioButton;
import icyllis.modernui.mc.fabric.PreferencesFragment;
import icyllis.modernui.mc.ui.ThemeControl;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.*;
import icyllis.modernui.widget.*;
import org.lwjgl.glfw.GLFW;


import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.TAB_BUTTON_COLOR;
import static dev.undefinedteam.gensh1n.gui.frags.MainInfoSaver.NULL;
import static icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainGuiFragment extends Fragment {
    private static MainGuiFragment INSTANCE;

    public static MainGuiFragment get() {
        if (INSTANCE == null) {
            INSTANCE = new MainGuiFragment();
        }
        return INSTANCE;
    }

    public static final int id_left_container = 0xfff1;
    public static final int id_view = 0xfff2;

    private static final ParticlesRender _particles = new ParticlesRender();
    private final BlockViewGroup group = new BlockViewGroup(id_left_container);

    private float mouseX, mouseY;

    public MusicSpectrum mSpectrumDrawable;
    public MusicLayout musicLayout;


    public static MainInfoSaver saver = new MainInfoSaver();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        mSpectrumDrawable = new MusicSpectrum(
            base.dp(15),
            base.dp(2),
            base.dp(640),
            new Color(156, 197, 255, 135)
        );

        /*base.layout().setOnGenericMotionListener((__, event) -> {
            mouseX = event.getX();
            mouseY = event.getY();
            if (event.isButtonPressed(MotionEvent.BUTTON_PRIMARY)) {
                _particles.mouseClicked(mouseX, mouseY, MotionEvent.BUTTON_PRIMARY);
            }
            return false;
        });
        base.fg(_particles.getDrawable(() -> this.mouseX, () -> this.mouseY));*/

        if (!Client.isOnMinecraftEnv()) {
            Image bg_image;
            try {
                InputStream icon = ModulesFragment.class.getResourceAsStream("/download.png");
                bg_image = Image.createTextureFromBitmap(BitmapFactory.decodeStream(icon));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            base.bg(new Drawable() {
                @Override
                public void draw(Canvas canvas) {
                    Paint paint = Paint.obtain();
                    paint.setRGB(255, 255, 255);
                    canvas.drawImage(bg_image, getBounds(), getBounds(), paint);
                    paint.recycle();
                }
            });
        }

        {
            this.group.clear();
            this.group.add(new BlockView("Modules", "All modules.", 0, "m"));
            this.group.add(new BlockView("Music", "Netease music.", 1, "y"));
            this.group.add(new BlockView("C", "class viewer.", 2, null));
            if (GChat.INSTANCE != null && GChat.INSTANCE.success)
                this.group.add(new BlockView(Client.SINGLE_SPECIAL_NAME, "chat' genshin.", 3, null));

            this.group.add(new BlockView("GUI", "gui settings.", 4, null));
            this.group.add(new BlockView("HUD", "HUD", 5, null));
        }

        var left_bar = LayoutBuilder.newLinerBuilder(requireContext());
        left_bar
            .vOrientation()
            .vGravity(Gravity.CENTER_VERTICAL);
        {

            var radioGroup = LayoutBuilder.wrapRadioGroup(this.group.getLayout(requireContext(), TAB_BUTTON_COLOR,saver));
            radioGroup.onCheck(id -> {
                var fm = getChildFragmentManager();
                saver.tab_last_checked = id;

                var ft = switch (id) {
                    case 0 -> fm.beginTransaction()
                        .replace(id_view, GMainGui.class, null, "modules");
                    case 1 -> fm.beginTransaction()
                        .replace(id_view, MusicMainFragment.class, null, "music");
                    case 2 -> fm.beginTransaction()
                        .replace(id_view, ClassViewFragment.class, null, "class_view");
                    case 3 -> fm.beginTransaction()
                        .replace(id_view, ChatFragment.class, null, "chat_genshin");
                    case 4 -> fm.beginTransaction()
                        .replace(id_view, PreferencesFragment.class, null, "gui_setting");
                    case 5 -> fm.beginTransaction()
                        .replace(id_view, HudEditorFragment.class, null, "hud");
                    default -> fm.beginTransaction();
                };
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setReorderingAllowed(true)
                    .commit();
            });
            int dp2 = base.dp(2);
            radioGroup
                .padding(dp2, dp2, dp2, dp2)
                .vGravity(Gravity.CENTER_VERTICAL)
                .params()
                .gravity(Gravity.CENTER_VERTICAL)
                .h_wrap_content().v_wrap_content();
            left_bar.add(radioGroup.build(), 0);
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setColor(new Color(80, 80, 80, 120).getRGB());
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setCornerRadius(base.dp(2));
                left_bar.bg(drawable);
            }
            left_bar.params().width(base.dp(60)).v_match_parent();
            left_bar.id(id_left_container);
            base.add(left_bar.build(), 0);
        }

        {
            var center = LayoutBuilder.newLinerBuilder(requireContext());
            center
                .vOrientation()
                .gravity(Gravity.CENTER)
                .hGravity(Gravity.CENTER_HORIZONTAL)
                .vGravity(Gravity.CENTER_VERTICAL)
                .bg(mSpectrumDrawable);

            var scrollView = new LinearLayout(requireContext());
            scrollView.setGravity(Gravity.CENTER);
            scrollView.setId(id_view - 10);
            {
                var params = ParamsBuilder.newLinerBuilder();
                params
                    .h_match_parent().v_match_parent()
                ;
                //params.rule(RelativeLayout.CENTER_IN_PARENT);
                scrollView.setLayoutParams(params.build());
            }

            var containerView = ViewBuilder.wrapLinear(new FragmentContainerView(requireContext()));

            containerView.id(id_view);
            containerView.params()
                .h_match_parent().v_match_parent()
            ;
            scrollView.addView(containerView.build(), 0);

            {
                if (saver.tab_last_checked != NULL) {
                    var fm = getChildFragmentManager();
                    var ft = switch (saver.tab_last_checked) {
                        case 0 -> fm.beginTransaction()
                            .replace(id_view, GMainGui.class, null, "modules");
                        case 1 -> fm.beginTransaction()
                            .replace(id_view, MusicMainFragment.class, null, "music");
                        case 2 -> fm.beginTransaction()
                            .replace(id_view, ClassViewFragment.class, null, "class_view");
                        case 3 -> fm.beginTransaction()
                            .replace(id_view, ChatFragment.class, null, "chat_genshin");
                        case 4 -> fm.beginTransaction()
                            .replace(id_view, PreferencesFragment.class, null, "gui_setting");
                        default -> fm.beginTransaction();
                    };
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .setReorderingAllowed(true)
                        .commit();
                }
            }

            //base.setRotation(30);
            center.add(scrollView, 0);
            var musicLayout = LayoutBuilder.wrapLinerLayout(new MusicLayout(requireContext(), mSpectrumDrawable));
            {
                var params_p = ParamsBuilder.newLinerBuilder();
                params_p
                    .margin(base.dp(10), base.dp(10), base.dp(10), base.dp(10))
                    .h_wrap_content()
                    .v_wrap_content();
                //params_p.rule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_PARENT_START);
                musicLayout.layout().setLayoutParams(params_p.build());
                this.musicLayout = (MusicLayout) musicLayout.layout();
                center.add(musicLayout.build(), 1);
            }

            center.params()
                .h_match_parent()
                .v_match_parent();

            base.add(center.build(), 1);
        }

        base.params().v_match_parent().h_match_parent();
        return base.build();
    }

    public static class ModulesFragment extends Fragment {

        private ModulesViewPager mModulePager;
        public TextView mTitleRight;
        private EditText mSearchBox;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
            LinearLayout base = new LinearLayout(requireContext());
            base.setOrientation(LinearLayout.HORIZONTAL);
            base.setBackground(new Drawable() {
                @Override
                public void draw(icyllis.modernui.graphics.Canvas canvas) {
                    Rect bounds = getBounds();
                    Paint paint = Paint.obtain();
                    paint.setRGBA(253, 253, 253, 255);
                    canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, 13f, paint);
                    paint.recycle();
                }
            });

            {
                LinearLayout leftSide = new LinearLayout(requireContext());
                leftSide.setOrientation(LinearLayout.VERTICAL);
                leftSide.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                {
                    leftSide.addView(logo(), 0);
                    leftSide.addView(categoryLayout(), 1);
                }
                base.addView(leftSide, 0);
            }
            {
                LinearLayout rightSide = new LinearLayout(requireContext());
                ThemeControl.makeDivider(rightSide);
                rightSide.setOrientation(LinearLayout.VERTICAL);
                rightSide.setHorizontalGravity(Gravity.START);
                rightSide.setVerticalGravity(Gravity.CENTER_VERTICAL);
                {
                    LinearLayout titleAndSearch = new LinearLayout(requireContext());
                    titleAndSearch.setOrientation(LinearLayout.HORIZONTAL);
                    {
                        titleAndSearch.addView(titleLayout(), 0);
                        titleAndSearch.addView(searchLayout(), 1);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(base.dp(500), base.dp(35));
                    titleAndSearch.setLayoutParams(params);
                    rightSide.addView(titleAndSearch, 0);
                }
                {
                    mModulePager = new ModulesViewPager(this, Categories.Combat, requireContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                    mModulePager.setLayoutParams(params);
                    rightSide.addView(mModulePager, 1);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
                params.setMargins(base.dp(5), base.dp(5), base.dp(5), base.dp(5));
                rightSide.setLayoutParams(params);
                base.addView(rightSide, 1);
            }

            {

                ShapeDrawable dDrawable = new ShapeDrawable();
                dDrawable.setShape(ShapeDrawable.HLINE);
                dDrawable.setColor(new Color(216, 216, 216, 120).getRGB());
                dDrawable.setSize(-1, base.dp(1));
                base.setDividerDrawable(dDrawable);
                base.setDividerPadding(base.dp(8));
                base.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(base.dp(640), base.dp(400));
                base.setLayoutParams(params);
            }
            return base;
        }

        private ImageView logo() {
            ImageButton view = new ImageButton(requireContext());

            try {
                InputStream icon = ModulesFragment.class.getResourceAsStream("/assets/gensh1n/textures/genshin.png");
                Image image = Image.createTextureFromBitmap(BitmapFactory.decodeStream(icon));
                view.setImage(image);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(view.dp(70), view.dp(70));
                params.setMargins(view.dp(20), view.dp(10), view.dp(20), view.dp(10));
                view.setLayoutParams(params);
            } catch (IOException ignored) {
            }

            return view;
        }

        private LinearLayout categoryLayout() {
            LinearLayout base = new LinearLayout(requireContext());
            base.setOrientation(LinearLayout.VERTICAL);
            base.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

            RadioGroup group1 = new RadioGroup(requireContext());
            for (Category category : Modules.loopCategories()) {
                RadioButton catButton = new MaterialRadioButton(requireContext());
                catButton.setTextSize(12);
                catButton.setText(category.title);
                catButton.setTextColor(Color.BLACK.getRGB());
                catButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                catButton.setId(category.hashCode());
                //catButton.setTextStyle(Paint.BOLD);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(base.dp(95), base.dp(25));
                params.setMargins(base.dp(2.5f), base.dp(5), base.dp(2.5f), base.dp(5));
                catButton.setLayoutParams(params);
                group1.addView(catButton);
            }

            group1.setOnCheckedChangeListener((__, id) -> {
                for (Category category : Modules.loopCategories()) {
                    if (id == category.hashCode()) {
                        this.mModulePager.setCategory(category);
                        this.mTitleRight.setText(category.title + ">");
                    }
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(base.dp(105), base.dp(220));
            params.setMargins(base.dp(3.5f), base.dp(10), base.dp(3.5f), base.dp(10));
            group1.setLayoutParams(params);
            return group1;
        }

        private LinearLayout titleLayout() {
            LinearLayout base = new LinearLayout(requireContext());
            mTitleRight = new TextView(requireContext());
            mTitleRight.setTextColor(Color.BLACK.getRGB());
            mTitleRight.setTextSize(18);
            mTitleRight.setText(Categories.Combat.title + ">");
            mTitleRight.setTextIsSelectable(true);
            mTitleRight.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            mTitleRight.setMaxHeight(base.dp(30));
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(base.dp(250), base.dp(35));
                params.setMargins(0, 0, base.dp(10), 0);
                base.setLayoutParams(params);
            }
            base.addView(mTitleRight);
            return base;
        }

        private LinearLayout searchLayout() {
            LinearLayout base = new LinearLayout(requireContext());
            base.setOrientation(LinearLayout.HORIZONTAL);
            base.setVerticalGravity(Gravity.CENTER_VERTICAL);
            {
                this.mSearchBox = new EditText(requireContext());
                ThemeControl.addBackground(this.mSearchBox);
                mSearchBox.setSingleLine();
                mSearchBox.setOnKeyListener((v, code, e) -> {
                    mModulePager.setSearch(mSearchBox.getText().toString());
                    return false;
                });
                mSearchBox.setTextColor(Color.BLACK.getRGB());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, base.dp(20));
                int dp5 = base.dp(5);
                params.setMargins(dp5, dp5, base.dp(10), dp5);
                mSearchBox.setLayoutParams(params);
                base.addView(mSearchBox);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(base.dp(240), base.dp(30));
            int dp2_5 = base.dp(2.5f);
            params.setMargins(0, dp2_5, 0, dp2_5);
            base.setLayoutParams(params);
            return base;
        }

        private LinearLayout listLayout(Category cat) {
            LinearLayout base = new LinearLayout(requireContext());
            return base;
        }

        private LinearLayout valuesLayout(Module module) {
            LinearLayout base = new LinearLayout(requireContext());
            return base;
        }
    }

    public static class MusicMainFragment extends Fragment {
        private EditText searchBox;
        private LinearLayout musicList;

        private String currentSelect;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
            LinearLayout base = new LinearLayout(requireContext());
            base.setOrientation(LinearLayout.HORIZONTAL);
            base.setBackground(new Drawable() {
                @Override
                public void draw(icyllis.modernui.graphics.Canvas canvas) {
                    Rect bounds = getBounds();
                    Paint paint = Paint.obtain();
                    paint.setRGBA(80, 80, 80, 100);
                    canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, 13f, paint);
                    paint.recycle();
                }
            });
            {
                EditText box = new EditText(requireContext());
                box.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                box.setText(saver.lastSearch);
                box.setOnKeyListener((__, keyCode, event) -> {
                    if (keyCode == GLFW.GLFW_KEY_ENTER) {
                        saver.lastSearch = box.getText().toString();
                        SearchPageObj obj = MusicLayout.api.searchSingle(box.getText().toString().split("\n"));
                        saver.lastSearchList = obj;
                        reloadML(obj);
                        return true;
                    }
                    return false;
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                params.setMargins(base.dp(5), base.dp(5), base.dp(5), base.dp(5));
                box.setLayoutParams(params);
                this.searchBox = box;
                base.addView(box, 0);
            }

            {
                var scroll = ViewBuilder.wrapLinear(new ScrollView(requireContext()));
                scroll.params().h_match_parent().v_match_parent();

                LinearLayout ml = new LinearLayout(requireContext());
                base.setOrientation(LinearLayout.VERTICAL);
                base.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                base.setBackground(new Drawable() {
                    @Override
                    public void draw(icyllis.modernui.graphics.Canvas canvas) {
                        Rect bounds = getBounds();
                        Paint paint = Paint.obtain();
                        paint.setColor(new Color(80, 80, 80, 100).getRGB());
                        canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, 5f, paint);
                        paint.recycle();
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                params.setMargins(base.dp(5), base.dp(5), base.dp(5), base.dp(5));
                ml.setLayoutParams(params);
                this.musicList = ml;
                scroll.view().addView(ml);
                base.addView(scroll.build(), 1);

                if (saver.lastSearchList != null) {
                    reloadML(saver.lastSearchList);
                }
            }

            return base;
        }

        public void reloadML(SearchPageObj obj) {
            if (obj == null || obj.resData == null) {
                Toast.makeText(requireContext(), "歌曲搜索出现错误", Toast.LENGTH_SHORT).show();
            } else {

                this.musicList.removeAllViews();
                RadioGroup mGroup = new RadioGroup(requireContext());

                for (SearchMusicObj resDatum : obj.resData) {
                    RadioButton button = new MaterialRadioButton(requireContext());
                    button.setText(resDatum.name + " - " + resDatum.author);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, musicList.dp(30));
                    params.setMargins(musicList.dp(2.5f), musicList.dp(1.5f), musicList.dp(2.5f), musicList.dp(1.5f));
                    button.setLayoutParams(params);
                    if (currentSelect != null && !currentSelect.isEmpty() && resDatum.id.equals(this.currentSelect)) {
                        ThemeControl.addBackground(button);
                        button.setClickable(false);
                        button.setChecked(true);
                    }
                    button.setOnClickListener((e) -> {
                        currentSelect = resDatum.id;
                        MainGuiFragment.get().musicLayout.mMusicPlayer.replaceTrack(resDatum.name + " - " + resDatum.author, MusicLayout.api.getPlayUrl(resDatum.id));
                        GMusic.setCurrent(resDatum);
                    });
                    mGroup.addView(button);
                }

                this.musicList.addView(mGroup);
            }
        }
    }
}
