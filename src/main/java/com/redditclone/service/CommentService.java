package com.redditclone.service;

import org.springframework.stereotype.Service;

import com.redditclone.dto.CommentsDto;
import com.redditclone.exception.SpringRedditException;
import com.redditclone.mapper.CommentMapper;
import com.redditclone.modelentity.Comment;
import com.redditclone.modelentity.NotificationEmail;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.User;
import com.redditclone.repository.CommentRepository;
import com.redditclone.repository.PostRepository;
import com.redditclone.repository.UserRepository;

import lombok.AllArgsConstructor;
import static java.util.stream.Collectors.toList;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

	private static final String POST_URL = "";
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final CommentMapper commentMapper;
	private final CommentRepository commentRepository;
	private final MailContentBuilder mailContentBuilder;
	private final MailService mailService;

	public CommentsDto save(CommentsDto commentsDto) {
		Post post = postRepository.findById(commentsDto.getPostId()).orElseThrow(
				() -> new SpringRedditException("Post not found for id - " + commentsDto.getPostId().toString()));
		Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
		commentRepository.save(comment);
		commentsDto.setId(comment.getCommentId());

		String message = mailContentBuilder
				.build("" + post.getUser().getUsername() + " posted a comment on your post." + "");
		sendCommentNotification(message, post.getUser());

		return commentsDto;

	}

	private void sendCommentNotification(String message, User user) {
		mailService.sendMail(
				new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
	}

	public List<CommentsDto> getAllCommentsForPost(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new SpringRedditException("Post not found for id - " + postId.toString()));
		return commentRepository.findByPost(post).stream().map(commentMapper::mapToDto).collect(toList());
	}

	public List<CommentsDto> getAllCommentsForUser(String userName) {
		User user = userRepository.findByUsername(userName)
				.orElseThrow(() -> new SpringRedditException("User not found by name - " + userName));
		return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto).collect(toList());
	}
}
