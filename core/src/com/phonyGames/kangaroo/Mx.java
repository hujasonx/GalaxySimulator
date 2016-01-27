package com.phonyGames.kangaroo;

import com.badlogic.gdx.graphics.Color;
/*
 *
 * Mx is the same as MathEx, but with a shorter name -JHu 07/04/2015
 *
 */

public class Mx
{
    public static Color hsvToRgb(double hue, double saturation, double value) {

        int h = (int)(hue * 6);
        double f = hue * 6 - h;
        double p = value * (1 - saturation);
        double q = value * (1 - f * saturation);
        double t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: return rgbToColor(value, t, p);
            case 1: return rgbToColor(q, value, p);
            case 2: return rgbToColor(p, value, t);
            case 3: return rgbToColor(p, q, value);
            case 4: return rgbToColor(t, p, value);
            case 5: return rgbToColor(value, p, q);
            default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }
    public static Color rgbToColor(double r, double g, double b) {
        String rs = Integer.toHexString((int)(r * 256));
        String gs = Integer.toHexString((int)(g * 256));
        String bs = Integer.toHexString((int)(b * 256));
        //return rs + gs + bs;
        return new Color((float)r,(float)g,(float)b,1f);
    }

    public static double manhattan(double x1, double y1, double x2, double y2)
    {
        return Math.abs(x1-x2)+Math.abs(y1-y2);
    }
    public static double within(double num, double min, double max)
    {
        return Math.max(Math.min(num, max), min);

    }
    public static double calcAngle(double p1x,double p1y,double p2x,double p2y)
    {
        double ret = (double)(Math.atan2(p2y-p1y,p2x-p1x)*180.0f/Math.PI)%360;
        while (ret<0)
            ret+=360;
        return ret;
    }
    public static int nearestPow2(int a)
    {
        int ret = (int)Math.pow(2,(int)(Math.log(a)/Math.log(2)));
        return ret;
    }
    public static double pythag(double a, double b)
    {
        return (double)Math.sqrt(Math.pow(a,2)+Math.pow(b,2));
    }
    public static double velocity(double mass, double y, double gravity, double energy)
    {
        //(double)(mass*velocity*velocity/2)+(mass*y*gravity)=constant;

        if (sign(energy-(mass*y*gravity))==-1)
            return .01f;
        return (double)Math.sqrt(Math.abs(energy-(mass*y*gravity))*2/mass);
    }
    public static double fromVelocity(double mass, double y, double gravity, double velocity)
    {
        //()(mass*velocity*velocity/2)+(mass*y*gravity)=constant;

        return (mass*velocity*velocity/2)+(mass*y*gravity);
    }
    public static double lengthdirX(double length ,double dir)
    {
        return (double)(length*(Math.cos(Math.toRadians(dir))));

    }
    public static double lengthdirY(double length ,double dir)
    {
        return (double)(length*(Math.sin(Math.toRadians(dir))));

    }
    public static int digits(double x)
    {
        return (""+x).length();

    }
    public static double calcDis(double p1x,double p1y,double p2x,double p2y)
    {
        return (double)Math.sqrt((Math.pow(p1x-p2x,2))+(Math.pow(p1y-p2y,2)));
    }
    public static double calcDisManhattan(double p1x,double p1y,double p2x,double p2y)
    {
        return (double)(Math.abs(p1x-p2x)+Math.abs(p1y-p2y));
    }
    public static int sign(double an)
    {
        if (an>0)
            return 1;
        if (an==0)
            return 0;
        return -1;
    }
    public static double rotate(double dstart, double dend, double dspeed)
    {
        double ret=dend-dstart;
        if (ret>180)
            ret-=360;
        if (ret<-180)
            ret+=360;
        return (double)(Mx.sign(ret)*Math.min(dspeed,Math.abs(ret)));
    }
    /*public static Point median(double x1, double y1, double x2, double y2)
    {
        return new Point((int)((x2+x1)/2.0),(int)((y2+y1)/2.0));

    }*/
    public static double modulus(double num, double mod)
    {
        double ret=num;
        if (num<0)
            ret=mod*(num/mod-(int)(num/mod))+mod;
        else
            ret=mod*(num/mod-(int)(num/mod));
        return ret;
    }
    public static double bodyManDis(Body a, Body b)
    {
        return Math.abs(a.pos.x-b.pos.x)+Math.abs(a.pos.y-b.pos.y)+Math.abs(a.pos.z-b.pos.z);
    }
    public static double bodyDis(Body a, Body b)
    {
        return (double)Math.sqrt(Math.pow(a.pos.x-b.pos.x,2)+Math.pow(a.pos.y-b.pos.y,2)+Math.pow(a.pos.z-b.pos.z,2));
    }
    public static double calcDis(Vector3 a, Vector3 b)
    {
        return (double)Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2)+Math.pow(a.z-b.z,2));
    }
    public static int randSign()
    {
        return (int)(2*((int)(2*Math.random())-.5));
    }
    public static Vector3 rotateVector(Vector3 start, Vector3 axis, double theta)
    {
        /*
        Given angle r in radians and unit vector u = ai + bj + ck or [a,b,c]', define:

q0 = cos(r/2),  q1 = sin(r/2) a,  q2 = sin(r/2) b,  q3 = sin(r/2) c
and construct from these values the rotation matrix:

     (q0² + q1² - q2² - q3²)      2(q1q2 - q0q3)          2(q1q3 + q0q2)

Q  =      2(q2q1 + q0q3)     (q0² - q1² + q2² - q3²)      2(q2q3 - q0q1)

          2(q3q1 - q0q2)          2(q3q2 + q0q1)     (q0² - q1² - q2² + q3²)
Multiplication by Q then effects the desired rotation, and in particular:

Q u = u
         */
        double q0 = Math.cos(theta/2);
        double q1 = Math.sin(theta/2)*axis.x;
        double q2 = Math.sin(theta/2)*axis.y;
        double q3 = Math.sin(theta/2)*axis.z;
        double retx = (double)((Math.pow(q0,2)+Math.pow(q1,2)-Math.pow(q2,2)-Math.pow(q3,2))*start.x+2*(q1*q2-q0*q3)*start.y+2*(q1*q3+q0*q2)*start.z);
        double rety = (double)(2*(q2*q1+q0*q3)*start.x+(Math.pow(q0,2)-Math.pow(q1,2)+Math.pow(q2,2)-Math.pow(q3,2))*start.y+2*(q2*q3-q0*q1)*start.z);
        double retz = (double)(2*(q3*q1-q0*q3)*start.x+2*(q2*q3+q0*q1)*start.y+(Math.pow(q0,2)-Math.pow(q1,2)-Math.pow(q2,2)+Math.pow(q3,2))*start.z);
        return new Vector3(retx, rety, retz);
    }
    public static String toSNotation(double n, int sigFigs)
    {
        int pow=0;
        if (n==0)
            return "0";
        while (n>=10)
        {
            pow++;
            n/=10;
        }
        while (n<1)
        {
            pow--;
            n*=10;
        }
        n=(double)((int)(Math.pow(10,sigFigs-1)*n))/Math.pow(10,sigFigs-1);
        if (pow==0)
            return ""+n;

        return n+" E"+pow;
    }
}