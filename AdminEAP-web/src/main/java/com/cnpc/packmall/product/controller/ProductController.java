package com.cnpc.packmall.product.controller;

import java.util.*;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cnpc.framework.base.entity.Dict;
import com.cnpc.framework.base.service.DictService;
import com.cnpc.framework.constant.RedisConstant;
import com.cnpc.framework.utils.StrUtil;
import com.cnpc.packmall.SKU.entity.Sku;
import com.cnpc.packmall.SKU.service.SkuService;
import com.cnpc.packmall.center.entity.ShippingAddress;
import com.cnpc.packmall.product.entity.ProductDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cnpc.packmall.product.service.ProductService;
import com.cnpc.framework.annotation.RefreshCSRFToken;
import com.cnpc.framework.annotation.VerifyCSRFToken;
import com.cnpc.framework.base.pojo.Result;
import com.cnpc.packmall.product.entity.Product;

/**
* 商品管理控制器
* @author jrn
* 2018-08-20 16:10:15由代码生成器自动生成
*/
@Controller
@RequestMapping("/packmall/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @Resource
    private DictService dictService;

    @Resource
    private SkuService skuService;

    @RequestMapping(value="/list",method = RequestMethod.GET)
    public String list(){
        return "packmall/product/product_list";
    }

    @RequestMapping(value="/add",method = RequestMethod.GET)
    public String add(){
        return "packmall/product/product_add";
    }

    @RequestMapping(value="/getProductDict",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> getProductDict(){
        Map<String,Object> result = new HashMap<>(8);
        List<Dict> colorDictList = dictService.getDictsByCode("PACKMALL_PRODUCT_COLOR");
        List<Dict> typeDictList = dictService.getDictsByCode("PACKMALL_PRODUCT_TYPE");
        List<Dict> qualityDictList = dictService.getDictsByCode("PACKMALL_PRODUCT_QUALITY");
        result.put("colorDictList",colorDictList);
        result.put("typeDictList",typeDictList);
        result.put("qualityDictList",qualityDictList);
         return result;
    }

    /**
     * 保存 字典值
     * @param dict
     * @return
     */
    @RequestMapping(value = "/saveDict", method = RequestMethod.POST)
    @ResponseBody
    public Result saveDict(Dict dict) {
        if(dict!=null&& StringUtils.isNotEmpty(dict.getName())&&StringUtils.isNotEmpty(dict.getCode())){
            return dictService.saveEntity(dict );
        }else{
            return new Result(false);
        }
    }

    @RefreshCSRFToken
    @RequestMapping(value="/edit",method = RequestMethod.GET)
    public String edit(String id,HttpServletRequest request){
        request.setAttribute("productId", id);
        return "packmall/product/product_edit";
    }

    @RequestMapping(value="/getbyId",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> getbyId(String id){
        Map<String,Object> result = productService.findDetailByProducid(id);
        result.put("product",productService.get(Product.class, id));
        return result;
    }

    @RequestMapping(value="/save",method = RequestMethod.POST)
    @ResponseBody
    public Result save(String con,String productCon){
        if(StringUtils.isEmpty(con)||StringUtils.isEmpty(productCon)){
            return  new Result(false);
        }
        try{
            Product product =JSONObject.parseObject(productCon,Product.class);
            if(product==null||StringUtils.isEmpty(product.getHeadImgId())
                    ||StringUtils.isEmpty(product.getProductName())
                    ||product.getProductCycle()==null||product.getProductCycle()<1
                    ||product.getProductSort()==null||product.getProductSort()<1){
                return new Result(false,"数据不完整");
            }
            List<ProductDetail> productDetailList = JSON.parseArray(con,ProductDetail.class);
            return  productService.savedata(productDetailList,product);
        }catch (Exception e) {
            return new Result(false);
        }
    }

    @RequestMapping(value="/editProdcut",method = RequestMethod.POST)
    @ResponseBody
    public Result editProdcut(String con,String productCon){
        if(StringUtils.isEmpty(con)||StringUtils.isEmpty(productCon)){
            return  new Result(false);
        }
        try {
            Product product =JSONObject.parseObject(productCon,Product.class);
            if(product==null||StringUtils.isEmpty(product.getId())
                      ||StringUtils.isEmpty(product.getProductName())||StringUtils.isEmpty(product.getHeadImgId())
                    ||product.getProductCycle()==null||product.getProductCycle()<1
                    ||product.getProductSort()==null||product.getProductSort()<1){
                return new Result(false,"数据不完整");
            }
            List<ProductDetail> productDetailList = JSON.parseArray(con, ProductDetail.class);
            return productService.updatedata(productDetailList, product);
        }catch (Exception e) {
            return new Result(false);
        }
    }

    @RequestMapping(value="/delete/{id}",method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@PathVariable("id") String id){
        Product product=productService.get(Product.class,id);
        try{
            if(product!=null) {
                //查询是否有sku
                Integer skuNum = skuService.findSkuNumByProductId(product.getId());
                if(skuNum>0){
                    return new Result(false,"该商品有sku，请先删除该产品的sku！");
                }
            }
            product.setDeleted(1);
        	productService.update(product);
            return new Result();
        }
        catch(Exception e){
            return new Result(false,"该数据已经被引用，不可删除");
        }
    }

    /**
     * sku获取商品列表
     * @return
     */
    @RequestMapping(value="/getProductList",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> getProductList(){
        Map<String,Object> result = new HashMap<>(2);
        List<Product> productList = productService.findProductList();
        result.put("productList",productList);
        return result;
    }

     //获取商品 对应质量等信息
    @RequestMapping(value="/getDetailbyProductId",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> getDetailbyProductId(String id){
        return productService.findDetailByProducid(id);
    }

    /**
     * 修改商品状态 启用 暂停
     * @param id
     * @return
     */
    @RequestMapping(value="/updateStauts",method = RequestMethod.POST)
    @ResponseBody
    public boolean updateStauts(String id){
        if(StringUtils.isEmpty(id)){
            return false;
        }
        return productService.updateStauts(id);
    }

    //————————————————————小程序接口start——————————————————————————

    /**
     * 获取所有商品list
     * @return
     */
    @RequestMapping(value="/pack_mall_api/getList")
    @ResponseBody
    public  Result getList(){
        Map<String,Object> result = new HashMap<>(8);
        List<Product> productList = productService.findList();
        result.put("productList", productList);
        return new Result(true,result);
    }

    /**
     * 获取 商品页信息
     * @param id
     * @return
     */
    @RequestMapping(value="/pack_mall_api/getProductById",method = RequestMethod.POST)
    @ResponseBody
    public  Result getProductById(String id){
        Map<String,Object> result = productService.findDetailByProducid(id);
        Product  product  =productService.get(Product.class, id);
        List<Sku> skuList = skuService.findByProductId(id);
        result.put("skuList", skuList);
        //把最小价格填写上
        if(skuList!=null&&skuList.size()>0){
            for(Sku s :skuList) {
                if(product.getId().equals(s.getProductId())){
                    if(product.getMixPrice()==null||product.getMixPrice().equals(0)){
                        product.setMixPrice(s.getMixPrice());
                    }else{
                        if(product.getMixPrice().compareTo(s.getMixPrice())== 1){
                            product.setMixPrice(s.getMixPrice());
                        }
                    }
                }
            }
        }
        result.put("product",product);
        return new Result(true,result);
    }

    /**
     * 通过商品ids获取商品送后周期
     * @param ids
     * @return
     */
    @RequestMapping(value="/pack_mall_api/getProductCycleByIds",method = RequestMethod.POST)
    @ResponseBody
    public  Result getProductCycleByIds(String ids[]){
        if(ids==null||ids.length==0){
            return new Result(false,"请传入商品id");
        }
        Set<String> idList =  new HashSet<>(Arrays.asList(ids));
        if(idList!=null&&idList.size()>0){
            return new Result(true,productService.getProductCycleByIds(idList));
        }
        return new Result(false,"查询错误");
    }
}
