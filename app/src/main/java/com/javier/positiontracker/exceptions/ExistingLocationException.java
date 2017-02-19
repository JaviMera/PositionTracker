package com.javier.positiontracker.exceptions;

/**
 * Created by javie on 2/19/2017.
 */
public class ExistingLocationException extends Exception {

    @Override
    public String getMessage() {

        return "Location already exists";
    }
}
