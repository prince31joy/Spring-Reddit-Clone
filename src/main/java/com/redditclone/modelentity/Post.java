package com.redditclone.modelentity;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_generator")
	@SequenceGenerator(name="post_generator", sequenceName = "post_seq")
    private Long postId;
	
    @NotBlank(message = "Post Name cannot be empty or Null")
    private String postName;
    
    @Nullable
    private String url;
    
    @Nullable
    @Lob
    private String description;
    
    private Integer voteCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;
    
    private Instant createdDate;
    
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "subredditId", referencedColumnName = "subredditId")
    private Subreddit subreddit;

}
