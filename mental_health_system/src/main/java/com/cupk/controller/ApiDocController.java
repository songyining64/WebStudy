package com.cupk.controller;

import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * API文档控制器
 * 用于生成API路由清单，方便前端开发
 */
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" }, allowCredentials = "true")
@RestController
@RequestMapping("/api/docs")
public class ApiDocController {

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping
    public Result<Map<String, Object>> getAllApis() {
        try {
            RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

            // 分类存储API信息
            Map<String, List<Map<String, String>>> apisByController = new HashMap<>();

            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();

                // 跳过框架自带的接口和错误处理接口
                String controllerName = handlerMethod.getBeanType().getSimpleName();
                if (controllerName.contains("Basic") ||
                        controllerName.contains("ErrorController") ||
                        controllerName.equals("ApiDocController")) {
                    continue;
                }

                // 获取接口信息
                Set<String> patterns = mappingInfo.getPatternValues();
                Set<String> methods = new HashSet<>();
                if (mappingInfo.getMethodsCondition() != null) {
                    mappingInfo.getMethodsCondition().getMethods().forEach(method -> methods.add(method.name()));
                }

                // 获取接口描述（从方法或类的注释中）
                String methodName = handlerMethod.getMethod().getName();

                // 添加到对应的控制器分类中
                if (!apisByController.containsKey(controllerName)) {
                    apisByController.put(controllerName, new ArrayList<>());
                }

                for (String pattern : patterns) {
                    Map<String, String> apiInfo = new HashMap<>();
                    apiInfo.put("path", pattern);
                    apiInfo.put("method", methods.isEmpty() ? "ALL" : String.join(",", methods));
                    apiInfo.put("methodName", methodName);
                    apisByController.get(controllerName).add(apiInfo);
                }
            }

            // 构造返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("apiCount", countApis(apisByController));
            result.put("apis", apisByController);
            result.put("baseUrl", "/mental");

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取API列表失败：" + e.getMessage());
        }
    }

    private int countApis(Map<String, List<Map<String, String>>> apisByController) {
        int count = 0;
        for (List<Map<String, String>> apis : apisByController.values()) {
            count += apis.size();
        }
        return count;
    }
}