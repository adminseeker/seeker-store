package com.adminseeker.userservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.userservice.entities.EmailRequest;
import com.adminseeker.userservice.entities.User;
import com.adminseeker.userservice.entities.UserResponse;
import com.adminseeker.userservice.exceptions.DuplicateEmailException;
import com.adminseeker.userservice.exceptions.LoginError;
import com.adminseeker.userservice.exceptions.ResourceNotFound;
import com.adminseeker.userservice.exceptions.ResourceUpdateError;
import com.adminseeker.userservice.repository.UserRepo;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepo repo;

    public UserResponse addUser(User user){

        User userdb = repo.findByEmail(user.getEmail()).orElse(null); 
        
        if(userdb!=null) throw new DuplicateEmailException("Email Already Exists!");

        if(!user.getRole().equals("buyer") && !user.getRole().equals("seller")) throw new ResourceUpdateError("Invalid User Role!");
        
        User newUser = repo.save(user);

        return UserResponse
        .builder()
        .name(newUser.getName())
        .userId(newUser.getUserId())
        .role(newUser.getRole())
        .phone(newUser.getPhone())
        .email(newUser.getEmail())
        .createdDate(newUser.getCreatedDate())
        .modifiedDate(newUser.getModifiedDate())
        .build();

    }

    public UserResponse getUserById(Long id,Map<String,String> headers){
        User user = repo.findById(id).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        if(!user.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        return UserResponse
        .builder()
        .name(user.getName())
        .userId(user.getUserId())
        .role(user.getRole())
        .phone(user.getPhone())
        .email(user.getEmail())
        .addressList(user.getAddressList())
        .createdDate(user.getCreatedDate())
        .modifiedDate(user.getModifiedDate())
        .build();
        
    }

    public UserResponse getUser(Map<String,String> headers){
        User user = repo.findByEmail(headers.get("x-auth-user-email")).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        return UserResponse
        .builder()
        .name(user.getName())
        .userId(user.getUserId())
        .role(user.getRole())
        .phone(user.getPhone())
        .email(user.getEmail())
        .addressList(user.getAddressList())
        .createdDate(user.getCreatedDate())
        .modifiedDate(user.getModifiedDate())
        .build();
    }

    public User getUserByEmail(EmailRequest request){
        User user = repo.findByEmail(request.getEmail()).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        return user;
    }

    public UserResponse updateUserById(User user,Long id,Map<String,String> headers) throws Exception{
        
        User userdb = repo.findById(id).orElseThrow(()->new ResourceNotFound("User Not Found!"));
        if(!userdb.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        
        if(user.getEmail()!=null){
            throw new ResourceUpdateError("Email Change Is Not Allowed!");
        }

        if(user.getRole()!=null){
            throw new ResourceUpdateError("Role Change Is Not Allowed!");
        }

        if(user.getName()==null && user.getPassword()==null && user.getPhone()==null) throw new Exception("Nothing to update!");

        if(user.getName()!=null){
            userdb.setName(user.getName());
        }

        if(user.getPassword()!=null){
            userdb.setPassword(user.getPassword());
        }

        if(user.getPhone()!=null){
            userdb.setPhone(user.getPhone());
        }
        User updatedUser = repo.save(userdb);
        return UserResponse
        .builder()
        .name(updatedUser.getName())
        .userId(updatedUser.getUserId())
        .role(updatedUser.getRole())
        .phone(updatedUser.getPhone())
        .email(updatedUser.getEmail())
        .addressList(updatedUser.getAddressList())
        .createdDate(updatedUser.getCreatedDate())
        .modifiedDate(updatedUser.getModifiedDate())
        .build(); 
        
    }

    public UserResponse DeleteUserById(Long id,Map<String,String> headers){
        User user = repo.findById(id).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        if(!user.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        repo.delete(user);
        return UserResponse
        .builder()
        .name(user.getName())
        .userId(user.getUserId())
        .role(user.getRole())
        .phone(user.getPhone())
        .email(user.getEmail())
        .addressList(user.getAddressList())
        .createdDate(user.getCreatedDate())
        .modifiedDate(user.getModifiedDate())
        .build();        
    }
}
