package com.deepthinking.service;

import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.entity.DragonDept;

import java.util.List;

public interface DragonDeptService {


    Result<Integer> syncDragonDeptList(String date);

}
