package com.example.myapplication.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Task {

    @PrimaryKey (autoGenerate = true)
    private long id;

    private String title;
    private String body;
    private String state;
    private String imageKey;

    public Task(String title, String body, String state, String imageKey) {
        this.title = title;
        this.body = body;
        this.state = state;
        this.imageKey = imageKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
