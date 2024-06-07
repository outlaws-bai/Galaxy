package org.m2sec.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
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
