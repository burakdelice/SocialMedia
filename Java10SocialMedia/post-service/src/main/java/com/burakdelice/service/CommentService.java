package com.burakdelice.service;

import com.burakdelice.repository.ICommentRepository;
import com.burakdelice.repository.entity.Comment;
import com.burakdelice.utility.JwtTokenManager;
import com.burakdelice.utility.ServiceManager;
import org.springframework.stereotype.Service;

@Service
public class CommentService extends ServiceManager<Comment, String> {
    private final ICommentRepository commentRepository;
    private final JwtTokenManager jwtTokenManager;


    public CommentService(ICommentRepository commentRepository, JwtTokenManager jwtTokenManager) {
        super(commentRepository);
        this.commentRepository = commentRepository;
        this.jwtTokenManager = jwtTokenManager;
    }

}
