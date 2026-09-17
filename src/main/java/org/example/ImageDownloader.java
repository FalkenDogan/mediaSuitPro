package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageDownloader {

    public static void main(String[] args) {
        // Örnek olarak bir Bluesky veya haber sitesi linki
        String url = "https://bsky.app/profile/steppebirdsmove.bsky.social";
        String saveDir = "resim_indirilenler/";

        try {
            // 1. Klasör yoksa oluştur
            Files.createDirectories(Paths.get(saveDir));

            // 2. Sayfaya bağlan ve HTML'i çek (Tarayıcı gibi görünmek için User-Agent ekliyoruz)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            // 3. Sayfadaki tüm <img> etiketlerini bul
            Elements images = doc.select("img");

            System.out.println("Bulunan resim sayısı: " + images.size());

            int count = 1;
            for (Element img : images) {
                String imgUrl = img.absUrl("src"); // Tam URL'yi al

                if (!imgUrl.isEmpty()) {
                    System.out.println("İndiriliyor: " + imgUrl);
                    downloadFile(imgUrl, saveDir + "resim_" + count + ".jpg");
                    count++;
                }
            }

        } catch (IOException e) {
            System.err.println("Hata oluştu: " + e.getMessage());
        }
    }

    // Dosyayı URL'den indirip diske yazan yardımcı metod
    private static void downloadFile(String fileUrl, String fileName) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.err.println("Dosya indirilemedi: " + fileUrl);
        }
    }
}