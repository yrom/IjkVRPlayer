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

import com.android.grafika.gles.FullFrameRect;
import com.android.grafika.gles.Texture2dProgram;

/**
 * Show side-by-side 'full' frame rect
 * @author yrom.
 */
public class SideBySideFrameRect extends FullFrameRect {
    private final Left mLeft;
    private final Right mRight;
    public SideBySideFrameRect(Texture2dProgram program) {
        super(program);
        mLeft = new Left(program);
        mRight = new Right(program);
    }

    @Override
    public void release(boolean doEglCleanup) {
        mLeft.release(false);
        mRight.release(false);
        super.release(doEglCleanup);
    }

    @Override
    public void changeProgram(Texture2dProgram program) {
        mLeft.changeProgram(program, false);
        mRight.changeProgram(program, false);
        super.changeProgram(program);
    }

    @Override
    public void drawFrame(int textureId, float[] texMatrix) {
        mLeft.drawFrame(textureId, texMatrix);
        mRight.drawFrame(textureId, texMatrix);
    }
}
