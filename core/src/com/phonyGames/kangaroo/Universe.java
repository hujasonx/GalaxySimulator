package com.phonyGames.kangaroo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Jason on 7/4/2015.
 * Contains the entire in-game universe
 */
public class Universe{
    public static ArrayList<Body> bodies;
    public static Vector3 c_pos, c_lookat, c_plane, c_plane2;
    public static double time, c_maxRenderDis, c_d, c_zoom, c_justChangedFocus = 0, c_maxZoom, c_minZoom, c_zoomGoal, c_mouseOverDis=0;
    public static Body c_focus, c_mouseOver, c_mouseOverPrev;

    // these are all cosmetic
    public static float gui_pathLineDrawAmount = 0;

    public static void init() {
        c_pos = new Vector3(100, 1, 1);
        c_lookat = new Vector3(0, 1, 0);
        c_plane = new Vector3(1, 0, 0);
        c_plane2 = new Vector3(0, 0, 1);
        c_maxRenderDis = 900000000;
        c_d = 1000f;//field of view
        c_zoom = 5;
        c_zoomGoal = c_zoom;
        c_maxZoom = 20000000;
        c_minZoom = .00002;
        time = 0;
        bodies = new ArrayList<Body>();
        create("");
        c_focus = bodies.get(0);
    }

