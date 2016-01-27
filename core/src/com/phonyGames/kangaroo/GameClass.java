package com.phonyGames.kangaroo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bitfire.postprocessing.PostProcessor;
import com.bitfire.postprocessing.effects.Bloom;
import com.bitfire.utils.ShaderLoader;

public class GameClass extends ApplicationAdapter {
	public static SpriteBatch batch;
    public static TextureRegion t_light, t_circle;
    public static BitmapFont font;
    public static OrthographicCamera camera;
    public static Color batchCurrentColor = Color.WHITE;
    public static com.badlogic.gdx.math.Vector3 touchPos;
    public static ShapeRenderer shapeRenderer;
    public static double distanceScale = .0002;//The multipliers whenever a distance is displayed
    public static String distanceUnit = "ly";
    public static int viewportWidth=1280, viewportHeight=720, step=0;
    public static int mouseX=0, mouseY=0, mouseXLast=0, mouseYLast=0, mouseXS=0, mouseYS=0;
    public static int mouseX2=0, mouseY2=0, mouseX2Last=0, mouseY2Last=0, mouseX2S=0, mouseY2S=0;
    public static boolean mouseGUI=false,/*the mouse clicked in the GUI last*/ mouseClicked=false, mouseClickedLast=false, mouse2Clicked=false, mouse2ClickedLast=false, mouseRClicked=false, mouseRClickedLast=false;
	public static PostProcessor postProcessor;
    Texture img;





	@Override
	public void create () {
		batch = new SpriteBatch();
        camera = new OrthographicCamera();
        touchPos=new com.badlogic.gdx.math.Vector3();
        camera.setToOrtho(false, 1280, 720);
        shapeRenderer = new ShapeRenderer();
        /*System.out.println("10 mod 15:"+Mx.modulus(10,15));
        System.out.println("25 mod 15:"+Mx.modulus(25,15));
        System.out.println("-10 mod 15:"+Mx.modulus(-10,15));
        System.out.println("-25 mod 15:"+Mx.modulus(-25,15));*/
		img = new Texture("data/texture.png");
        img.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        t_light = new TextureRegion(img,0,0,256,256);
        t_circle = new TextureRegion(img,256,0,256,256);
        font = new BitmapFont(Gdx.files.internal("data/fonts/calibri.fnt"),Gdx.files.internal("data/fonts/calibri.png"), false, true);
        font.setColor(Color.WHITE);
        Universe.init();
        Gdx.input.setInputProcessor(new InputPro());



        ShaderLoader.BasePath = "data/shaders/";
        postProcessor = new PostProcessor( false, false, true );
        Bloom bloom = new Bloom( (int)(Gdx.graphics.getWidth() * 0.25f), (int)(Gdx.graphics.getHeight() * 0.25f) );

        bloom.setSettings(new Bloom.Settings("default", 2, 0.67f, 1f, .85f, 2.4f, .95f));
        postProcessor.addEffect( bloom );

	}
    private class InputPro extends InputAdapter
    {
        public InputPro()
        {

        }
        public boolean scrolled (int amount) {

        //zoom
        if (amount>0)
            Universe.c_zoomGoal *= 1.5;
        else
            Universe.c_zoomGoal /= 1.5;

        return false;
        }

    }
	@Override
	public void render () {
        mouseLocation();
        androidCamera();

        if (Gdx.app.getType().equals(Application.ApplicationType.Desktop))
            Universe.debug();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        long nanotime = System.nanoTime();

        Universe.update();
        step++;
        long nanotime2 = System.nanoTime();



        postProcessor.capture();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		batch.begin();
        Universe.draw();

        font.draw(batch,""+Gdx.graphics.getFramesPerSecond(),viewportWidth-40,viewportHeight-40);
        font.draw(batch, "x", mouseX, mouseY);
        batch.end();
        shapeRenderer.end();


        //Gdx.app.log(" ",(nanotime2-nanotime)+",\t"+(System.nanoTime()-nanotime2)+",\t"+((System.nanoTime()-nanotime2))/(nanotime2-nanotime));
        Universe.delta();
        deltaUpdate();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        postProcessor.render();
	}
    public void mouseLocation()
    {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android))
        {
            if (Gdx.input.isTouched(0))
            {
                mouseClicked=true;


                touchPos.set(Gdx.input.getX(0), Gdx.input.getY(0), 0);
                camera.unproject(touchPos);
                mouseX=(int)touchPos.x;
                mouseY=(int)touchPos.y;

            }
            if (Gdx.input.isTouched(1))
            {
                mouse2Clicked=true;
                touchPos.set(Gdx.input.getX(1), Gdx.input.getY(1), 0);
                camera.unproject(touchPos);
                mouseX2=(int)touchPos.x;
                mouseY2=(int)touchPos.y;
            }
        }
    }
    public void androidCamera()
    {
        if (Gdx.app.getType().equals(Application.ApplicationType.Android) && mouse2ClickedLast && mouseClickedLast) {
            Universe.c_zoom *= 1+.03*(Mx.calcDis(mouseX,mouseY,mouseX2,mouseY2)-Mx.calcDis(mouseXLast,mouseYLast,mouseX2Last,mouseY2Last));
        }
    }
    public void deltaUpdate()
    {
        mouseXS=mouseX-mouseXLast;
        mouseYS=mouseY-mouseYLast;
        mouseXLast=mouseX;
        mouseYLast=mouseY;
        mouseClickedLast=mouseClicked;

        mouseX2S=mouseX2-mouseX2Last;
        mouseY2S=mouseY2-mouseY2Last;
        mouseX2Last=mouseX2;
        mouseY2Last=mouseY2;
        mouse2ClickedLast=mouse2Clicked;
        mouseClicked=false;
        mouse2Clicked=false;
        mouseRClickedLast=mouseRClicked;
        mouseRClicked=false;
    }
}
