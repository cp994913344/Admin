package com.cnpc.packmall.order.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.http.HttpRequest;
import com.aliyuncs.http.HttpResponse;
import com.cnpc.framework.base.entity.Dict;
import com.cnpc.framework.utils.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cnpc.packmall.order.service.OrderService;
import com.cnpc.framework.annotation.RefreshCSRFToken;
import com.cnpc.framework.annotation.VerifyCSRFToken;
import com.cnpc.framework.base.pojo.Result;
import com.cnpc.framework.util.wxpay.MyConfig;
import com.cnpc.framework.util.wxpay.WXPayUtil;
import com.cnpc.packmall.order.entity.Order;
import com.cnpc.packmall.order.entity.OrderStateChange;
import com.cnpc.packmall.order.pojo.dto.OrderDTO;

/**
* 订单管理管理控制器
* @author jrn
* 2018-08-16 16:52:48由代码生成器自动生成
*/
@Controller
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @RequestMapping(value="/list",method = RequestMethod.GET)
    public String list(){
        return "packmall/order/order_list";
    }

    @RefreshCSRFToken
    @RequestMapping(value="/edit",method = RequestMethod.GET)
    public String edit(String id,HttpServletRequest request){
        request.setAttribute("id", id);
        return "packmall/order/order_edit";
    }

    @RequestMapping(value="/detail",method = RequestMethod.GET)
    public String detail(String id,HttpServletRequest request){
        request.setAttribute("id", id);
        return "packmall/order/order_detail";
    }

    @RequestMapping(value="/get/{id}",method = RequestMethod.POST)
    @ResponseBody
    public Order get(@PathVariable("id") String id){
        return orderService.get(Order.class, id);
    }

    @VerifyCSRFToken
    @RequestMapping(value="/save")
    @ResponseBody
    public Result save(Order order){
        if(StrUtil.isEmpty(order.getId())){
            orderService.save(order);
        }
        else{
            Order order_old=orderService.get(Order.class,order.getId());
            BeanUtils.copyProperties(order,order_old, "userId");
            order_old.setUpdateDateTime(new Date());
            orderService.update(order_old);
        }
        return new Result(true);
    }



    @RequestMapping(value="/delete/{id}",method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@PathVariable("id") String id){
        Order order=this.get(id);
        try{
        	orderService.delete(order);
            return new Result();
        }
        catch(Exception e){
            return new Result(false,"该数据已经被引用，不可删除");
        }
    }

    @RequestMapping(value="/change",method = RequestMethod.GET)
    public String change(String id,HttpServletRequest request){
    	request.setAttribute("id", id);
        return "packmall/order/order_change";
    }
    /**
     * 获取订单列表
     * @param openid
     * @param param
     * @return
     */
    @RequestMapping(value="/pack_mall_api/getlist/{openid}",method = RequestMethod.POST)
    @ResponseBody
    public Result packMallgetList(@PathVariable("openid") String openid,@RequestParam Map<String, String> param){
    	
        List<OrderDTO> orders=orderService.packMallgetList(openid,param);
        Result result = new Result();
        result.setData(orders);
        return result;
    }
    /**
     * 小程序下单
     * @param openid
     * @param con
     * @return
     */
    @RequestMapping(value="/pack_mall_api/save/{openid}",method = RequestMethod.POST)
    @ResponseBody
    public Result packMallgetSave(@PathVariable("openid") String openid,String con){
    	
    	OrderDTO orderDTO = JSON.parseObject(con,OrderDTO.class);
    	Map<String, String> orderMap=orderService.savePackMallOrder(openid,orderDTO);
        Result result = new Result();
        result.setData(orderMap);
        return result;
    }
    /**
     * 订单状态变更
     * @param openid
     * @param orderId
     * @param type
     * @param remark
     * @return
     */
    @RequestMapping(value="/pack_mall_api/update_state/{openid}",method = RequestMethod.POST)
    @ResponseBody
    public Result packMallgetUpdateState(@PathVariable("openid") String openid,String orderId,String type,String remark){
        orderService.doConfirm(openid,orderId,type,remark);
        return new Result(true);
    }
    
    /**
     * 查询订单流转信息
     * @param orderId
     * @return
     */
    @RequestMapping(value="/pack_mall_api/find_order_change",method = RequestMethod.POST)
    @ResponseBody
    public Result packMallgetUpdateState(String orderId){
    	
    	List<OrderStateChange> orderStateChanges = orderService.findOrderChangeByOrderId(orderId);
    	
    	return new Result(true,orderStateChanges);
    }
    /**
     * 订单信息统计
     * @param openid
     * @return
     */
    @RequestMapping(value="/pack_mall_api/order_statistics/{openId}",method = RequestMethod.POST)
    @ResponseBody
    public Result orderStatistics(@PathVariable("openId") String openId){
    	
    	Map<String, String> orderStatistics = orderService.findStatisticsByOpenId(openId);
    	
    	return new Result(true,orderStatistics);
    }
    
    /**
     * 根据订单id查询支付id并签名
     * @param orderId
     * @return
     */
    @RequestMapping(value="/pack_mall_api/get_order_payid/{openId}",method = RequestMethod.POST)
    @ResponseBody
    public Result getOrderPayid(String orderId,@PathVariable("openId") String openId){
    	
    	Map<String, String> result = orderService.doOrderPay(orderId,openId);
    	
    	return new Result(true,result);
    }
}
