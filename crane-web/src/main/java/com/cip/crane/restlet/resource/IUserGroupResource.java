package com.cip.crane.restlet.resource;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import com.cip.crane.restlet.shared.UserGroupDTO;

public interface IUserGroupResource {

	@Post
	public boolean create(UserGroupDTO userGroupDTO);  
	
	@Get
	public UserGroupDTO read(int id);

	@Put
	public boolean update(UserGroupDTO userGroupDTO);
	
	@Delete
	public boolean deleteById();
	
}
