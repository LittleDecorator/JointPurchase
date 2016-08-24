package com.acme;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidTest {

    public static void main(String[] args){
        System.out.println(UUID.randomUUID().toString());

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1ODdjY2MyYS1hOTEyLTQxYWItOGVkZi1hYmY4ZWI5ODZiODYiLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE0NzE5NDI0OTh9.2du3QzCfkvxYyHUG-jtioiPFBCJPyfwafNL1YjQNxsc";
        byte[] bytes = ByteBuffer.allocate(8).putInt(token.hashCode()).array();
        String uuid = java.util.UUID.nameUUIDFromBytes(bytes).toString();
        System.out.println(uuid);
        System.out.println(uuid.replace("-", "").toUpperCase());
    }

}
