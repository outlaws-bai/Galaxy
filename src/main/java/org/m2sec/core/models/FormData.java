package org.m2sec.core.models;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@NoArgsConstructor
public class FormData<T> extends Parameters<T> {

    public FormData(Map<String, List<T>> multiMap) {
        super(multiMap);
    }
}
