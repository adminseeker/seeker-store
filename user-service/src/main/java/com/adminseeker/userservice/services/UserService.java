package com.adminseeker.userservice.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.userservice.entities.EmailRequest;
import com.adminseeker.userservice.entities.User;
import com.adminseeker.userservice.exceptions.DuplicateEmailException;
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

    public User addUser(User user){

        User userdb = repo.findByEmail(user.getEmail()).orElse(null); 
        
        if(userdb!=null) throw new DuplicateEmailException("Email Already Exists!");

        if(!user.getRole().equals("buyer") && !user.getRole().equals("seller")) throw new ResourceUpdateError("Invalid User Role!");
        
        return repo.save(user);
    }

    public List<User> getUsers(Map<String,String> headers){
        return repo.findAll();
    }

    public User getUserById(Long id){
        User user = repo.findById(id).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        return user;
        
    }

    public User getUser(Map<String,String> headers){
        User user = repo.findByEmail(headers.get("x-auth-user-email")).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        return user;
        
    }

    public User getUserByEmail(EmailRequest request){
        User user = repo.findByEmail(request.getEmail()).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        return user;
        
    }

    public User updateUserById(User user,Long id) throws Exception{
        
        User userdb = repo.findById(id).orElseThrow(()->new ResourceNotFound("User Not Found!"));
        
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

        return repo.save(userdb);
        
    }

    public User DeleteUserById(Long id){
        User user = repo.findById(id).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        repo.delete(user);
        return user;        
    }
}
