package com.burakdelice.controller;


import com.burakdelice.dto.request.UserSaveRequestDto;
import com.burakdelice.dto.request.UserUpdateRequestDto;
import com.burakdelice.dto.response.UserProfileFindAllResponseDto;
import com.burakdelice.repository.entity.UserProfile;
import com.burakdelice.repository.enums.EStatus;
import com.burakdelice.service.UserService;
import com.burakdelice.utility.JwtTokenManager;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.burakdelice.constant.EndPoints.*;

@RestController
@RequestMapping(USER)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;

    @PostMapping(SAVE)
    public ResponseEntity<Boolean> save(@RequestBody UserSaveRequestDto dto,@RequestHeader("Authorization") String token){
        System.out.println("Authdan gelen token=>"+token);
        return ResponseEntity.ok(userService.createNewUser(dto));
    }
    @PutMapping(UPDATE)
    public ResponseEntity<String> updateUserProfile(@RequestBody @Valid UserUpdateRequestDto dto){
        return  ResponseEntity.ok(userService.updateUserProfile(dto));
    }
    @PostMapping(ACTIVATE_STATUS)
    public ResponseEntity<String> activateStatus(@RequestParam String token){
        return ResponseEntity.ok(userService.activateStatus(token));
    }

    @GetMapping(FIND_ALL)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserProfileFindAllResponseDto>> findAll(){
        return ResponseEntity.ok(userService.findAllUserProfile());
    }
    @GetMapping("/find_by_username/{username}")
    public ResponseEntity<UserProfile> findByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.findByUsername(username));
    }
    @GetMapping("/find_by_status/{status}")
    public ResponseEntity<List<UserProfile>> findUserProfileByStatus(@PathVariable EStatus status){
        return ResponseEntity.ok(userService.findUserProfileByStatus(status));
    }
    @DeleteMapping(DELETE_BY_ID)
    public ResponseEntity<String> deleteById(@RequestHeader("Authorization") String token){
       return ResponseEntity.ok(userService.deleteUserProfile(token));
    }
    @GetMapping("/find_all_by_pageable")
    public ResponseEntity<Page<UserProfile>> findAllByPageable(int pageSize,@RequestParam(required = false,defaultValue = "ASC") int pageNumber, String direction,@RequestParam(required = false,defaultValue = "id") String sortParameter){
        return ResponseEntity.ok(userService.findAllByPageable(pageSize,pageNumber,direction,sortParameter));
    }
    @GetMapping("/find_all_by_slice")
    public ResponseEntity<Slice<UserProfile>> findAllBySlice(int pageSize, @RequestParam(required = false,defaultValue = "ASC") int pageNumber, String direction, @RequestParam(required = false,defaultValue = "id") String sortParameter){
        return ResponseEntity.ok(userService.findAllBySlice(pageSize,pageNumber,direction,sortParameter));
    }
}
