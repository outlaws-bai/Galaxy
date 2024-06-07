package org.m2sec.common.models;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/4/24 15:42
 * @description:
 */
@NoArgsConstructor
public class FormDatas<T> extends Parameters<T> {
    public FormDatas(Map<String, List<T>> map) {
        super(map);
    }
}
