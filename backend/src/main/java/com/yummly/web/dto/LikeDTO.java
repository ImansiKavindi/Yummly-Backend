package com.yummly.web.dto;

import com.yummly.web.model.Like;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LikeDTO {
    private Long id;
    private Long postId;
    private String userName;

    public LikeDTO(Like like) {
        this.id = like.getId();
        this.postId = like.getPost().getId();
        this.userName = like.getUser() != null ? like.getUser().getName() : null;
    }
}
