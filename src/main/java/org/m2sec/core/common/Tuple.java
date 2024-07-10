package org.m2sec.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@AllArgsConstructor
public class Tuple<A, B> {
    private A first;
    private B second;

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
