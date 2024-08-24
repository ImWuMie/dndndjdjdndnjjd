package dev.undefinedteam.gensh1n.gui.frags;

import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.GCUser;
import dev.undefinedteam.gclient.GChat;
import dev.undefinedteam.gclient.assets.AssetsManager;
import dev.undefinedteam.gclient.data.UserData;
import dev.undefinedteam.gclient.packets.c2s.play.ChatMessageC2S;
import dev.undefinedteam.gclient.packets.s2c.play.ChatMessageS2C;
import dev.undefinedteam.gclient.packets.s2c.play.MessageS2C;
import dev.undefinedteam.gensh1n.gui.animators.ViewAnimators;
import dev.undefinedteam.gensh1n.gui.builders.LayoutBuilder;
import dev.undefinedteam.gensh1n.gui.builders.ViewBuilder;
import dev.undefinedteam.gensh1n.utils.StringUtils;
import icyllis.modernui.animation.Animator;
import icyllis.modernui.animation.AnimatorListener;
import icyllis.modernui.animation.ObjectAnimator;
import icyllis.modernui.animation.TimeInterpolator;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.Paint;
import icyllis.modernui.graphics.drawable.ImageDrawable;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.graphics.drawable.StateListDrawable;
import icyllis.modernui.mc.ui.ThemeControl;
import icyllis.modernui.text.*;
import icyllis.modernui.text.style.ForegroundColorSpan;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.util.StateSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import icyllis.modernui.widget.Button;
import org.apache.commons.codec.digest.DigestUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.IOException;

import static dev.undefinedteam.gensh1n.gui.frags.GMainGui.*;

public class ChatFragment extends Fragment {
    public static final int BACKGROUND_COLOR = new Color(80, 80, 80, 20).getRGB();
    public static final int EDIT_COLOR = new Color(80, 80, 80, 70).getRGB();
    public static final int CHAT_NAME_COLOR = new Color(255, 255, 255,200).getRGB();

    public static final int DISCONNECT_COLOR = Color.RED.getRGB();
    public static final int CONNECT_COLOR = GREEN;
    public static final int WARN_COLOR = Color.YELLOW.getRGB();

    public GCUser session = GCClient.INSTANCE.session();
    private boolean lastIsNull, lastIsLogged = false;

    private LinearLayout baseLayout;
    private LayoutBuilder.LinearLayoutBuilder<LinearLayout> baseBuilder;

    private TextView mStatus, mInfo;
    private ObjectAnimator mInfoAnimatorIn, mInfoAnimatorOut;

    private LinearLayout mChat;
    private ScrollView mScroll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        var main = LayoutBuilder.newLinerBuilder(requireContext());

