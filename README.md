# 🚀 MediaSuite Pro

Modern, hızlı ve kullanımı kolay **Medya İndirici, Kesici, Script/Transkript Çekici ve Format Dönüştürücü** masaüstü uygulaması.

![Java](https://img.shields.io/badge/Java-19+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![yt--dlp](https://img.shields.io/badge/yt--dlp-Powered-red.svg)
![FFmpeg](https://img.shields.io/badge/FFmpeg-Enabled-brightgreen.svg)

---

## ✨ Özellikler

### 1. 📥 Medya İndirici (Downloader)
- **Evrensel Video İndirme (MP4):** YouTube, Twitter/X, Bluesky, Facebook vb. yüzlerce siteden yüksek kalitede video indirme.
- **Ses / MP3 İndirme:** Videolardan sesi otomatik ayıklayıp yüksek kaliteli MP3 olarak kaydetme.
- **Script / Transkript Çekici (.srt & .txt):** Videoları indirmeden saniyeler içinde altyazı ve temiz metin/makale dökümlerini alma.
- **Oynatma Listesi (Playlist / Seri) Desteği:** Tek bir link ile tüm seriyi sırayla ve numaralandırılmış olarak indirme.
- **Web Sayfası Resim İndirici:** Belirtilen web sayfasındaki tüm görselleri JSoup ile toplu olarak kaydetme.

### 2. ✂️ Medya Kesici / Tıraşlayıcı (Trimmer)
- Zaman kodları (`00:01:30` veya saniye cinsinden) belirterek videoları ve sesleri kayıpsız veya istenilen formatta kesme.
- **Otomatik MP4 Dönüştürme:** Kesilen MKV/WebM gibi videoları internete ve sosyal medyaya hazır standart `.mp4` formatına dönüştürme.

### 3. 🔄 Format Dönüştürücü (Converter)
- Bilgisayarınızdaki mevcut **MKV, WebM, AVI, MOV, FLV, TS, WMV** vb. formatları tek tıkla dönüştürme:
  - 🎬 **MP4 Video (H.264 / AAC):** Web, mobil ve montaj programlarıyla %100 uyumlu (`+faststart`).
  - 🎵 **MP3 Ses (320 kbps):** Yüksek bit hızında kristal netliğinde ses.
  - 🎬 **WebM Video (VP9 / Opus)**
  - 🎵 **WAV Ses (Kayıpsız)**

---

## 🛠️ Gereksinimler & Kurulum

### Gereksinimler:
- **Java 19+** (JDK)
- **Maven 3.8+**
- Uygulama dizininde (veya sistem PATH'inde):
  - `yt-dlp.exe`
  - `ffmpeg.exe` ve `ffprobe.exe`

### Kaynak Koddan Çalıştırma:
```bash
# Depoyu klonlayın
git clone <REPO_URL>
cd yt-dlpProjekt

# Derleyin ve başlatın
mvn javafx:run
```

### JAR Olarak Paketleme:
```bash
mvn clean package
```

---

## ⚖️ Sorumluluk Reddi (Disclaimer)

Bu yazılım yalnızca **eğitim, araştırma ve kişisel arşivleme** amaçlarıyla geliştirilmiştir. 
Yazılımın kullanılmasıyla indirilen veya dönüştürülen materyallerin telif hakkı ve kullanım şartlarına uygunluğundan **tamamen son kullanıcı sorumludur**. Geliştiriciler, üçüncü taraf platformların veya telifli içeriklerin izinsiz kullanımından sorumlu tutulamaz.

---

## 📄 Lisans

Bu proje [MIT Lisansı](LICENSE) altında lisanslanmıştır.
