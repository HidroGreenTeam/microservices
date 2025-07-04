package com.hidrogreen.payment.gateway.platform.interfaces.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class PageController {

  @GetMapping("/") 
  public String showPaymentForm() {
    return "payment"; 
  }

  @GetMapping("/payment-success")
  public String showPaymentSuccessPage(@RequestParam(value = "internalPaymentId", required = false) String internalPaymentId, Model model) {
    if (internalPaymentId != null) {
      model.addAttribute("internalPaymentId", internalPaymentId);
    }
    return "payment-success"; 
  }

  @GetMapping("/payment-error")
  public String showPaymentErrorPage(@RequestParam(value = "error", required = false) String errorMessage, Model model) {
    if (errorMessage != null) {
      model.addAttribute("errorMessage", java.net.URLDecoder.decode(errorMessage, java.nio.charset.StandardCharsets.UTF_8));
    }
    return "payment-error"; 
  }

  @GetMapping("/payment-cancelled")
  public String showPaymentCancelPage() {
    return "payment-cancelled"; 
  }
}
