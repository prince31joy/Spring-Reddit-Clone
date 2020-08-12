package com.redditclone.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.redditclone.dto.CommentsDto;
import com.redditclone.modelentity.Comment;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	@Mapping(target = "commentId", ignore = true)
	@Mapping(target = "text", source = "commentsDto.text")
	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "post", source = "post")
	Comment map(CommentsDto commentsDto, Post post, User user);

	@Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
	@Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
	@Mapping(target = "id", source = "commentId")
	CommentsDto mapToDto(Comment comment);
}
