package com.itecheasy.ph3.web.tag;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.buyer.order.OrderSessionUtils;
import com.itecheasy.ph3.web.buyer.order.SessionOrder;
import com.itecheasy.ph3.web.utils.CurrencyUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.CookieArea;

/**
 * 界面币种转换
 */
public class CurrencyTag extends TagSupport {
	private static final long serialVersionUID = -6837223616208542444L;
	private static Log log = LogFactory.getLog(CurrencyTag.class);
	private BigDecimal price;

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			HttpSession session = request.getSession();
			CookieArea areaConfig = SessionUtils.getAreaInfo(session);
			SessionOrder sessionOrder = OrderSessionUtils.getSessionOrder(session);
			Currency currency = null;
			PlaceOrderPage placeOrderPage = OrderSessionUtils.getPlaceOrderBeginPage(request);
			//使用信用卡下单，在Payment页面选择币种，点击“Continue”,信用卡支付页面在设置区域(改变币种)，信用卡支付页面币种保持Payment页面设置币种相同
			if(placeOrderPage == PlaceOrderPage.PAGE_CREDIT_CARD){
				currency = sessionOrder != null ? sessionOrder.getPaymentInfo() != null ? sessionOrder.getPaymentInfo().getCurrency() : null : null;
			}
			if(currency == null){
				if(areaConfig != null && price != null){
					currency = areaConfig.getCurrency();
				}
			}
			String lable = currency.getSymbol();
			BigDecimal showPrice = CurrencyUtils.USDToOrderCurrency(currency, price);
			String showPriceStr = FuncitonUtils.getPriceString(showPrice);
			pageContext.getOut().write(lable + showPriceStr);	
			
		} catch (IOException e) {
			log.error(price == null ? "price is null " : price + "calculate currency error" + e.getMessage());
		}
		return SKIP_BODY;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
