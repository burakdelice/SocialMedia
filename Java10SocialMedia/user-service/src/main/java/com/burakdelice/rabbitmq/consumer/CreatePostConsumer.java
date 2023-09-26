package com.burakdelice.rabbitmq.consumer;

import com.burakdelice.dto.response.UserProfileResponseDto;
import com.burakdelice.mapper.IUserMapper;
import com.burakdelice.rabbitmq.model.CreatePostModel;
import com.burakdelice.repository.entity.UserProfile;
import com.burakdelice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePostConsumer {

    private final UserService userService;

    @RabbitListener(queues = "post-queue")
    public Object createPost(CreatePostModel createPostModelmodel) {
        Optional<UserProfile> userProfile = userService.findByUserWithAuthId(createPostModelmodel.getAuthId());
        return UserProfileResponseDto.builder()
                .userId(userProfile.get().getId())
                .userAvatar(userProfile.get().getAvatar())
                .username(userProfile.get().getUsername())
                .build();
    }
}
