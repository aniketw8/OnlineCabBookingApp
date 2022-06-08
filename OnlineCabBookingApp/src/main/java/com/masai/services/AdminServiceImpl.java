package com.masai.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.DTO.AdminDTO;
import com.masai.exceptions.CustomerException;
import com.masai.exceptions.UsernameNotFoundException;
import com.masai.modelEntity.Admin;
import com.masai.modelEntity.AdminSession;
import com.masai.modelEntity.ModelUser;
import com.masai.repository.AdminDao;
import com.masai.repository.AdminSessionDao;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	AdminDao adminDao;
	@Autowired
	AdminSessionDao adminSessionDao;

	@Override
	public Admin adminRegister(Admin admin) {
		return adminDao.save(admin);
	}

	@Override
	public Admin updatePassword(AdminDTO dto, String username, String key) {
		Optional<AdminSession> otp=adminSessionDao.findByUuid(key);
		Admin updated= null;
		if(otp.isEmpty()) throw new CustomerException("Admin is not logged in, Please login first!");
		Optional<Admin> opt = adminDao.findByUserUsername(username);
		if (opt.isEmpty()) throw new UsernameNotFoundException("Username not found");
		else {
			Admin toUpdate = opt.get();
			Integer id = toUpdate.getAdminId();
			ModelUser user = toUpdate.getUser();
			user.setPassword(dto.getPassword());
			Admin newOne = new Admin(id, user);
			updated= adminDao.save(newOne);
		}
		return updated;

	}

	@Override
	public Admin update(Admin admin, String Username, String key) {
		Admin updated =null;
		
		Optional<AdminSession> otp=adminSessionDao.findByUuid(key);
		
		if(otp.isEmpty()) throw new CustomerException("Admin is not logged in, Please login first!");
		else {
		Optional<Admin> opt=adminDao.findByUserUsername(Username);
		if(opt.isEmpty()) throw new UsernameNotFoundException("Username not found");
		else{
			Admin toUpdate = opt.get();
			Integer id = toUpdate.getAdminId();
			Admin newOne = new Admin(id, admin.getUser());
			updated= adminDao.save(newOne);
		}
		}
		return updated;
	
	}

	@Override
	
	public String deleteByUsername(AdminDTO dto, String key) {
		Optional<AdminSession> otp=adminSessionDao.findByUuid(key);
		if(otp.isEmpty()) throw new CustomerException("Admin is not logged in, Please login first!");
		else {
		Optional<Admin> opt= adminDao.findByUserUsername(dto.getUsername());
		if(opt.isEmpty())  throw new UsernameNotFoundException("Username not found");
		else {
			Admin toBeDelete=opt.get();
			adminDao.delete( toBeDelete);
		}
		}
		return "Your Id with Username "+dto.getUsername()+" is Deleted.";
	}

}