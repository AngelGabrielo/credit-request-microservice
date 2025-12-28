package org.app.credit.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Error {
    private String message;
    private String error;
    private int status;
    private LocalDateTime timestamp;
}
