package com.chenguihao.code_generate.service.service.shopauditserviceimpl.impl;

import com.chenguihao.code_generate.bean.ShopAuditServiceImpl;
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
    public Integer save(ShopAuditServiceImpl shopAudit) throws Exception {
        return shopAuditMapper.save(shopAudit);
    }

    @Override
    public Integer deleteOne(String shopAuditId) throws Exception {
        return shopAuditMapper.deleteOne(shopAuditId);
    }

    @Override
    public Integer updateBySelective(ShopAuditServiceImpl shopAudit) throws Exception {
        if(Strings.isNullOrEmpty(shopAudit.getId()) throw new RuntimeException("要修改的ID不能为空");
    }


    @Override
    public ShopAuditServiceImpl selectById(String id) {
        ShopAuditServiceImpl shopAudit = new ShopAuditServiceImpl();
        shopAudit.setId(id);
        List<ShopAuditServiceImpl> shopAudits = shopAuditMapper.selectBySelective(shopAudit);
        return shopAudits != null && shopAudits.size() == 1 ? shopAudits.get(0) : null;
    }

    @Override
    public List<ShopAuditServiceImpl> selectBySelective(ShopAuditServiceImpl shopAudit) {
        return shopAuditMapper.selectBySelective(shopAudit);

    }

    @Override
    public List<ShopAuditServiceImpl> getShopAuditMapperByPage(Page pg) {
        return shopAuditMapper.getShopAuditMapperByPage(pg);
    }

    @Override
    public Integer saveByList(List<ShopAuditServiceImpl> shopAuditMappers) {
        return shopAuditMapper.saveByList(shopAuditMappers);
    }

    @Override
    public Integer deleteArray(String[] ids) {
        return shopAuditMapper.deleteArray(ids);
    }


}

