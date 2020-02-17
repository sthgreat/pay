package com.dzkjdx.pay.dao;

import com.dzkjdx.pay.pojo.PayInfo;
import org.apache.ibatis.annotations.Mapper;

public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    PayInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);
}