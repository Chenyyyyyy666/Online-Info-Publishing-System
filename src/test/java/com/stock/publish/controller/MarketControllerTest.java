package com.stock.publish.controller;

import com.stock.publish.calculation.KLineAggregator;
import com.stock.publish.calculation.TopTraderEngine;
import com.stock.publish.service.MarketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MarketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MarketService marketService;

    @Mock
    private KLineAggregator kLineAggregator;

    @Mock
    private TopTraderEngine topTraderEngine;

    @InjectMocks
    private MarketController marketController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(marketController)
                .setMessageConverters(
                        new StringHttpMessageConverter(StandardCharsets.UTF_8),
                        new MappingJackson2HttpMessageConverter()
                )
                .build();
    }

    @Test
    void testKline_GuestAccess_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/publish/market/kline")
                        .param("stockCode", "600519")
                        .param("period", "1D")
                        .requestAttr("userRole", "GUEST"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("游客无权查看K线图"));
    }

    @Test
    void testKline_StandardAccess_1H_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/publish/market/kline")
                        .param("stockCode", "600519")
                        .param("period", "1H") 
                        .requestAttr("userRole", "STANDARD"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("普通用户仅可查看日K线(1D)"));
    }

    @Test
    void testKline_VipAccess_Success() throws Exception {
        when(kLineAggregator.getKLineData(anyString(), anyString(), any(), any()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/publish/market/kline")
                        .param("stockCode", "600519")
                        .param("period", "5M") 
                        .requestAttr("userRole", "PREMIUM_VIP"))
                .andExpect(status().isOk());
    }
}