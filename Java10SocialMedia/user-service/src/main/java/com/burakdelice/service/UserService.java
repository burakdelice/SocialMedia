package com.burakdelice.service;


import com.burakdelice.dto.request.UserSaveRequestDto;
import com.burakdelice.dto.request.UserUpdateRequestDto;
import com.burakdelice.dto.response.UserProfileFindAllResponseDto;
import com.burakdelice.exception.ErrorType;
import com.burakdelice.exception.UserManagerException;
import com.burakdelice.manager.IAuthManager;
import com.burakdelice.mapper.IUserMapper;
import com.burakdelice.rabbitmq.model.ActivationModel;
import com.burakdelice.rabbitmq.model.RegisterModel;
import com.burakdelice.rabbitmq.producer.RegisterElasticProducer;
import com.burakdelice.repository.IUserRepository;
import com.burakdelice.repository.entity.UserProfile;
import com.burakdelice.repository.enums.EStatus;
import com.burakdelice.utility.JwtTokenManager;
import com.burakdelice.utility.ServiceManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    1-
    Register işlemi yapcaz
    dto alsın dto dönsün
    request dto --> username, email, password
    response dto --> username, id, activationCode
    repodan controllera kadar yazalım


    GetMapping --> veriler nerden geliyor url
    PostMapping --> Body

    2- Login methodu yazalım
        dto alsın eğer veritabnında kayıt varsa true dönsün yoksa false dönsün

    3- Active status --> Boolean dönsün

    */

@Service
public class UserService extends ServiceManager<UserProfile, String> {


    //Dependency Injec -->> constructor injection, setter injection, field injection
    private final IUserRepository userRepository;
    private final JwtTokenManager jwtTokenManager; //singleton üretilen JwtTokenManager sınıfının bu AuthService'e çağırılıp kullanıma açılması işlemidir..
    private final IAuthManager authManager;
    private final CacheManager cacheManager;
    private final RegisterElasticProducer registerElasticProducer;

    public UserService(IUserRepository userRepository, JwtTokenManager jwtTokenManager, IAuthManager authManager, CacheManager cacheManager, RegisterElasticProducer registerElasticProducer) {
        super(userRepository);
        this.userRepository = userRepository;
        this.jwtTokenManager = jwtTokenManager;
        this.authManager = authManager;
        this.cacheManager = cacheManager;
        this.registerElasticProducer = registerElasticProducer;
    }

    public Boolean createNewUser(UserSaveRequestDto dto) {
        try {
            UserProfile userProfile = IUserMapper.INSTANCE.toUserProfile(dto);

            save(userProfile);
            return true;
        } catch (Exception e) {
            throw new UserManagerException(ErrorType.USER_NOT_CREATED);
        }
    }

   /* public String activateStatus(String token) {
        Optional<Long> authId = jwtTokenManager.getAuthIdFromToken(token);
        if (authId.isEmpty()){
            throw new UserManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> userProfile = userRepository.findByAuthId(authId.get());
        if(userProfile.isEmpty()){
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setStatus(EStatus.ACTIVE);
        update(userProfile.get());
        return "Hesabınız aktive olmuştur.";
    }*/

    @Transactional
    public String updateUserProfile(UserUpdateRequestDto dto) {
        Optional<Long> authId = jwtTokenManager.getAuthIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new UserManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> userProfile = userRepository.findByAuthId(authId.get());
        if (userProfile.isEmpty()) {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }

        if (!userProfile.get().getEmail().equals(dto.getEmail()) ||
                !userProfile.get().getUsername().equals(dto.getUsername())) {
            authManager.updateAuth(IUserMapper.INSTANCE.toAuthUpdateRequestDto(userProfile.get()), "Bearer " + dto.getToken());
            userProfile.get().setEmail(dto.getEmail());
            userProfile.get().setUsername(dto.getUsername());
        }
        // userProfile = Optional.of(IUserMapper.INSTANCE.toUserProfile(dto));
        //  update(IUserMapper.INSTANCE.toUserProfile(dto));
        userProfile.get().setAbout(dto.getAbout());
        userProfile.get().setPhone(dto.getPhone());
        userProfile.get().setAddress(dto.getAddress());
        userProfile.get().setName(dto.getName());
        userProfile.get().setSurName(dto.getSurName());
        userProfile.get().setAvatar(dto.getAvatar());
        update(userProfile.get());

        try {
            cacheManager.getCache("find_by_username").evict(dto.getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return "Kullanıcı Güncellendi";

    }

    public Boolean createNewUserWithRabbitmq(RegisterModel model) {
        try {
            UserProfile userProfile = IUserMapper.INSTANCE.toUserProfile(model);
            save(userProfile);
            registerElasticProducer.sendNewUser(IUserMapper.INSTANCE.toRegisterElasticModel(userProfile));
            return true;
        } catch (Exception e) {
            throw new UserManagerException(ErrorType.USER_NOT_CREATED);
        }
    }

    public String activateStatus(String token) {
        Optional<Long> authId = jwtTokenManager.getAuthIdFromToken(token);
        if (authId.isEmpty()) {
            throw new UserManagerException(ErrorType.INVALID_TOKEN);
        }
        Optional<UserProfile> userProfile = userRepository.findByAuthId(authId.get());
        if (userProfile.isEmpty()) {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setStatus(EStatus.ACTIVE);
        update(userProfile.get());
        return "Hesabınız aktive olmuştur";
    }

    @Cacheable(value = "find_by_username")
    public UserProfile findByUsername(String username) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Optional<UserProfile> userProfile = userRepository.findByUsername(username);
        if (userProfile.isEmpty()) {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        return userProfile.get();
    }

    @Cacheable(value = "find_by_status")
    public List<UserProfile> findUserProfileByStatus(EStatus status) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<UserProfile> list = userRepository.findByStatus(status);
        return list;
    }

    @Cacheable(value = "find_by_status2", key = "#status.toUpperCase(T(java.util.Locale).ENGLISH)")
    public List<UserProfile> findByStatus(String status) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        EStatus myStatus;
        try {
            myStatus = EStatus.valueOf(status.toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            throw new UserManagerException(ErrorType.STATUS_NOT_FOUND);
        }
        return userRepository.findByStatus(myStatus);

    }


    public String deleteUserProfile(String token) {
        Optional<Long> authId = jwtTokenManager.getAuthIdFromToken(token.substring(7));

        Optional<UserProfile> userProfile = userRepository.findByAuthId(authId.get());
        if (userProfile.isEmpty()) {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        userProfile.get().setStatus(EStatus.DELETED);
        update(userProfile.get());
        cacheManager.getCache("find_by_usernmae").evict(userProfile.get().getUsername());
        return userProfile.get().getId() + " id li kullanıcı silinmiştir.";
    }

    public List<UserProfileFindAllResponseDto> findAllUserProfile() {
        List<UserProfile> userProfileList = findAll();
        return userProfileList.stream().map(x -> IUserMapper.INSTANCE.toUserProfileFindAllResponseDto(x)).collect(Collectors.toList());
    }

    public Page<UserProfile> findAllByPageable(int pageSize, int pageNumber, String direction, String sortParameter) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortParameter);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return userRepository.findAll(pageable);
    }

    public Slice<UserProfile> findAllBySlice(int pageSize, int pageNumber, String direction, String sortParameter) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortParameter);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return userRepository.findAll(pageable);
    }
}
