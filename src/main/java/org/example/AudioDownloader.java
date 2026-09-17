package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AudioDownloader {
    public static void main(String[] args) {

        // Link argümanla verilebilir; verilmezse örnek bir link kullanılır
        String link = (args != null && args.length > 0) ? args[0] : "https://x.com/Fehim_Isik/status/2039809777421656416?s=20";
        try {
            Path downloadDir = Paths.get("audio_downloads");
            Files.createDirectories(downloadDir);

            Path ytDlpPath = Paths.get("yt-dlp.exe").toAbsolutePath().normalize();
            String normalizedLink = normalizeYouTubeLink(link);

            // yt-dlp'yi mp3 olarak çıkarmak için gerekli parametreler
            ProcessBuilder pb = new ProcessBuilder(
                    List.of(
                            ytDlpPath.toString(),
                            "--extract-audio",
                            "--audio-format", "mp3",
                            "--audio-quality", "0",
                            "-P", downloadDir.toString(),
                            normalizedLink
                    )
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("İşlem bitti. Çıkış kodu: " + exitCode);

        } catch (Exception e) {
            System.err.println("MP3 indirme başarısız: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Shorts linklerini normal watch?v= formatına çevirir
    private static String normalizeYouTubeLink(String link) {
        String shortsPrefix = "https://www.youtube.com/shorts/";
        if (link != null && link.startsWith(shortsPrefix)) {
            String videoId = link.substring(shortsPrefix.length());
            int queryIndex = videoId.indexOf('?');
            if (queryIndex >= 0) {
                videoId = videoId.substring(0, queryIndex);
            }

            int slashIndex = videoId.indexOf('/');
            if (slashIndex >= 0) {
                videoId = videoId.substring(0, slashIndex);
            }

            return "https://www.youtube.com/watch?v=" + videoId;
        }

        return link;
    }
}

