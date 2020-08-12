package com.redditclone.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redditclone.dto.PostsDto;
import com.redditclone.exception.SpringRedditException;
import com.redditclone.mapper.PostMapper;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.Subreddit;
import com.redditclone.modelentity.User;
import com.redditclone.repository.PostRepository;
import com.redditclone.repository.SubredditRepository;
import com.redditclone.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

	private final SubredditRepository subredditRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final PostMapper postMapper;

	public PostsDto save(PostsDto postsDto) {

		Subreddit subreddit = subredditRepository.findByName(postsDto.getSubredditName()).orElseThrow(
				() -> new SpringRedditException("Subreddit not found name - " + postsDto.getSubredditName()));

		User currentUser = authService.getCurrentUser();
		Post post = postRepository.save(postMapper.map(postsDto, subreddit, currentUser));
		postsDto.setId(post.getPostId());
		postsDto.setUserName(currentUser.getUsername());
		return postsDto;

	}

	@Transactional(readOnly = true)
	public List<PostsDto> getAllPosts() {

		return postRepository.findAll().stream().map(postMapper::mapToDto).collect(toList());
	}

	@Transactional(readOnly = true)
	public PostsDto getPost(Long id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new SpringRedditException("Post not found for id -" + id));

		return postMapper.mapToDto(post);
	}

	@Transactional(readOnly = true)
	public List<PostsDto> getPostsBySubreddit(Long subredditId) {
		Subreddit subreddit = subredditRepository.findById(subredditId)
				.orElseThrow(() -> new SpringRedditException("Subreddit not found id - " + subredditId));

		List<Post> posts = postRepository.findAllBySubreddit(subreddit);
		return posts.stream().map(postMapper::mapToDto).collect(toList());

	}

	@Transactional(readOnly = true)
	public List<PostsDto> getPostsByUsername(String username) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new SpringRedditException("User name not found - " + username));
		List<Post> posts = postRepository.findByUser(user);
		return posts.stream().map(postMapper::mapToDto).collect(toList());
	}

	}
