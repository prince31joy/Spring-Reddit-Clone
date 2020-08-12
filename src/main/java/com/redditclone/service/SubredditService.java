package com.redditclone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redditclone.dto.SubredditDto;
import com.redditclone.exception.SpringRedditException;
import com.redditclone.mapper.SubredditMapper;
import com.redditclone.modelentity.Subreddit;
import com.redditclone.modelentity.User;
import com.redditclone.repository.PostRepository;
import com.redditclone.repository.SubredditRepository;
import static java.util.stream.Collectors.toList;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

	private final SubredditRepository subredditRepository;
	private final SubredditMapper subredditMapper;
	private final AuthService authService;
	private final PostRepository postRepository;

	@Transactional
	public SubredditDto save(SubredditDto subredditDto) {
		User currentUser = authService.getCurrentUser();
		Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto, currentUser));
		subredditDto.setId(save.getSubredditId());
		subredditDto.setUserName(save.getUser().getUsername());
		return subredditDto;
	}

	@Transactional(readOnly = true)
	public List<SubredditDto> getAll() {
		return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto).collect(toList());
	}

	public SubredditDto getSubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id)
				.orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
		return subredditMapper.mapSubredditToDto(subreddit);
	}
}



//code before using mapstruct

//	@Transactional
//	public SubredditDto save(SubredditDto subredditDto) {
//		Subreddit subreddit = subredditRepository.save(mapSubredditDto(subredditDto));
//		subredditDto.setId(subreddit.getSubredditId());
//		return subredditDto;
//	}
//
//	@Transactional(readOnly = true)
//	public List<SubredditDto> getAll() {
//
//		return subredditRepository.findAll().stream().map(this::mapToDto).collect(toList());
//
//	}
//
//	@Transactional(readOnly = true)
//	public SubredditDto getSubreddit(Long id) {
//
//		Subreddit subreddit = subredditRepository.findById(id)
//				.orElseThrow(() -> new SpringRedditException("Subreddit not found with id -" + id));
//		return mapToDto(subreddit);
//	}
//
//	private SubredditDto mapToDto(Subreddit subreddit) {
//		return SubredditDto.builder().name(subreddit.getName()).id(subreddit.getSubredditId())
//				.postCount(subreddit.getPosts().size()).build();
//	}
//
//	private Subreddit mapSubredditDto(SubredditDto subredditDto) {
//		return Subreddit.builder().name(subredditDto.getName()).description(subredditDto.getDescription())
//				.createdDate(Instant.now()).build();
//	}
//
//}