    public static void create(String seed) {
        /*
        galaxy generation parameters
        */
        double g_maxDis = 6200000;//radius of the galaxy in pc
        double g_thickness = 300000;//how thick the core is in pc
        double g_spin = 3;//how twisted the arms are
        double g_centerDense = 3f;//how much denser the stars are at the core
        double g_armDense = 1.4f;//how defined the arms are, 1=no arm definition
        double g_numArms = 2;//number of spiral arms
        double g_numStars = 600;//number of stars
        double g_armThickness = 1.1;//how thick the arms can be
        double g_offsetDense = 1.5;//how dense the core is from side view
        double g_blueShiftSkew = -.1;//how much the blueshift is greater at the core than the edge of the galaxy
        float g_blueShiftRange = .3f;
        float g_blueShift = -.1f;
        double g_starRadiusRange=.4;
        float g_colorLeft = 1-Math.abs(g_blueShift)-g_blueShiftRange-(float)Math.abs(g_blueShiftSkew);
        /*
        galaxy generation
        */
        // public Body(int type, boolean isOrbiting, double radius, double rotateSpeed, double orbitSpeed, String name, Body orbiting, ArrayList<Body> orbiters, Vector3 orbitAxis, Vector3 rotateAxis, Vector3 pos, Color luminosity, boolean accurate, double pathDistance, Path path) {

        bodies.add(new Body(0, false, 0, .01f, 0, 0, "Supermassive Black Hole", null, new ArrayList<Body>(), new Vector3(0, 0, 1), new Vector3(0, 0, 1), new Vector3(0, 0, 0), Color.BLACK, 1, false, 0, null));
        /*Body te=new Body(true, .01f, Math.PI/60, "Star", bodies.get(0), new ArrayList<Body>(), bodies.get(0).rotateAxis.wobbled(.01f), bodies.get(0).rotateAxis.wobbled(.01f),  new Vector3(10,0,0), Color.WHITE, true, 0, null);
        bodies.get(0).addOrbiter(te);
        bodies.add(te);*/

        for (int a = 0; a < g_numStars; a++) {
            double r_dis = (1 - Math.pow(Math.random(), 1.0 / g_centerDense)) * g_maxDis;
            double r_angle = (Math.PI * (r_dis / g_maxDis * g_spin + (2.0 / g_numArms) * ((int) (g_numArms * Math.random())) + g_armThickness * (Math.pow(Math.random(), 1.0 / g_armDense) * Mx.randSign() * (1.0 / g_numArms))));
            double r_offset = (1 - .5f * r_dis / g_maxDis) * (Math.pow(Math.random(), g_offsetDense) * g_thickness) * Mx.randSign();//*(-g_thickness+Math.random()*2*g_thickness);
            double r_rotateSpeed = ((Math.random() * .0045) + .0005) * Mx.randSign();
            double r_radius = 1-g_starRadiusRange+2*Math.random()*g_starRadiusRange;
            float r_blueShift = (float) (Math.random() * 2 * g_blueShiftRange) - g_blueShiftRange + g_blueShift + (float)(g_blueShiftSkew*2*(.5-r_dis/g_maxDis));
            //new Color(1f - Math.max(0, r_blueShift), 1 + Math.min(0, r_blueShift), 1f + Math.min(0, r_blueShift), 1f)
            //Color r_color = new Color(1f - Math.max(0, r_blueShift), 1 + Math.min(0, Math.max(r_blueShift, r_blueShift * 2 + g_blueShiftRange / 1.2f)), 1f + Math.min(0, r_blueShift), 1f);//
            Color r_color = new Color(1f - Math.max(0,-r_blueShift-g_colorLeft/4)*.6f - Math.max(0, 1.3f*r_blueShift), 1 - Math.min(0,Math.abs(r_blueShift)*.5f-g_colorLeft/4) - Math.max(0,-r_blueShift-g_colorLeft/5), 1f - Math.max(0,-r_blueShift-g_colorLeft/4) + Math.min(0, r_blueShift), 1f);//

            System.out.println(r_blueShift+":\t"+r_color.r+",\t"+r_color.g+",\t"+r_color.b+",\t"+r_color.a);
            Body te = new Body(1, false, r_radius, .01f, r_rotateSpeed, Math.PI / 100, "Star", bodies.get(0), new ArrayList<Body>(), bodies.get(0).rotateAxis.unitVector(), bodies.get(0).rotateAxis.wobbled(.3f), new Vector3(Mx.lengthdirX(r_dis, Math.toDegrees(r_angle)), Mx.lengthdirY(r_dis, Math.toDegrees(r_angle)), r_offset), r_color, 1 / (1 - r_blueShift * (1 / (g_blueShiftRange * 2 + g_blueShift))), true, 0, null);
            //bodies.get(0).addOrbiter(te);
            bodies.add(te);

            int r_planets = (int) (Math.random() * 10);
            int r_mostCommoneNumofPlanets = 1;
            if (r_planets > r_mostCommoneNumofPlanets)
                r_planets -= (int) ((r_planets - r_mostCommoneNumofPlanets) * (Math.pow(Math.random(), .5)));
            double r_planetMaxDis = (.5 + Math.random() * 2);
            double r_planetSpin = r_rotateSpeed;
            double p_radius = .00001+.002*Math.random();
            for (int b = 0; b < r_planets; b++) {//te.pos.add(Vector3.random().scale(.2f+Math.random()*r_planetMaxDis))
                double r_planetDis = Math.random() * r_planetMaxDis;
                Body pl = new Body(2, true, p_radius, .001f, 0, (r_planetSpin * (Math.random() * .004 + .04) * (r_planetMaxDis / r_planetDis)), "Planet", te, new ArrayList<Body>(), te.rotateAxis.unitVector(), te.rotateAxis.wobbled(.4), te.pos.add(te.rotateAxis.crs(Vector3.random()).scale(r_planetDis)), new Color(.2f,.4f,.4f,1f), 1, true, 0, null);
                te.addOrbiter(pl);
                //System.out.println(pl.pos.sub(te.pos).len());
                bodies.add(pl);
                pl.initOrbit();

                int r_moons = (int) (Math.random() * 10);
                int r_mostCommoneNumofMoons = 1;
                if (r_moons > r_mostCommoneNumofMoons)
                    r_moons -= (int) ((r_planets - r_mostCommoneNumofMoons) * (Math.pow(Math.random(), .5)));
                double r_moonMaxDis = (.005 + Math.random() * .002);
                double r_moonSpin = r_rotateSpeed*10;
                double m_radius = p_radius*(.01+.1*Math.random())+.000003;
                for (int c = 0; c < r_moons; c++) {//te.pos.add(Vector3.random().scale(.2f+Math.random()*r_planetMaxDis))
                    double r_moonDis = Math.random() * r_moonMaxDis+pl.radius;
                    Body mo = new Body(3, true, m_radius, .001f, 0, (r_moonSpin * (Math.random() * .008 + .08) * (r_moonMaxDis / r_moonDis)), "Moon", pl, new ArrayList<Body>(), pl.rotateAxis.unitVector(), pl.rotateAxis.wobbled(.1), pl.pos.add(pl.rotateAxis.crs(Vector3.random()).scale(r_moonDis)), new Color(.2f,.2f,.4f,1f), 1, true, 0, null);
                    pl.addOrbiter(mo);
                    //System.out.println(pl.pos.sub(te.pos).len());
                    bodies.add(mo);
                    mo.initOrbit();
                }

            }
        }

    }

