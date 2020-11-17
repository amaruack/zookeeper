package com.eseict.zoo.proc;

public class ZookeeperException extends Exception {

    String message ;

    public ZookeeperException(String message) {
        super();
        this.message = message;
    }


}
