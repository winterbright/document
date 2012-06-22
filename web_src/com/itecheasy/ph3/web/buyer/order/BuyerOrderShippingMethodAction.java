package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.itecheasy.ph3.customer.Address;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.email.EmailService;
import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.system.DeliveryRemoteInfo;
import com.itecheasy.ph3.system.ShippingService.DeliveryTypeEnum;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.vo.DeliveryFreightRegionVO;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;
import com.itecheasy.sslplugin.annotation.Secured;

@Secured
public class BuyerOrderShippingMethodAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;

	/**
	 * 取默认货运方式时，EMS比较运费的系数
	 */
	private static final BigDecimal EMS_FREIGHT_RATE = new BigDecimal("1.2");

	/**
	 * 获取货运方式 
	 */
	public String doShippingMethod() 
	{
		String commandName = param("commandName");
		if ( commandName == null || commandName.equals(""))
		{//初始化，第一次进入页面
			return initShippingMethod();
		}
		else
		{//在本页面刷新
			return postShippingMethod();
		}		
	}
	
	private String initShippingMethod()
	{
		SessionOrder sessionOrder = getSessionOrder();
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_SHIPPING_METHOD);
		if (checkResult != null) 
		{
			return checkResult;
		}

		Address shippingAddress = sessionOrder.getShippingAddress();

		// 检查货运地址是否为P.O.Box地址
		boolean isPOBoxAddress = checkPOBoxAddress(shippingAddress.getStreet1()) || checkPOBoxAddress(shippingAddress.getStreet2());

		List<DeliveryFreightRegionVO> deliveryFreightRegionVOs = this.getDeliveryFreightRegionVOs(sessionOrder.getOrderWeight(), isPOBoxAddress,shippingAddress);
		Integer currentDeliveryTypeId = null;
		if( deliveryFreightRegionVOs == null || deliveryFreightRegionVOs.size() == 0)
		{
			Customer customer = getLoginedUserBuyer();
			
			//没有运费，则记录日志并发邮件给客服
			warnNotHaveFreight(customer.getEmail() , shippingAddress.getCountry().getName(), sessionOrder.getOrderWeight(), "网站Checkout", "ph3");
		}
		else 
		{
			//获得当前使用的货运类型			
			if( !isPOBoxAddress )
			{				
				currentDeliveryTypeId = sessionOrder.getDelivery() == null || sessionOrder.getDelivery().getDeliveryType() == null ? null : sessionOrder.getDelivery().getDeliveryType().getId();
				if( currentDeliveryTypeId == null)
				{
					//注意：下单过程中不能用Sesson缓存的偏远信息，因为可能在下单过程中改变区域设置,但下单中的运费依据的是货运地址信息
//					List<DeliveryRemoteInfo> deliveryRemoteInfos = SessionUtils.getDeliveryRemoteInfos(getSession());
					DeliveryFreightRegion defaultDeliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(shippingAddress.getCountry(), sessionOrder.getOrderWeight(), shippingAddress.getCity(),shippingAddress.getZip());
					if( defaultDeliveryFreightRegion != null)
					{
						currentDeliveryTypeId = defaultDeliveryFreightRegion.getDeliveryTypeId();
						sessionOrder.setDelivery(toDeliveryVO(defaultDeliveryFreightRegion));	   
					}
				}
			}
			else 
			{//如果是P.O.BOX地址，则只能使用平邮
				DeliveryFreightRegion currentDeliveryFreightRegion = null;
				for (DeliveryFreightRegionVO deliveryFreightRegionVO : deliveryFreightRegionVOs) 
				{
					if(deliveryFreightRegionVO.getDeliveryType().getId().intValue() == DeliveryTypeEnum.Standard.getValue().intValue() )
					{
						currentDeliveryTypeId = deliveryFreightRegionVO.getDeliveryType().getId();
						currentDeliveryFreightRegion = deliveryFreightRegionVO.getDeliveryFreightRegion();						
					}
				}
				
				if( currentDeliveryFreightRegion != null )
				{
					sessionOrder.setDelivery(toDeliveryVO(currentDeliveryFreightRegion));	    
				}
				else 
				{
					sessionOrder.setDelivery(null);
				}
			}
		}
		
		beginPage(PlaceOrderPage.PAGE_SHIPPING_METHOD);

		
		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("deliveryFreightRegionVOs", deliveryFreightRegionVOs);
		request.setAttribute("isPOBoxAddress", isPOBoxAddress);
		request.setAttribute("currentDeliveryTypeId", currentDeliveryTypeId);
		request.setAttribute("currentPage",PlaceOrderPage.PAGE_SHIPPING_METHOD);

		return SUCCESS;
	}
	
	//获取运费区间
	public List<DeliveryFreightRegionVO> getDeliveryFreightRegionVOs(BigDecimal orderWeigth, boolean isPOBoxAddress,Address shippingAddress)
	{
		//下单过程中不能用Sesson缓存的偏远信息，因为可能在下单过程中改变区域设置
		List<DeliveryFreightRegion> deliveryFreightRegions = this.getDeliveryFreightRegions(getSessionOrder().getShippingAddress().getCountry(),orderWeigth,shippingAddress.getCity(),shippingAddress.getZip());
		
		List<DeliveryFreightRegionVO> deliveryFreightRegionVOs = new ArrayList<DeliveryFreightRegionVO>();
		for(DeliveryFreightRegion deliveryFreightRegion : deliveryFreightRegions)
		{
			DeliveryFreightRegionVO deliveryFreightRegionVO = new DeliveryFreightRegionVO();
			deliveryFreightRegionVO.setDeliveryFreightRegion(deliveryFreightRegion);	
			deliveryFreightRegionVO.setDeliveryType(this.shippingService.getDeliveryTypeByDeliveryId(deliveryFreightRegion.getDeliveryId()));
			
			//P.O.BOX地址不能选择快递方式
			if (isPOBoxAddress && deliveryFreightRegion.getDeliveryTypeId().intValue() == DeliveryTypeEnum.Expedited.getValue().intValue()) 
			{
				deliveryFreightRegionVO.setEnable(false);
			}
			else 
			{
				deliveryFreightRegionVO.setEnable(true);
			}

			deliveryFreightRegionVOs.add(deliveryFreightRegionVO);
		}
		return deliveryFreightRegionVOs;
	}	
	
	private String postShippingMethod(){
		SessionOrder sessionOrder = getSessionOrder();
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_SHIPPING_METHOD);
		if (checkResult != null) {
			return checkResult;
		}

		Address shippingAddress = sessionOrder.getShippingAddress();

		// 检查货运地址是否为P.O.Box地址
		boolean isPOBoxAddress = checkPOBoxAddress(shippingAddress.getStreet1());
		if (!isPOBoxAddress) {
			isPOBoxAddress = checkPOBoxAddress(shippingAddress.getStreet2());
		}

