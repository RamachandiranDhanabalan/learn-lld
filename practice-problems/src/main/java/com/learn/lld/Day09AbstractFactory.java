package com.learn.lld;

/**
 * Day 09 Practice Problem — Media Player using Abstract Factory
 *
 * Problem: MediaPlayer had if-else for mp3/flac/mp4, mixed creation + playback,
 * and coupled to concrete decoders/outputs.
 *
 * Solution: Abstract Factory with ISP-split factory interfaces.
 * - AudioMediaFactory (all formats implement)
 * - VideoMediaFactory (only MP4 implements)
 *
 * Grid:
 *               | AudioDecoder | VideoDecoder | AudioOutput | VideoOutput |
 *    MP3        | Mp3Decoder   |      -       | Speaker     |      -      |
 *    FLAC       | FlacDecoder  |      -       | Speaker     |      -      |
 *    MP4        | Mp4Decoder   | Mp4Decoder   | Speaker     | Screen      |
 *
 * Factories: Mp3MediaFactory, FlacMediaFactory, Mp4MediaFactory
 * Factory interfaces: AudioMediaFactory (all), VideoMediaFactory (MP4 only)
 */

// ══════════ PRODUCTS (interfaces) ══════════

interface AudioDecoder {
    AudioStream decode(String filePath);
}

interface VideoDecoder {
    VideoStream decode(String filePath);
}

interface AudioOutput {
    void play(AudioStream stream);
}

interface VideoOutput {
    void display(VideoStream stream);
}

// ══════════ PRODUCT DATA ══════════

class AudioStream {
    private final String source;
    AudioStream(String source) { this.source = source; }
    String getSource() { return source; }
}

class VideoStream {
    private final String source;
    VideoStream(String source) { this.source = source; }
    String getSource() { return source; }
}

// ══════════ CONCRETE PRODUCTS ══════════

class Mp3Decoder implements AudioDecoder {
    private int bitrate;
    void setBitrate(int bitrate) { this.bitrate = bitrate; }
    public AudioStream decode(String file) {
        System.out.println("Decoding MP3 at " + bitrate + "kbps: " + file);
        return new AudioStream(file);
    }
}

class FlacDecoder implements AudioDecoder {
    private boolean lossless;
    void setLossless(boolean lossless) { this.lossless = lossless; }
    public AudioStream decode(String file) {
        System.out.println("Decoding FLAC (lossless=" + lossless + "): " + file);
        return new AudioStream(file);
    }
}

class Mp4Decoder implements AudioDecoder, VideoDecoder {
    private String resolution;
    void setResolution(String resolution) { this.resolution = resolution; }

    public AudioStream decode(String file) {
        System.out.println("Extracting audio from MP4: " + file);
        return new AudioStream(file);
    }

    public VideoStream decodeVideo(String file) {
        System.out.println("Decoding video at " + resolution + ": " + file);
        return new VideoStream(file);
    }

    // VideoDecoder interface method
    // Note: Java doesn't allow same method name with different return types
    // Using decodeVideo for video, decode for audio
}

class SpeakerOutput implements AudioOutput {
    public void play(AudioStream stream) {
        System.out.println("Playing audio: " + stream.getSource());
    }
}

class ScreenOutput implements VideoOutput {
    public void display(VideoStream stream) {
        System.out.println("Displaying video: " + stream.getSource());
    }
}

// ══════════ FACTORY INTERFACES (ISP-split) ══════════

// All families support audio
interface AudioMediaFactory {
    AudioDecoder createAudioDecoder();
    AudioOutput createAudioOutput();
}

// Only MP4 family supports video
interface VideoMediaFactory {
    VideoDecoder createVideoDecoder();
    VideoOutput createVideoOutput();
}

// ══════════ CONCRETE FACTORIES ══════════

class Mp3MediaFactory implements AudioMediaFactory {
    @Override
    public AudioDecoder createAudioDecoder() {
        Mp3Decoder decoder = new Mp3Decoder();
        decoder.setBitrate(320);
        return decoder;
    }

    @Override
    public AudioOutput createAudioOutput() {
        return new SpeakerOutput();
    }
}

class FlacMediaFactory implements AudioMediaFactory {
    @Override
    public AudioDecoder createAudioDecoder() {
        FlacDecoder decoder = new FlacDecoder();
        decoder.setLossless(true);
        return decoder;
    }

    @Override
    public AudioOutput createAudioOutput() {
        return new SpeakerOutput();
    }
}

class Mp4MediaFactory implements AudioMediaFactory, VideoMediaFactory {
    @Override
    public AudioDecoder createAudioDecoder() {
        Mp4Decoder decoder = new Mp4Decoder();
        decoder.setResolution("1080p");
        return decoder;
    }

    @Override
    public AudioOutput createAudioOutput() {
        return new SpeakerOutput();
    }

    @Override
    public VideoDecoder createVideoDecoder() {
        Mp4Decoder decoder = new Mp4Decoder();
        decoder.setResolution("1080p");
        return decoder;
    }

    @Override
    public VideoOutput createVideoOutput() {
        return new ScreenOutput();
    }
}

// ══════════ CLIENTS ══════════

class AudioPlayer {
    private final AudioMediaFactory factory;

    AudioPlayer(AudioMediaFactory factory) {
        this.factory = factory;
    }

    void play(String filePath) {
        AudioDecoder decoder = factory.createAudioDecoder();
        AudioStream stream = decoder.decode(filePath);
        AudioOutput output = factory.createAudioOutput();
        output.play(stream);
    }
}

class VideoPlayer {
    private final AudioMediaFactory audioFactory;
    private final VideoMediaFactory videoFactory;

    VideoPlayer(AudioMediaFactory audioFactory, VideoMediaFactory videoFactory) {
        this.audioFactory = audioFactory;
        this.videoFactory = videoFactory;
    }

    void play(String filePath) {
        // Audio
        AudioDecoder aDecoder = audioFactory.createAudioDecoder();
        AudioStream aStream = aDecoder.decode(filePath);
        audioFactory.createAudioOutput().play(aStream);

        // Video
        VideoDecoder vDecoder = videoFactory.createVideoDecoder();
        VideoStream vStream = vDecoder.decodeVideo(filePath);
        videoFactory.createVideoOutput().display(vStream);
    }
}

// ══════════ WIRING (Simple Factory to select the right Abstract Factory) ══════════

class MediaFactorySelector {
    static AudioMediaFactory getAudioFactory(String extension) {
        return switch (extension) {
            case "mp3" -> new Mp3MediaFactory();
            case "flac" -> new FlacMediaFactory();
            case "mp4" -> new Mp4MediaFactory();
            default -> throw new IllegalArgumentException("Unsupported format: " + extension);
        };
    }

    static VideoMediaFactory getVideoFactory(String extension) {
        if (extension.equals("mp4")) return new Mp4MediaFactory();
        throw new IllegalArgumentException("No video support for: " + extension);
    }
}

// ══════════ PLAYLIST (SRP — separate from playback) ══════════

class PlaylistService {
    void display(java.util.List<String> files) {
        for (int i = 0; i < files.size(); i++) {
            System.out.println((i + 1) + ". " + files.get(i));
        }
    }
}
