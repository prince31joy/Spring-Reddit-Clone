package com.redditclone.mapper;

import java.util.Optional;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.redditclone.dto.PostsDto;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.Subreddit;
import com.redditclone.modelentity.User;
import com.redditclone.modelentity.Vote;
import com.redditclone.modelentity.VoteType;
import com.redditclone.repository.CommentRepository;
import com.redditclone.repository.VoteRepository;
import com.redditclone.service.AuthService;
import static com.redditclone.modelentity.VoteType.*;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private AuthService authService;

	@Mapping(target = "id", source = "postId")
	@Mapping(target = "subredditName", source = "subreddit.name")
	@Mapping(target = "userName", source = "user.username")
	@Mapping(target = "commentCount", expression = "java(commentCount(post))")
	@Mapping(target = "duration", expression = "java(getDuration(post))")
	@Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
	@Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
	public abstract PostsDto mapToDto(Post post);

	@InheritInverseConfiguration
	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "description", source = "postsDto.description")
	@Mapping(target = "voteCount", constant = "0")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "subreddit", source = "subreddit")
	public abstract Post map(PostsDto postsDto, Subreddit subreddit, User user);

	Integer commentCount(Post post) {
		return commentRepository.findByPost(post).size();
	}

	String getDuration(Post post) {
		return TimeAgo.using(post.getCreatedDate().toEpochMilli());
	}

	boolean isPostUpVoted(Post post) {
		return checkVoteType(post, UPVOTE);
	}

	boolean isPostDownVoted(Post post) {
		return checkVoteType(post, DOWNVOTE);
	}

	private boolean checkVoteType(Post post, VoteType voteType) {
		if(authService.isLoggedIn()) {
			Optional<Vote> voteForPostByUser = 
					voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
			
			return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
		}
		return false;
	}

}
