package com.phonyGames.kangaroo;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Jason on 7/4/2015.
 * Body is the superclass of all objects that orbit: stars, planets, moons, spaceships
 */
public class Body {

    public double radius, orbitComplete, orbitOffset, orbitSpeed, orbitRadius, rotateSpeed, brightness;
    public String name;
    public Body orbiting;
    public ArrayList<Body> orbiters;
    public ArrayList<Integer> orbiterDepthList;
    public Vector3 orbitAxis, orbitAxis2, oAxis, rotateAxis, pos, draw;
    public Color color;
    public boolean orbitInited=false, beingDrawn=false, isOrbiting,/*If not orbiting, it is part of a Path*/accurate/*False for orbiter spaceships where it doesnt really matter where they are*/, discovered/*whether or not it updates*/;
    public double PathDistance = 0,  minSize=0/*How big it is when zoomed out fully*/;//The distance covered in the path
    public Path path;//The path the body is on, if it is on one
    public int type=-1;
    public double dWidth, dHeight;
    public static double rad, draw_x, draw_y, multM, mult;
    public Body(int type, boolean isOrbiting, double radius, double orbitRadius, double rotateSpeed, double orbitSpeed, String name, Body orbiting, ArrayList<Body> orbiters, Vector3 oAxis, Vector3 rotateAxis, Vector3 posit, Color color, double brightness, boolean accurate, double pathDistance, Path path) {
        this.type=type;
        orbiterDepthList = new ArrayList<Integer>();
        draw = new Vector3(0,0,0);
        this.radius=radius;
        this.isOrbiting = isOrbiting;
        this.orbitRadius = orbitRadius;
        this.rotateSpeed = rotateSpeed;
        this.orbitSpeed = orbitSpeed;
        this.name = name;
        this.orbiting = orbiting;
        this.orbiters = orbiters;
        this.brightness=brightness;
        this.oAxis=oAxis;
        this.rotateAxis = rotateAxis;
        this.pos = posit;
        this.color = color;
        this.accurate = accurate;
        PathDistance = pathDistance;
        this.path = path;
        orbitOffset=-orbitComplete;

        switch (type)
        {
            case 1: minSize=8;
                break;
            case 2: minSize=1;
                break;
            case 3: minSize=.5;
                break;
            default: minSize=0;
                break;
        }
    }
    public Body getStar()
    {
        Body r = this;
        while (r.type>1)
            r=r.orbiting;
        return r;
    }
    public void setDiscovered(boolean discovered)
    {
        for (Body b:orbiters)
            b.setDiscovered(discovered);
        this.discovered=discovered;
    }
    public void initOrbit()
    {

        if (orbiting!=null)
        {
            orbitInited=true;
            orbitAxis = pos.sub(orbiting.pos);
            //System.out.print(this.orbitAxis+",");
            orbitAxis2 = orbitAxis.crs(oAxis).unitVector();
            orbitRadius = orbitAxis.len();
            orbitAxis=orbitAxis.unitVector();
            //start experimental
/*

            if (orbitAxis.dot(orbitAxis2)!=0)
                System.err.println(orbitAxis.dot(orbitAxis2));

            Vector3 axis = orbitAxis.crs(orbitAxis2);
            orbitComplete=(double)(Math.random()*Math.PI*2);
            pos = new Vector3(
                    (double)(orbiting.pos.x+orbitRadius*Math.cos(orbitComplete)*orbitAxis.x+orbitRadius*Math.sin(orbitComplete)*orbitAxis2.x),
                    (double)(orbiting.pos.y+orbitRadius*Math.cos(orbitComplete)*orbitAxis.y+orbitRadius*Math.sin(orbitComplete)*orbitAxis2.y),
                    (double)(orbiting.pos.z+orbitRadius*Math.cos(orbitComplete)*orbitAxis.z+orbitRadius*Math.sin(orbitComplete)*orbitAxis2.z)
            );

            this.orbitAxis = new Vector3(pos.x-orbiting.pos.x,pos.y-orbiting.pos.y,pos.z-orbiting.pos.z).unitVector();
            this.orbitAxis2 = orbitAxis.crs(axis);//this.orbitAxis.crs(this.orbitAxis.crs()).unitVector();
            System.out.println(orbitRadius);

            orbitComplete=-orbitComplete;
*/
            //end experimental
        }
    }
    public void addOrbiter(Body body)
    {
        body.orbiting=this;
        orbiters.add(body);
    }
    public void update(double dT)
    {
        if (isOrbiting && orbitInited)
        {
            updateOrbit(dT);
        }
        else
        {

        }
    }
    public void updateOrbit(double dT)
    {
        /*x(θ)=c1+rcos(θ)a1+rsin(θ)b1
        y(θ)=c2+rcos(θ)a2+rsin(θ)b2
        z(θ)=c3+rcos(θ)a3+rsin(θ)b3*/
        orbitComplete+=orbitSpeed*dT;
        pos.set(
                (double) (orbiting.pos.x + orbitRadius * Math.cos(orbitComplete) * orbitAxis.x + orbitRadius * Math.sin(orbitComplete) * orbitAxis2.x),
                (double) (orbiting.pos.y + orbitRadius * Math.cos(orbitComplete) * orbitAxis.y + orbitRadius * Math.sin(orbitComplete) * orbitAxis2.y),
                (double) (orbiting.pos.z + orbitRadius * Math.cos(orbitComplete) * orbitAxis.z + orbitRadius * Math.sin(orbitComplete) * orbitAxis2.z)
        );
    }
    public void draw()
    {
        if ( Math.abs(pos.x-Universe.c_pos.x)+Math.abs(pos.y-Universe.c_pos.y)+Math.abs(pos.z-Universe.c_pos.z)<Universe.c_maxRenderDis)//if the manhattan distance is small enough
        {
            project3d(pos,draw);
            if (draw.x!=-1 || (Universe.c_focus!=null && Universe.c_focus.orbiting!=null && Universe.c_focus.orbiting.equals(this)))//null if it is out of view
            {
                float starScale=10;
                if (draw.x==-1)
                    project3d(pos,draw,true);
                if (type==1)//star
                {
                    beingDrawn=true;
                    GameClass.batchCurrentColor.set(color.r, color.g, color.b, .2f + (float) (brightness / 10f) + (float) (.9 * Math.min(1, draw.z * brightness * 10)));
                    GameClass.batch.setColor(GameClass.batchCurrentColor);
                    //these scales effect the bloomlight
                    dWidth = (radius*GameClass.t_light.getRegionWidth()*draw.z*brightness/starScale/starScale+minSize);
                    dHeight = (radius*GameClass.t_light.getRegionHeight()*draw.z*brightness/starScale/starScale+minSize);
//t_light is the original sprite
                    //draw the circle
                    double dsWidth = (radius/starScale*GameClass.t_light.getRegionWidth()*draw.z*brightness+minSize)*1.3/starScale;
                    double dsHeight = (radius/starScale*GameClass.t_light.getRegionHeight()*draw.z*brightness+minSize)*1.3/starScale;
                    GameClass.batch.draw(GameClass.t_circle, (int) (GameClass.viewportWidth / 2 + draw.x - dsWidth), (int) (GameClass.viewportHeight / 2 + draw.y - dsHeight), (int) (2 * dsWidth), (int) (2 * dsHeight));
                    //draw the light
                    GameClass.batch.setColor(new Color(GameClass.batchCurrentColor.r,GameClass.batchCurrentColor.g,GameClass.batchCurrentColor.b,GameClass.batchCurrentColor.a/4));
                    GameClass.batch.draw(GameClass.t_light, (int) (GameClass.viewportWidth / 2 + draw.x - dWidth), (int) (GameClass.viewportHeight / 2 + draw.y - dHeight), (int) (2 * dWidth), (int) (2 * dHeight));
                    //if (this.equals(Universe.c_focus))
                    //GameClass.font.draw(GameClass.batch, "0,"+draw.z, (int)(GameClass.viewportWidth / 2 + draw.x), (int)(GameClass.viewportHeight / 2 + draw.y));
                }
                else
                {
                    beingDrawn=true;
                    GameClass.batchCurrentColor.set(color.r, color.g, color.b, 1);
                    GameClass.batch.setColor(color.cpy());
                    //System.out.println(color.a+","+GameClass.batchCurrentColor.a+","+GameClass.batch.getColor().a);
                    dWidth = (radius*GameClass.t_circle.getRegionWidth()*draw.z/starScale+minSize);
                    dHeight = (radius*GameClass.t_circle.getRegionHeight()*draw.z/starScale+minSize);
  //                  if (type==2)
//                    GameClass.font.draw(GameClass.batch, orbiting.orbiters.indexOf(this)+","+draw.z, (int)(GameClass.viewportWidth / 2 + draw.x), (int)(GameClass.viewportHeight / 2 + draw.y));

                    GameClass.batch.draw(GameClass.t_circle, (int) (GameClass.viewportWidth / 2 + draw.x - dWidth), (int) (GameClass.viewportHeight / 2 + draw.y - dHeight), (int) (2 * dWidth), (int) (2 * dHeight));
                }

                if (Universe.c_mouseOver!=null && Universe.c_mouseOver.equals(this))//if the mouse is over this one
                {
                    if (Universe.c_focus!=null)
                        GameClass.font.draw(GameClass.batch, "" + Mx.toSNotation(GameClass.distanceScale*Mx.calcDis(Universe.c_focus.pos, Universe.c_mouseOver.pos),4)+" "+GameClass.distanceUnit, (int) (GameClass.viewportWidth / 2 + draw.x), (int) (GameClass.viewportHeight / 2 + draw.y));
                }
                /*if (orbiters.size()>0 && Universe.c_focus==this)
                {
                    for (int a=0; a<orbiters.size(); a++)
                        orbiters.get(a).draw();
                }*/
            }
        }
    }
    public Vector3 project3d(Vector3 po)
    {
        Vector3 ret = new Vector3(0,0,0);
        project3d(po, ret);
        return ret;
    }
    public void project3d(Vector3 po, Vector3 out)
    {
        project3d(po,out,false);
    }
    public void project3d(Vector3 po, Vector3 out, boolean force)//Z value is the size multiplier
    {

        beingDrawn=false;
        Vector3 p1 = po.sub(Universe.c_pos);
        rad = p1.crs(Universe.c_lookat).len()/(p1.dot(Universe.c_lookat));
        //rad=10*(double)Math.sqrt(rad);
        if (po.x!=1)
        //GameClass.font.draw(GameClass.batch,p1.toStringShort(4)+",\t"+Universe.c_lookat.toStringShort(4)+",\t"+(int)rad+","+(int)(180.0/Math.PI*Math.acos(p1.dot(Universe.c_lookat)/(p1.len()*Universe.c_lookat.len())))+","+(int)(180.0/Math.PI*Math.asin(p1.crs(Universe.c_lookat).len()/(p1.len()*Universe.c_lookat.len()))),50,120);
        if (!force && (p1.dot(Universe.c_lookat)/p1.len()/Universe.c_lookat.len()<0 || rad<0)) {//if cos<1/2 in other words within 60 degrees
            out.set(-1,-1,-1);
            return;
        }
        draw_x = p1.sub(Universe.c_lookat).dot(Universe.c_plane);///p1.cpy().sub(Universe.c_lookat).len();
        draw_y = p1.sub(Universe.c_lookat).dot(Universe.c_plane2);///p1.cpy().sub(Universe.c_lookat).len();
        multM=(double)Math.sqrt(Math.pow(draw_x,2)+Math.pow(draw_y,2));
        /*double drawAng = (double)Math.atan(draw_y/draw_x);
        if (draw_x<=0)
            drawAng+=Math.PI;*/
        mult=0;
        if (multM!=0)
            mult = rad*Universe.c_d/multM;
        else
            mult=0;
        //return new Vector3((double)Math.cos(drawAng)*rad, (double)Math.sin(drawAng)*rad, 1/(po.sub(Universe.c_pos).dot(Universe.c_lookat)/Universe.c_lookat.len()));
        out.set((double) (draw_x) * mult, (double) (draw_y) * mult, 1 / (po.sub(Universe.c_pos).dot(Universe.c_lookat) / Universe.c_lookat.len()));
    }
}
