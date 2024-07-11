package org.m2sec.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description: Upload File
 */
@Getter
@Setter
@AllArgsConstructor
public class UploadFile {
    private String filename;
    private Headers headers;
    private byte[] content;
}
