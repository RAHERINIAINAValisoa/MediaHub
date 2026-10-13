package com.mediahub.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

/**
 * Vérifie le contenu réel d'un fichier (octets d'en-tête) en plus de son extension,
 * afin d'empêcher qu'un fichier quelconque soit renommé en .jpg ou .mp4.
 */
public final class FileSniffer {

    private FileSniffer() {
    }

    public static boolean matches(InputStream in, String extension) throws IOException {
        byte[] h = in.readNBytes(16);
        if (h.length < 12) {
            return false;
        }
        return switch (extension) {
            case "jpg", "jpeg" -> (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF;
            case "png" -> (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
            case "webp" -> ascii(h, 0, "RIFF") && ascii(h, 8, "WEBP");
            case "wav" -> ascii(h, 0, "RIFF") && ascii(h, 8, "WAVE");
            case "mp4" -> ascii(h, 4, "ftyp");
            case "webm" -> (h[0] & 0xFF) == 0x1A && (h[1] & 0xFF) == 0x45 && (h[2] & 0xFF) == 0xDF && (h[3] & 0xFF) == 0xA3;
            case "mp3" -> ascii(h, 0, "ID3") || ((h[0] & 0xFF) == 0xFF && (h[1] & 0xE0) == 0xE0);
            default -> false;
        };
    }

    private static boolean ascii(byte[] data, int offset, String expected) {
        byte[] exp = expected.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        return Arrays.equals(Arrays.copyOfRange(data, offset, offset + exp.length), exp);
    }

    public static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "webp");
    public static final Set<String> VIDEO_EXT = Set.of("mp4", "webm");
    public static final Set<String> AUDIO_EXT = Set.of("mp3", "wav");
}
