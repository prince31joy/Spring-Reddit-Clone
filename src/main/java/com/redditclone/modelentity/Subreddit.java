package com.redditclone.modelentity;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Subreddit {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subreddit_generator")
	@SequenceGenerator(name="subreddit_generator", sequenceName = "subreddit_seq")
	private Long subredditId;
	
    @NotBlank(message = "Community name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @OneToMany(mappedBy = "subreddit",fetch = FetchType.LAZY)
    private List<Post> posts;
    
    private Instant createdDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

}
