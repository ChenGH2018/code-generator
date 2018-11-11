package com.chenguihao.code_generate.service.service.shopauditservice.impl;

import com.chenguihao.code_generate.bean.shopaudit.ShopAudit;
import com.chenguihao.code_generate.plugin.Page;
import com.chenguihao.code_generate.service.service.shopauditservice.ShopAuditService;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 说明： 商家审核管理
 * 创建人：chenguihao
 * 创建时间：2018-11-11
 */

@Service
@Transactional
@Log4j
public class ShopAuditServiceImpl implements ShopAuditService {

    @Autowired
    ShopAuditMapper shopAuditMapper;

    @Override
    public Integer save(ShopAudit shopAudit) throws Exception {
        return shopAuditMapper.save(shopAudit);
    }

    @Override
    public Integer deleteOne(String shopAuditId) throws Exception {
        return shopAuditMapper.deleteOne(shopAuditId);
    }

    @Override
    public Integer updateBySelective(ShopAudit shopAudit) throws Exception {
        if(Strings.isNullOrEmpty(shopAudit.getId()) throw new RuntimeException("要修改的ID不能为空");
    }


    @Override
    public ShopAudit selectById(String id) {
        ShopAudit shopAudit = new ShopAudit();
        shopAudit.setId(id);
        List<ShopAudit> shopAudits = shopAuditMapper.selectBySelective(shopAudit);
        return shopAudits != null && shopAudits.size() == 1 ? shopAudits.get(0) : null;
    }

    @Override
    public List<ShopAudit> selectBySelective(ShopAudit shopAudit) {
        return shopAuditMapper.selectBySelective(shopAudit);

    }

    @Override
    public List<ShopAudit> getShopAuditMapperByPage(Page pg) {
        return shopAuditMapper.getShopAuditMapperByPage(pg);
    }

    @Override
    public Integer saveByList(List<ShopAudit> shopAuditMappers) {
        return shopAuditMapper.saveByList(shopAuditMappers);
    }

    @Override
    public Integer deleteArray(String[] ids) {
        return shopAuditMapper.deleteArray(ids);
    }


}

