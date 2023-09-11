package com.burakdelice.service;

import com.burakdelice.dto.request.ActivateRequestDto;
import com.burakdelice.dto.request.AuthUpdateRequestDto;
import com.burakdelice.dto.request.LoginRequestDto;
import com.burakdelice.dto.request.RegisterRequestDto;
import com.burakdelice.dto.response.RegisterResponseDto;
import com.burakdelice.exception.AuthManagerException;
import com.burakdelice.exception.ErrorType;
import com.burakdelice.manager.IUserManager;
import com.burakdelice.mapper.IAuthMapper;
import com.burakdelice.rabbitmq.model.MailModel;
import com.burakdelice.rabbitmq.producer.ActivationProducer;
import com.burakdelice.rabbitmq.producer.MailProducer;
import com.burakdelice.rabbitmq.producer.RegisterProducer;
import com.burakdelice.repository.IAuthRepository;
import com.burakdelice.repository.entity.Auth;
import com.burakdelice.repository.enums.EStatus;
import com.burakdelice.utility.CodeGenerator;
import com.burakdelice.utility.JwtTokenManager;
import com.burakdelice.utility.ServiceManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

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
public class AuthService extends ServiceManager<Auth, Long> {

    private final IAuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final IUserManager userManager;
    private final RegisterProducer registerProducer;
    private final ActivationProducer activationProducer;
    private final MailProducer mailProducer;

    public AuthService(IAuthRepository authRepository, JwtTokenManager jwtTokenManager, IUserManager userManager, RegisterProducer registerProducer, ActivationProducer activationProducer, MailProducer mailProducer) {
        super(authRepository);
        this.authRepository = authRepository;
        this.jwtTokenManager = jwtTokenManager;
        this.userManager = userManager;
        this.registerProducer = registerProducer;
        this.activationProducer = activationProducer;
        this.mailProducer = mailProducer;
    }

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {

        Auth auth = IAuthMapper.INSTANCE.toAuth(dto);
        //Auth auth = Convertor.convertFromDtoToAuth(dto);

        auth.setActivationCode(CodeGenerator.generateCode());

        if (authRepository.existsByUsername(dto.getUsername())) {
            throw new AuthManagerException(ErrorType.USERNAME_ALREADY_EXIST);
        }

        save(auth);

        RegisterResponseDto responseDto = IAuthMapper.INSTANCE.toRegisterResponseDto(auth);

        String token = jwtTokenManager.createToken(auth.getId(),auth.getRole())
                .orElseThrow(()->new AuthManagerException(ErrorType.INVALID_TOKEN));

        userManager.save(IAuthMapper.INSTANCE.toUserSaveRequestDto(auth),"Bearer "+token);

        responseDto.setToken(token);
        return responseDto;
    }

    @Transactional
    public RegisterResponseDto registerWithRabbitMq(RegisterRequestDto dto) {

        Auth auth = IAuthMapper.INSTANCE.toAuth(dto);
        auth.setActivationCode(CodeGenerator.generateCode());
        if (authRepository.existsByUsername(dto.getUsername())) {
            throw new AuthManagerException(ErrorType.USERNAME_ALREADY_EXIST);
        }
        save(auth);
        // rabbitmq ile haberleştireceğiz
        registerProducer.sendNewUser(IAuthMapper.INSTANCE.toRegisterModel(auth));

        RegisterResponseDto responseDto = IAuthMapper.INSTANCE.toRegisterResponseDto(auth);
        // Register Token Oluşturma
        String token = jwtTokenManager.createToken(auth.getId())
                .orElseThrow(()->new AuthManagerException(ErrorType.INVALID_TOKEN));
        responseDto.setToken(token);

        //mail atma işlemi için mail servis ile haberleşilecek
        MailModel mailModel = IAuthMapper.INSTANCE.toMailModel(auth);
        mailModel.setToken(token);
        mailProducer.sendMail(mailModel);
        return responseDto;
    }

    public String login(LoginRequestDto dto) {
        Optional<Auth> optionalAuth = authRepository.findOptionalByUsernameAndPassword(dto.getUsername(), dto.getPassword());
        if (optionalAuth.isEmpty()) {
            throw new AuthManagerException(ErrorType.LOGIN_ERROR);
        }
        if (!optionalAuth.get().getStatus().equals(EStatus.ACTIVE)) {
            throw new AuthManagerException(ErrorType.ACCOUNT_NOT_ACTIVE);
        }
       return jwtTokenManager.createToken(optionalAuth.get().getId(),optionalAuth.get().getRole())
               .orElseThrow(()-> new AuthManagerException(ErrorType.TOKEN_NOT_CREATED));
    }


    public String activateStatus(ActivateRequestDto dto) {

        Optional<Long> authId = jwtTokenManager.getIdFromToken(dto.getToken());
        if (authId.isEmpty()) {
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Auth> optionalAuth = findById(authId.get());

        if (optionalAuth.isEmpty()) {
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        if (optionalAuth.get().getStatus().equals(EStatus.ACTIVE)) {
            throw new AuthManagerException(ErrorType.ALREADY_ACTIVE);
        }
        if (dto.getActivationCode().equals(optionalAuth.get().getActivationCode())) {
            optionalAuth.get().setStatus(EStatus.ACTIVE);
            update(optionalAuth.get());
           // userManager.activateStatus(dto.getToken());
            activationProducer.activateUser(IAuthMapper.INSTANCE.toActivationModel(dto));
            return "Hesabınız aktive edilmiştir.";
        } else {
            throw new AuthManagerException(ErrorType.INVALID_CODE);
        }
    }

    @Transactional
    public String updateAuth(AuthUpdateRequestDto dto) {
        Optional<Auth> optionalAuth = findById(dto.getId());
        if (optionalAuth.isEmpty()) {
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }

        if (!optionalAuth.get().getUsername().equals(dto.getUsername()) || !optionalAuth.get().getEmail().equals(dto.getEmail())) {
            optionalAuth.get().setUsername(dto.getUsername());
            optionalAuth.get().setEmail(dto.getEmail());
            update(optionalAuth.get());
        }
        optionalAuth.get().setUsername(dto.getUsername());
        optionalAuth.get().setEmail(dto.getEmail());
        update(optionalAuth.get());
        return "Kullanıcının Giriş Bilgileri Güncellendi";
    }

    public String deleteAuth(String token) {
        Optional<Long> id= jwtTokenManager.getIdFromToken(token);
        if (id.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        Optional<Auth> auth = findById(id.get());
        if (auth.isEmpty()){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND);
        }
        if (auth.get().getStatus().equals(EStatus.DELETED)){
            throw new AuthManagerException(ErrorType.USER_NOT_FOUND,"Hesap zaten silinmiş");
        }
        auth.get().setStatus(EStatus.DELETED);
        update(auth.get());
        userManager.deleteById("Bearer "+token);
        return id+" id li kullanıcı başarıyla silindi";
    }
}
