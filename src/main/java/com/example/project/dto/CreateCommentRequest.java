package com.example.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
    private String message;
    private Long ticketId;
    private Long authorId;
}
