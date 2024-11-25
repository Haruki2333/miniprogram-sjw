package com.haruki.poker.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * index控制器
 */
@RestController
public class PokerController {

  /**
   * 主页页面
   * @return API response html
   */
  @GetMapping
  public String index() {
    return "poker";
  }

}
