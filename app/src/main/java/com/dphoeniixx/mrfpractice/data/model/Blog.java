package com.dphoeniixx.mrfpractice.data.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Blog {
    private String title;
    private String description;
    private String image;
    private String createdAt;
    private String _id;
    private ArrayList<String> tags;

    public Blog(String _id, String title, String description, String image, ArrayList<String> tags, String createdAt) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.tags = tags;
        this.createdAt = createdAt;
    }

    public String getID() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", tags=" + tags.toString() +
                '}';
    }
}
