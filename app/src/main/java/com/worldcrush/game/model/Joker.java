package com.worldcrush.game.model;

public class Joker {
    public JokerType type;
    public int count;

    public Joker(JokerType type, int count) {
        this.type = type;
        this.count = count;
    }

    public enum JokerType {
        FISH("🐟 Balık", 100, "Rastgele 5 harfi yok eder"),
        WHEEL("🎯 Tekerlek", 200, "Seçilen harfin satır ve sütunundaki tüm harfleri yok eder"),
        LOLLIPOP("🍭 Lolipop Kırıcı", 75, "Seçilen tek bir harfi yok eder"),
        SWAP("✋ Serbest Değiştirme", 125, "Komşu iki harfin yerini değiştirir"),
        SHUFFLE("🎲 Harf Karıştırma", 300, "Gridi karıştırır"),
        PARTY("🎉 Parti Güçlendirici", 400, "Tüm harfleri yeniler");

        public final String displayName;
        public final int cost;
        public final String description;

        JokerType(String displayName, int cost, String description) {
            this.displayName = displayName;
            this.cost = cost;
            this.description = description;
        }
    }
}