//		// 获取所有的货运方式
//		List<DeliveryVO> deliveryVOList = getDeliverys(shippingAddress,
//				sessionOrder.getTotalWeight(), sessionOrder.getTotalVolume(),
//				isPOBoxAddress);
//
//		// 获取当前已选择的货运方式
//		Integer currentDeliveryId = null;
//		if (deliveryVOList != null && !deliveryVOList.isEmpty()) {
//			if (sessionOrder.getDelivery() == null) { // 如果当前没有选择货运方式，则获取默认的货运方式
//				currentDeliveryId = getDefaultDelivery(deliveryVOList,
//						isPOBoxAddress);
//			} else {
//				currentDeliveryId = sessionOrder.getDelivery().getBaseInfo()
//						.getId();
//			}
//
//			// 如果地址是PO BOX ,当前货运方式是不允许的货运方式，则去掉当前选择
//			if (isPOBoxAddress && currentDeliveryId != null
//					&& isDisabledDeliveryOfPOBOX(currentDeliveryId)) {
//				sessionOrder.setDelivery(null);
//				currentDeliveryId = null;
//			}
//		}
		MinShoppingCartTotalInfo miniCartTotal = getMinShoppingCartTotalInfo();
		List<DeliveryFreightRegionVO> deliveryFreightRegionVOs = null;
		if(miniCartTotal != null){
			deliveryFreightRegionVOs = this.getDeliveryFreightRegionVOs(miniCartTotal.getOrderWeigth(), isPOBoxAddress,shippingAddress);
		}
		
		beginPage(PlaceOrderPage.PAGE_SHIPPING_METHOD);

		Integer currentDeliveryTypeId = paramInt("delivery");
		
		sessionOrder.setIsRealInvoice(paramBool("customs"));
		
		String shippingComment = request.getParameter("shippingComment");
		sessionOrder.setShippingComment(shippingComment);
		
		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("deliveryFreightRegionVOs", deliveryFreightRegionVOs);
		request.setAttribute("isPOBoxAddress", isPOBoxAddress);
		request.setAttribute("currentDeliveryTypeId", currentDeliveryTypeId);
		request.setAttribute("currentPage",PlaceOrderPage.PAGE_SHIPPING_METHOD);

		return SUCCESS;
	}

	// 保存货运方式
	public void saveShippingMethod() throws IOException {
		if (!isValidSessionByJson())
			return;

		String fm = "[{\"result\":%1$s}]";
		int result = 0;

		try {

			Integer deliveryTypeId = paramInt("ShippingMethodId");

			if (deliveryTypeId != null && param("CustomsType", null) != null) {				
				// 设置发票类型
				boolean isRealInvoice = paramBool("CustomsType");

				// 设置发货提醒
				String shippingComment = param("ShippingComment").trim();
				if (shippingComment.length() > 0) {
					//shippingComment = StringEscapeUtils.escapeHtml(shippingComment).replace("\n", "<br/>");
				}
				// 设置货运方式
				setShippingMethod(deliveryTypeId,isRealInvoice,shippingComment);


				result = 1;
				endPage(PlaceOrderPage.PAGE_SHIPPING_METHOD);
			}

		} catch (Exception e) {
			errorLog(e);
		}

		returnJson(String.format(fm, result));
	}

