package com.itecheasy.ph3.web.buyer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.itecheasy.common.Page;
import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.product.Product;
import com.itecheasy.ph3.product.ProductSalePrice;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.shopping.ShoppingCartItem;
import com.itecheasy.ph3.shopping.ShoppingCartProduct;
import com.itecheasy.ph3.shopping.ShoppingCartTotal;
import com.itecheasy.ph3.shopping.ShoppingService;
import com.itecheasy.ph3.shopping.ShoppingService.ShoppingCartSearchCriteria;
import com.itecheasy.ph3.shopping.ShoppingService.ShoppingCartSearchOrder;
import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.system.DeliveryFreightRegions;
import com.itecheasy.ph3.system.ShippingService.DeliveryTypeEnum;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.tag.FuncitonUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;
import com.itecheasy.ph3.web.vo.ShoppingCartItemVO;

public class BuyerShoppingCartAction extends BuyerBaseAction {
	private static final long serialVersionUID = 5420101218L;

	private ProductService productService;
	
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	/**
	 * 购物车列表
	 * 
	 * @return
	 */
	public String doShoppingCart() {
		
		Integer cartId = getShoppingCartId();
		List<ShoppingCartItemVO> cartList = null;
		ShoppingCartTotal cartTotal = null;
		Page page = null;
		int totalCartQty = 0;
		
		//保存被屏蔽的商品的页面显示信息
		List<ShoppingCartProduct> displayShoppingCartProductList = null;
		int displayTotalCartQty=0;
		
		if (cartId != null) {
			
			//查询被屏蔽的商品信息
			PageList<ShoppingCartProduct> displayShoppingCartProductPageList = getShoppingCartPage(cartId,1, 1000000, true, true,false,false);
			if(displayShoppingCartProductPageList.getData()!=null && !displayShoppingCartProductPageList.getData().isEmpty()){
				displayShoppingCartProductList = displayShoppingCartProductPageList.getData();
				displayTotalCartQty=displayShoppingCartProductPageList.getPage().getTotalRowCount();
			}
			
			//查询所有的商品信息
			PageList<ShoppingCartProduct> items = getShoppingCartPage(cartId,
					currentPage, 100, true, true,true,false);
			page = items.getPage();
			totalCartQty = page.getTotalRowCount();
			if (items.getData() != null && !items.getData().isEmpty()) {
				cartTotal = shoppingService.getShoppingCartTotalInfo(cartId);
				cartList = new LinkedList<ShoppingCartItemVO>();
				ShoppingCartItemVO vo;
				Product product;
				int itemStatus = 0;
				ShoppingCartItem item;
				for (ShoppingCartProduct scp : items.getData()) {
					product = scp.getProduct();
					item = scp.getItem();
					if (product == null) {
						continue;
					}
					if (!product.getIsDisplay()) {
						continue;
					} else if (product.getBatchStock() < 1) {
						itemStatus = 1; // 无库存
						item.setProductQty(0);
					} else if (product.getBatchStock().compareTo(
							item.getProductQty()) < 0) {
						itemStatus = 2; // 库存不足
						item.setProductQty(product.getBatchStock());
					} else {
						itemStatus = 0;
					}

					vo = new ShoppingCartItemVO();
					vo.setItem(item);
					vo.setProduct(product);
					vo.setProductSalePrice(scp.getProductSalePrice());
					vo.setProductCategoryName(scp.getShowCategoryName());
					vo.setItemStatus(itemStatus);
					cartList.add(vo);
				}
			}
		}

		// 设置下单时当前显示的页面
		beginPage(PlaceOrderPage.PAGE_SHIPPING_CART);

		//取得现金券可抵扣金额
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();
		if(minShoppingCartTotalInfo != null){
			BigDecimal cashCouponRedemptionAmount = minShoppingCartTotalInfo == null ? BigDecimal.ZERO : minShoppingCartTotalInfo.getCashCouponRedemptionAmount();
			//获取当前选择的货运方式
			DeliveryFreightRegion currentyDeliveryFreightRegion = minShoppingCartTotalInfo.getCurrentyDeliveryFreightRegion();
			//获取应付金额
			BigDecimal dueAmount = minShoppingCartTotalInfo.getDueAmount();
			//获取所有的货运类型
			List<DeliveryFreightRegion> deliveryFreightRegions = null;
			if(cartTotal != null)
			{
				deliveryFreightRegions = this.getDeliveryFreightRegions(SessionUtils.getAreaInfo(getSession()).getCountry().getId(),cartTotal.getTotalWeight(), cartTotal.getTotalVolume());
			}
			
			request.setAttribute("cashCouponRedemptionAmount", cashCouponRedemptionAmount);
			request.setAttribute("currentyDeliveryFreightRegion", currentyDeliveryFreightRegion);
			request.setAttribute("dueAmount", dueAmount);
			request.setAttribute("deliveryFreightRegions", deliveryFreightRegions);
		}
		

		request.setAttribute("error", param("error"));
		request.setAttribute("cartList", cartList);
		request.setAttribute("cartTotal", cartTotal);
		request.setAttribute("totalCartQty", totalCartQty);
		request.setAttribute("page", page);
		request.setAttribute("lastShoppingUrl", SessionUtils.getLastShoppingPageUrl(request));
		
		
		request.setAttribute("displayShoppingCartProductList", displayShoppingCartProductList);
		request.setAttribute("displayTotalCartQty", displayTotalCartQty);
		
		
		return SUCCESS;
	}

