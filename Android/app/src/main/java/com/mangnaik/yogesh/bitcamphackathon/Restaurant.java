package com.mangnaik.yogesh.bitcamphackathon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yogesh Mangnaik on 3/30/2019.
 */
public class Restaurant {
    String name;
    String aggRating;
    String ratingText;
    String hexColor;
    String costForTwo;
    List<String> bestOffers;
    List<String> reviews;
    String imageURL;
    String zomatoLink;

    public Restaurant(){
        bestOffers = new ArrayList<>();
        reviews = new ArrayList<>();
    }

    @Override
    public String toString() {
        System.out.println(name);
        System.out.println(aggRating);
        System.out.println(ratingText);
        System.out.println(hexColor);
        System.out.println(costForTwo);
        System.out.println(imageURL);
        System.out.println(zomatoLink);
        System.out.println(reviews);
//        System.out.println(name);
//        System.out.println(name);
        return "";
    }
}
