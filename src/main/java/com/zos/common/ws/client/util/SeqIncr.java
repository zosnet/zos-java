package com.zos.common.ws.client.util;

public class SeqIncr {
    private static int seq = 6;
    public synchronized static int next() {
        if (Integer.MAX_VALUE == seq) {
            seq = 7;
        } else {
            seq++;
        }
        return seq;
    }
}
