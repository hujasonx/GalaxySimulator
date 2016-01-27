package com.phonyGames.kangaroo;

/**
 * Created by Jason on 7/5/2015.
 */
public class Vector3 {
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double dot(Vector3 v)
    {
        return (v.x*x+v.y*y+v.z*z);
    }
    public double len()
    {
        return (double)Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
    }
    public Vector3 crs(Vector3 v)
    {
        return new Vector3(y*v.z-z*v.y,v.x*z-v.z*x,x*v.y-v.x*y);
    }
    public Vector3 sub(Vector3 v)
    {
        return new Vector3(x-v.x,y-v.y,z-v.z);
    }
    public Vector3 add(Vector3 v)
    {
        return new Vector3(x+v.x,y+v.y,z+v.z);
    }
    public void changeby(double a, double b, double c)
    {
        this.x+=a; this.y+=b; this.z+=c;
    }
    public String toString()
    {
        return "["+x+","+y+","+z+"]";
    }
    public String toStringShort(int chars)
    {
        return "["+(""+x).substring(0,Math.min(chars,(""+x).length()))+","+(""+y).substring(0,Math.min(chars,(""+y).length()))+","+(""+z).substring(0,Math.min(chars,(""+z).length()))+"]";
    }
    public Vector3 unitVector()
    {
        return scale(1.0f/len());
    }
    public Vector3 scale(double n)
    {
        return new Vector3(x*n,y*n,z*n);
    }
    public static Vector3 random()
    {
        return new Vector3((double)Math.random()-.5f,(double)Math.random()-.5f,(double)Math.random()-.5f).unitVector();
    }
    public Vector3 wobbled(double n)//a random vector wobbled, n is how much
    {
        return add(random().scale(n/len()));
    }
    public void set(double x, double y, double z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }
}
