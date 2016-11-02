package kr.ac.kaist.vclab.bubble.models;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import kr.ac.kaist.vclab.bubble.MyGLRenderer;

/**
 * Created by mnswpr on 10/29/2016.
 */

public class BubbleSphere {

    private final int mProgram;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mNormalBuffer;

    // attribute handles
    private int mPositionHandle;
    private int mNormalHandle;

    // uniform handles
    private int mProjMatrixHandle;
    private int mModelViewMatrixHandle;
    private int mNormalMatrixHandle;
    // matrix for affine transformation of normal vector from model frame --> eye frame

    private int mLightHandle;
    private int mLight2Handle;
    private int mColorHandle;

    private static final int COORDS_PER_VERTEX = 3;
    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;


    private float[] origin;
    private float radius;
    private int res;

    private static float vertices[];
    private static float normals[];
    public float[] color = { 0.2f, 0.709803922f, 0.898039216f };


    public BubbleSphere(float[] _origin, float _radius, int _res){
        this.origin = _origin;
        this.radius = _radius;
        this.res = _res;

        updateVertices();
        updateNormals();

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShaderFromFile(
                GLES20.GL_VERTEX_SHADER, "basic-gl2.vshader");
        int fragmentShader = MyGLRenderer.loadShaderFromFile(
                GLES20.GL_FRAGMENT_SHADER, "diffuse-gl2.fshader");

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void draw(float[] projMatrix,
                     float[] modelViewMatrix,
                     float[] normalMatrix,
                     float[] light,
                     float[] light2) {
        GLES20.glUseProgram(mProgram);

        // uniforms
        mProjMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uProjMatrix");
        mModelViewMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uModelViewMatrix");
        mNormalMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uNormalMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mLightHandle = GLES20.glGetUniformLocation(mProgram, "uLight");
        mLight2Handle = GLES20.glGetUniformLocation(mProgram, "uLight2");

        GLES20.glUniformMatrix4fv(mProjMatrixHandle, 1, false, projMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelViewMatrixHandle, 1, false, modelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(mNormalMatrixHandle, 1, false, normalMatrix, 0);

        GLES20.glUniform3fv(mColorHandle, 1, color, 0);
        GLES20.glUniform3fv(mLightHandle, 1, light, 0);
        GLES20.glUniform3fv(mLight2Handle, 1, light2, 0);

        // attributes
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, mVertexBuffer);

        GLES20.glVertexAttribPointer(
                mNormalHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, mNormalBuffer);

        // Draw the sphere
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertices.length / 3);
        GLES20.glLineWidth(2.0f);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
    }

    private void updateVertices(){
        //implement code to define vertices

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuf.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    private void updateNormals(){
        //implement code to define normals

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(normals.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mNormalBuffer = byteBuf.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);
    }

}