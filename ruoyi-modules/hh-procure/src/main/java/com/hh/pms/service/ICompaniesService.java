package com.hh.pms.service;

import com.ruoyi.system.api.domain.Companies;

import java.util.List;

/**
 * 公司表Service接口
 * 
 * @author ruoyi
 * @date 2023-12-04
 */
public interface ICompaniesService 
{
    /**
     * 查询公司表
     * 
     * @param companiesId 公司表主键
     * @return 公司表
     */
    public Companies selectCompaniesByCompaniesId(Long companiesId);

    /**
     * 查询公司表列表
     * 
     * @param companies 公司表
     * @return 公司表集合
     */
    public List<Companies> selectCompaniesList(Companies companies);

    /**
     * 新增公司表
     * 
     * @param companies 公司表
     * @return 结果
     */
    public int insertCompanies(Companies companies);

    /**
     * 修改公司表
     * 
     * @param companies 公司表
     * @return 结果
     */
    public int updateCompanies(Companies companies);

    /**
     * 批量删除公司表
     * 
     * @param companiesIds 需要删除的公司表主键集合
     * @return 结果
     */
    public int deleteCompaniesByCompaniesIds(Long[] companiesIds);

    /**
     * 删除公司表信息
     * 
     * @param companiesId 公司表主键
     * @return 结果
     */
    public int deleteCompaniesByCompaniesId(Long companiesId);
}
