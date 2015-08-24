package com.acme.helper;

public class SubjectCredential {

        public String name;
        public String password;

        @Override
        public String toString() {
            return "UserLogin{" +
                    "name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }

}
