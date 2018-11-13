package com.chenguihao.code_generate.mapper.memberinfo;

import com.chenguihao.code_generate.bean.memberinfo.Memberinfo;
import com.chenguihao.code_generate.plugin.Page;
import java.util.List;

/**
 * 说明： 人员信息管理
 * 创建人：chenguihao
 * 创建时间：2018-11-13
 */
public interface MemberinfoMapper {

    Integer save(Memberinfo memberinfo);

    Integer saveByList(List<Memberinfo> memberinfos);

    Integer updateBySelective(Memberinfo memberinfo);

    Integer deleteOne(String id);

    List<Memberinfo> selectBySelective(Memberinfo memberinfo);

    List<Memberinfo> getMemberinfoByPage(Page pg);

    Integer deleteArray(String[] ids);

}
