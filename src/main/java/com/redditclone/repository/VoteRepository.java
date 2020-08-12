package com.redditclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.User;
import com.redditclone.modelentity.Vote;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>  {

	Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

}
