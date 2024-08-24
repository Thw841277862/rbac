package com.example.rbac.sys.service;

import com.example.rbac.sys.resp.UserResp;

import java.util.List;

public interface IDataService {
    List<Long> getDeptIds(UserResp userResp);
}