	/**
	 * Min 购物车列表
	 * 
	 * @return
	 */
	public String doMinCartList() {
		int pageIndex = paramInt("pageIndex", 1);
		if (pageIndex < 1) {
			pageIndex = 1;
		}
		List<ShoppingCartItemVO> items = null;
		ShoppingCartTotal cartTotal = null;
		MinShoppingCartTotalInfo miniCartTotal = null;
		int cartItemQty = 0;
		int prevPageIndex = -1;
		int nextPageIndex = -1;
		Integer cartId = getShoppingCartId();
		if (cartId != null) {
			PageList<ShoppingCartProduct> cartPageList = getShoppingCartPage(
					cartId, pageIndex, 8, pageIndex == 1, false,true,true);
			if (cartPageList != null) {
				cartItemQty = cartPageList.getPage().getTotalRowCount();
				if (cartItemQty > 0) {
					items = new LinkedList<ShoppingCartItemVO>();
					ShoppingCartItemVO vo;
					for (ShoppingCartProduct scp : cartPageList.getData()) {
						if (scp.getProduct() == null
								|| false == scp.getProduct().getIsDisplay()) {
							continue;
						}
						vo = new ShoppingCartItemVO();
						vo.setItem(scp.getItem());
						vo.setProduct(scp.getProduct());
						vo.setProductSalePrice(scp.getProductSalePrice());
						vo.setProductCategoryName(scp.getShowCategoryName());
						items.add(vo);
					}

					
					int totalPageCount = cartPageList.getPage().getPageCount();
					if (pageIndex > totalPageCount) {
						pageIndex = totalPageCount;
					}
					if (pageIndex > 1) {
						prevPageIndex = pageIndex - 1;
					}
					if (pageIndex < totalPageCount) {
						nextPageIndex = pageIndex + 1;
					}
				}
			}
			try {
				cartTotal = shoppingService.getShoppingCartTotalInfo(cartId);
				if( cartTotal != null)
				{
					miniCartTotal = getMinShoppingCartTotalInfo();
					if( miniCartTotal == null) miniCartTotal = new MinShoppingCartTotalInfo();
					
					miniCartTotal.setProductQty(cartTotal.getTotalProductQty());
					miniCartTotal.setProductPriceAfterDiscount(cartTotal.getPriceAfterDiscount());
					
					SessionUtils.setMinShoppingCartInfo(request, miniCartTotal);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//MinShoppingCartTotalInfo miniCartTotal = getMinShoppingCartTotalInfo();
		if(miniCartTotal != null){
			//获取购物车中的现金券可抵扣金额
			BigDecimal cashCouponRedemptionAmount = miniCartTotal.getCashCouponRedemptionAmount();
			//获取当前选择的货运方式
			DeliveryFreightRegion currentyDeliveryFreightRegion = miniCartTotal.getCurrentyDeliveryFreightRegion();
			//获取应付金额
			BigDecimal dueAmount = miniCartTotal.getDueAmount();
			//获取所有的货运类型
			List<DeliveryFreightRegion> deliveryFreightRegions = this.getDeliveryFreightRegions(SessionUtils.getAreaInfo(getSession()).getCountry().getId(),cartTotal.getTotalWeight(), cartTotal.getTotalVolume());
			
			request.setAttribute("cashCouponRedemptionAmount", cashCouponRedemptionAmount);
			request.setAttribute("currentyDeliveryFreightRegion", currentyDeliveryFreightRegion);
			request.setAttribute("dueAmount", dueAmount);
			request.setAttribute("deliveryFreightRegions", deliveryFreightRegions);
		}
		
		request.setAttribute("minCartItemQty", cartItemQty);
		request.setAttribute("minCartItems", items);
		request.setAttribute("minCartTotal", cartTotal);
		request.setAttribute("prevPageIndex", prevPageIndex);
		request.setAttribute("nextPageIndex", nextPageIndex);
		setCurrentPage(pageIndex);
		
		return SUCCESS;
	}

	/**
	 * 添加商品至购物车
	 */
	public void addCartItem() {
		int productId = paramInt("productId", 0);
		int qty = paramInt("qty", 0);
		String fm = "[{\"result\":%1$s,\"batchStock\":%2$s,\"productQty\":%3$s,\"cartPrice\":\"%4$s\"}]";
		int result = 0;
		int batchStock = 0;
		int cartProductQty = 0;
		String price = "0.00";
		if (productId > 0 && qty > 0) {
			Integer cartId = getShoppingCartId();
			try {
				//添加商品到购物车
				Integer id = shoppingService.addProductToShoppingCart(cartId,productId, qty);
				if (cartId == null) 
				{
					WebUtils.setShoppingCartId(response,request, id);
				}
				result = 1;
				batchStock = productService.getProductBatchStock(productId);

				cartProductQty = shoppingService.getShoppingCartTotalProductQty(id);
				BigDecimal totalPrice = cartProductQty < 1 ? BigDecimal.ZERO : shoppingService.getShoppingCartTotalPriceAfterDiscount(id);
				//price = FuncitonUtils.getPriceString(totalPrice);
	
				ShoppingCartTotal shoppingCartTotal = this.shoppingService.getShoppingCartTotalInfo(id);
				
				//添加商品重新计算订单重量和运费
				reComputeMiniCartOrderWeightAndFreight(shoppingCartTotal);
				
				MinShoppingCartTotalInfo miniCartTotal = getMinShoppingCartTotalInfo();
				if(miniCartTotal != null)
				{
					price = FuncitonUtils.getPriceString(miniCartTotal.getDueAmount());
				}
			} catch (BussinessException e) {
				String err = e.getErrorMessage();
				if (ShoppingService.ERROR_PRODUCT_OUT_OF_STOCK.equals(err)) {
					result = -1;
				} else if (ShoppingService.ERROR_PRODUCT_UNDER_STOCK
						.equals(err)) {
					result = -2;
					batchStock = productService.getProductBatchStock(productId);
				} else if (ShoppingService.ERROR_PRODUCT_PRICE_INVALID
						.equals(err)) {
					result = -3;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		try {
			//表示当前用户已修改了购物车信息
			beginPage(PlaceOrderPage.PAGE_SHIPPING_CART);
			
			returnJson(String.format(fm, result, batchStock, cartProductQty,
					price));
		} catch (IOException e) {
		}
	}

	/**
	 * 更新购物车商品数量
	 */
	public void updateCartItemQty() {
		int productId = paramInt("productId", 0);
		int qty = paramInt("qty", -1);
		Integer cartId = getShoppingCartId();
		String fm = "[{\"result\":%1$s,\"batchStock\":%2$s,%3$s,\"salePrice\":\"%4$s\",\"subSalePrice\":\"%5$s\",\"cashCouponRedemptionAmount\":%6$s}]";
		int result = 0;
		int batchStock = 0;
		String price = "0";
		String subPrice = "0";
		String totalInfoStr = "\"totalInfo\":null";
		BigDecimal cashCouponRedemptionAmount = BigDecimal.ZERO;//现金券可抵扣金额
		
		if (cartId != null && productId > 0 && qty > -1) {
			try {
				shoppingService.updateProductQtyOfShoppingCart(cartId,productId, qty);
				ShoppingCartTotal totalInfo = shoppingService.getShoppingCartTotalInfo(cartId);
				if (totalInfo == null) 
				{
					totalInfo = ShoppingCartTotal.EMPTY();
				}
				
				//修改商品重新计算订单重量和运费
				reComputeMiniCartOrderWeightAndFreight(totalInfo);		
				
				ProductSalePrice salePrice = productService.getProductFirstSalePrice(productId);
				price = FuncitonUtils.getPriceString(salePrice.getSalePriceAfterDiscount());
				subPrice = FuncitonUtils.getPriceString(salePrice.getSalePriceAfterDiscount().setScale(2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(qty)));
				result = 1;
				
				//检查现金券是否还可用，不可用则移除
				if( !checkCashCouponIsCanUse())
				{
					removeCashCouponFromShoppingCart();		
				}		
				cashCouponRedemptionAmount = getCashCouponRedemptionAmount();	
				
				//返回购物车的信息
				totalInfoStr = getShoppingCartTotalInfo(totalInfo);
			} catch (BussinessException e) {
				String errMsg = e.getErrorMessage();
				if (ShoppingService.ERROR_PRODUCT_OUT_OF_STOCK.equals(errMsg)) {
					result = -1;
				} else if (ShoppingService.ERROR_PRODUCT_UNDER_STOCK.equals(errMsg)) {
					batchStock = productService.getProductBatchStock(productId);
				}
			}
		}
		try {
			//表示当前用户已修改了购物车信息
			beginPage(PlaceOrderPage.PAGE_SHIPPING_CART);
			
			returnJson(String.format(fm, result, batchStock, totalInfoStr,price, subPrice,cashCouponRedemptionAmount));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除购物车记录
	 */
	public void deleteCartItem() {
		int productId = paramInt("productId", 0);
		Integer cartId = getShoppingCartId();
		StringBuilder jsonString = new StringBuilder();
		BigDecimal cashCouponRedemptionAmount = BigDecimal.ZERO;//现金券可抵扣金额

		if (cartId != null && productId > 0) {
			try {
				shoppingService.removeProductFromShoppingCart(cartId, productId);
				ShoppingCartTotal totalInfo = shoppingService.getShoppingCartTotalInfo(cartId);
				if (totalInfo == null) 
				{
					totalInfo = ShoppingCartTotal.EMPTY();
				}
				
				if( totalInfo.getTotalProductQty() > 0)
				{
					//删除商品重新计算订单重量和运费
					reComputeMiniCartOrderWeightAndFreight(totalInfo);	
				}
				else
				{//没有商品则移除Session
					SessionUtils.removeMinShoppingCartInfo(request);
				}
				
				jsonString.append("[{\"result\":1");
				jsonString.append(",").append(getShoppingCartTotalInfo(totalInfo));
				jsonString.append(",\"cashCouponRedemptionAmount\":%1$s}]");
				
				//检查现金券是否还可用，不可用则移除
				if( !checkCashCouponIsCanUse())
				{
					removeCashCouponFromShoppingCart();		
				}			
				cashCouponRedemptionAmount = getCashCouponRedemptionAmount();		
						
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//表示当前用户已修改了购物车信息
			beginPage(PlaceOrderPage.PAGE_SHIPPING_CART);
			
			jsonString.append("[{\"result\":0}]");
		}
		try {
			returnJson(String.format(jsonString.toString(),cashCouponRedemptionAmount));
		} catch (IOException e) {
		}
	}

	/**
	 * 清空购物车
	 */
	public void clearShoppingCart() {
		Integer cartId = getShoppingCartId();
		String result = "1";
		if (cartId != null) {
			try {
				shoppingService.clearShoppingCart(cartId);
				SessionUtils.removeMinShoppingCartInfo(request);
				
				removeCashCouponFromShoppingCart();				
			} catch (Exception e) {
				result = "0";
				e.printStackTrace();
			}
		}
		try {
			//表示当前用户已修改了购物车信息
			beginPage(PlaceOrderPage.PAGE_SHIPPING_CART);
			
			returnHtml(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 货运方式下拉菜单改变时保存货运方式
	 */
	public void changeShippingMethod(){
		String fm = "[{\"result\":%1$s,\"grantTotal\":%2$s,\"shippingAmount\":%3$s}]";
		int result = 0;
		BigDecimal dueAmount = BigDecimal.ZERO;
		BigDecimal shippingAmount = BigDecimal.ZERO;
		MinShoppingCartTotalInfo miniCartTotal = getMinShoppingCartTotalInfo();
		//MinShoppingCartTotalInfo miniCartTotal = SessionUtils.getMinShoppingCartInfo(request);
		CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
		if(cookieArea == null){
			result = 0;
		}else{
			Integer deliveryTypeId = paramInt("deliverTypeId");
			DeliveryTypeEnum deliveryType =null;
			if(deliveryTypeId == 1){
				deliveryType = DeliveryTypeEnum.Standard;
			}
			if(deliveryTypeId == 2){
				deliveryType = DeliveryTypeEnum.Expedited;
			}
			DeliveryFreightRegion currentyDeliveryFreightRegion = this.shippingService.getDeliveryFreightRegion(deliveryType, cookieArea.getCountry().getId(), miniCartTotal.getOrderWeigth(), SessionUtils.getDeliveryRemoteInfos(getSession()));
			miniCartTotal.setCurrentyDeliveryFreightRegion(currentyDeliveryFreightRegion);
			dueAmount = miniCartTotal.getDueAmount();
			shippingAmount = miniCartTotal.getTotalFreight();
			SessionUtils.setMinShoppingCartInfo(getRequest(), miniCartTotal);
			result = 1;
		}
		
		try {
			returnJson(String.format(fm, result, dueAmount, shippingAmount));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doShoppingCartInfo() {
		String fm = "[{\"productQty\":%1$s,\"cartPrice\":\"%2$s\"}]";
		int qty = 0;
		String price = "0.00";
		MinShoppingCartTotalInfo info = getMinShoppingCartTotalInfo();
		if (info != null) {
			qty = info.getProductQty();
			price = FuncitonUtils.getPriceString(info.getDueAmount());
		}
		try {
			returnJson(String.format(fm, qty, price));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getShoppingCartTotalInfo(ShoppingCartTotal totalInfo) 
	{
	    MinShoppingCartTotalInfo miniCartTotal = getMinShoppingCartTotalInfo();
	    DeliveryFreightRegion deliveryFreightRegion = miniCartTotal.getCurrentyDeliveryFreightRegion();
	
		StringBuilder jsonString = new StringBuilder();
		jsonString.append("\"totalInfo\":{");
		jsonString.append("\"discount\":").append(totalInfo.getDiscount());
		jsonString.append(",\"weight\":\"").append(FuncitonUtils.getWeightString(totalInfo.getTotalWeight(), true)).append("\"");
		jsonString.append(",\"priceBeforeDiscount\":\"").append(FuncitonUtils.getPriceString(totalInfo.getPriceBeforeDiscount())).append("\"");
		jsonString.append(",\"priceAfterDiscount\":\"").append(FuncitonUtils.getPriceString(totalInfo.getPriceAfterDiscount())).append("\"");
		jsonString.append(",\"productQty\":").append(totalInfo.getTotalProductQty());
		if(deliveryFreightRegion != null){
			jsonString.append(",\"shippingAmount\":\"").append(FuncitonUtils.getPriceString(deliveryFreightRegion.getTotalFreight())).append("\"");
			jsonString.append(",\"shippingType\":\"").append(deliveryFreightRegion.getDeliveryTypeId()).append("\"");
		}else{
			jsonString.append(",\"shippingAmount\":\"0.00\"");
			jsonString.append(",\"shippingType\":\"0\"");
		}
		if(miniCartTotal != null){
			jsonString.append(",\"grantTotal\":\"").append(FuncitonUtils.getPriceString(miniCartTotal.getDueAmount())).append("\"");
		}else{
			jsonString.append(",\"grantTotal\":\"0.00\"");
		}
		BigDecimal standard = BigDecimal.ZERO;
		BigDecimal expedited = BigDecimal.ZERO;
		List<DeliveryFreightRegion> deliveryFreightRegions = this.getDeliveryFreightRegions(SessionUtils.getAreaInfo(getSession()).getCountry().getId(),totalInfo.getTotalWeight(), totalInfo.getTotalVolume());
		if(! deliveryFreightRegions.isEmpty()){
			for(DeliveryFreightRegion df : deliveryFreightRegions){
				if(df.getDeliveryTypeId() == 1){
					standard = df.getTotalFreight();
				}
				if(df.getDeliveryTypeId() == 2){
					expedited = df.getTotalFreight();
					
				}
			}
			jsonString.append(",\"standard\":").append(standard);
			jsonString.append(",\"expedited\":").append(expedited);
//			if(deliveryFreightRegions.get(0) != null && deliveryFreightRegions.get(0).getDeliveryTypeId() == 1){
//				jsonString.append(",\"standard\":").append(deliveryFreightRegions.get(0).getTotalFreight());
//			}
//			if(deliveryFreightRegions.get(1) != null && deliveryFreightRegions.get(1).getDeliveryTypeId() == 2){
//				jsonString.append(",\"expedited\":").append(deliveryFreightRegions.get(1).getTotalFreight());
//			}
		}else{
			jsonString.append(",\"standard\":\"0.00\"");
			jsonString.append(",\"expedited\":\"0.00\"");
		}
		
		jsonString.append(",\"priceSaved\":\"").append(FuncitonUtils.getPriceString(totalInfo.getPriceSaved())).append("\"}");
		
		
		return jsonString.toString();
	}  

	/**
	 * 参数isDisplay是否显示被屏蔽的商品
	 * 参数isMini是正常的购物车还是Mini购物车
	 */
	private PageList<ShoppingCartProduct> getShoppingCartPage(Integer cartId,
			int pageIndex, int pageSize, boolean checkOutProductQty,
			boolean isRemoveInvalidItems,boolean isDisplay,boolean isMini) {
		// 1、先获取购物车的原始信息
		PageList<ShoppingCartProduct> items=null;
		Map<ShoppingCartSearchCriteria, Object> searchCriteria = new HashMap<ShoppingCartSearchCriteria, Object>();
		searchCriteria.put(ShoppingCartSearchCriteria.ISDISPLAY, isDisplay);

		//获取被屏蔽的商品信息，需要进行条件查询、排序
		if(!isDisplay)
		{			
			List<SearchOrder<ShoppingCartSearchOrder>> searchOrder = new ArrayList<SearchOrder<ShoppingCartSearchOrder>>();
			searchOrder.add(new SearchOrder<ShoppingCartSearchOrder>(ShoppingCartSearchOrder.JOIN_DATE,false));
			
			items = shoppingService.searchShoppingCart(pageIndex, pageSize, cartId,searchCriteria, searchOrder);
		}
		else
		{		
			if(!isMini)
			{
				items = shoppingService.searchShoppingCart(pageIndex, pageSize, cartId,searchCriteria, null);
				if (checkOutProductQty) {
					// 2、则校验购物车信息的有效性
					shoppingService.checkOutProductQtyOfShoppingCart(cartId);
				}
				if (isRemoveInvalidItems) {
					// 从购物车中移除购买量为0 的数据
					shoppingService.removeInvalidItemsFromShoppingCart(cartId);
				}
				if (checkOutProductQty || isRemoveInvalidItems) {
					//SessionUtils.removeMinShoppingCartInfo(request);
				}				
			}
			else
			{
				items = shoppingService.searchShoppingCart(pageIndex, pageSize, cartId,searchCriteria, null);
			}			
		}		
		
		//清除无库存和库存不足的订购量后，需要重新计算迷你购物车中的信息
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();		
		ShoppingCartTotal totalInfo = shoppingService.getShoppingCartTotalInfo(getShoppingCartId());
		if( minShoppingCartTotalInfo != null && totalInfo != null )
		{
			BigDecimal currentOrderWeight = shippingService.computeOrderWeight(totalInfo.getTotalWeight(), totalInfo.getTotalVolume());
			BigDecimal oldOrderWeight = minShoppingCartTotalInfo.getOrderWeigth();
			if( currentOrderWeight.compareTo(oldOrderWeight) != 0)
			{//当前重量与Session重量不相同时则重新计算						
				reComputeMiniCartOrderWeightAndFreight(totalInfo);
			}							
		}
		
		return items;
	}	
	
	

	/**
	 * 检查当前购物车中的现金券是否仍能使用
	 * 
	 * @return 是否可使用，如果没有使用现金券或可用则返回true,否则返回false
	 */
	private boolean checkCashCouponIsCanUse()
	{				
		//如已使用了现金券，检查当前商品金额是否达到现金券最小限额，如不能达到则取消使用		
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();		
		if( minShoppingCartTotalInfo != null && minShoppingCartTotalInfo.getCashCouponInfo() != null )			
		{
			//如果货物折后金额小于现金券最小限额，则移除现金券
			return minShoppingCartTotalInfo.getProductPriceAfterDiscount().compareTo(minShoppingCartTotalInfo.getCashCouponInfo().getMinOrderAmount()) >= 0;
		}		
		else
		{//没有使用现金券则返回true
			return true;
		}
	}
	
	private BigDecimal getCashCouponRedemptionAmount()
	{
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();

		if( minShoppingCartTotalInfo == null) return BigDecimal.ZERO;
		
		return minShoppingCartTotalInfo.getCashCouponRedemptionAmount();
	}
	
	private void removeCashCouponFromShoppingCart()
	{
		setCashCouponForShoppingCart(null);
	}
}
