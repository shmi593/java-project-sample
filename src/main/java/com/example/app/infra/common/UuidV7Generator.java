package com.example.app.infra.common;

import com.fasterxml.uuid.Generators;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.uuid.UuidValueGenerator;

import java.util.UUID;

/**
 * UUIDv7 ジェネレーター
 * Hibernate の UUID 生成をカスタマイズして UUIDv7 を使用する
 */
public class UuidV7Generator implements UuidValueGenerator {

    @Override
    public UUID generateUuid(SharedSessionContractImplementor session) {
        return Generators.timeBasedEpochGenerator().generate();
    }
}
