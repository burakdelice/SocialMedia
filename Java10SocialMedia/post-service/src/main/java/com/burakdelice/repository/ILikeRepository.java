package com.burakdelice.repository;

import com.burakdelice.repository.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ILikeRepository extends MongoRepository<Like, String> {


}
