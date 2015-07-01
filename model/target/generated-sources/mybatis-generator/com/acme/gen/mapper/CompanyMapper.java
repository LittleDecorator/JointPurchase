package com.acme.gen.mapper;

import com.acme.gen.domain.Company;
import com.acme.gen.domain.CompanyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CompanyMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int countByExample(CompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int deleteByExample(CompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int insert(Company record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int insertSelective(Company record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    List<Company> selectByExample(CompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    Company selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int updateByExampleSelective(@Param("record") Company record, @Param("example") CompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int updateByExample(@Param("record") Company record, @Param("example") CompanyExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int updateByPrimaryKeySelective(Company record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table COMPANY
     *
     * @mbggenerated Wed Jul 01 14:43:43 MSK 2015
     */
    int updateByPrimaryKey(Company record);
}