package org.m2sec.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author: outlaws-bai
 * @date: 2024/4/24 15:42
 * @description: Upload File
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class UploadFile {
    private String filename;
    private Headers headers;
    private byte[] content;
}
