package com.itecheasy.ph3.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.common.Utilities;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.seo.Seo;
import com.itecheasy.ph3.seo.SeoService;
import com.itecheasy.ph3.shopping.ShoppingCartTotal;
import com.itecheasy.ph3.shopping.ShoppingService;
import com.itecheasy.ph3.system.CookieConfig;
import com.itecheasy.ph3.system.CookieConfigService;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.system.DeliveryRemoteInfo;
import com.itecheasy.ph3.system.ShippingService;
import com.itecheasy.ph3.system.CookieConfigService.CookieType;
import com.itecheasy.ph3.system.ShippingService.DeliveryTypeEnum;
import com.itecheasy.ph3.web.buyer.BuyerPageController;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.buyer.order.OrderSessionUtils;
import com.itecheasy.ph3.web.buyer.order.SessionOrder;
import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;
import com.itecheasy.ph3.web.vo.ShowCategoryTree;
import com.sun.istack.FinalArrayList;

/**
 * 前台页面Action基类 <br/> 已注册以下服务(继承类不要重复注册)： <br/>CategoryService
 * <br/>ShoppingService
 */
public class BuyerBaseAction extends BaseAction {

	private static final long serialVersionUID = 122225455L;
	/**
	 * 当前类别
	 */
	private ShowCategory currentCategory;
	private Integer shoppingCartId = -1;

	protected CategoryService categoryService;
	protected ShoppingService shoppingService;
	protected CustomerService customerService;
	protected SeoService seoService;
	protected ShippingService shippingService;
	protected CookieConfigService cookieConfigService;
	protected Seo seo;


	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setShoppingService(ShoppingService shoppingService) {
		this.shoppingService = shoppingService;
	}

	public ShowCategory getCurrentCategory() {
		return currentCategory;
	}
	
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setCurrentCategory(ShowCategory currentCategory) {
		this.currentCategory = currentCategory;
	}

	public void setShippingService(ShippingService shippingService) {
		this.shippingService = shippingService;
	}

	public void setCookieConfigService(CookieConfigService cookieConfigService) {
		this.cookieConfigService = cookieConfigService;
	}

	/**
	 * 获取2级类别选定ID
	 */
	public Integer getSelectedNavCategoryId() {
		if (this.currentCategory == null) {
			return null;
		}
		int level = categoryService.getShowCategoryLevel(this.currentCategory
				.getId());
		if (2 == level) {
			return this.currentCategory.getId();
		} else if (3 == level) {
			if (this.currentCategory.getParent() != null) {
				return this.currentCategory.getParent().getId();
			}
		}
		return null;
	}

	/**
	 * 类别树
	 */
	public List<ShowCategoryTree> getCategoryTreeList() {
		return getCategoryTree(null);
	}

	protected List<ShowCategoryTree> getCategoryTree(Integer parentCategoryId) {
		List<ShowCategoryTree> categoryTreeList = null;
		List<ShowCategory> categories;
		if (parentCategoryId == null) {
			categories = categoryService.getVisibleRootShowCategories();
		} else {
			categories = categoryService
					.getVisibleSubShowCategories(parentCategoryId);
		}
		if (categories != null && !categories.isEmpty()) {
			categoryTreeList = new ArrayList<ShowCategoryTree>();
			ShowCategoryTree categoryTree;
			for (ShowCategory category : categories) {
				categoryTree = new ShowCategoryTree();
				categoryTree.setCategory(category);
				categoryTree
						.setSubCategories(getCategoryTree(category.getId()));
				categoryTreeList.add(categoryTree);
			}
		}
		return categoryTreeList;
	}

	public Integer getShoppingCartId() {
		if (shoppingCartId == null || this.shoppingCartId.equals(-1)) {
			this.shoppingCartId = WebUtils.getShoppingCartId(request);
		}
		return this.shoppingCartId;
	}

