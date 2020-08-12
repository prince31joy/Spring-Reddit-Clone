package com.redditclone.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.redditclone.dto.SubredditDto;
import com.redditclone.modelentity.Post;
import com.redditclone.modelentity.Subreddit;
import com.redditclone.modelentity.User;


@Mapper(componentModel = "spring")
public interface SubredditMapper {
	
	
	@Mapping(target = "postCount", expression = "java(mapPosts(subreddit.getPosts()))")
	@Mapping(target = "id", source = "subredditId")
	@Mapping(target = "userName", source = "user.username")
    SubredditDto mapSubredditToDto(Subreddit subreddit);
	


    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }
    
    @InheritInverseConfiguration
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    Subreddit mapDtoToSubreddit(SubredditDto subreddit, User user);
    


}