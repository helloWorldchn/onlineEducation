<template>
    <div class="app-container">
        <h2 style="text-align: center;">发布新课程</h2>
        <el-steps :active="1" process-status="wait" align-center style="margin-bottom: 40px;">
            <el-step title="填写课程基本信息"/>
            <el-step title="创建课程大纲"/>
            <el-step title="最终发布"/>
        </el-steps>

        <el-form label-width="120px">
            <el-form-item label="课程标题">
                <el-input v-model="courseInfo.title" placeholder="示例：机器学习项目课：从基础到搭建项目视频课程。专业名称注意大小写"/>
            </el-form-item>

            <!-- 所属分类 TODO -->
            <!-- 所属分类：级联下拉列表 -->            
            <el-form-item label="课程类别">
                <!-- 一级分类 -->
                <el-select
                    v-model="courseInfo.subjectParentId"
                    placeholder="一级分类"  @change="subjectLevelOneChanged">
                    <el-option
                        v-for="subject in subjectOneList"
                        :key="subject.id"
                        :label="subject.title"
                        :value="subject.id"/>
                </el-select>
                <!-- 二级分类 -->
                <el-select v-model="courseInfo.subjectId" placeholder="二级分类">
                    <el-option
                        v-for="subject in subjectTwoList"
                        :key="subject.id"
                        :label="subject.title"
                        :value="subject.id"/>
                </el-select>
            </el-form-item>

            <!-- 课程讲师 TODO -->
            <!-- 课程讲师 -->
            <el-form-item label="课程讲师">
                <el-select
                    v-model="courseInfo.teacherId"
                    placeholder="请选择">
                    <el-option
                        v-for="teacher in teacherList"
                        :key="teacher.id"
                        :label="teacher.name"
                        :value="teacher.id"/>
                </el-select>
            </el-form-item>

            <el-form-item label="总课时">
                <el-input-number :min="0" v-model="courseInfo.lessonNum" controls-position="right" placeholder="请填写课程的总课时数"/>
            </el-form-item>

            <!-- 课程简介 TODO -->
            <!-- 课程简介-->
            <el-form-item label="课程简介">
                <tinymce :height="300" v-model="courseInfo.description"/>
            </el-form-item>
            <!-- 课程封面 TODO -->
            <!-- 课程封面-->
            <el-form-item label="课程封面">
                <el-upload
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    :action="BASE_API+'/eduoss/fileoss/upload?host=cover'"
                    class="avatar-uploader">
                    <img :src="courseInfo.cover" :width="500" :height="300">
                </el-upload>
            </el-form-item>

            <el-form-item label="课程价格">
                <el-input-number :min="0" v-model="courseInfo.price" controls-position="right" placeholder="免费课程请设置为0元"/> 元
            </el-form-item>
            <el-form-item>
                <el-button :disabled="saveBtnDisabled" type="primary" @click="saveOrUpdate">保存并下一步</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
