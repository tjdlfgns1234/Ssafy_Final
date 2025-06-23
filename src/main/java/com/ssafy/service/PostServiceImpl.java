package com.ssafy.service;

import com.ssafy.dto.PostDTO;
import com.ssafy.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<PostDTO> getAllPosts() throws Exception {
        return postRepository.postSelectAll();
    }

    @Override
    public List<PostDTO> getTop3Posts() throws Exception {
        return postRepository.postSelectTop3();
    }

    @Override
    public List<PostDTO> getPostsByTag(String tag) throws Exception {
        return postRepository.postSelectByTag(tag);
    }

    @Override
    public PostDTO getPostById(int postId) throws Exception {
        return postRepository.postSelect(postId);
    }

    @Override
    @Transactional
    public void createPost(PostDTO post) throws Exception {
        postRepository.postInsert(post);
    }

    @Override
    @Transactional
    public void updatePost(PostDTO post) throws Exception {
        postRepository.postUpdate(post);
    }

    @Override
    @Transactional
    public void deletePost(int postId) throws Exception {
        postRepository.postDelete(postId);
    }

    @Override
    @Transactional
    public void incrementLikes(int postId, int userId) throws Exception {
        postRepository.incrementLikes(postId, userId);
    }

    @Override
    @Transactional
    public void decrementLikes(int postId, int userId) throws Exception {
        postRepository.decrementLikes(postId, userId);
    }

    @Override
    public boolean isPostLiked(int postId, int userId) throws Exception {
        return postRepository.isPostLiked(postId, userId) == 1;
    }
}
