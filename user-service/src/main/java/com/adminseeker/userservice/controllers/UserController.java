package com.adminseeker.userservice.controllers;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.adminseeker.userservice.services.UserService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody User user, @RequestHeader Map<String,String> headers){
        try {
            ResponseEntity<User> newUser = new ResponseEntity<User>(userService.addUser(user),HttpStatus.CREATED);
            logger.debug("New User Registered!, User Email: "+user.getEmail()+" correlation-id: "+headers.get("correlation-id"));
            return newUser;
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.error("Error registering User Email: "+user.getEmail()+" "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestHeader Map<String,String> headers){
        return new ResponseEntity<List<User>>(userService.getUsers(headers),HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        try {
            User user = userService.getUserById(id); 
            return new ResponseEntity<User>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUser(@RequestHeader Map<String,String> headers){
        try {
            User user = userService.getUser(headers); 
            return new ResponseEntity<User>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/getuser")
    public ResponseEntity<?> getUserByEmail(@RequestBody EmailRequest request, @RequestHeader Map<String,String> headers ){
        try {
            User user = userService.getUserByEmail(request); 
            logger.debug("User Requested By Email!, User Email: "+user.getEmail()+" , User Response: "+user.toString() +" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<User>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.error("Error Fetching User By Email: "+request.getEmail()+" "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/id/{id}")
    public ResponseEntity<?> updateById(@RequestBody User user, @PathVariable Long id){
        try{
            User usrdb = userService.updateUserById(user, id);
            return new ResponseEntity<User>(usrdb,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> DeleteById(@PathVariable Long id){
        try {
            User user = userService.DeleteUserById(id); 
            return new ResponseEntity<User>(user,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
