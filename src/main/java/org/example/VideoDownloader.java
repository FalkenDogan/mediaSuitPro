package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VideoDownloader {
    public static void main(String[] args) {

        String link = "https://www.youtube.com/watch?v=XPvPO1MxCMQ"; // İndirmek istediğiniz videonun linki
        try {
            Path downloadDir = Paths.get("video_downloads");
            Files.createDirectories(downloadDir);
            Path ytDlpPath = Paths.get("yt-dlp.exe").toAbsolutePath().normalize();
            String normalizedLink = normalizeYouTubeLink(link);

            // Çalıştırılacak komut (yt-dlp yolu ve indirme linki)
            //String command = "./yt-dlp.exe -f bestvideo+bestaudio --merge-output-format mp4 https://www.youtube.com/watch?v=ORNEK_LINK";
            //String command = "./yt-dlp.exe -f \\\"b\\\" --merge-output-format mp4 "+link;
            // En basit ve en uyumlu komut formatı
            ProcessBuilder pb = new ProcessBuilder(
                    List.of(ytDlpPath.toString(), "-P", downloadDir.toString(), normalizedLink)
            );
            pb.redirectErrorStream(true); // Hataları da ana akışta gör

            Process process = pb.start();

            // Çıktıyı terminale yazdıralım (ilerlemeyi görmek için)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("İşlem bitti. Çıkış kodu: " + exitCode);

        } catch (Exception e) {
            System.err.println("Download fehlgeschlagen: " + e.getMessage());
        }
    }

    private static String normalizeYouTubeLink(String link) {
        String shortsPrefix = "https://www.youtube.com/shorts/";
        if (link.startsWith(shortsPrefix)) {
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