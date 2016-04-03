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

import android.os.Handler;

import com.android.grafika.gles.FullFrameRect;
import com.android.grafika.gles.Texture2dProgram;

/**
 * Use {@link SideBySideFrameRect} to render video frame.
 * @author yrom.
 */
public class IjkVRRender extends GLTextureRender {
    public IjkVRRender(Handler handler) {
        super(handler);
    }

    @Override
    protected FullFrameRect onCreateFrameRect() {
        return new SideBySideFrameRect(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
    }
}
