package com.acme;

import com.acme.util.PasswordHashing;

public class PasswordTest {

    public static void main(String args[]){
        //System.out.println(PasswordHashing.validatePassword("25oct87!", "1f9c15a7de0090c35d6693b76533819231d2adae494d70db74d3c12a9c0cdf189560c3623cbae47b2bc13a8ed0973aa47d9f94474fc706095786b3183da6ca20"));
        String newHash = PasswordHashing.hashPassword("nazar2017");
        System.out.println(newHash);
        //System.out.println(PasswordHashing.validatePassword("q1w2E3", newHash));
        //System.out.println(PasswordHashing.validatePassword("q1w2E3", "78fa4e1e93941754e275dbd6cf55efa3d7ff63a387b656abd60dc4980a9550ebc1dfeb3acb04c0691f9c071b68084facc1f9f485f8d38c57f34b12c76c97a655"));
    }

}
