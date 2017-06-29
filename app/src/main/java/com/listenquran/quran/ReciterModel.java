package com.listenquran.quran;

/**
 * Created by lenovo on 6/29/2017.
 */

public class ReciterModel {
    String name,server, id ;

    public ReciterModel(String name, String server, String id) {
        this.name = name;
        this.server = server;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
