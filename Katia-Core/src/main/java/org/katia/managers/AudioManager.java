package org.katia.managers;

import lombok.Data;
import org.katia.Logger;
import org.katia.game.Game;
import org.katia.gfx.resources.Audio;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;

import static org.katia.factory.AudioFactory.checkALCError;
import static org.katia.factory.AudioFactory.checkALError;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memFree;

@Data
public class AudioManager {

    private Game game;

    private long device;
    private boolean useTLC;
    private ALCapabilities caps;
    private long context;

    /**
     * Audio Manager Constructor.
     * @param game Game.
     */
    public AudioManager(Game game) {
        Logger.log(Logger.Type.INFO, "Audio Manager Constructor ...");
        this.game = game;

        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open an OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        if (!deviceCaps.OpenALC10) {
            throw new IllegalStateException();
        }
        System.out.println("OpenALC10  : " + deviceCaps.OpenALC10);
        System.out.println("OpenALC11  : " + deviceCaps.OpenALC11);
        System.out.println("ALC_EXT_EFX: " + deviceCaps.ALC_EXT_EFX);

        if (deviceCaps.OpenALC11) {
            List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
            if (devices == null) {
                checkALCError(NULL);
            } else {
                for (int i = 0; i < devices.size(); i++) {
                    System.out.println(i + ": " + devices.get(i));
                }
            }
        }

        String defaultDeviceSpecifier = Objects.requireNonNull(alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER));
        System.out.println("Default device: " + defaultDeviceSpecifier);

        System.out.println("ALC device specifier: " + alcGetString(device, ALC_DEVICE_SPECIFIER));

        context = alcCreateContext(device, (IntBuffer)null);
        checkALCError(device);

        useTLC = deviceCaps.ALC_EXT_thread_local_context && alcSetThreadContext(context);
        if (!useTLC) {
            if (!alcMakeContextCurrent(context)) {
                throw new IllegalStateException();
            }
        }
        checkALCError(device);

        caps = AL.createCapabilities(deviceCaps, MemoryUtil::memCallocPointer);

        System.out.println("ALC_FREQUENCY     : " + alcGetInteger(device, ALC_FREQUENCY) + "Hz");
        System.out.println("ALC_REFRESH       : " + alcGetInteger(device, ALC_REFRESH) + "Hz");
        System.out.println("ALC_SYNC          : " + (alcGetInteger(device, ALC_SYNC) == ALC_TRUE));
        System.out.println("ALC_MONO_SOURCES  : " + alcGetInteger(device, ALC_MONO_SOURCES));
        System.out.println("ALC_STEREO_SOURCES: " + alcGetInteger(device, ALC_STEREO_SOURCES));
    }

    /**
     * Play audio.
     * @param path Relative path to audio file.
     */
    public void play(String path) {
        Audio audio = game.getResourceManager().getAudios().get(path);
        if (audio == null) {
            Logger.log(Logger.Type.ERROR, "Audio not loaded:", path);
            return;
        }
        alSourcePlay(audio.getSource());
        checkALError();
    }

    /**
     * Stop audio.
     * @param path Relative path to audio file.
     */
    public void stop(String path) {
        Audio audio = game.getResourceManager().getAudios().get(path);
        if (audio == null) {
            Logger.log(Logger.Type.ERROR, "Audio not loaded:", path);
            return;
        }
        alSourceStop(audio.getSource());
        checkALError();
    }

    /**
     * Dispose of audio manager.
     */
    public void dispose() {
        alcMakeContextCurrent(NULL);
        if (useTLC) {
            AL.setCurrentThread(null);
        } else {
            AL.setCurrentProcess(null);
        }
        memFree(caps.getAddressBuffer());

        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
