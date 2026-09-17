# 🚀 MediaSuite Pro

A modern, fast, and feature-rich desktop application for **Media Downloading, Trimming, Script/Transcript Extraction, and Format Conversion**.

![Java](https://img.shields.io/badge/Java-19+-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![yt--dlp](https://img.shields.io/badge/yt--dlp-Powered-red.svg)
![FFmpeg](https://img.shields.io/badge/FFmpeg-Enabled-brightgreen.svg)

---

## ✨ Key Features

### 1. 📥 Media Downloader
- **Universal Video Download (MP4):** Download high-quality videos from YouTube, Twitter/X, Bluesky, Facebook, and hundreds of other supported platforms. All downloads are automatically remuxed into web-friendly `.mp4` (H.264 / AAC) format.
- **Audio / MP3 Extraction:** Extract audio directly and encode to high-bitrate MP3.
- **Script / Transcript Extractor (.srt & .txt):** Download auto-generated or manual subtitles without downloading the video itself, and automatically generate clean, timestamp-free text transcripts (`.txt`) alongside `.srt` files.
- **Playlist & Series Support:** Seamlessly process full playlists and series in indexed order.
- **Web Page Image Scraper:** Extract and download all images from any webpage using Jsoup.

### 2. ✂️ Media Cutter (Trimmer)
- Trim video and audio with precision using timestamps (`hh:mm:ss` or seconds).
- **Auto MP4 Re-encoding:** Convert trimmed clips directly to universal MP4 format for immediate web and mobile compatibility, or use lossless stream copy.

### 3. 🔄 Format Converter
- Convert local video/audio files (MKV, WebM, AVI, MOV, FLV, TS, WMV, etc.) into standard formats:
  - 🎬 **MP4 Video (H.264 / AAC):** 100% compatible across all browsers, mobile devices, and video editors (`+faststart` web-optimized).
  - 🎵 **MP3 Audio (320 kbps):** High-bitrate crystal clear audio.
  - 🎬 **WebM Video (VP9 / Opus)**
  - 🎵 **WAV Audio (Lossless)**

---

## 🛠️ Prerequisites & Setup

### Prerequisites
- **Java 19+** (JDK)
- **Maven 3.8+**
- Executables placed in the application directory or available in system `PATH`:
  - `yt-dlp.exe`
  - `ffmpeg.exe` and `ffprobe.exe`

### Running from Source
```bash
# Clone the repository
git clone https://github.com/FalkenDogan/mediaSuitPro.git
cd mediaSuitPro

# Build and run with JavaFX
mvn javafx:run
```

### Packaging into JAR
```bash
mvn clean package
```

---

## ⚖️ Legal Disclaimer

This software is developed strictly for **educational, research, and personal archiving purposes**.
The end user is solely responsible for ensuring compliance with copyright laws, intellectual property rights, and terms of service of third-party platforms. The developers assume no liability for misuse of this tool.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
