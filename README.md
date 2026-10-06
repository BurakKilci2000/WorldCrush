# 🔤 Word Crush — Türkçe Kelime Oyunu

Android platformu için Java ile geliştirilmiş, Türkçe kelime tabanlı bir mobil oyun. Oyuncu, iki boyutlu bir grid üzerindeki komşu harfleri parmağıyla sürükleyerek anlamlı Türkçe kelimeler oluşturur. Bulunan kelimeler patlar, üstteki harfler yerçekimiyle aşağı düşer ve boşluklara yeni harfler gelir. Amaç, verilen hamle sayısı içinde en yüksek puanı toplamaktır.

> Kocaeli Üniversitesi Bilgisayar Mühendisliği — Yazılım Laboratuvarı-II, Proje II

---

## ✨ Özellikler

- **Kullanıcı profili:** İlk girişte kullanıcı adı alınır ve saklanır, ana ekrandan değiştirilebilir.
- **Üç zorluk seviyesi:** 10x10 Kolay (25 hamle), 8x8 Orta (20 hamle), 6x6 Zor (15 hamle).
- **Akıllı harf üretimi:** Türkçe harf frekanslarına göre ağırlıklı rastgele üretim ve komşuluk tabanlı sesli/sessiz dengesi.
- **Kelime garantisi:** Grid her zaman en az bir geçerli kelime içerir; kelime kalmazsa otomatik karıştırılır.
- **8 yönlü harf seçimi:** Yatay, dikey ve çapraz komşu harflerle kelime oluşturma.
- **Türkçe sözlük:** 51.148 kelimelik sözlük ile anlık kelime doğrulama.
- **Combo mekaniği:** Ana kelimenin içindeki anlamlı alt kelimeler de puana eklenir (örn. MASAL → MASA, ASA, SAL).
- **Özel güçler:** Uzun kelimeler, son harfin yerine özel bir simge bırakır.
- **Joker sistemi:** Marketten oyun içi altınla satın alınan 6 farklı joker.
- **Skor tablosu:** Genel istatistikler ve tüm oyun geçmişi.

---

## 🎮 Oyun Kuralları

### Harf Puanları

| Harf | Puan | Harf | Puan | Harf | Puan |
|------|------|------|------|------|------|
| A, E, İ, K, L, N, R, T | 1 | I, M, O, S, U | 2 | B, D, Ü, Y | 3 |
| C, Ç, Ş, Z | 4 | G, H, P | 5 | F, Ö, V | 7 |
| Ğ | 8 | J | 10 | | |

### Özel Güçler

| Kelime Uzunluğu | Simge | Etki |
|-----------------|-------|------|
| 4 harf | ⇆ | Bulunduğu satırı temizler |
| 5 harf | ✹ | Çevresindeki 3x3 alanı patlatır |
| 6 harf | ⇅ | Bulunduğu sütunu temizler |
| 7+ harf | ✪ | Çevresindeki 5x5 alanı patlatır |

### Jokerler

| Joker | Altın | Etki |
|-------|-------|------|
| 🐟 Balık | 100 | Rastgele 5 harfi yok eder |
| 🎯 Tekerlek | 200 | Seçilen harfin satır ve sütununu temizler |
| 🍭 Lolipop Kırıcı | 75 | Seçilen tek harfi yok eder |
| ✋ Serbest Değiştirme | 125 | Komşu iki harfin yerini değiştirir |
| 🎲 Harf Karıştırma | 300 | Griddeki harfleri karıştırır |
| 🎉 Parti Güçlendirici | 400 | Tüm harfleri yeniden üretir |

Her kelime denemesi (geçerli ya da geçersiz) bir hamle harcar. Hamleler bittiğinde veya oyuncu çıkış yaptığında sonuç skor tablosuna kaydedilir.

---

## 🛠️ Kullanılan Teknolojiler

- **Dil:** Java
- **Platform:** Android (Minimum SDK 24, Hedef SDK 34)
- **Veritabanı:** Room Persistence Library (SQLite)
- **Arayüz:** XML layout, RecyclerView, GridLayoutManager
- **Kalıcı depolama:** SharedPreferences (kullanıcı adı), Room (oyun kayıtları, altın, joker stokları)

---

## 🧠 Kullanılan Algoritmalar ve Yapılar

- **Ağırlıklı rastgele seçim (kümülatif):** Türkçe harf frekanslarına göre harf üretimi.
- **DFS + Backtracking:** Grid üzerindeki olası kelimelerin taranması ve kelime garantisi kontrolü.
- **HashSet:** Sözlükte O(1) ortalama sürede kelime araması.
- **Yerçekimi algoritması:** Patlatılan hücrelerin sütun bazında doldurulması.
- **Substring taraması:** Combo için alt kelimelerin bulunması.
- **Singleton deseni:** Sözlük ve veritabanı için tek örnek kullanımı.

---

## 📁 Proje Yapısı

```
app/src/main/java/com/worldcrush/game/
├── activity/      Ekranlar (Splash, Grid/Hamle seçimi, Oyun, Skor, Market)
├── adapter/       RecyclerView adaptörleri (GridAdapter, GameRecordAdapter)
├── database/      Room veritabanı ve DAO (AppDatabase, GameDao)
├── game/          Oyun motoru (GridEngine)
├── model/         Veri sınıfları (Cell, GameRecord, UserProfile, Joker)
├── util/          Yardımcı sınıflar (LetterGenerator, WordDictionary, ComboHelper)
└── MainActivity   Ana ekran

app/src/main/assets/kelimeler.txt   Türkçe kelime listesi
```

---

## 🚀 Kurulum ve Çalıştırma

1. Depoyu klonlayın:
   ```bash
   git clone https://github.com/BurakKilci2000/WorldCrush.git
   ```
2. Android Studio'da **File → Open** ile proje klasörünü açın.
3. Gradle senkronizasyonunun bitmesini bekleyin.
4. Bir emülatör (önerilen: Pixel 4, API 30, x86_64) veya gerçek cihaz seçip **Run** butonuna basın.

> **Not:** Proje klasörünün yolunda Türkçe karakter (ı, ş, ğ vb.) bulunursa Gradle derleme hatası verebilir. Projeyi `C:\Projeler\` gibi Türkçe karakter içermeyen bir konuma taşıyın.

---

## 📄 Proje Raporu

IEEE formatında hazırlanan proje raporu: [WordCrush_Rapor.pdf](WordCrush_Rapor.pdf)

---

## 👤 Geliştirici

**Burak Kılcı** — Bilgisayar Mühendisliği Öğrencisi
GitHub: [@BurakKilci2000](https://github.com/BurakKilci2000)
