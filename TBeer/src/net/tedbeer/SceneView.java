package net.tedbeer;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.*;

/**
 * User: TedBeer
 * Date: 30/01/12
 * Time: 12:32
 */
public class SceneView extends View {
    private static Bitmap bmSprite;
    private static Bitmap bmBackground;
    private static Rect rSrc, rDest;
    
    //points defining our curve
    private List<PointF> aPoints = new ArrayList<PointF>();
    private Paint paint;
    private Path ptCurve = new Path(); //curve
    private PathMeasure pm;            //curve measure
    private float fSegmentLen;         //curve segment length
    private float v = 90f;

    float k1 = 0.1f;
    float k2 = 0.001f;
    float g = 9.8f;
    
    public SceneView(Context context) {
        super(context);
        //destination rectangle
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rDest = new Rect(0, 0, display.getWidth(), display.getHeight());
        
        //load background
        if (bmBackground == null) {
            bmBackground = BitmapFactory.decodeResource(getResources(), R.drawable.winter_mountains);
            rSrc = new Rect(0, 0, bmBackground.getWidth(), bmBackground.getHeight());
        }

        //load sprite
        if (bmSprite == null)
            bmSprite = BitmapFactory.decodeResource(getResources(), R.drawable.sledge3);

        //init random set of points
        aPoints.add(new PointF(10f, 60f));
        aPoints.add(new PointF(15f, 120f));
        aPoints.add(new PointF(20f, 200f));
        aPoints.add(new PointF(25f, 300f));
        aPoints.add(new PointF(30f, 400f));
        aPoints.add(new PointF(40f, 500f));
        aPoints.add(new PointF(50f, 550f));
        aPoints.add(new PointF(100f, 560f));
        aPoints.add(new PointF(150f, 520f));
        aPoints.add(new PointF(200f, 500f));
        aPoints.add(new PointF(600f, 350f));
        aPoints.add(new PointF(900f, 600f));
        aPoints.add(new PointF(1200f, 500f));
        
        //init smooth curve
        PointF point = aPoints.get(0);
        ptCurve.moveTo(point.x, point.y);
        for(int i = 0; i < aPoints.size() - 1; i++){
            point = aPoints.get(i);
            PointF next = aPoints.get(i+1);
            ptCurve.quadTo(point.x, point.y, (next.x + point.x) / 2, (point.y + next.y) / 2);
        }
        pm = new PathMeasure(ptCurve, false);
        fSegmentLen = 0;

        //init paint object
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.rgb(0, 148, 255));
        
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmBackground, rSrc, rDest, null);
        canvas.drawPath(ptCurve, paint);
        //animate the sprite
        Matrix mxTransform = new Matrix();
        if (fSegmentLen <= pm.getLength()) {
            pm.getMatrix(fSegmentLen, mxTransform,
                    PathMeasure.POSITION_MATRIX_FLAG + PathMeasure.TANGENT_MATRIX_FLAG);
            mxTransform.preTranslate(-bmSprite.getWidth(), -bmSprite.getHeight());
            canvas.drawBitmap(bmSprite, mxTransform, null);

            float at[] = {0f, 0f};
            pm.getPosTan(fSegmentLen, null , at);
                     
            float a = g*at[1] - v*k1 - v*v*k2;
            v = v + a/2 ;
            fSegmentLen = fSegmentLen + v; 

            invalidate();
        } else {
        	fSegmentLen = 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN ) { //run animation
        	v = 90f;
            invalidate();
            return true;
        }
        return false;
    }

}
