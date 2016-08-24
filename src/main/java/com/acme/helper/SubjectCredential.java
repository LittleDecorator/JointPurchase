package com.acme.helper;

public class SubjectCredential {

        String name;
        String password;

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        @Override
        public String toString() {
                return "SubjectCredential{" +
                        "name='" + name + '\'' +
                        ", password='" + password + '\'' +
                        '}';
        }
}
