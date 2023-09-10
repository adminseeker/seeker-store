package com.adminseeker.userservice.controllers;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminseeker.userservice.entities.EmailRequest;
import com.adminseeker.userservice.entities.ErrorResponse;
import com.adminseeker.userservice.entities.User;
import com.adminseeker.userservice.entities.UserResponse;
import com.adminseeker.userservice.entities.UserResponseWithPassword;
import com.adminseeker.userservice.services.UserService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    

    @Autowired
    UserService userService;

    @PostMapping("/inapi")
    public ResponseEntity<?> save(@RequestHeader Map<String,String> headers,@RequestBody User user){
        try {
            ResponseEntity<UserResponse> newUser = new ResponseEntity<UserResponse>(userService.addUser(user),HttpStatus.CREATED);
            return newUser;
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            UserResponse user = userService.getUserById(id,headers);
            return new ResponseEntity<UserResponse>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/inapi/{id}")
    public ResponseEntity<?> getUserByIdPublic(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            UserResponse user = userService.getUserByIdPublic(id,headers);
            return new ResponseEntity<UserResponse>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getUser(@RequestHeader Map<String,String> headers){
        try {
            UserResponse user = userService.getUser(headers); 
            return new ResponseEntity<UserResponse>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/inapi/getuser")
    public ResponseEntity<?> getUserByEmail(@RequestHeader Map<String,String> headers,@RequestBody EmailRequest request){
        try {
            UserResponseWithPassword user = userService.getUserByEmail(request); 
            return new ResponseEntity<UserResponseWithPassword>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateById(@RequestHeader Map<String,String> headers,@RequestBody User user, @PathVariable Long id){
        try{
            UserResponse usrdb = userService.updateUserById(user, id, headers);
            return new ResponseEntity<UserResponse>(usrdb,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            UserResponse user = userService.DeleteUserById(id,headers);
            return new ResponseEntity<UserResponse>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
