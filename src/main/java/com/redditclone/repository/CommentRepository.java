package com.redditclone.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redditclone.dto.CommentsDto;
import com.redditclone.modelentity.Comment;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

	List<Comment> findByPost(Post post);

	List<Comment> findAllByUser(User user);

}
