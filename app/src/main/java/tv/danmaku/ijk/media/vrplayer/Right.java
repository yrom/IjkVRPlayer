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

import android.opengl.Matrix;

import com.android.grafika.gles.Drawable2d;
import com.android.grafika.gles.Texture2dProgram;

/**
 * @author yrom.
 */
public class Right extends Square {
    private final float[] mModelMatrix = new float[16];
    public Right(Texture2dProgram program) {
        super(program);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, 0.5f, 0.5f, 0.5f);
        Matrix.translateM(mModelMatrix, 0, 1f, 0, 0); // translation to the right
    }

    @Override
    protected Drawable2d createDrawable2d() {
        // TODO: use the half of 'full' rectangle instead?
        return new Drawable2d(Drawable2d.Prefab.FULL_RECTANGLE);
    }

    @Override
    protected float[] getMvpMatrix() {
        return mModelMatrix;
    }
}
