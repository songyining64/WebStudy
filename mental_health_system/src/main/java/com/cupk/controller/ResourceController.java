package com.cupk.controller;

import com.cupk.entity.TextResource;
import com.cupk.entity.VideoResource;
import com.cupk.service.TextResourceService;
import com.cupk.service.VideoResourceService;
import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @Autowired
    private VideoResourceService videoResourceService;

    @Autowired
    private TextResourceService textResourceService;

    // Video Resources
    @GetMapping("/videos")
    public Result<List<VideoResource>> listVideos() {
        return Result.success(videoResourceService.list());
    }

    @PostMapping("/videos")
    public Result<?> addVideo(@RequestBody VideoResource videoResource, HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        videoResource.setUploadTime(LocalDateTime.now());
        videoResourceService.save(videoResource);
        return Result.success();
    }

    @DeleteMapping("/videos/{id}")
    public Result<?> deleteVideo(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        boolean success = videoResourceService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping("/videos")
    public Result<?> updateVideo(@RequestBody VideoResource videoResource, HttpServletRequest request) {
        if (!isAdmin(request))
            return Result.error("无权限");
        boolean success = videoResourceService.updateById(videoResource);
        return success ? Result.success() : Result.error("更新失败");
    }


    // Text Resources
    @GetMapping("/texts")
    public Result<List<TextResource>> listTexts() {
        return Result.success(textResourceService.list());
    }

     @PostMapping("/texts")
    public Result<?> addText(@RequestBody TextResource textResource, HttpServletRequest request) {
         if (!isAdmin(request))
             return Result.error("无权限");
        textResource.setCreateTime(LocalDateTime.now());
        textResourceService.save(textResource);
        return Result.success();
    }

    @DeleteMapping("/texts/{id}")
    public Result<?> deleteText(@PathVariable Long id, HttpServletRequest request) {
         if (!isAdmin(request))
             return Result.error("无权限");
        boolean success = textResourceService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    @PutMapping("/texts")
    public Result<?> updateText(@RequestBody TextResource textResource, HttpServletRequest request) {
         if (!isAdmin(request))
             return Result.error("无权限");
        textResource.setUpdateTime(LocalDateTime.now());
        boolean success = textResourceService.updateById(textResource);
        return success ? Result.success() : Result.error("更新失败");
    }

    // 工具方法：判断是否为管理员
    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        return "admin".equals(role);
    }
}
