package com.nebulasnews.news;

import com.nebulas.io.net.retrofit.NoProguard;

@NoProguard
public class Ad {
    private String title;
    private String desc;
    private String url;
    private String balance;
    private String nasPerShare;
    private String from;
    private String time;

    public Ad() {
    }


    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }

    public String getBalance() {
        return balance;
    }

    public String getNasPerShare() {
        return nasPerShare;
    }


    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }

    public Ad setTitle(String title) {
        this.title = title;
        return this;
    }

    public Ad setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public Ad setUrl(String url) {
        this.url = url;
        return this;
    }

    public Ad setBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public Ad setNasPerShare(String nasPerShare) {
        this.nasPerShare = nasPerShare;
        return this;
    }


    public Ad setFrom(String from) {
        this.from = from;
        return this;
    }

    public Ad setTime(String time) {
        this.time = time;
        return this;
    }

}
