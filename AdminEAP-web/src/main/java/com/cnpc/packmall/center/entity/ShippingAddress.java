package com.cnpc.packmall.center.entity;

import com.cnpc.framework.annotation.Header;
import com.cnpc.framework.base.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 收货地址
 * @author cuipeng
 * @create 2018-08-15 10:38
 **/
@Entity
@Table(name="TB_PACKMALL_SHIPPING_ADDRESS")
public class ShippingAddress extends BaseEntity{

    @Header(name="openID")
    @Column(name="open_id",length = 60)
    private String openId;

    @Header(name="收货人")
    @Column(name="shipping_name")
    private String shippingName;

    @Header(name="城市编号")
    @Column(name = "AREACODE", length = 20)
    private String areaCode;

    @Header(name="城市name")
    @Column(name = "AREA_ADDRESS", length = 20)
    private String areaAddress;

    @Header(name="收货地址")
    @Column(name="shipping_addresss", length = 100)
    private String shippingAddress;

    @Header(name="联系方式")
    @Column(name="shipping_phone")
    private String shippingPhone;

    /**
     * 1 默认 2 非默认
     */
    @Header(name="是否默认收货地址")
    @Column(name="shipping_default",length = 4)
    private Integer shippingDefault;

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingName() {

        return shippingName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaCode() {

        return areaCode;
    }

    public void setShippingDefault(Integer shippingDefault) {
        this.shippingDefault = shippingDefault;
    }

    public Integer getShippingDefault() {

        return shippingDefault;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getOpenId() {

        return openId;
    }

    public void setAreaAddress(String areaAddress) {
        this.areaAddress = areaAddress;
    }

    public String getAreaAddress() {

        return areaAddress;
    }
}
