package com.adminseeker.userservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.userservice.entities.User;
import com.adminseeker.userservice.exceptions.DuplicateEmailException;
import com.adminseeker.userservice.exceptions.ResourceNotFound;
import com.adminseeker.userservice.exceptions.ResourceUpdateError;
import com.adminseeker.userservice.repository.UserRepo;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    UserRepo repo;

    public User addUser(User user){

        User userdb = repo.findByEmail(user.getEmail()).orElse(null); 
        
        if(userdb!=null) throw new DuplicateEmailException("Email Already Exists!");

        return repo.save(user);
    }

    public List<User> getUsers(){
        return repo.findAll();
    }

    public User getUserById(Long id){
        User user = repo.findById(id).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        return user;
        
    }

    public User updateUserById(User user,Long id) throws Exception{
        
        User userdb = repo.findById(id).orElseThrow(()->new ResourceNotFound("User Not Found!"));
        
        if(user.getEmail()!=null){
            throw new ResourceUpdateError("Email Change Is No Allowed!");
        }

        if(user.getRole()!=null){
            throw new ResourceUpdateError("Role Change Is No Allowed!");
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
