package dev.undefinedteam.gensh1n.music.objs;

import lombok.ToString;

public class SearchMusicObj {
    public final String id;
    public final String name;
    public final String author;
    public final String al;

    public SearchMusicObj(String ID, String Name, String Author, String Al) {
        this.id = ID;
        this.name = Name;
        this.author = Author;
        this.al = Al;
    }
}
