package com.redditclone.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redditclone.dto.PostsDto;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.Subreddit;
import com.redditclone.modelentity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findAllBySubreddit(Subreddit subreddit);

	List<Post> findByUser(User user);

}
