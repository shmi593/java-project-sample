package com.example.app.config;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDComparator;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.UUID;

/**
 * UUID 関連の Bean 設定
 * <p>
 * 注意: Java の標準 UUID.compareTo() は符号付き比較を行うため、
 * UUIDv7 のような時系列ソートを期待する場合は正しく動作しない。
 * UUID のソートには必ず uuidComparator Bean を使用すること。
 */
@Configuration
public class UuidConfig {

    /**
     * UUIDv7 (Time-based Epoch) ジェネレーター
     *
     * @return TimeBasedEpochGenerator インスタンス
     */
    @Bean
    public TimeBasedEpochGenerator timeBasedEpochGenerator() {
        return Generators.timeBasedEpochGenerator();
    }

    /**
     * UUID 比較用 Comparator
     * <p>
     * Java 標準の UUID.compareTo() は内部で符号付き64ビット比較を行うため、
     * MSB の最上位ビットが1の場合に期待通りの順序にならない。
     * UUIDComparator は符号なし比較を行い、正しい辞書順序を保証する。
     *
     * @return UUID 用の正しい Comparator
     */
    @Bean
    public Comparator<UUID> uuidComparator() {
        return new UUIDComparator();
    }
}
