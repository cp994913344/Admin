package com.cnpc.framework.query.service;

import java.util.List;
import java.util.Map;

import com.cnpc.framework.base.service.BaseService;
import com.cnpc.framework.query.entity.QueryConfig;

public interface QueryService extends BaseService {

    /**
     * 通用加载数据方法
     *
     * @param reqObj 前台请求参数
     * @return
     */
    Map<String, Object> loadData(String reqObj) throws Exception;


    /**
     * 通用数据导出，生成excel
     *
     * @param reqObj    请求参数，可能含多个tab页面
     * @param tableName 表名
     * @return 生成导出文件的名称
     * @throws Exception
     */
    String exportData(String reqObj, String tableName) throws Exception;

    /**
     * 用户自定义的隐藏列
     * @param queryId
     * @param pageName
     * @param userid
     * @return
     */
    List<String> getSelectedColumns(String queryId, String pageName, String userid);

    /**
     * 保存用户自定义设置
     *
     * @param queryConfig
     */
    void deleteAndSave(QueryConfig queryConfig);

    /**
     * 恢复默认
     *
     * @param queryConfig
     */
    void delete(QueryConfig queryConfig);
}
