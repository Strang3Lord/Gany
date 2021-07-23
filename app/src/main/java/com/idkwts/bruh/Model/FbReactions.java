package com.idkwts.bruh.Model;

import com.amrdeveloper.reactbutton.Reaction;
import com.idkwts.bruh.R;

public final class FbReactions {

    public static Reaction defaultReact = new Reaction(
            ReactConstants.LIKE,
            ReactConstants.DEFAULT,
            ReactConstants.GRAY,
            R.drawable.ic_baseline_insert_emoticon_24);

    public static Reaction[] reactions = {
            new Reaction(ReactConstants.LIKE, ReactConstants.BLUE, R.drawable.ic_liked),
            new Reaction(ReactConstants.LOVE, ReactConstants.RED_LOVE, R.drawable.ic_heart),
            new Reaction(ReactConstants.SMILE, ReactConstants.YELLOW_WOW, R.drawable.ic_happy),
            new Reaction(ReactConstants.WOW, ReactConstants.YELLOW_WOW, R.drawable.ic_surprise),
            new Reaction(ReactConstants.SAD, ReactConstants.YELLOW_HAHA, R.drawable.ic_sad),
            new Reaction(ReactConstants.ANGRY, ReactConstants.RED_ANGRY, R.drawable.ic_angry),
    };

    private static class ReactConstants {
        //Color Constants
        static final String BLUE  = "#0366d6";
        static final String RED_LOVE  = "#f0716b";
        static final String RED_ANGRY  = "#f15268";
        static final String YELLOW_HAHA = "#fde99c";
        static final String YELLOW_WOW = "#f0ba15";
        static final String GRAY = "#616770";

        //Text Constants
        static final String DEFAULT = "";
        static final String LIKE = "";
        static final String LOVE = "";
        static final String SMILE = "";
        static final String WOW = "";
        static final String SAD = "";
        static final String ANGRY = "";
    }
}
