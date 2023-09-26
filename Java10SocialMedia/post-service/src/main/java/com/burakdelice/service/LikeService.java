package com.burakdelice.service;

import com.burakdelice.repository.ILikeRepository;
import com.burakdelice.repository.entity.Like;
import com.burakdelice.utility.JwtTokenManager;
import com.burakdelice.utility.ServiceManager;
import org.springframework.stereotype.Service;

@Service
public class LikeService extends ServiceManager<Like, String> {
    private final ILikeRepository likeRepository;
    private final JwtTokenManager jwtTokenManager;


    public LikeService(ILikeRepository likeRepository, JwtTokenManager jwtTokenManager) {
        super(likeRepository);
        this.likeRepository = likeRepository;
        this.jwtTokenManager = jwtTokenManager;
    }

}
