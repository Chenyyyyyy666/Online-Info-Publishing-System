package com.stock.publish.controller;

import com.stock.publish.calculation.KLineAggregator;
import com.stock.publish.calculation.TopTraderEngine;
import com.stock.publish.service.MarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * 市场行情数据对外暴露接口
 * 负责接收前端的图表数据请求，并在此层把关权限越界访问。
 */
@RestController
@RequestMapping("/api/publish/market") 
public class MarketController {

    private final MarketService marketService;
    private final KLineAggregator kLineAggregator;
    private final TopTraderEngine topTraderEngine; // 注入供后续开发实时盘口 Quote 时使用

    public MarketController(MarketService marketService, KLineAggregator kLineAggregator, TopTraderEngine topTraderEngine) {
        this.marketService = marketService;
        this.kLineAggregator = kLineAggregator;
        this.topTraderEngine = topTraderEngine;
    }

    /**
     * 获取 K 线图核心数据 (GET /api/publish/market/kline)
     * 1. GUEST: 完全拦截，禁止访问 K 线数据。
     * 2. STANDARD: 仅放行 period = "1D" (日K线) 的请求。
     * 3. PREMIUM_VIP: 放行所有周期 (5M/1H/1D/1M/1Y)。
     * * @param stockCode 请求的股票代码
     * @param period    K线周期（默认 1D）
     * @param userRole  由 AuthInterceptor 全局拦截器提前解析校验好并注入在请求作用域中的角色标识
     */
    @GetMapping("/kline")
    public ResponseEntity<?> kline(@RequestParam String stockCode,
                                   @RequestParam(defaultValue = "1D") String period,
                                   @RequestAttribute("userRole") String userRole) {
        
        // --- 1. 越权拦截区 ---
        if ("GUEST".equals(userRole)) {
            // 游客态直接阻断，返回 HTTP 403 Forbidden 供前端触发 "请登录/绑证书" 提示
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("游客无权查看K线图");
        }
        
        if ("STANDARD".equals(userRole) && !"1D".equals(period)) {
            // 普通用户试图查看非 1D 周期时阻断，返回 403 供前端弹出 "升级 VIP" 高斯模糊弹窗
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("普通用户仅可查看日K线(1D)");
        }

        // --- 2. 业务处理区 ---
        // 为了保护数据库与内存性能，默认限制查询范围为最近一年的数据
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusYears(1);

        // 调用计算中心的核心聚合组件，下发 DTO 数据集封装为 200 OK 返回
        return ResponseEntity.ok(kLineAggregator.getKLineData(stockCode, period, start, end));
    }
}