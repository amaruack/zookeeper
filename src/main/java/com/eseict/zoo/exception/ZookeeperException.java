package com.eseict.zoo.exception;

public class ZookeeperException extends Exception {

    String message ;

    public ZookeeperException(String message) {
        super();
        this.message = message;
    }


}
