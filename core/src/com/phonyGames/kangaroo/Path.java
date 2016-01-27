package com.phonyGames.kangaroo;

import java.util.ArrayList;

/**
 * Created by Jason on 7/4/2015.
 * The Path class represents the path between two Bodies
 */
public class Path {
    public ArrayList<Body> travelers;
    public float distance;
    public Body[] endpoints = new Body[2];
    public Path(Body start, Body end)
    {
        endpoints[0]=start;
        endpoints[1]=end;
    }
    public void update()
    {

    }
}
