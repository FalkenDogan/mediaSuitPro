package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AudioTrimmer {
    public static void main(String[] args) {
        // ==========================================
        // AYARLARINIZI BURADAN DEĞİŞTİREBİLİRSİNİZ
        // Saat:dakika:saniye formatını (HH:MM:SS) kullanabilirsiniz
        // Örnek: "00:38:36" veya sadece saniye olarak "2300"
        // ==========================================
        String dosyaYolu = "video_downloads\\bestepe.webm"; // Videonun bilgisayarınızdaki tam yolu
        String baslangic = "00:35:49"; //  Başlangıç zaman kodu (HH:MM:SS)
        String bitis = "00:36:21";     //  Bitiş zaman kodu (HH:MM:SS)
        double saniyedenItibaren = timeToSeconds(baslangic);                           // Kesmeye başlanacak saniye
        double saniyeyeKadar = timeToSeconds(bitis);                               // Kesmenin biteceği saniye
        // ==========================================

        Path inputPath = Paths.get(dosyaYolu);
        if (!Files.exists(inputPath) || Files.isDirectory(inputPath)) {
            System.err.println("HATA: Belirttiğiniz video dosyası bulunamadı: " + inputPath);
            return;
        }

        if (saniyedenItibaren < 0) saniyedenItibaren = 0;
        if (saniyeyeKadar <= saniyedenItibaren) {
            System.err.println("HATA: 'saniyeyeKadar' değeri (" + saniyeyeKadar + "), 'saniyedenItibaren' değerinden (" + saniyedenItibaren + ") büyük olmalıdır.");
            return;
        }

        // FFmpeg için alınacak toplam süreyi hesapla (Bitiş - Başlangıç)
        double duration = saniyeyeKadar - saniyedenItibaren;

        try {
            // Çıktı dosyasını girdi dosyasıyla aynı klasörde oluşturma ayarları
            Path parent = inputPath.getParent();
            if (parent == null) parent = Paths.get(".");

            String baseName = inputPath.getFileName().toString();
            int dot = baseName.lastIndexOf('.');
            String nameNoExt = (dot > 0) ? baseName.substring(0, dot) : baseName;
            String ext = (dot > 0) ? baseName.substring(dot) : "";

            // Yeni dosya adı örn: ornek_video_trimmed.mp4
            String outName = nameNoExt + "_trimmed" + ext;
            Path outPath = parent.resolve(outName);

            // Eğer aynı isimde bir dosya zaten varsa, ismin sonuna tarih/saat damgası ekler
            if (Files.exists(outPath)) {
                String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                outName = nameNoExt + "_trimmed_" + ts + ext;
                outPath = parent.resolve(outName);
            }

            // FFmpeg executable dosyasının projenin kök dizininde olduğunu varsayıyoruz
            Path ffmpegPath = Paths.get("ffmpeg.exe").toAbsolutePath().normalize();

            // FFmpeg Komut Yapısı
            List<String> cmd = List.of(
                    ffmpegPath.toString(),
                    "-y",
                    "-ss", String.valueOf(saniyedenItibaren),
                    "-i", inputPath.toString(),
                    "-t", String.valueOf(duration),
                    "-c", "copy", // Yeniden encode etmeden hızlıca kopyalar
                    outPath.toString()
            );

            System.out.println("FFmpeg Başlatılıyor... Komut: " + String.join(" ", cmd));

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process ff = pb.start();

            // FFmpeg konsol çıktılarını Java konsoluna yazdırır
            BufferedReader reader = new BufferedReader(new InputStreamReader(ff.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = ff.waitFor();
            if (exitCode == 0) {
                System.out.println("\n[BAŞARILI] Video başarıyla kesildi!");
                System.out.println("Yeni Dosya Konumu: " + outPath.toAbsolutePath());
            } else {
                System.err.println("\n[HATA] FFmpeg hata kodu döndürdü: " + exitCode);
            }

        } catch (Exception e) {
            System.err.println("İşlem sırasında beklenmedik bir hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static double timeToSeconds(String time) {
        if (time == null || time.isBlank()) return 0;
        String t = time.trim();
        // If it's only digits, treat as seconds
        if (t.matches("^\\d+$")) {
            return Double.parseDouble(t);
        }
        String[] parts = t.split(":");
        int len = parts.length;
        double seconds = 0;
        try {
            if (len == 3) { // HH:MM:SS
                seconds += Integer.parseInt(parts[0]) * 3600;
                seconds += Integer.parseInt(parts[1]) * 60;
                seconds += Double.parseDouble(parts[2]);
            } else if (len == 2) { // MM:SS
                seconds += Integer.parseInt(parts[0]) * 60;
                seconds += Double.parseDouble(parts[1]);
            } else if (len == 1) { // SS
                seconds += Double.parseDouble(parts[0]);
            } else {
                throw new IllegalArgumentException("Geçersiz zaman formatı: " + time);
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Zaman kodu sayıya dönüştürülemedi: " + time, ex);
        }
        return seconds;
    }
}
