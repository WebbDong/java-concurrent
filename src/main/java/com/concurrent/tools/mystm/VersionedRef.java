package com.concurrent.tools.mystm;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 带版本号的对象引用
 * @param <T>
 */
@Data
@AllArgsConstructor
public class VersionedRef<T> {

    private final T value;

    private final long version;

}
