package org.m2sec.burp.intruder;

import burp.api.montoya.intruder.GeneratedPayload;
import burp.api.montoya.intruder.IntruderInsertionPoint;
import burp.api.montoya.intruder.PayloadGenerator;

import java.util.ArrayList;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class CommonPayloadGenerator implements PayloadGenerator {

    public ArrayList<String> payloadList;
    private int payloadIndex = 0;


    public CommonPayloadGenerator(ArrayList<String> payloadList) {
        this.payloadList = payloadList;
    }

    @Override
    public GeneratedPayload generatePayloadFor(IntruderInsertionPoint insertionPoint) {

        if (payloadIndex == payloadList.size()) {
            return GeneratedPayload.end();
        }

        String payload = payloadList.get(payloadIndex);

        payloadIndex++;
        return GeneratedPayload.payload(payload);
    }
}
