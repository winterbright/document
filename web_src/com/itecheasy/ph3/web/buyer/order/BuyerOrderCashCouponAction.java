package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.math.BigDecimal;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.order.CreditCardType;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.web.utils.CurrencyUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;

public class BuyerOrderCashCouponAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;
	/**
	 * 操作成功
	 */
	private static final Integer ERROR_OPERATE_SUCCESS = 1;
	/**
	 * 操作失败
	 */
	private static final Integer ERROR_OPERATE_FAIL = 0;
	/**
	 * 未达到现金券最小使用限额
	 */
	private static final Integer ERROR_LESS_THAN_MIN_AMOUNT = 2;
	/**
	 * 无法使用现金券
	 */
	private static final Integer ERROR_CANNOT_USE = 3;
	
	private CashCouponService cashCouponService;
	
	public void setCashCouponService(CashCouponService cashCouponService) 
	{
		this.cashCouponService = cashCouponService;
	}
	
	/**
	 * 购物车中使用现金券
	 * @throws IOException
	 */
	public void userCashCouponForShoppingCart() throws IOException 
	{
		String fm = "[{\"result\":%1$s,\"cashCouponRedemptionAmount\":%2$s,\"dueAmount\":%3$s,\"minOrderAmount\":%4$s}]";
		
		int result = ERROR_OPERATE_FAIL;
		
		BigDecimal cashCouponRedemptionAmount = BigDecimal.ZERO;//现金券可抵扣金额
		BigDecimal dueAmount = BigDecimal.ZERO;//还应付金额
		BigDecimal minOrderAmount = BigDecimal.ZERO;//现金券最小使用限额
		
		//获得当前购物车信息
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();
		if( minShoppingCartTotalInfo != null)
		{
			//获得现金券编号
			String cashCouponCode = param("cashCouponCode");
			
			//获取当前购物车中的商品折后总金额
			BigDecimal productPrice = minShoppingCartTotalInfo.getProductPriceAfterDiscount();
			
			//获取客户Id
			Integer customerId=null;
			Customer customer = getLoginedUserBuyer();
			if(customer!=null){
				customerId=customer.getId();
			}
			
			//检查现金券是否能使用
			result = checkCashCoupon(productPrice,cashCouponCode,customerId);
			
			if( ERROR_OPERATE_SUCCESS.equals(result))
			{//如果现金券可用
				//则先获取现金券信息
				CashCoupon cashCoupon = cashCouponService.getCashCouponByCodeAndCustomer(cashCouponCode,customerId);
				
				//在购物车中使用现金券			
				setCashCouponForShoppingCart(cashCoupon);
				cashCouponRedemptionAmount = minShoppingCartTotalInfo.getCashCouponRedemptionAmount();
				dueAmount = minShoppingCartTotalInfo.getDueAmount();
			}
			else
			{
				//如果是没有达到最小订单金额
				if( ERROR_LESS_THAN_MIN_AMOUNT.equals(result))
				{
					//则先获取现金券信息
					CashCoupon cashCoupon = cashCouponService.getCashCouponByCodeAndCustomer(cashCouponCode,customerId);
					minOrderAmount = cashCoupon.getMinOrderAmount();
				}
				
				//同时清空当前购物车的现金券
//				setCashCouponForShoppingCart(null);
			}
		}		
		
		SessionUtils.setMinShoppingCartInfo(request,minShoppingCartTotalInfo);
		
		try 
		{
			returnJson(String.format(fm, result, cashCouponRedemptionAmount, dueAmount,minOrderAmount));
		} 
		catch (IOException e) 
		{
		}
	}
	
	/**
	 * 下单中使用现金券
	 * @throws IOException
	 */
	public void userCashCouponForPlaceOrder() throws IOException 
	{
		String fm = "[{\"result\":%1$s,\"cashCouponRedemptionAmount\":%2$s,\"dueAmount\":%3$s,\"minOrderAmount\":%4$s}]";
		
		int result = ERROR_OPERATE_FAIL;
		
		BigDecimal cashCouponRedemptionAmount = BigDecimal.ZERO;//现金券可抵扣金额
		BigDecimal dueAmount = BigDecimal.ZERO;//还应付金额
		BigDecimal minOrderAmount = BigDecimal.ZERO;//现金券最小使用限额
		
		//获得当前正在使用的订单
		SessionOrder order = OrderSessionUtils.getSessionOrder(request.getSession());
		if( order != null)
		{
			//获得现金券编号
			String cashCouponCode = param("cashCouponCode");
			
			//获取当前订单中的商品折后总金额
			BigDecimal productPrice = order.getProductPriceAfterDiscount();
			
			//获取用户的Id
			Integer customerId=order.getCustomerId();
			
			//检查现金券是否能使用
			result = checkCashCoupon(productPrice,cashCouponCode,customerId);
			
			if( ERROR_OPERATE_SUCCESS.equals(result))
			{//如果现金券可用
				
				//则先获取现金券信息
				CashCoupon cashCoupon = cashCouponService.getCashCouponByCodeAndCustomer(cashCouponCode,customerId);
				minOrderAmount = cashCoupon.getMinOrderAmount();
				
				//在下单中使用现金券			
				order = setCashCouponForSessionOrder(cashCoupon);
				
				//同时更新当前购物车中的现金券
				setCashCouponForShoppingCart(cashCoupon);
				
				//获取使用现金券后的应付金额和现金券抵扣金额
				cashCouponRedemptionAmount = order.getCashCouponRedemptionAmount();
				dueAmount = order.getDueAmount();		
				
				//更新支付信息
				updatePaymentInfo();
			}
			else
			{
				//如果是没有达到最小订单金额
				if( ERROR_LESS_THAN_MIN_AMOUNT.equals(result))
				{
					//则先获取现金券信息
					CashCoupon cashCoupon = cashCouponService.getCashCouponByCodeAndCustomer(cashCouponCode,customerId);
					minOrderAmount = cashCoupon.getMinOrderAmount();
				}
				
				//现金券无法使用时，则移除现有现金券
//				order = setCashCouponForSessionOrder(null);
				
				//同时清空当前购物车的现金券
//				setCashCouponForShoppingCart(null);
			}
		}
		setSessionOrder(order);			
		
		try 
		{
			returnJson(String.format(fm, result, cashCouponRedemptionAmount, dueAmount,minOrderAmount));
		} 
		catch (IOException e) 
		{
		}
	}

	
	/**
	 * 取消购物车中使用的现金券
	 * @throws IOException
	 */
	public void cancelCashCouponForShoppingCart() throws IOException 
	{
        String fm = "[{\"result\":%1$s}]";
		
		int result = ERROR_OPERATE_SUCCESS;
		
		try 
		{
			//清空当前购物车的现金券
			setCashCouponForShoppingCart(null);
			
			returnJson(String.format(fm, result));
		} 
		catch (IOException e) 
		{
		}
	}
	
	/**
	 * 取消下单中使用的现金券
	 * @throws IOException
	 */
	public void cancelCashCouponForPlaceOrder() throws IOException 
	{
        String fm = "[{\"result\":%1$s,\"dueAmount\":%2$s}]";
		
		int result = ERROR_OPERATE_SUCCESS;
		BigDecimal dueAmount = BigDecimal.ZERO;//还应付金额
		
		try 
		{
			SessionOrder sessionOrder = getSessionOrder();
			
			//清空Session中使用的现金券
			setCashCouponForSessionOrder(null);			
			//同时清空当前购物车的现金券
			setCashCouponForShoppingCart(null);
			
			//更新支付信息
			updatePaymentInfo();
			
			//获得清除后的应付金额
			sessionOrder = OrderSessionUtils.getSessionOrder(request.getSession());
			dueAmount = sessionOrder.getDueAmount();
			
			returnJson(String.format(fm, result, dueAmount));			
		} 
		catch (IOException e) 
		{
		}
	}
	
	private void updatePaymentInfo()
	{
		SessionOrder sessionOrder = getSessionOrder();
		if( sessionOrder == null || !sessionOrder.isConfirmPayInfo()) return ;
		
		OrderPaymentInfo orderPaymentInfo = sessionOrder.getPaymentInfo();	
		

		//获取Cash支付金额
		BigDecimal cashPayAmount = orderPaymentInfo.getCashPay();
		//获取订单应支付金额
		BigDecimal dueAmount = sessionOrder.getDueAmount();
		
		//重新计算在线应支付金额 = 应支付金额 - Cash支付金额
		BigDecimal onLinePayAmountUS = dueAmount.subtract(cashPayAmount);
		BigDecimal onLinePayAmount = CurrencyUtils.USDToOrderCurrency(orderPaymentInfo.getCurrency(), onLinePayAmountUS);
		
		orderPaymentInfo.setOnLinePayUs(onLinePayAmountUS);
		orderPaymentInfo.setOnLinePay(onLinePayAmount);
		sessionOrder.setPaymentInfo(orderPaymentInfo);		
		setSessionOrder(sessionOrder);
		
		CreditCardType creditCardType = orderPaymentInfo.getOnlinePayInfo().getCreditCardPayInfo() != null ? orderPaymentInfo.getOnlinePayInfo().getCreditCardPayInfo().getCreditCardType() : null;
		
		//更改数据库中的支付信息
		setPaymentInfo(cashPayAmount, onLinePayAmount, onLinePayAmountUS,orderPaymentInfo.getCurrency(), orderPaymentInfo.getOnLinePayType(), creditCardType);
	}
	
	/**
	 * 检查现金券是否可用
	 */
	private int checkCashCoupon(BigDecimal productPrice,String cashCouponCode,Integer customerId)
	{		
		try
		{
			cashCouponService.checkIsAbleUse(productPrice,cashCouponCode,customerId);		
			
			return ERROR_OPERATE_SUCCESS;	
		}
		catch(BussinessException ex)
		{
			if(ex.getErrorMessage() == CashCouponService.ERROR_LESS_THAN_MIN_AMOUNT)
			{
                return ERROR_LESS_THAN_MIN_AMOUNT;
			}
			else
			{
				return ERROR_CANNOT_USE;						
			}			
		}			
	}
	
	private SessionOrder setCashCouponForSessionOrder(CashCoupon cashCoupon)
	{
		SessionOrder order = OrderSessionUtils.getSessionOrder(request.getSession());
		if( order != null && order.getPaymentInfo() != null)
		{
			order.getPaymentInfo().setCashCouponInfo(cashCoupon);	
			setSessionOrder(order);
		}			
		return order;
	}	
}