	/**
	 * 购物车商品信息
	 */
	public MinShoppingCartTotalInfo getMinShoppingCartTotalInfo() 
	{
		Integer id = getShoppingCartId();
		if (id == null) 
		{
			SessionUtils.setMinShoppingCartInfo(request, null);
			return null;
		}
		
		MinShoppingCartTotalInfo info = SessionUtils.getMinShoppingCartInfo(request);
		if (info == null) 
		{
			ShoppingCartTotal shoppingCartTotal = this.shoppingService.getShoppingCartTotalInfo(id);
			if(shoppingCartTotal != null)
			{
				int productQty = shoppingCartTotal.getTotalProductQty();
				BigDecimal totalPrice = productQty <1 ? BigDecimal.ZERO : shoppingCartTotal.getPriceAfterDiscount();
				
				//获取货默认运方式信息，存入到Session中
				BigDecimal orderWeight = shippingService.computeOrderWeight(shoppingCartTotal.getTotalWeight(), shoppingCartTotal.getTotalVolume());
				info = new MinShoppingCartTotalInfo(productQty, totalPrice,orderWeight,this.getDefaultDeliveryFreightRegionByAreaInfo(orderWeight));
				SessionUtils.setMinShoppingCartInfo(request, info);
		     }
		}

		return info;
	}
	
	/**
	 * 重新计算购物车中的订单信息
	 * @param totalInfo
	 */
	protected void reComputeMiniCartOrderWeightAndFreight(ShoppingCartTotal totalInfo)
	{
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();
		if( minShoppingCartTotalInfo == null) return;
		BigDecimal orderWeight = shippingService.computeOrderWeight(totalInfo.getTotalWeight(), totalInfo.getTotalVolume());
		minShoppingCartTotalInfo.setOrderWeight(orderWeight);
		minShoppingCartTotalInfo.setProductQty(totalInfo.getTotalProductQty());
		minShoppingCartTotalInfo.setProductPriceAfterDiscount(totalInfo.getPriceAfterDiscount());
		
		SessionUtils.setMinShoppingCartInfo(request,minShoppingCartTotalInfo);
		
		//重新计算订单重量后，需重新计算运费
		reComputeMiniShoppingCartFreight();
	}
	
	
	/**
	 * 重新计算迷你购物车中的运费(根据区域设置和购物车中的商品的订单重量计算运费)
	 * 注意：重新计算运费时，将采用默认的运输类型
	 */
	protected void reComputeMiniShoppingCartFreight()
	{
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = SessionUtils.getMinShoppingCartInfo(request);
		if( minShoppingCartTotalInfo == null) return;
		
		BigDecimal orderWeight = minShoppingCartTotalInfo.getOrderWeigth();
		
		CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
		List<DeliveryRemoteInfo> deliveryRemoteInfos = SessionUtils.getDeliveryRemoteInfos(getSession());
		DeliveryFreightRegion deliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(cookieArea.getCountry().getId(),orderWeight, deliveryRemoteInfos);
		
		minShoppingCartTotalInfo.setCurrentyDeliveryFreightRegion(deliveryFreightRegion);
	
		SessionUtils.setMinShoppingCartInfo(request,minShoppingCartTotalInfo);
	}

	/**
	 * 获取未登录时的客户信息(即Cookie中保存的购物车对应的客户信息)
	 */
	public Customer getDefaultCustomer()
	{
		//如果当前客户已登录，则直接返回当前客户信息
		if( getLoginedUserBuyer() != null)
		{
			return getLoginedUserBuyer();
		}
		
		//从Cookie中获得购物车ID
		Integer shoppingCartID = getShoppingCartId();
		if (shoppingCartID == null) 
		{//如果没有购物车ID，则没有客户信息
			return null;
		}
		
		//根据购物ID获取客户ID
		Integer customerId = shoppingService.getCustomerIdOfShoppingCart(shoppingCartId);
		if( customerId == null)
		{//如果购物车没有对应的客户，则直接返回
			return null;
		}
		
		//返回查到的客户信息
		Customer customer = customerService.getCustomer(customerId);
		return customer;
	}
	

	protected String getAlias( ShowCategory category){
		return category.getAlias()!= null && !category.getAlias().equals("")?category.getAlias():category.getName();
	}
	public void setSeoService(SeoService seoService) {
		this.seoService = seoService;
	}

	public Seo getSeo() {
		return seo;
	}
	protected void redirect301(String url){
		if(url != null){
		      response.setStatus(301);
		      response.setHeader("Location", url);
			  response.setHeader("Connection", "close");
		  }
	}
	
