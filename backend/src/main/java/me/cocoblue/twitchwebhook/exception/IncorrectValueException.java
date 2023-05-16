package me.cocoblue.twitchwebhook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect Value Presented.")
public class IncorrectValueException extends RuntimeException {
    public IncorrectValueException(String message) {
        super(message);
    }
}