import course from '@/api/edu/course'
import subject from '@/api/edu/subject'
import Tinymce from '@/components/Tinymce' // 引入组件
export default {
    components: { Tinymce }, // 声明组件
    data() {
        return {
            saveBtnDisabled: false, // 保存按钮是否禁用
            courseInfo :{
                title: '',
                subjectId: '', // 二级分类ID"
                subjectParentId: '', // 一级分类Id"
                teacherId: '',
                lessonNum: 0,
                description: '',
                cover: '',
                price: 0
            },
            courseId: '',
            BASE_API: process.env.BASE_API, // 接口API地址
            teacherList:[], // 封装所有讲师数据
            subjectOneList: [], //一级分类列表
            subjectTwoList: [] //二级分类列表

        }
    },
    created() {
        // 获取路由id值
        this.init()
    },
    watch: { // 监听
        $route(to, from) { // 路由变化方式，路由发生变化，方法就会执行
            // console.log('watch $route')

            this.init()
        }
    },
    methods: {
        init(){
            // 获取路由id值            
            // 判断路径是否有Id值。如果有，说明是修改方法，获取信息；如果没有则是添加方法，不用获取信息
            if (this.$route.params && this.$route.params.id) {
                this.courseId = this.$route.params.id
                // 根据课程id查询课程信息
                this.getInfo()
            } else {
                this.courseInfo={cover: 'https://online-education-chn.oss-cn-beijing.aliyuncs.com/cover/default1.jpg',}
                this.description = ''
                // 初始化所有讲师
                this.getListTeacher()
                // 初始化一级分类
                this.getOneSubject()
            }
        },
        // 根据课程id查询课程信息
        getInfo() {
            course.getCourseInfoById(this.courseId).then(response => {
                // 在courseInfo中获取课程信息，包含一级分类id和二级分类id
                this.courseInfo = response.data.courseInfoVo
                // 1.查询所有的分类，包含一级和二级
                subject.getSubjectList().then(response => {
                    // 2.获取所有的一级分类
                    this.subjectOneList = response.data.list
                    // 3.把所有的一级分类数据进行遍历
                    for (let i = 0; i < this.subjectOneList.length; i++) {
                        // 获取每个一级分类
                        const oneSubject = this.subjectOneList[i]; 
                        // 比较当前courseInfo里面一级分类id和所有的一级分类id
                        if (this.courseInfo.subjectParentId === oneSubject.id) {
                            // 获取一级分类所有的二级分类
                            this.subjectTwoList = oneSubject.children
                        }
                    }
                })
                // 初始化所有讲师
                this.getListTeacher()
            })
        },
        // 上传封面成功调用的方法
        handleAvatarSuccess(res, file) {
            console.log(res)// 上传响应
            console.log(URL.createObjectURL(file.raw))// base64编码
            this.courseInfo.cover = res.data.url
        },
        // 上传之前调用的方法
        beforeAvatarUpload(file) {
            const isJPG = file.type === 'image/jpeg'
            const isLt2M = file.size / 1024 / 1024 < 2
            if (!isJPG) {
                this.$message.error('上传头像图片只能是 JPG 格式!')
            }
            if (!isLt2M) {
                this.$message.error('上传头像图片大小不能超过 2MB!')
            }
            return isJPG && isLt2M
        },
        // 点击某个一级分类时，触发change事件，显示对应的二级分类
        subjectLevelOneChanged(value) {
            // value就是一级分类id值
            // console.log(value)
            // 遍历所有的分类，包含一级和二级
            for (let i = 0; i < this.subjectOneList.length; i++) {
                // 每个一级分类
                var oneSubject = this.subjectOneList[i];
                // 判断所有一级分类id和点击一级分类id是否一样
                if (oneSubject.id === value) {
                    // 从一级分类获取里面所有的二级分类
                    this.subjectTwoList = oneSubject.children
                    // 把二级分类的id值清空
                    this.courseInfo.subjectId = ''
                }
            }
        },
        // 查询所有的一级分类
        getOneSubject() {
            subject.getSubjectList()
                .then(response => {
                    this.subjectOneList = response.data.list
            })
        },
        // 查询所有的讲师
        getListTeacher() {
            course.getListTeacher()
                .then(response => {
                    this.teacherList=response.data.items
                })
        },
        // 添加课程
        addCourse() {
            course.addCourseInfo(this.courseInfo)
                .then(response => {
                    // 提示信息
                    this.$message({
                        type: 'success',
                        message: '添加课程信息成功!'
                    });                    
                    // 跳转到第二步
                    this.$router.push({ path: '/course/chapter/'+response.data.courseId})
                })
        },
        // 修改课程
        updateCourse() {
            course.updateCourseInfo(this.courseInfo).then(response => {
                // 提示信息
                this.$message({
                    type: 'success',
                    message: '修改课程信息成功!'
                });                    
                // 跳转到第二步
                this.$router.push({ path: '/course/chapter/'+this.courseId})
            })
        },
        // 添加和修改公用此方法
        saveOrUpdate() {
            // 判断是添加还是修改
            if (!this.courseInfo.id) {
                // 无id，添加
                this.addCourse()
            } else {
                // 有id，修改
                this.updateCourse()
            }

        }

    }
}
</script>
<style scoped>
    .tinymce-container {
        line-height: 29px;
    }
</style>