	protected void refreshMiniShoppingCart(Integer shoppingCartId) {
		ShoppingCartTotal shoppingCartTotal = this.shoppingService.getShoppingCartTotalInfo(shoppingCartId);
		Integer cartProductQty = shoppingCartTotal.getTotalProductQty();
		BigDecimal totalPrice = cartProductQty <1 ? BigDecimal.ZERO : shoppingCartTotal.getPriceAfterDiscount();
		BigDecimal orderWeight = shippingService.computeOrderWeight(shoppingCartTotal.getTotalWeight(), shoppingCartTotal.getTotalVolume());
		SessionUtils.setMinShoppingCartInfo(request,new MinShoppingCartTotalInfo(cartProductQty,totalPrice,orderWeight,this.getDefaultDeliveryFreightRegionByAreaInfo(orderWeight)));
		
//		Integer cartProductQty = shoppingService.getShoppingCartTotalProductQty(shoppingCartId);
//		BigDecimal totalPrice = cartProductQty < 1 ? BigDecimal.ZERO : shoppingService.getShoppingCartTotalPriceAfterDiscount(shoppingCartId);
//		SessionUtils.setMinShoppingCartInfo(request,new MinShoppingCartTotalInfo(cartProductQty,totalPrice));
	}
	
	protected MinShoppingCartTotalInfo setCashCouponForShoppingCart(CashCoupon cashCoupon)
	{
		SessionUtils.setCashCouponToShoppingCart(request, cashCoupon);
		
		return getMinShoppingCartTotalInfo();
	}
	
	/**
	 * 获得默认的货运方式信息
	 */
	public DeliveryFreightRegion getDefaultDeliveryFreightRegionByAreaInfo(BigDecimal orderWeight){
		DeliveryFreightRegion currentyDeliveryFreightRegion = null;
		CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
		if(cookieArea != null)
		{
			List<DeliveryRemoteInfo> deliveryRemoteInfos = SessionUtils.getDeliveryRemoteInfos(getSession());
			if( deliveryRemoteInfos != null )
			{
				currentyDeliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(cookieArea.getCountry().getId(),orderWeight, deliveryRemoteInfos);
			}
			else
			{
				currentyDeliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(cookieArea.getCountry(),orderWeight, cookieArea.getCity(),cookieArea.getZip());
			}
		}
		return currentyDeliveryFreightRegion;
	}
	
	public DeliveryFreightRegion getDefaultDeliveryFreightRegion(BigDecimal totalWeight, BigDecimal totalVolume){
		BigDecimal orderWeight = shippingService.computeOrderWeight(totalWeight, totalVolume);
		return getDefaultDeliveryFreightRegionByAreaInfo(orderWeight);
	}
	

	protected DeliveryTypeEnum getDeliveryTypeEnum(Integer deliveryTypeId)
	{
		if( deliveryTypeId == null) return null;
		
		if( DeliveryTypeEnum.Expedited.getValue().intValue() == deliveryTypeId.intValue()) return DeliveryTypeEnum.Expedited;
		else return DeliveryTypeEnum.Standard;
	}
	    
	/**
	 * 获取所有的货运类型
	 * @param totalWeight
	 * @param totalVolume
	 * @return
	 */
	public List<DeliveryFreightRegion> getDeliveryFreightRegions(Integer countryId,BigDecimal totalWeight, BigDecimal totalVolume)
	{
		BigDecimal orderWeight = shippingService.computeOrderWeight(totalWeight, totalVolume);
		return this.getDeliveryFreightRegions(countryId,orderWeight);
	}
	
	public List<DeliveryFreightRegion> getDeliveryFreightRegions(Country country,BigDecimal orderWeight,String city,String zip)
	{
		if( country == null) return null;

		DeliveryFreightRegion standard = shippingService.getDeliveryFreightRegion(DeliveryTypeEnum.Standard, country, orderWeight, city,zip);
		DeliveryFreightRegion expedited = shippingService.getDeliveryFreightRegion(DeliveryTypeEnum.Expedited, country, orderWeight, city,zip);
		List<DeliveryFreightRegion> deliveryFreightRegions = new ArrayList<DeliveryFreightRegion>();
		if(standard != null){
			deliveryFreightRegions.add(standard);
		}
		if(expedited != null){
			deliveryFreightRegions.add(expedited);
		}
		
		return deliveryFreightRegions;
	}
	
	public List<DeliveryFreightRegion> getDeliveryFreightRegions(Integer countryId,BigDecimal orderWeight)
	{
		if( countryId == null) return null;

		DeliveryFreightRegion standard = shippingService.getDeliveryFreightRegion(DeliveryTypeEnum.Standard, countryId, orderWeight, SessionUtils.getDeliveryRemoteInfos(getSession()));
		DeliveryFreightRegion expedited = shippingService.getDeliveryFreightRegion(DeliveryTypeEnum.Expedited, countryId, orderWeight, SessionUtils.getDeliveryRemoteInfos(getSession()));
		List<DeliveryFreightRegion> deliveryFreightRegions = new ArrayList<DeliveryFreightRegion>();
		if(standard != null){
			deliveryFreightRegions.add(standard);
		}
		if(expedited != null){
			deliveryFreightRegions.add(expedited);
		}
		
		return deliveryFreightRegions;
	}
	
