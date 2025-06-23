package com.ssafy.service;

import com.ssafy.dto.CommentDTO;
import com.ssafy.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public void createComment(CommentDTO comment) throws Exception {
        if (comment.getLikes() == null) {
            comment.setLikes(0);
        }
        commentRepository.CommentInsert(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByPost(int postId) throws Exception {
        return commentRepository.CommentSelectAllByPost(postId);
    }

    @Override
    public List<CommentDTO> getTop3CommentsByLikes(int postId) throws Exception {
        return commentRepository.CommentSelectTop3ByLikes(postId);
    }

    @Override
    public CommentDTO getCommentById(int commentId) throws Exception {
        return commentRepository.CommentSelect(commentId);
    }

    @Override
    @Transactional
    public void updateComment(CommentDTO comment) throws Exception {
        commentRepository.CommentUpdate(comment);
    }

    @Override
    @Transactional
    public void deleteComment(int commentId) throws Exception {
        commentRepository.CommentDelete(commentId);
    }

    @Override
    @Transactional
    public void incrementCommentLikes(int commentId, int userId) throws Exception {
        commentRepository.incrementLikes(commentId, userId);
    }

    @Override
    @Transactional
    public void decrementCommentLikes(int commentId, int userId) throws Exception {
        commentRepository.decrementLikes(commentId, userId);
    }

    @Override
    public boolean isCommentLiked(int commentId, int userId) throws Exception {
        return commentRepository.isCommentLiked(commentId, userId) == 1;
    }
}
