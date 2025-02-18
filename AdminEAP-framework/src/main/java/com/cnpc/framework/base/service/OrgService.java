package com.cnpc.framework.base.service;

import java.util.List;

import com.cnpc.framework.base.entity.Org;
import com.cnpc.framework.base.pojo.Result;
import com.cnpc.framework.base.pojo.TreeNode;

public interface OrgService extends BaseService {

    List<TreeNode> getTreeData();

    List<Org> getOrgsByCode(String code);

    boolean referByUser(String orgId);

    Result getOrgNames(String id);
}