/*	private List<DeliveryVO> getDeliverys(Address shippingAddress,
			BigDecimal totalWeight, BigDecimal totalVolume,
			boolean isPOBoxAddress) {
		if (shippingAddress == null)
			return null;
		// 获取所有的货运方式
		List<Delivery> deliveryList = shippingService
				.getVisibleDeliverysByCountry(shippingAddress.getCountry(),
						totalWeight, totalVolume);

		List<DeliveryVO> deliveryVOList = new ArrayList<DeliveryVO>();
		DeliveryVO deliveryVO;
		BigDecimal orderWeight = null;
		for (Delivery delivery : deliveryList) {
			// 计算订单重量
			orderWeight = shippingService.computeOrderWeight(delivery.getId(),
					shippingAddress.getCountry().getId(), totalWeight,
					totalVolume);

			// 检查是否有DHL运输方式，如果有，则到DHL站点查询是否偏远地区
			if (!isPOBoxAddress
					&& ConfigHelper.DELIVERY_DHL_ID == delivery.getId()) {
				try {
					shippingService.isRemoteAreaByDHLSite(shippingAddress
							.getCountry(), shippingAddress.getCity(),
							shippingAddress.getZip());
				} catch (Exception e) {
					// TODO: handle exception
					errorLog(e);
				}
			}
			deliveryVO = toDeliveryVO(delivery, shippingAddress, orderWeight);
			deliveryVO.setEnable(true);

			// 如果是P.O.Box地址，则屏蔽DHL、UPS、Fedex三种货运方式
			if (isPOBoxAddress) {
				if (isDisabledDeliveryOfPOBOX(delivery.getId())) {
					deliveryVO.setEnable(false);
				}
			}

			deliveryVOList.add(deliveryVO);
		}

		return deliveryVOList;
	}*/

	/**
	 * 获得默认的选择的货运方式
	 * 
	 * @param address
	 * @return
	 */
/*	private Integer getDefaultDelivery(List<DeliveryVO> deliveryVOList,
			boolean isPOBoxAddress) {
		BigDecimal currentFrgith = null;
		BigDecimal minFreight = new BigDecimal(Integer.MAX_VALUE);
		Integer defaultDeliveryId = null;
		Integer deliveryId = null;
		for (DeliveryVO delivery : deliveryVOList) {
			deliveryId = delivery.getBaseInfo().getId();
			// 如果客户是第一次下单,则在EMS x 120%，DHL，UPS,选择最便宜的一个
			if (deliveryId.compareTo(ConfigHelper.DELIVERY_EMS_ID) == 0
					|| deliveryId.compareTo(ConfigHelper.DELIVERY_UPS_ID) == 0
					|| deliveryId.compareTo(ConfigHelper.DELIVERY_DHL_ID) == 0) {
				currentFrgith = delivery.getFreight();
				if (delivery.getBaseInfo().getId().compareTo(
						ConfigHelper.DELIVERY_EMS_ID) == 0) { // EMS x 120%
					currentFrgith = currentFrgith.multiply(EMS_FREIGHT_RATE);
				} else if (isPOBoxAddress) {// 如果是P.O.Box地址，则屏蔽DHL、UPS、Fedex三种货运方式,不允许选择DHL、UPS
					continue;
				}

				if (minFreight.compareTo(currentFrgith) > 0) {
					minFreight = delivery.getFreight();
					defaultDeliveryId = delivery.getBaseInfo().getId();
				}
			}
		}

		return defaultDeliveryId;
	}*/

}
