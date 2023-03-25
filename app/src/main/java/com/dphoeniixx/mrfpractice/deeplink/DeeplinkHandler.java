package com.dphoeniixx.mrfpractice.deeplink;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeeplinkHandler {
    private static final DeeplinkEntry HOME_DEEPLINK              = new DeeplinkEntry("^mrf://dphoeniixx/home$", "handleHome");
    private static final DeeplinkEntry BLOGPOST_DEEPLINK          = new DeeplinkEntry("^mrf://dphoeniixx/blog/(.*?)$", "handleBlog");
    private static final DeeplinkEntry REDEEM_DEEPLINK            = new DeeplinkEntry("^mrf://dphoeniixx/redeem$", "handleRedeem");
    private static final ArrayList<DeeplinkEntry> deeplinkEntries = new ArrayList<>(Arrays.asList(HOME_DEEPLINK, BLOGPOST_DEEPLINK, REDEEM_DEEPLINK));

    private static DeeplinkEntry deeplinkEntry = null;
    private static Uri deeplinkUri;

    public DeeplinkHandler(Uri uri) {
        deeplinkUri = uri;
        deeplinkEntry = getDeeplinkEntry();
    }

    public static DeeplinkEntry getDeeplinkEntry(){
        String URI = String.format("%s://%s%s", deeplinkUri.getScheme(), deeplinkUri.getAuthority(), deeplinkUri.getPath());
        for(DeeplinkEntry entry : deeplinkEntries){
            Pattern pattern = Pattern.compile(entry.getRegex());
            Matcher matcher = pattern.matcher(URI);
            if(matcher.matches()){
                return entry;
            }
        }
        return null;
    }

    public void handle(){
        if(deeplinkEntry != null){
            try {
                DeeplinkHandlers.class.getMethod(deeplinkEntry.getHandlerMethod(), Uri.class).invoke(null, deeplinkUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
