package com.example.paperdoll;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class CanvasView extends View {

    public float lastX;
    public float lastY;
    public BodyPart body;
    public BodyPart selectedBodyPart;
    public boolean ifDoll;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor;

    public CanvasView(Context context)
    {
        super(context);
        DefaultInit(context);
        invalidate();
    }

    public CanvasView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        DefaultInit(context);
        invalidate();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        DefaultInit(context);
        invalidate();
    }

    private void InitDoll() {
        // Torso
        BodyPart torso = new BodyPart(Part.Torso, Style.Rectan, 0, 200, 400);
        torso.matrix.preTranslate(800,400);

        // Head
        BodyPart head = new BodyPart(Part.Head, Style.Oval, 50, 140, 200);
        head.matrix.preTranslate(30,-210);

        // Left upper arm
        BodyPart leftUpArm = new BodyPart(Part.LeftUpArm, Style.Oval, 360, 200, 60);
        leftUpArm.matrix.preTranslate(-200,0);

        // Right upper arm
        BodyPart rightUpArm = new BodyPart(Part.RightUpArm, Style.Oval, 360, 200, 60);
        rightUpArm.matrix.preTranslate(200, 0);

        // Left lower arm
        BodyPart leftLowArm = new BodyPart(Part.LeftLowArm, Style.Oval, 135, 180, 60);
        leftLowArm.matrix.preTranslate(-180,0);

        // Right lower arm
        BodyPart rightLowArm = new BodyPart(Part.RightLowArm, Style.Oval, 135, 180, 60);
        rightLowArm.matrix.preTranslate(200, 0);

        // Left hand
        BodyPart leftHand = new BodyPart(Part.LeftHand, Style.Oval, 35, 60, 60);
        leftHand.matrix.preTranslate(-60,0);

        // Right hand
        BodyPart rightHand = new BodyPart(Part.RightHand, Style.Oval, 35, 60, 60);
        rightHand.matrix.preTranslate(180, 0);

        // Left upper leg
        BodyPart leftUpLeg = new BodyPart(Part.LeftUpLeg, Style.Oval, 90, 60, 250);
        leftUpLeg.matrix.preTranslate(15, 400);

        // Right upper leg
        BodyPart rightUpLeg = new BodyPart(Part.RightUpLeg, Style.Oval, 90, 60, 250);
        rightUpLeg.matrix.preTranslate(135,400);

        // Left lower leg
        BodyPart leftLowLeg = new BodyPart(Part.LeftLowLeg, Style.Oval, 90, 60, 200);
        leftLowLeg.matrix.preTranslate(0, 250);

        // Right lower leg
        BodyPart rightLowLeg = new BodyPart(Part.RightLowLeg, Style.Oval, 90, 60, 200);
        rightLowLeg.matrix.preTranslate(0, 250);

        // Left foot
        BodyPart leftFoot = new BodyPart(Part.LeftFoot, Style.Oval, 35, 100, 50);
        leftFoot.matrix.preTranslate(-40, 200);

        // Right foot
        BodyPart rightFoot = new BodyPart(Part.RightFoot, Style.Oval, 35, 100, 50);
        rightFoot.matrix.preTranslate(0,200);


        // Attach doll
        torso.addInherit(head);
        torso.addInherit(leftUpArm);
        torso.addInherit(rightUpArm);
        torso.addInherit(leftUpLeg);
        torso.addInherit(rightUpLeg);

        leftUpArm.addInherit(leftLowArm);
        rightUpArm.addInherit(rightLowArm);

        leftLowArm.addInherit(leftHand);
        rightLowArm.addInherit(rightHand);

        leftUpLeg.addInherit(leftLowLeg);
        rightUpLeg.addInherit(rightLowLeg);

        leftLowLeg.addInherit(leftFoot);
        rightLowLeg.addInherit(rightFoot);

        // Set current body
        this.body = torso;
    }

    private void InitTable() {
        // Table
        BodyPart table = new BodyPart(Part.Table, Style.Rectan, 0, 600, 200);
        table.matrix.preTranslate(800,600);

        // Table left leg
        BodyPart tableLeftLeg = new BodyPart(Part.TableLeftLeg, Style.Oval, 20, 40, 300);
        tableLeftLeg.matrix.preTranslate(20,200);

        // Table right leg
        BodyPart tableRightLeg = new BodyPart(Part.TableRightLeg, Style.Oval, 20, 40, 300);
        tableRightLeg.matrix.preTranslate(540,200);

        // Cup
        BodyPart cup = new BodyPart(Part.Cup, Style.Rectan, 0, 80, 100);
        cup.matrix.preTranslate(260,-110);

        // Spoon
        BodyPart spoon = new BodyPart(Part.Spoon, Style.Oval, 50, 20, 50);
        spoon.matrix.preTranslate(45,-50);

        // Attach table
        table.addInherit(tableLeftLeg);
        table.addInherit(tableRightLeg);
        table.addInherit(cup);

        cup.addInherit(spoon);

        this.body = table;
    }

    private void DefaultInit(Context context) {
        body = null;
        selectedBodyPart = null;
        ifDoll = true;
        if (ifDoll) {
            InitDoll();
        }
        else {
            InitTable();
        }
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mScaleFactor = 1;
    }

    public void resetBody() {
        body = null;
        selectedBodyPart = null;
        if (ifDoll) {
            InitDoll();
        }
        else {
            InitTable();
        }
    }

    public void switchBody() {
        body = null;
        selectedBodyPart = null;
        if (ifDoll) {
            ifDoll = false;
            InitTable();
        }
        else {
            ifDoll = true;
            InitDoll();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint brush = new Paint(Paint.ANTI_ALIAS_FLAG);
        brush.setColor(Color.parseColor("#3A3A3A"));
        body.draw(canvas, brush);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);
        mScaleDetector.onTouchEvent(e);

        float touchX = e.getX();
        float touchY = e.getY();
        int gesture = e.getActionMasked();

        if (gesture == MotionEvent.ACTION_DOWN) {
            lastX = -1;
            lastY = -1;
            selectedBodyPart = null;
            selectedBodyPart = body.hitTest(touchX, touchY);
            if (selectedBodyPart != null) {
                lastX = touchX;
                lastY = touchY;
            }
        }
        else if (gesture == MotionEvent.ACTION_MOVE) {
            if (selectedBodyPart != null && lastX != -1 && lastY != -1) {
                if (selectedBodyPart.style == Style.Oval) {
                    selectedBodyPart.rotate(touchX, touchY, lastX, lastY);
                }
                else {
                    selectedBodyPart.move(touchX, touchY, lastX, lastY);
                }
                lastX = touchX;
                lastY = touchY;
            }
        }
        else if (gesture == MotionEvent.ACTION_UP) {
            lastX = -1;
            lastY = -1;
            selectedBodyPart = null;
        }

        invalidate();
        return true;
    }

    // According to Android Developer Guide
    // https://developer.android.com/training/gestures/scale
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            super.onScaleBegin(detector);

            float touchX = detector.getFocusX();
            float touchY = detector.getFocusY();
            selectedBodyPart = null;
            selectedBodyPart = body.hitTest(touchX, touchY);
            if (selectedBodyPart != null) {
                if (selectedBodyPart.name == Part.LeftUpLeg ||
                    selectedBodyPart.name == Part.RightUpLeg ||
                    selectedBodyPart.name == Part.LeftLowLeg ||
                    selectedBodyPart.name == Part.RightLowLeg ||
                    selectedBodyPart.name == Part.Cup) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mScaleFactor = 1;

            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            super.onScale(detector);

            mScaleFactor = detector.getScaleFactor();

            if (selectedBodyPart != null) {
                selectedBodyPart.scale(mScaleFactor);
            }

            invalidate();
            return true;
        }
    }
}