        {
            main.vOrientation().gravity(Gravity.CENTER);
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(BACKGROUND_COLOR);
                drawable.setStroke(main.dp(1), EDGE_SIDES_COLOR);
                drawable.setCornerRadius(main.dp(10));
                main.bg(drawable);
            }
        }

        {
            var base = LayoutBuilder.newLinerBuilder(requireContext());
            this.baseBuilder = base;
            this.baseLayout = base.layout();
            base.vOrientation().gravity(Gravity.CENTER);

            if (session == null) {
                base.add(noConnect());
                lastIsNull = true;
            } else {
                if (session.logged()) {
                    overlay(base);
                    lastIsLogged = true;
                } else connect(base);
                lastIsNull = false;
            }
            base.params().v_wrap_content().h_match_parent();
            main.add(base.build());
        }

        {
            this.mStatus = new TextView(requireContext());
            var builder = ViewBuilder.wrapLinear(mStatus);
            mStatus.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            mStatus.setTextColor(FONT_COLOR);
            mStatus.setTextSize(10);
            updateColor(session == null ? DISCONNECT_COLOR : CONNECT_COLOR, session == null ? "Disconnect." : "Connect.");

            int dp2 = main.dp(2);
            builder.params().margin(dp2, dp2, dp2, dp2);
            builder.params().v_wrap_content().v_wrap_content();

            this.mInfo = new TextView(requireContext());
            mInfo.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            mInfo.setTextColor(FONT_COLOR);
            mInfo.setTextSize(14);

            {
                mInfoAnimatorIn = ObjectAnimator.ofFloat(mInfo, ViewAnimators.ALPHA_255, 0, 255);
                mInfoAnimatorOut = ObjectAnimator.ofFloat(mInfo, ViewAnimators.ALPHA_255, 255, 0);

                mInfoAnimatorIn.setInterpolator(TimeInterpolator.DECELERATE);
                mInfoAnimatorOut.setInterpolator(TimeInterpolator.DECELERATE);

                mInfoAnimatorOut.addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mInfo.setText("");
                    }
                });
            }

            var builder1 = ViewBuilder.wrapLinear(mInfo);
            builder1.params().margin(dp2, dp2, dp2, dp2);
            builder1.params().v_wrap_content().v_wrap_content();

            main.add(builder.build()).add(builder1.build());
        }

        main.params().v_match_parent().h_match_parent();

        baseLayout.post(this::updateStatus);
        return main.build();
    }

    private void updateStatus() {
        this.session = GCClient.INSTANCE.session();

        if (session != null) {
            if (session.logged() && !lastIsLogged) {
                baseLayout.removeAllViews();
                overlay(baseBuilder);
                lastIsLogged = true;
            } else if (!session.logged() && lastIsLogged) {
                baseLayout.removeAllViews();
                connect(baseBuilder);
                lastIsLogged = false;
            }
        }

        if (session == null && !lastIsNull) {
            baseLayout.removeAllViews();

            baseBuilder.vGravity(Gravity.CENTER_VERTICAL);
            baseBuilder.gravity(Gravity.CENTER);
            baseBuilder.add(noConnect());
            updateColor(session == null ? DISCONNECT_COLOR : CONNECT_COLOR, session == null ? "Disconnect." : "Connect.");
            lastIsNull = true;
        } else if (session != null && lastIsNull) {
            baseLayout.removeAllViews();
            if (session.logged()) {
                overlay(baseBuilder);
                lastIsLogged = true;
            } else connect(baseBuilder);
            updateColor(session == null ? DISCONNECT_COLOR : CONNECT_COLOR, session == null ? "Disconnect." : "Connect.");
            lastIsNull = false;
        }

        if (session != null && session.logged()) {
            updateColor(CONNECT_COLOR, "Ping: " + session.getPing());
        }

        baseLayout.postDelayed(this::updateStatus, 200);
    }

    private void overlay(LayoutBuilder.LinearLayoutBuilder<LinearLayout> main) {
        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation()
            .gravity(Gravity.CENTER).vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL);

        var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
        text.view().setTextSize(20);
        text.view().setTextStyle(Paint.BOLD);
        text.view().setText(GChat.SPECIAL_NAME + " - 已登陆");


        var button = ViewBuilder.wrapLinear(new Button(requireContext()));
        button.view().setText("退出登录");
        button.view().setTextStyle(Paint.BOLD);
        int dp5 = base.dp(5);
        button.params().margin(dp5, dp5, dp5, dp5);
        button.view().setOnClickListener((e) -> {
            GCClient.INSTANCE.session().disconnect("Quit.");
            GChat.get().isQuit = true;
            button.view().postDelayed(() -> GCClient.INSTANCE.connect(),500);
        });

        {
            int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2f - 4);
            StateListDrawable background = new StateListDrawable();
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(EDIT_COLOR);
            drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
            drawable.setCornerRadius(base.dp(5));
            background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
            //background.addState(new int[]{R.attr.state_checked},drawable);
            background.setEnterFadeDuration(250);
            background.setExitFadeDuration(250);
            button.bg(background);
        }

        base.add(text.build());
        base.add(button.build());
        main.add(base.build());
    }

    private void connect(LayoutBuilder.LinearLayoutBuilder<LinearLayout> base) {
        mChat = null;

        var layout = LayoutBuilder.newLinerBuilder(requireContext());
        layout.vOrientation()
            .gravity(Gravity.CENTER).vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL);

        GCClient.INSTANCE.setListener((session, packet) -> {
            if (packet instanceof MessageS2C p) {
                info(WARN_COLOR, p.message);
                mStatus.postDelayed(this::clearInfo, 3000);
            }
        });

        var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
        text.view().setTextSize(20);
        text.view().setTextStyle(Paint.BOLD);
        text.view().setText(GChat.SPECIAL_NAME + " - Login");
        text.view().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        int dp5 = base.dp(5);
        int dp3 = base.dp(5);
        text.params().margin(dp5, dp5, dp5, dp5);

        layout.add(text.build());

        {
            var username = ViewBuilder.wrapLinear(new EditText(requireContext()));
            var token = ViewBuilder.wrapLinear(new EditText(requireContext()));

            String u_name = GChat.INSTANCE.username;
            String u_token = GChat.INSTANCE.token;
            if (u_name != null) username.view().setText(u_name);
            if (u_token != null) token.view().setText(u_token);

            username.view().setHint("Name");
            token.view().setHint("Token");

            username.view().setTextColor(FONT_COLOR);
            token.view().setTextColor(FONT_COLOR);
            username.view().setHintTextColor(HINT_FONT_COLOR);
            token.view().setHintTextColor(HINT_FONT_COLOR);

            username.view().setSingleLine();
            token.view().setSingleLine();

            username.view().setTextSize(12);
            token.view().setTextSize(12);

            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(EDIT_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setPadding(dp5, dp5, dp5, dp5);
                drawable.setCornerRadius(base.dp(5));
                username.bg(drawable);

                username.params().margin(dp3, dp3, dp3, dp3)
                    .height(base.dp(30)).width(base.dp(250));
            }
            {
                ShapeDrawable drawable = new ShapeDrawable();
                drawable.setShape(ShapeDrawable.RECTANGLE);
                drawable.setColor(EDIT_COLOR);
                drawable.setStroke(base.dp(1), EDGE_SIDES_COLOR);
                drawable.setPadding(dp5, dp5, dp5, dp5);
                drawable.setCornerRadius(base.dp(5));
                token.bg(drawable);

                token.params().margin(dp3, dp3, dp3, dp3)
                    .height(base.dp(30)).width(base.dp(250));
            }

            layout.add(username.build());
            layout.add(token.build());

            var hwid = ViewBuilder.wrapLinear(new TextView(requireContext()));
            {
                class hwid {
                    public String hwid() {
                        return DigestUtils.sha256Hex(
                            System.getenv("os")
                                + System.getProperty("os.name")
                                + System.getProperty("os.arch")
                                + System.getProperty("user.name")
                                + System.getenv("PROCESSOR_LEVEL")
                                + System.getenv("PROCESSOR_REVISION")
                                + System.getenv("PROCESSOR_IDENTIFIER")
                                + System.getenv("PROCESSOR_ARCHITEW6432")
                        );
                    }
                }

                hwid.view().setText("HWID: " + new hwid().hwid());
                hwid.view().setTextIsSelectable(true);
                hwid.view().setTextSize(12);
            }

            layout.add(hwid.build());


            {
                var button = ViewBuilder.wrapLinear(new Button(requireContext()));
                button.view().setText("Login");
                button.view().setTextStyle(Paint.BOLD);
                button.params().margin(dp5, dp5, dp5, dp5);
                button.view().setOnClickListener((e) -> {
                    GChat.INSTANCE.username = String.valueOf(username.view().getText());
                    GChat.INSTANCE.token = String.valueOf(token.view().getText());
                    try {
                        GChat.INSTANCE.saveUser();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    session.loginInChat(GChat.INSTANCE.username, GChat.INSTANCE.token);
                });

                {
                    int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2f - 4);
                    StateListDrawable background = new StateListDrawable();
                    ShapeDrawable drawable = new ShapeDrawable();
                    drawable.setShape(ShapeDrawable.RECTANGLE);
                    drawable.setColor(EDIT_COLOR);
                    drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
                    drawable.setCornerRadius(base.dp(5));
                    background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
                    //background.addState(new int[]{R.attr.state_checked},drawable);
                    background.setEnterFadeDuration(250);
                    background.setExitFadeDuration(250);
                    button.bg(background);
                }

                layout.add(button.build());
            }
        }

        base.vGravity(Gravity.CENTER_VERTICAL);
        base.gravity(Gravity.CENTER);
        base.add(layout.build());
    }

    private LinearLayout noConnect() {
        mChat = null;

        var base = LayoutBuilder.newLinerBuilder(requireContext());
        base.vOrientation()
            .gravity(Gravity.CENTER).vGravity(Gravity.CENTER_VERTICAL).hGravity(Gravity.CENTER_HORIZONTAL);

        var text = ViewBuilder.wrapLinear(new TextView(requireContext()));
        text.view().setTextSize(20);
        text.view().setTextStyle(Paint.BOLD);
        text.view().setText(GChat.SPECIAL_NAME + " - 无法连接到服务器");

        var button = ViewBuilder.wrapLinear(new Button(requireContext()));
        button.view().setText("Reconnect");
        button.view().setTextStyle(Paint.BOLD);
        int dp5 = base.dp(5);
        button.params().margin(dp5, dp5, dp5, dp5);
        button.view().setOnClickListener((e) -> {
            GCClient.INSTANCE.connect();

            button.view().setEnabled(false);
            button.view().setAlpha(80 / 255f);

            button.view().postDelayed(() -> {
                button.view().setEnabled(true);
                button.view().setAlpha(1f);
            }, 5000);
        });

        {
            int tab_margin_dp2 = base.dp(TAB_MARGIN_PTS / 2f - 4);
            StateListDrawable background = new StateListDrawable();
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(ShapeDrawable.RECTANGLE);
            drawable.setColor(EDIT_COLOR);
            drawable.setPadding(tab_margin_dp2, tab_margin_dp2, tab_margin_dp2, tab_margin_dp2);
            drawable.setCornerRadius(base.dp(5));
            background.addState(StateSet.get(StateSet.VIEW_STATE_HOVERED), drawable);
            //background.addState(new int[]{R.attr.state_checked},drawable);
            background.setEnterFadeDuration(250);
            background.setExitFadeDuration(250);
            button.bg(background);
        }

        base.add(text.build());
        base.add(button.build());
        return base.build();
    }

    private void updateColor(int color, String src) {
        Spannable spannable = new SpannableString("● " + src);

        spannable.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        var precomputed = PrecomputedText.create(spannable, mStatus.getTextMetricsParams());
        mStatus.post(() -> mStatus.setText(precomputed, TextView.BufferType.SPANNABLE));
    }

    private void info(int color, String src) {
        Spannable spannable = new SpannableString("● " + src);

        spannable.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        var precomputed = PrecomputedText.create(spannable, mInfo.getTextMetricsParams());
        mInfo.post(() -> {
            mInfo.setText(precomputed, TextView.BufferType.SPANNABLE);
            if (mInfoAnimatorOut.isRunning()) mInfoAnimatorOut.cancel();

            mInfoAnimatorIn.start();
        });
    }

    private void clearInfo() {
        if (mInfoAnimatorIn.isRunning()) mInfoAnimatorIn.cancel();

        mInfoAnimatorOut.start();
    }
}
