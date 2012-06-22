package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.math.BigDecimal;

import com.itecheasy.common.Page;
import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.order.OrderItem;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.sslplugin.annotation.Secured;

public class BuyerPlaceOrderAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;
	private static final int pageSize =100;

	/**
	 *确认订单页面
	 */
	@Secured
	public String confirmOrder() 
	{
		SessionOrder sessionOrder = getSessionOrder();
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_CONFIRM_ORDER);
		if( checkResult != null)
		{
			return checkResult;
		}
		
		Integer pageIndex = paramInt("currentPage");
		if( pageIndex == null || pageIndex <0){
			pageIndex = 1;
		}
		
		PageList<OrderItem> orderItmes = orderService.getTempOrderDetails(pageIndex, pageSize, sessionOrder.getTempOrderId()); 
		
		Page page = orderItmes.getPage();
		int itemCount = page.getTotalRowCount();
		
		beginPage(PlaceOrderPage.PAGE_CONFIRM_ORDER);
				
		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("orderItmes", orderItmes.getData());
		request.setAttribute("page", page);
		request.setAttribute("pageIndex", pageIndex);
		request.setAttribute("itemCount", itemCount);
		request.setAttribute("currentPage", PlaceOrderPage.PAGE_CONFIRM_ORDER);
		
		return SUCCESS;
	}
	
	
	/**
	 * 正式下订单
	 * @throws IOException
	 */
	@Secured
	public void placeOrder() throws IOException
	{
		if( !isValidSessionByJson()) return;
		
		String fm = "[{\"result\":%1$s,\"orderno\":%2$s,\"email\":%3$s}]";
		
		int result = 0;
		
		String orderno = "";
		String email = "";
		
		Integer customerId = this.getLoginedUserBuyer().getId();
		SessionOrder sessionOrder = getSessionOrder();
		String orderNo = sessionOrder.getOrderNo();
		
		//判断是否当前订单是否已正式下过单，但Ajax超时时可能发生此情况
	/*	if( getEndPage() == PlaceOrderPage.PAGE_CONFIRM_ORDER )
		{
			result =1;
			orderno = orderNo;
			email = this.getLoginedUserBuyer().getEmail();
		}
		else 
		{*/			
			setOrderNo(sessionOrder);
			try 
			{
				if( sessionOrder.getPaymentInfo().getOnLinePayType() != null && OrderService.PAY_TYPE_PAY_PAL == sessionOrder.getPaymentInfo().getOnLinePayType())
				{
					orderService.bookOrderDelay(sessionOrder.getCustomerId(), sessionOrder.getShoppingCartId());
					
					//如果需要Cash支付，则先判断余额是否足够
					if(BigDecimal.ZERO.compareTo(sessionOrder.getPaymentInfo().getCashPay()) < 0 ){
						//获取用户Cash账户余额
						BigDecimal balance = cashAccountService.getBalance(customerId);		
						if(balance.compareTo(sessionOrder.getPaymentInfo().getCashPay()) < 0 ){
						   throw new BussinessException(OrderService.ERROR_CASH_PAY_FAIL);
						}
					}
					
					result = 2; //PayPal支付，跳转到PayPal支付页面
				}
				else 
				{
					placeOrder(sessionOrder);
					result =1;
					orderno = sessionOrder.getOrderNo();
					email = this.getLoginedUserBuyer().getEmail();
					
					//清空Session
					SessionUtils.removeMinShoppingCartInfo(request);	
					//移除现金券
					SessionUtils.removeCashCouponFromShoppingCart(request);
					OrderSessionUtils.clearSessionOrder(request);
					OrderSessionUtils.clearSessionOrderPage(request);
					OrderSessionUtils.clearSessionOrderByCreditCard(request);
					OrderSessionUtils.clearCreditCardUrl(request);
					
					endPage(PlaceOrderPage.PAGE_CONFIRM_ORDER);
				}				
			}  
			catch (BussinessException e) 
			{
				String error = e.getErrorMessage();
				if( OrderService.ERROR_PRODUCT_UNDER_STOCK == error ){
					result = -1; //库存不足，跳转到购物车页面
				}
				else if ( OrderService.ERROR_CASH_PAY_FAIL == error ){
					result = -2; //Cash支付失败，跳转到支付方式页面
				}
				else if ( OrderService.ERROR_CREDIT_CARD_PAY_FAIL == error ){
					result = -3; //信用卡支付失败，跳转到信用卡信息页面
				}
				else if ( OrderService.ERROR_PAY_AMOUNT_ERROR == error ){
					result = -4; //支付金额小于等于0，跳转到支付方式页面
				}
				else if ( OrderService.ERROR_ORDER_AMOUNT_ERROR == error ){
					result = -5; //支付金额与订单金额不对，跳转到支付方式页面
				}
				else 
				{
					errorLog(e);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				errorLog(e);
			}
//		}
		
		returnJson(String.format(fm, result,"\"" + orderno + "\"","\"" + email + "\""));
	}
	
	/**
	 * 订单成功页面
	 * @return
	 */
	public String doOrderSuccess() 
	{
		SessionOrder sessionOrder = getSessionOrder();
		if( sessionOrder != null)
		{
			//清空Session
			OrderSessionUtils.clearSessionOrder(request);
			OrderSessionUtils.clearSessionOrderPage(request);
			OrderSessionUtils.clearSessionOrderByCreditCard(request);
			OrderSessionUtils.clearCreditCardUrl(request);
		}
		if( SessionUtils.getMinShoppingCartInfo(request) != null)
		{
			SessionUtils.removeMinShoppingCartInfo(request);	
		}
		if( SessionUtils.getCashCouponFromShoppingCart(request) != null)
		{	//移除现金券
			SessionUtils.removeCashCouponFromShoppingCart(request);
		}
		
		
		String orderno = param("orderno");
		String email  = param("email");
		
		request.setAttribute("orderno", orderno);
		request.setAttribute("email", email);

		return SUCCESS;
	}

}
