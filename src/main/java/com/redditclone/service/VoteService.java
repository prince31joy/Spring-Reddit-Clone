package com.redditclone.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.redditclone.dto.VoteDto;
import com.redditclone.exception.SpringRedditException;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.Vote;
import com.redditclone.repository.PostRepository;
import com.redditclone.repository.VoteRepository;
import static com.redditclone.modelentity.VoteType.UPVOTE;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VoteService {

	private final VoteRepository voteRepository;
	private final PostRepository postRepository;
	private final AuthService authService;

	public VoteDto vote(VoteDto voteDto) {
		Post post = postRepository.findById(voteDto.getPostId())
				.orElseThrow(() -> new SpringRedditException("Post Not Found with ID - " + voteDto.getPostId()));
		Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
				authService.getCurrentUser());

		if (voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
			throw new SpringRedditException("You have already " + voteDto.getVoteType() + "'d for this post");
		}

		if (UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		} else {
			post.setVoteCount(post.getVoteCount() - 1);
		}

		Vote vote = mapToVote(voteDto, post);
		voteRepository.save(vote);
		postRepository.save(post);
		voteDto.setVoteType(vote.getVoteType());
		return voteDto;
	}

	private Vote mapToVote(VoteDto voteDto, Post post) {

		return Vote.builder().voteType(voteDto.getVoteType()).post(post).user(authService.getCurrentUser()).build();
	}
}