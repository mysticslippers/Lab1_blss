package me.ifmo.backend.exceptions;

public class AlreadyProcessedException extends RuntimeException {
    public AlreadyProcessedException() {
        super("Already processed");
    }
}