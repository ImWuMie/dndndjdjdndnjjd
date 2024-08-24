package dev.undefinedteam.gclient.data;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

@ToString
public class UserData {
    @SerializedName("nick_name")
    public String mNickName;
    @SerializedName("hwid")
    public String mHwid;
    @SerializedName("token")
    public String mToken;
    @SerializedName("name_tag")
    public String mNameTag;
    @SerializedName("head_icon")
    public String mHeadIcon;
    @SerializedName("last_login")
    public long mLastLogin;
    @SerializedName("name_color")
    public NameColor name_color;
    @SerializedName("group")
    public String group;
}
