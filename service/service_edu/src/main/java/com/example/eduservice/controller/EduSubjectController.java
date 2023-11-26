package com.example.eduservice.controller;


import com.example.commonutils.R;
import com.example.eduservice.entity.subject.OneSubject;
import com.example.eduservice.service.EduSubjectService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author helloWorld
 * @since 2023-06-08
 */
@Api(description = "课程分类")
@RestController
@RequestMapping("/eduservice/subject")
//@CrossOrigin // 解决跨域问题
public class EduSubjectController {

    @Autowired
    private EduSubjectService subjectService;

    // 添加课程分类
    // 获取上传过来的文件，把文件内容读取出来
    @PostMapping("addSubject")
    public R addSubject(MultipartFile file) {
        // 上传过来的excel文件
        subjectService.saveSubject(file, subjectService);
        return R.ok();
    }

    // 课程分类列表（树形）
    @GetMapping("getAllSubject")
    public R getAllSubject() {
        // list集合泛型是一级分类
        List<OneSubject> list = subjectService.getAllOneTwoSubject();
        return R.ok().data("list", list);
    }

}

