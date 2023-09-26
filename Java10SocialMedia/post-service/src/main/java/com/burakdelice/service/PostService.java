package com.burakdelice.service;


import com.burakdelice.dto.request.CreateNewPostRequestDto;
import com.burakdelice.dto.request.UpdatePostRequestDto;
import com.burakdelice.dto.response.UserProfileResponseDto;
import com.burakdelice.exception.ErrorType;
import com.burakdelice.exception.PostManagerException;
import com.burakdelice.manager.IUserManager;
import com.burakdelice.rabbitmq.model.CreatePostModel;
import com.burakdelice.rabbitmq.producer.CreatePostProducer;
import com.burakdelice.repository.IPostRepository;
import com.burakdelice.repository.entity.Post;
import com.burakdelice.utility.JwtTokenManager;
import com.burakdelice.utility.ServiceManager;
import org.springframework.stereotype.Service;


@Service
public class PostService extends ServiceManager<Post, String> {


    //Dependency Injec -->> constructor injection, setter injection, field injection
    private final IPostRepository postRepository;
    private final JwtTokenManager jwtTokenManager;
    private final CreatePostProducer createPostProducer;
    private final IUserManager userManager;


    public PostService(IPostRepository postRepository, JwtTokenManager jwtTokenManager, CreatePostProducer createPostProducer, IUserManager userManager) {
        super(postRepository);
        this.postRepository = postRepository;
        this.jwtTokenManager = jwtTokenManager;
        this.createPostProducer = createPostProducer;
        this.userManager = userManager;
    }

    public Post createPost(String token, CreateNewPostRequestDto dto){
        Long authId = jwtTokenManager.getAuthIdFromToken(token)
                .orElseThrow(() -> {throw new PostManagerException(ErrorType.INVALID_TOKEN);});
        CreatePostModel createPostModel = CreatePostModel.builder()
                .authId(authId)
                .build();
        UserProfileResponseDto userProfile = (UserProfileResponseDto) createPostProducer.createPost(createPostModel);
        Post post = Post.builder()
                .userId(userProfile.getUserId())
                .username(userProfile.getUsername())
                .userAvatar(userProfile.getUserAvatar())
                .content(dto.getContent())
                .mediaUrls(dto.getMediaUrls())
                .build();
        return save(post);
    }

    public Post update(String token, String postId, UpdatePostRequestDto dto){
        Long authId = jwtTokenManager.getAuthIdFromToken(token)
                .orElseThrow(() -> {throw new PostManagerException(ErrorType.INVALID_TOKEN);});
        UserProfileResponseDto userProfileResponseDto = userManager.findByUserSimpleDataWithAuthId(authId).getBody();
        Post post = postRepository.findById(postId).get();
        post.setUserId(userProfileResponseDto.getUserId());
        post.setUsername(userProfileResponseDto.getUsername());
        post.setUserAvatar(userProfileResponseDto.getUserAvatar());
        post.setContent(dto.getContent());
        post.setMediaUrls(dto.getMediaUrls());
        save(post);
        return post;
    }
}
