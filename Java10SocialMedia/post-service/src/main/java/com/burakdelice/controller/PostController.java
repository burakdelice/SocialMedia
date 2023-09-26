package com.burakdelice.controller;



import com.burakdelice.dto.request.CreateNewPostRequestDto;
import com.burakdelice.dto.response.UserProfileResponseDto;
import com.burakdelice.rabbitmq.model.CreatePostModel;
import com.burakdelice.repository.entity.Post;
import com.burakdelice.service.PostService;
import com.burakdelice.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.burakdelice.constant.EndPoints.*;

@RestController
@RequestMapping(POST)
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final JwtTokenManager jwtTokenManager;

  /*  bu metodun controller' ını yazınız.
    createPost işlemi için user-service' de consumer işlemlerini yapınız.
    */
    @PostMapping("/create-post/{token}")
    public ResponseEntity<Post> createPost(@PathVariable String token, @RequestBody CreateNewPostRequestDto dto){
        return ResponseEntity.ok(postService.createPost(token, dto));
    }
}