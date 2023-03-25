package com.dphoeniixx.mrfpractice.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dphoeniixx.mrfpractice.BlogpostActivity;
import com.dphoeniixx.mrfpractice.R;
import com.dphoeniixx.mrfpractice.data.model.Blog;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class BlogAdapter extends ArrayAdapter<Blog> {
    public BlogAdapter(@NonNull Context context, ArrayList<Blog> dataArrayList) {
        super(context, R.layout.blog_post, dataArrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Blog Blog = getItem(position);
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.blog_post, parent, false);
        }
        ShapeableImageView blogImage = view.findViewById(R.id.blogImage);
        TextView blogTitle = view.findViewById(R.id.blogTitle);
        TextView blogTime = view.findViewById(R.id.blogTime);

        blogTitle.setText(Blog.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        CharSequence ago = null;
        try {
            long time = sdf.parse(Blog.getCreatedAt()).getTime();
            long now = System.currentTimeMillis();
            ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        blogTime.setText(ago);

        ImageDownloader imageDownloader = new ImageDownloader(Uri.parse(Blog.getImage()));
        blogImage.setImageBitmap(imageDownloader.download());

        view.setOnClickListener(view1 -> {
            Intent intentBlogpost = new Intent(getContext(), BlogpostActivity.class);
            intentBlogpost.putExtra(BlogpostActivity.BLOGPOST_ID, Blog.getID());
            getContext().startActivity(intentBlogpost);
        });


        return view;
    }
}