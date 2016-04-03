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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.widget.Toast;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author yrom.
 */
public class VRPlayerActivity extends Activity {
    private static final String TAG = "VRPlayer";
    private GLSurfaceView mGLView;
    private GLTextureRender mRenderer;
    private IjkMediaPlayer mMediaPlayer;
    private boolean mSurfaceTextureReady;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Uri uri = getIntent().getData();
        if (uri == null) {
            Toast.makeText(this, "no media file to open!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activitiy_vr_player);
        mGLView = (GLSurfaceView) findViewById(R.id.surface);
        mGLView.setEGLContextClientVersion(2); //GLES 2.0
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    prepareMediaPlayerIfReady();
                    return;
                } else if (msg.what == 2) {
                    mGLView.requestRender();
                }
                super.handleMessage(msg);
            }
        };
        // TODO: implements your TextureRender
        mRenderer = new IjkVRRender(mHandler);
        mGLView.setRenderer(mRenderer);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            initMediaPlayer(uri);
        }
    }

    private void prepareMediaPlayerIfReady() {
        if (mMediaPlayer != null) {
            SurfaceTexture surfaceTexture = mRenderer.getSurfaceTexture();
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    mHandler.sendEmptyMessage(2);
                }
            });
            mMediaPlayer.setSurface(new Surface(surfaceTexture));
            mMediaPlayer.prepareAsync();
        } else {
            mSurfaceTextureReady = true;
        }
    }


    private void initMediaPlayer(Uri uri) {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        } else {
            mMediaPlayer = new IjkMediaPlayer();
            mMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer mp) {
                    mGLView.setKeepScreenOn(true);
                    mp.start();
                    mp.getVideoWidth();
                }
            });
            mMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer mp) {
                    mGLView.setKeepScreenOn(false);
                }
            });
        }
        try {
            mMediaPlayer.setDataSource(this, uri);
        } catch (IOException e) {
            Toast.makeText(this, "cannot open uri:" + uri, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (mSurfaceTextureReady) {
            prepareMediaPlayerIfReady();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMediaPlayer(getIntent().getData());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
        mGLView.onPause();
        mGLView.setKeepScreenOn(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        if (mMediaPlayer != null) {
            mGLView.setKeepScreenOn(true);
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                // Tell the renderer that it's about to be paused so it can clean up.
                mRenderer.requestPause();
            }
        });
    }

}
