package org.katia.factory;

import org.katia.Logger;
import org.katia.gfx.resources.Audio;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.katia.FileSystem.ioResourceToByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioFactory {

    /**
     * Create audio from audio file.
     * @param audioFile Path to audio file.
     * @return Audio
     */
    public static Audio createAudio(String audioFile) {
        Logger.log(Logger.Type.INFO, "Creating audio from:", audioFile);

        int buffer = alGenBuffers();
        checkALError();

        int source = alGenSources();
        checkALError();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = readVorbis(audioFile, 32 * 1024, info);

            alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
            checkALError();
        }

        alSourcei(source, AL_BUFFER, buffer);
        checkALError();

        return new Audio(buffer, source);
    }

    /**
     * Dispose of provided audio.
     * @param audio Audio.
     */
    public static void dispose(Audio audio) {
        alDeleteSources(audio.getSource());
        checkALError();
        alDeleteBuffers(audio.getBuffer());
        checkALError();
    }

    /**
     * Read OGG audio file.
     * @param resource Path to ogg audio file.
     * @param bufferSize Buffer size.
     * @param info Info.
     * @return ShortBuffer
     */
    private static ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) {
        ByteBuffer vorbis;
        try {
            vorbis = ioResourceToByteBuffer(resource, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoder = stb_vorbis_open_memory(vorbis, error, null);
        if (decoder == NULL) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
        }

        stb_vorbis_get_info(decoder, info);

        int channels = info.channels();

        ShortBuffer pcm = BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);

        stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        stb_vorbis_close(decoder);

        return pcm;
    }

    /**
     * Check AL error.
     */
    public static void checkALError() {
        int err = alGetError();
        if (err != AL_NO_ERROR) {
            throw new RuntimeException(alGetString(err));
        }
    }

    /**
     * Check ALC error.
     * @param device Device.
     */
    public static void checkALCError(long device) {
        int err = alcGetError(device);
        if (err != ALC_NO_ERROR) {
            throw new RuntimeException(alcGetString(device, err));
        }
    }
}
