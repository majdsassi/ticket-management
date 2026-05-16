package com.example.project.service;

import com.example.project.model.AppUser;
import com.example.project.model.Ticket;
import com.example.project.model.TicketComment;
import com.example.project.repository.TicketCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;

    public TicketCommentService(TicketCommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Add comment to ticket
     */
    public TicketComment addComment(Ticket ticket, AppUser author, String content) {
        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .author(author)
                .message(content)
                .build();
        return commentRepository.save(comment);
    }

    /**
     * Get comments for ticket
     */
    public List<TicketComment> getTicketComments(Ticket ticket) {
        return commentRepository.findByTicket(ticket);
    }

    /**
     * Get comment by ID
     */
    public Optional<TicketComment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * Delete comment
     */
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    /**
     * Update comment
     */
    public TicketComment updateComment(Long id, String content) {
        TicketComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.setMessage(content);
        return commentRepository.save(comment);
    }
}
