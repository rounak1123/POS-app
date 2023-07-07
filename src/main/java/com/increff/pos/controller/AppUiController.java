package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {

	@RequestMapping(value = "/ui/home")
	public ModelAndView home() {
		return mav("home.html");
	}

	@RequestMapping(value = "/ui/brand")
	public ModelAndView brand() { return mav("brand.html"); }

	@RequestMapping(value = "/ui/employee")
	public ModelAndView employee() {
		return mav("employee.html");
	}

	@RequestMapping(value = "/ui/product")
	public ModelAndView product() {
		return mav("product.html");
	}

	@RequestMapping(value = "/ui/inventory")
	public ModelAndView inventory() {
		return mav("inventory.html");
	}

	@RequestMapping(value = "/ui/order/new")
	public ModelAndView orderNew() {
		return mav("order.html");
	}

	@RequestMapping(value = "/ui/order/view")
	public ModelAndView orderView() {
		return mav("order-view.html");
	}
	@RequestMapping(value = "/ui/order-item/edit")
	public ModelAndView orderItemEdit() {
		return mav("order-item-edit.html");
	}

	@RequestMapping(value = "/ui/order-item/view")
	public ModelAndView orderItemView() {
		return mav("order-item-view.html");
	}

	@RequestMapping(value = "/ui/report/brand")
	public ModelAndView brandReport() {
		return mav("brand-report.html");
	}

	@RequestMapping(value = "/ui/report/inventory")
	public ModelAndView inventoryReport() {
		return mav("inventory-report.html");
	}

	@RequestMapping(value = "/ui/report/sales")
	public ModelAndView salesReport() {
		return mav("sales-report.html");
	}

	@RequestMapping(value = "/ui/report/day-sales")
	public ModelAndView daySalesReport() {
		return mav("daily-sales-report.html");
	}

	@RequestMapping(value = "/ui/admin")
	public ModelAndView admin() {
		return mav("user.html");
	}

}
