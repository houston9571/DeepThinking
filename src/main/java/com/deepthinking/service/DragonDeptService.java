package com.deepthinking.service;

import com.deepthinking.core.base.Result;
import com.deepthinking.mysql.entity.DragonDept;

import java.util.List;

public interface DragonDeptService {


    Result<List<DragonDept>> syncDragonDeptList(String date);

}
