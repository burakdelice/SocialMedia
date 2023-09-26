package com.burakdelice.utility;

import com.burakdelice.dto.request.UserSaveRequestDto;
import com.burakdelice.repository.entity.UserProfile;

public class Converter {
    public static UserProfile toUserProfile(UserSaveRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        UserProfile.UserProfileBuilder<?, ?> userProfile = UserProfile.builder();

        userProfile.authId( dto.getAuthId() );
        userProfile.username( dto.getUsername() );
        userProfile.email( dto.getEmail() );

        return userProfile.build();
    }
}
