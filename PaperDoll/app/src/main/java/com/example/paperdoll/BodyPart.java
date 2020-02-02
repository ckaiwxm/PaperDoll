package com.example.paperdoll;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class BodyPart {
    // self info
    public Part name;
    public Style style;
    public float curDeg;
    public float maxDeg;
    public float width;
    public float height;
    public float anchorX;
    public float anchorY;
    public RectF shape;
    public Matrix matrix;

    // inheritance
    public BodyPart parent;
    public ArrayList<BodyPart> children;

    public BodyPart(Part name, Style style, float maxDeg, float width, float height) {
        // Init self info
        this.name = name;
        this.style = style;
        this.curDeg = 0;
        this.maxDeg = maxDeg;
        this.width = width;
        this.height = height;

        // Init self info that needs calculation
        this.anchorX = -1;
        this.anchorY = -1;
        InitAnchor();
        this.matrix = new Matrix();
        this.shape = new RectF(0, 0, (int)(width), (int)(height));

        // Init inheritance info
        this.parent = null;
        this.children = new ArrayList<BodyPart>();
    }

    private void InitAnchor() {
        if (name == Part.Head ||
            name == Part.Spoon) {
            anchorX = width / 2;
            anchorY = height;
        }
        else if (name == Part.LeftUpArm ||
                 name == Part.LeftLowArm ||
                 name == Part.LeftHand ||
                 name == Part.LeftFoot) {
            anchorX = width;
            anchorY = height / 2;
        }
        else if (name == Part.RightUpArm ||
                 name == Part.RightLowArm ||
                 name == Part.RightHand ||
                 name == Part.RightFoot) {
            anchorX = 0;
            anchorY = height / 2;
        }
        else if (name == Part.LeftUpLeg ||
                 name == Part.RightUpLeg ||
                 name == Part.LeftLowLeg ||
                 name == Part.RightLowLeg ||
                 name == Part.TableLeftLeg ||
                 name == Part.TableRightLeg) {
            anchorX = width / 2;
            anchorY = 0;
        }
    }

    public void addInherit(BodyPart bodyPart) {
        children.add(bodyPart);
        bodyPart.parent = this;
    }

    public void move(float touchX, float touchY, float lastX, float lastY) {
        float diffX = touchX-lastX;
        float diffY = touchY-lastY;
        matrix.preTranslate(diffX, diffY);
    }

    public void rotate(float touchX, float touchY, float lastX, float lastY) {
        if (anchorX != -1 && anchorY != -1) {
            // Matrix of parents
            Matrix change = getParentMatrix();

            // Record targeted point after reverse
            float[] reversePoints = new float[2];
            reversePoints[0] = touchX;
            reversePoints[1] = touchY;

            // Record last point after reverse
            float[] reverseLastPoints = new float[2];
            reverseLastPoints[0] = lastX;
            reverseLastPoints[1] = lastY;

            // Reverse points
            Matrix reverseChange = new Matrix();
            change.invert(reverseChange);
            reverseChange.mapPoints(reversePoints);
            reverseChange.mapPoints(reverseLastPoints);

            // Calculate targeted degree
            float diffX = reversePoints[0] - anchorX;
            float diffY = reversePoints[1] - anchorY;
            double deg = Math.toDegrees(Math.atan2(diffY, diffX));

            // Calculate old degree
            float diffLastX = reverseLastPoints[0] - anchorX;
            float diffLastY = reverseLastPoints[1] - anchorY;
            double lastDeg = Math.toDegrees(Math.atan2(diffLastY, diffLastX));

            // Calculate diff and update current degree
            float diffDeg = (float) (deg - lastDeg);
            float targetDeg = curDeg + diffDeg;
            if (targetDeg >= -maxDeg && targetDeg <= maxDeg) {
                curDeg = targetDeg;
                matrix.preRotate(diffDeg, anchorX, anchorY);
            }
        }
    }

    public void scale(float scaleFactor) {
        if (name == Part.LeftUpLeg ||
            name == Part.RightUpLeg) {
            float oldHeight = height;
            height *= scaleFactor;
            shape = new RectF(0, 0, (int)width, (int)height);
            float shiftY = height - oldHeight;

            for (BodyPart child:children) {
                child.matrix.preTranslate(0, shiftY);
            }

            for (BodyPart child:children) {
                if ((name == Part.LeftUpLeg && child.name == Part.LeftLowLeg) ||
                    (name == Part.RightUpLeg && child.name == Part.RightLowLeg)){
                    float oldChildHeight = child.height;
                    child.height *= scaleFactor;
                    child.shape = new RectF(0, 0, (int)child.width, (int)(child.height));
                    float childShiftY = child.height - oldChildHeight;

                    for (BodyPart grandChild:child.children) {
                        grandChild.matrix.preTranslate(0, childShiftY);
                    }
                    break;
                }
            }
        }
        else if (name == Part.LeftLowLeg ||
                 name == Part.RightLowLeg) {
            float oldHeight = height;
            height *= scaleFactor;
            shape = new RectF(0, 0, (int)width, (int)height);
            float shiftY = height - oldHeight;

            for (BodyPart child:children) {
                child.matrix.preTranslate(0, shiftY);
            }
        }
        else if (name == Part.Cup) {
            float oldWidth = width;
            float oldHeight = height;
            width *= scaleFactor;
            height *= scaleFactor;
            shape = new RectF(0, 0, (int)width, (int)height);
            float shiftX = width - oldWidth;
            float shiftY = height - oldHeight;
            matrix.preTranslate(-shiftX/2, -shiftY/2);

            for (BodyPart child:children) {
                child.matrix.preTranslate(shiftX, 0);
            }
        }
    }

    // According to lecture hints
    public void draw(Canvas canvas, Paint brush) {
        Matrix backupMatrix = new Matrix(canvas.getMatrix());
        Matrix curMatrix = new Matrix(canvas.getMatrix());
        Matrix parentMatrix = getParentMatrix();
        curMatrix.preConcat(parentMatrix);

        canvas.setMatrix(curMatrix);
        if (style == Style.Rectan) {
            canvas.drawRect(shape, brush);
        }
        else if (style == Style.Oval) {
            canvas.drawOval(shape, brush);
        }

        canvas.setMatrix(backupMatrix);
        for (BodyPart child:children) {
            child.draw(canvas, brush);
        }
    }

    public BodyPart hitTest(float touchX, float touchY) {
        BodyPart retBodyPart = null;

        Matrix change = getParentMatrix();

        float[] reversePoints = new float[2];
        reversePoints[0] = touchX;
        reversePoints[1] = touchY;

        Matrix reverseChange = new Matrix();
        change.invert(reverseChange);
        reverseChange.mapPoints(reversePoints);

        // Hit test of self
        if (shape.contains(reversePoints[0], reversePoints[1])) {
            retBodyPart = this;
            return retBodyPart;
        }

        // Hit test of children
        for (BodyPart child:children) {
            retBodyPart = child.hitTest(touchX, touchY);
            if (retBodyPart != null) {
                return retBodyPart;
            }
        }

        return null;
    }

    public Matrix getParentMatrix() {
        // Matrix of self
        Matrix retMatrix = new Matrix(this.matrix);

        // Matrix if parents
        BodyPart parentBodyPart = this.parent;
        while (parentBodyPart != null) {
            Matrix parentMatrix = new Matrix(parentBodyPart.matrix);
            retMatrix.postConcat(parentMatrix);
            parentBodyPart = parentBodyPart.parent;
        }

        return retMatrix;
    }
}
