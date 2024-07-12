package org.m2sec.core.common;

import jdk.jfr.Timespan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@ToString
@AllArgsConstructor
public class Tuple<A, B> {
    private A first;
    private B second;

}
