/*
 * Copyright 2016 Yrom.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.vrplayer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

import com.android.grafika.gles.FullFrameRect;
import com.android.grafika.gles.GlUtil;
import com.android.grafika.gles.Texture2dProgram;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * The GLSurfaceView texture-render implementation
 */
public class GLTextureRender implements GLSurfaceView.Renderer {

    private static final String TAG = "TextureRender";
    private FullFrameRect mRect;
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private Handler mHandler;
    private final float[] mSTMatrix = new float[16];
    public GLTextureRender(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mRect = onCreateFrameRect();
        mTextureId = mRect.createTextureObject();

        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mHandler.sendEmptyMessage(1);
    }

    protected FullFrameRect onCreateFrameRect() {
        return new FullFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onSurfaceChanged " + width + "x" + height);
        GLES20.glViewport(0, 0, width, height);
        GlUtil.checkGlError("glViewport");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDrawFrame tex=" + mTextureId);
        }
        // Latch the latest frame.  If there isn't anything new, we'll just re-use whatever
        // was there before.
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
        // Clear dirty rect
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Draw the video frame.
        mRect.drawFrame(mTextureId, mSTMatrix);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void requestPause() {
        if (mSurfaceTexture != null) {
            Log.d(TAG, "Release surface texture");
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mRect != null) {
            mRect.release(false);
            mRect = null;
        }
    }
}
