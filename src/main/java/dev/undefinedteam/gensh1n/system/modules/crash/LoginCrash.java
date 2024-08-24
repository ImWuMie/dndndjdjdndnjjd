package dev.undefinedteam.gensh1n.system.modules.crash;

import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;

public class LoginCrash extends Module {
    public LoginCrash() {
        super(Categories.Crash, "login-crash", "Tries to crash the server on login using null packets. (By 0x150)");
    }
}
