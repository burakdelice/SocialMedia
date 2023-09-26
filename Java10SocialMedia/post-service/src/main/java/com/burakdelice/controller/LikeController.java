package com.burakdelice.controller;
import com.burakdelice.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.burakdelice.service.LikeService;
import static com.burakdelice.constant.EndPoints.LIKE;
@RestController
@RequestMapping(LIKE)
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final JwtTokenManager jwtTokenManager;

}