    public static void draw() {

        normalizeCameraPlane();
        Body mStar = c_focus;

        for (int a = 0; a < bodies.size(); a++) {

            boolean ismoon = ((bodies.get(a).equals(c_focus) || (c_focus.orbiting!=null && bodies.get(a).equals(c_focus.orbiting)) ||  ( c_focus.orbiting!=null && c_focus.orbiting.orbiting!=null && bodies.get(a).equals(c_focus.orbiting.orbiting))));//if its a star
            if (ismoon)
            {
                mStar=bodies.get(a);
                while (mStar.type!=1 && mStar.orbiting!=null)
                    mStar=mStar.orbiting;
            }
            if (bodies.get(a).type == 1 && !ismoon)
                bodies.get(a).draw();
            else if (ismoon) {
                //sort by depth, then draw
                //bodies.get(a).draw();
            }
        }
        ArrayList<Integer> sorted = depthSort(mStar);
        for (int a=0; a<sorted.size(); a++)
        {
            if (sorted.get(a)!=0)
            {
                ArrayList<Integer> sorted2 = depthSort(mStar.orbiters.get(sorted.get(a)-1));
                for (int b=0; b<sorted2.size(); b++)
                {
                    if (sorted2.get(b)!=0)
                    {
                        mStar.orbiters.get(sorted.get(a)-1).orbiters.get(sorted2.get(b)-1).draw();
                    }
                    else
                        mStar.orbiters.get(sorted.get(a)-1).draw();
                }
            }
            else
                mStar.draw();
        }
        drawGUI();
    }
    public static ArrayList<Integer> sortIndex(ArrayList<Double> num)
    {

        int len = num.size();
        double temp;
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int a=0; a<len; a++)
        {
            indices.add(a);
        }
            int i, j, first;
            for ( i = len - 1; i > 0; i -- )
            {
                first = 0;   //initialize to subscript of first element
                for(j = 1; j <= i; j ++)   //locate smallest element between positions 1 and i.
                {
                    if( num.get(j) > num.get(first) )
                        first = j;//first is the index of the smallest one
                }
                temp = num.get(first);   //swap smallest found with element in position i.
                num.set(first,num.get(i));
                num.set(i,temp);
                int temp2 = indices.get(i);
                indices.set(i,indices.get(first));
                indices.set(first,temp2);

            }

