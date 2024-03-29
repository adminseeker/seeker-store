package com.adminseeker.userservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.userservice.entities.Address;
import com.adminseeker.userservice.entities.User;
import com.adminseeker.userservice.exceptions.LoginError;
import com.adminseeker.userservice.exceptions.ResourceNotFound;
import com.adminseeker.userservice.exceptions.ResourceUpdateError;
import com.adminseeker.userservice.repository.UserRepo;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@Service
@Transactional
public class AddressService {
    
    @Autowired
    UserRepo repo;

    public Address addAddress(Long userId,Address address,Map<String,String> headers){

        User user = repo.findById(userId).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        if(!user.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        List<Address> addressList = user.getAddressList();
        addressList.add(address);
        user.setAddressList(addressList);
        User updatedUser = repo.save(user);

        return updatedUser.getAddressList().get(updatedUser.getAddressList().size()-1);
    }

    public List<Address> getAddressList(Long userId,Map<String,String> headers){
        User user = repo.findById(userId).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        if(!user.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        List<Address> addressList = user.getAddressList();
        return addressList;
    }

    public Address getAddressById(Long userId,Long addressId,Map<String,String> headers){
        User user = repo.findById(userId).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        if(!user.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        List<Address> addressList = user.getAddressList();
        Address address = null;
        for (Address addr : addressList){
            if(addr.getAddressId().equals(addressId)){
                address=addr;
                break;
            }
        }
        if(address==null) throw new ResourceNotFound("Address Not Found!");
        return address;
        
    }

    public Address updateAddressById(Long userId,Address address,Long addressId,Map<String,String> headers) throws Exception{
        
        User userdb = repo.findById(userId).orElseThrow(()->new ResourceNotFound("User Not Found!"));
        if(!userdb.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        
        if(address==null) throw new ResourceUpdateError("Nothing to update!");
        if(address.getCity()==null || address.getAddressId()!=null || address.getStreet()==null
            || address.getCountry()==null || address.getNickname()==null || address.getPhone()==null
            || address.getPostalCode()==null || address.getState()==null){
                throw new ResourceUpdateError("Invalid Address!");
            }

        List<Address> addressList = userdb.getAddressList();
        Boolean isPresent=false;
        for (Address addr : addressList){
            if(addr.getAddressId().equals(addressId)){
                Integer index = addressList.indexOf(addr);
                address.setAddressId(addressId);
                addressList.set(index, address);
                isPresent=true;
                break;
            }             
        }        
        if (!isPresent) throw new ResourceNotFound("Address Not Found!");
        
        userdb.setAddressList(addressList);
        repo.save(userdb);
        return address;
    }

    public Address deleteAddressById(Long userId,Long addressId,Map<String,String> headers){
        User user = repo.findById(userId).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        if(!user.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("Unauthorised User!");
        List<Address> addressList = user.getAddressList();
        Address address=null;
        Boolean isPresent = false;
        for (Address addr : addressList){
            if(addr.getAddressId().equals(addressId)){
                address=addr;
                addressList.remove(addr);
                isPresent=true;
                break;
            }
        }
        if (!isPresent) throw new ResourceNotFound("Address Not Found!");
        user.setAddressList(addressList);       
        repo.save(user);
        return address;        
    }
}
