package com.example.eduservice.mapper;

import com.example.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eduservice.entity.frontvo.CourseWebVo;
import com.example.eduservice.entity.vo.CoursePublishVo;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author helloWorld
 * @since 2023-06-10
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {

    public CoursePublishVo getPublishCourseInfo(String courseId);

    // 根据课程id，编写sql语句查询课程信息和讲师信息
    CourseWebVo getBaseCourseInfo(String courseId);
}