        return indices;
        /*
            int len = originalArray.size();
            Double[] sortedCopy = new Double[len];
            originalArray.toArray(sortedCopy);
            ArrayList<Integer> indices = new ArrayList<Integer>();

            // Sort the copy
            Arrays.sort(sortedCopy);

            // Go through the original array: for the same index, fill the position where the
            // corresponding number is in the sorted array in the indices array
        for (int index = 0; index < len; index++)
            indices.add(0);
            for (int index = 0; index < len; index++)
                indices.set(index,Arrays.binarySearch(sortedCopy, originalArray.get(index)));

            return indices;
*/
    }
    public static ArrayList<Integer> depthSort(Body obj)
    {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        ArrayList<Double> depths = new ArrayList<Double>();
        obj.project3d(obj.pos, obj.draw, true);
        depths.add(obj.draw.z);
        for (int a=0; a<obj.orbiters.size(); a++)
        {
            obj.orbiters.get(a).project3d(obj.orbiters.get(a).pos, obj.orbiters.get(a).draw, true);
            // obj.orbiters.get(a).draw.z
            depths.add(obj.orbiters.get(a).draw.z);
        }
        return sortIndex(depths);
    }

    public static void normalizeCameraPlane() {
        c_zoom=Mx.within(c_zoom,c_minZoom,c_maxZoom);
        if (GameClass.mouseClicked && GameClass.mouseClickedLast && !GameClass.mouseGUI && !GameClass.mouse2Clicked) {
            Vector3 rotateAlong = c_plane2.scale(GameClass.mouseXS / c_plane2.len()).add(c_plane.scale(-GameClass.mouseYS / c_plane.len()));
            double mag = Mx.pythag(GameClass.mouseXS, GameClass.mouseYS);
            c_lookat = Mx.rotateVector(c_lookat, rotateAlong, mag / 3000).unitVector();
            c_plane = Mx.rotateVector(c_plane, rotateAlong, mag / 3000).unitVector();
        }

        if (c_focus != null) {
            c_justChangedFocus--;
            if (c_justChangedFocus > 0)
                c_pos = c_pos.add(c_focus.pos.sub(c_lookat.unitVector().scale(c_zoom)).sub(c_pos).scale((30-c_justChangedFocus)/30));
            else
                c_pos = c_focus.pos.sub(c_lookat.unitVector().scale(c_zoom));
        }
        //debug

        //debug
        c_plane2 = c_lookat.crs(c_plane).unitVector();
        c_plane = c_lookat.crs(c_plane2).scale(-1).unitVector();
    }

    public static void update() {
        c_mouseOver =findNearestBodyDrawn(GameClass.mouseX,GameClass.mouseY,50);
        c_zoom+=(c_zoomGoal-c_zoom)/5;

        if (!c_focus.discovered)
        {
            c_focus.getStar().setDiscovered(true);
        }
        int atTime = 1;
        for (int a = GameClass.step % atTime; a < bodies.size(); a += atTime) {
            if (bodies.get(a).discovered)
                bodies.get(a).update(atTime);
        }
    }
    public static void drawGUI()
    {

        if (c_mouseOverPrev==null || (c_mouseOverPrev!=null && c_mouseOver!=null && !c_mouseOver.equals(c_mouseOverPrev)))
            gui_pathLineDrawAmount=0;
        if (c_focus!=null && c_mouseOver!=null)
        {
            gui_pathLineDrawAmount+=(1-gui_pathLineDrawAmount)*.3;
            double dir = Mx.calcAngle(c_focus.draw.x,c_focus.draw.y,c_mouseOver.draw.x,c_mouseOver.draw.y);
            double dis = gui_pathLineDrawAmount*Mx.calcDis(c_focus.draw.x,c_focus.draw.y,c_mouseOver.draw.x,c_mouseOver.draw.y);
            GameClass.shapeRenderer.line((float)c_focus.draw.x+GameClass.viewportWidth/2, (float)c_focus.draw.y+GameClass.viewportHeight/2, (float)c_focus.draw.x+(float)Mx.lengthdirX(dis,dir)+GameClass.viewportWidth/2, (float)c_focus.draw.y+(float)Mx.lengthdirY(dis,dir)+GameClass.viewportHeight/2);
        }

    }
    public static void delta()
    {
        c_mouseOverPrev = c_mouseOver;
    }
    public static void debug() {
        GameClass.mouseClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        GameClass.mouseRClicked = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        GameClass.mouseX = Gdx.input.getX();
        GameClass.mouseY = GameClass.viewportHeight-Gdx.input.getY();
        if (GameClass.mouseRClicked && !GameClass.mouseRClickedLast)
        {
            if (c_mouseOver!=null) {
                c_focus = c_mouseOver;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && c_zoom > c_minZoom) {
            c_d *= 2;
            c_zoomGoal /= 2;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && c_zoom < c_maxZoom) {
            c_d /= 2;
            c_zoomGoal *= 2;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            focusPrevBody();
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                while (c_focus.type!=1)
                    focusPrevBody();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            focusNextBody();
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                while (c_focus.type!=1)
                    focusNextBody();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && c_zoomGoal > c_minZoom)
            c_zoomGoal /= 1.1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && c_zoomGoal < c_maxZoom)
            c_zoomGoal *= 1.1;

    }
    public static Body findNearestBodyDrawn(float x, float y, float maxDis)
    {
        double min=999999;
        int soFar=0;
        for (int a=0; a<bodies.size(); a++)
        {
            if (bodies.get(a).beingDrawn)
            {
                double dis=Mx.calcDis(bodies.get(a).draw.x+GameClass.viewportWidth / 2,bodies.get(a).draw.y+GameClass.viewportHeight / 2,x,y);
                if (dis<min)
                {
                    min=dis;
                    soFar=a;
                }
            }
            bodies.get(a).beingDrawn=false;
        }
        if (min>maxDis)
            return null;
        return bodies.get(soFar);
    }
    public static void focusNextBody()
    {
        int toF = Universe.bodies.indexOf(c_focus) + 1;
        if (toF >= Universe.bodies.size())
            toF = 0;
        c_focus = Universe.bodies.get(toF);
        c_justChangedFocus = 30;
    }
    public static void focusPrevBody()
    {
        int toF = Universe.bodies.indexOf(c_focus) - 1;
        if (toF < 0)
            toF = Universe.bodies.size() - 1;
        c_focus = Universe.bodies.get(toF);
        c_justChangedFocus = 30;
    }

}
