package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TranscriptDownloader {
    public static void main(String[] args) {
        String link = (args != null && args.length > 0)
                ? args[0]
                : "https://www.youtube.com/watch?v=jNQXAC9IVRw";

        try {
            Path downloadDir = Paths.get("transcripts");
            Files.createDirectories(downloadDir);

            Path ytDlpPath = Paths.get("yt-dlp.exe").toAbsolutePath().normalize();
            String normalizedLink = normalizeYouTubeLink(link);

            System.out.println("Script/Transkript indiriliyor: " + normalizedLink);

            ProcessBuilder pb = new ProcessBuilder(
                    List.of(
                            ytDlpPath.toString(),
                            "--skip-download",
                            "--write-subs",
                            "--write-auto-subs",
                            "--sub-langs", "tr,en,.*-orig",
                            "--convert-subs", "srt",
                            "--ignore-errors",
                            "-o", "%(playlist_index&{:02d} - |)s%(title)s [%(id)s].%(ext)s",
                            "-P", downloadDir.toString(),
                            normalizedLink
                    )
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("yt-dlp işlemi bitti. Çıkış kodu: " + exitCode);

            // SRT dosyalarını temiz TXT formatına dönüştür
            try (var stream = Files.list(downloadDir)) {
                List<Path> srtFiles = stream.filter(p -> p.toString().toLowerCase().endsWith(".srt")).toList();
                for (Path srt : srtFiles) {
                    convertSrtToCleanText(srt);
                }
            }

            System.out.println("Tüm scriptler '" + downloadDir.toAbsolutePath() + "' klasörüne kaydedildi.");

        } catch (Exception e) {
            System.err.println("Script indirme başarısız: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean convertSrtToCleanText(Path srtFile) {
        try {
            String fileName = srtFile.getFileName().toString();
            int dot = fileName.lastIndexOf('.');
            String baseName = (dot > 0) ? fileName.substring(0, dot) : fileName;
            Path txtPath = srtFile.getParent().resolve(baseName + ".txt");

            if (Files.exists(txtPath) && Files.getLastModifiedTime(txtPath).toMillis() >= Files.getLastModifiedTime(srtFile).toMillis()) {
                return false;
            }

            List<String> lines = Files.readAllLines(srtFile, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            String lastLine = "";

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                if (trimmed.matches("^\\d+$")) continue;
                if (trimmed.contains("-->")) continue;
                trimmed = trimmed.replaceAll("<[^>]*>", "").trim();
                if (trimmed.isEmpty()) continue;

                if (!trimmed.equalsIgnoreCase(lastLine)) {
                    if (sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(trimmed);
                    lastLine = trimmed;
                }
            }

            Files.writeString(txtPath, sb.toString(), StandardCharsets.UTF_8);
            System.out.println("Temiz metin oluşturuldu: " + txtPath.getFileName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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