	protected void setAreaInfo(Currency currency,Country country,String city,String zip)
	{
		setAreaInfo(currency);
		setAreaInfo(country, city, zip);
	}
	
	protected void setAreaInfo(Country country,String city,String zip)
	{
		CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
		if(cookieArea == null || country.getId().intValue() != cookieArea.getCountry().getId().intValue() || StrUtils.convertEmptyStringOfNull(city).compareTo(StrUtils.convertEmptyStringOfNull(cookieArea.getCity())) != 0 || StrUtils.convertEmptyStringOfNull(zip).compareTo(StrUtils.convertEmptyStringOfNull(cookieArea.getZip())) != 0 )
		{
			setAreaInfo(request,shippingService,cookieConfigService,country,city,zip);
			
			//修改区域设置后重新计算运费
			reComputeMiniShoppingCartFreight();
		}
	}
	
	public static void setAreaInfo(HttpServletRequest request,ShippingService shippingService,CookieConfigService cookieConfigService,Currency currency,Country country,String city,String zip)
	{
		setAreaInfo(request, cookieConfigService, currency);
		setAreaInfo(request, shippingService, cookieConfigService, country, city, zip);
	}
	
	private  static void asynGetRemoteInfo(final HttpSession session,final ShippingService shippingService,final Country country,final String city,final String zip){
		Thread t = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//从DHL站点检查地址是否为偏远地区
				boolean dhlIsRemote = shippingService.isRemoteAreaByDHLSite(country,city, zip);
				
				//查询区域设置的地址信息在各种货运方式的偏远情况(**************注意此处代码不要删除，购物车中的运费计算皆从这里获得偏远信息，基于性能考虑***************************)
				/*List<DeliveryRemoteInfo> deliveryRemoteInfos = shippingService.getRemoteInfos(country, city, zip);
				SessionUtils.setDeliveryRemoteInfos(deliveryRemoteInfos, session);*/
				
				List<DeliveryRemoteInfo> deliveryRemoteInfos = SessionUtils.getDeliveryRemoteInfos(session);
				for (DeliveryRemoteInfo deliveryRemoteInfo : deliveryRemoteInfos) 
				{
					if( deliveryRemoteInfo.getDeliveryId().intValue() == ConfigHelper.DELIVERY_DHL_ID)
					{
						if( deliveryRemoteInfo.isRemoteArea() != dhlIsRemote)
						{
							deliveryRemoteInfo.setRemoteArea(dhlIsRemote);
							break;
						}
						else 
						{
							return;
						}
					}
				}
				
				//刷新购物车运费
				MinShoppingCartTotalInfo minShoppingCartTotalInfo = SessionUtils.getMinShoppingCartInfo(session);
				if( minShoppingCartTotalInfo == null) return;
				
				BigDecimal orderWeight = minShoppingCartTotalInfo.getOrderWeigth();				
				CookieArea cookieArea = SessionUtils.getAreaInfo(session);
				DeliveryFreightRegion deliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(cookieArea.getCountry().getId(),orderWeight, deliveryRemoteInfos);				
				minShoppingCartTotalInfo.setCurrentyDeliveryFreightRegion(deliveryFreightRegion);			
				SessionUtils.setMinShoppingCartInfo(session,minShoppingCartTotalInfo);
		   }
		});
		t.start();
	}
	public static void setAreaInfo(HttpServletRequest request,ShippingService shippingService,CookieConfigService cookieConfigService,Country country,String city,String zip)
	{
		CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());
		
		if( !Utilities.isEmpty(city) || !Utilities.isEmpty(zip) )
		{	//从DHL站点检查地址是否为偏远地区
		    //shippingService.isRemoteAreaByDHLSite(country,city, zip);
			
			//查询区域设置的地址信息在各种货运方式的偏远情况(**************注意此处代码不要删除，购物车中的运费计算皆从这里获得偏远信息，基于性能考虑***************************)
			List<DeliveryRemoteInfo> deliveryRemoteInfos = shippingService.getRemoteInfos(country, city, zip);
			SessionUtils.setDeliveryRemoteInfos(deliveryRemoteInfos, request.getSession());
			
			asynGetRemoteInfo(request.getSession(),shippingService, country, city, zip);
		}				
		
		city = city == null ? "" : city;
		zip = zip == null ? "" : zip;		
		//如果区域设置的地址信息与Session里的相同，则不用执行下面操作
		if( cookieArea != null && cookieArea.getCountry() != null )
		{
			String areaCity = cookieArea.getCity() == null ? "" : cookieArea.getCity();
			String areaZip = cookieArea.getZip() == null ? "" : cookieArea.getZip();
			if( cookieArea.getCountry().getId().intValue() == country.getId().intValue() && areaCity.equals(city) && areaZip.equals(zip)) return;
		}
		
		//将区域设置保存到数据库
		CookieConfig cookieConfig = new CookieConfig();
		if( cookieArea == null )
		{
			String uuid = UUID.randomUUID().toString();
			cookieConfig.setUuid(uuid);
			
			cookieArea = new CookieArea();
			cookieArea.setUuid(uuid);
		}
		else 
		{
			cookieConfig.setUuid(cookieArea.getUuid());
		}
		
		Map<CookieType, String> map = new HashMap<CookieType, String>();
		map.put(CookieType.COUNTRY, country.getId().toString());
		map.put(CookieType.CITY, city);
		map.put(CookieType.ZIP, zip);
		cookieConfig.setItems(map);
		cookieConfigService.setCookie(cookieConfig);
		
		//将区域设置保存到Session
		cookieArea.setCountry(country);
		cookieArea.setZip(zip);
		cookieArea.setCity(city);
		SessionUtils.setAreaInfo(cookieArea, request.getSession()); 
	}
	
	protected void setAreaInfo(Currency currency)
	{
		setAreaInfo(request,cookieConfigService,currency);
	}
	
	public static void setAreaInfo(HttpServletRequest request,CookieConfigService cookieConfigService,Currency currency)
	{
		if( currency == null ) return ;
		
		CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());
		
		if( cookieArea != null && cookieArea.getCurrency().getId().intValue() == currency.getId().intValue()) return;
		
		//将区域设置保存到数据库
		CookieConfig cookieConfig = new CookieConfig();
		if( cookieArea == null )
		{
			String uuid = UUID.randomUUID().toString();
			cookieConfig.setUuid(uuid);
			
			cookieArea = new CookieArea();
			cookieArea.setUuid(uuid);
		}
		else 
		{
			cookieConfig.setUuid(cookieArea.getUuid());
		}
		Map<CookieType, String> map = new HashMap<CookieType, String>();
		map.put(CookieType.CURRENCY, currency.getId().toString());
		cookieConfig.setItems(map);
		cookieConfigService.setCookie(cookieConfig);
		
		//将区域设置保存到Session
		cookieArea.setCurrency(currency);
		SessionUtils.setAreaInfo(cookieArea, request.getSession()); 
		
		//如果已开始下单，且未确认过支付方式，则修改下单流程中默认支付币种
		SessionOrder sessionOrder = OrderSessionUtils.getSessionOrder(request.getSession());
		if(sessionOrder != null && !sessionOrder.isConfirmPayInfo())
		{
			sessionOrder.getPaymentInfo().setCurrency(currency);
			OrderSessionUtils.setSessionOrder(request, sessionOrder);
		}
	}
	
	/**
	 * 检查下单过程中，上一步显示的页面是否与完成操作的页面相同,如果完成操作的页面是上一步显示的页面后面的步骤，则不允许往下操作
	 * 主要是防止通过直接输入URL链接跳转到后面的页面
	 * @return 
	 */
	protected String checkBeginPageAndEndPage(PlaceOrderPage currentPageNum)
	{
	   return BuyerPageController.checkBeginPageAndEndPage(request, currentPageNum);
	}
	
	protected void beginPage(PlaceOrderPage pageNum) 
	{
		BuyerPageController.beginPage(request,pageNum);
	}
	
	protected void endPage(PlaceOrderPage pageNum) 
	{
		BuyerPageController.endPage(request, pageNum);
	}
	
	protected PlaceOrderPage getBeginPage() {
		return BuyerPageController.getBeginPage(request);
	}
	
	protected PlaceOrderPage getEndPage() {
		return BuyerPageController.getEndPage(request);
	}	
}
