<template>
    <div class="app-container">

        <!--查询表单-->
        <el-form :inline="true" class="demo-form-inline">
        <!-- 所属分类：级联下拉列表 -->
        <!-- 一级分类 -->
        <el-form-item label="课程类别">
            <el-select
                v-model="courseQuery.subjectParentId"
                placeholder="请选择"
                @change="subjectLevelOneChanged">
                <el-option
                    v-for="subject in subjectOneList"
                    :key="subject.id"
                    :label="subject.title"
                    :value="subject.id"/>
            </el-select>
            <!-- 二级分类 -->
            <el-select v-model="courseQuery.subjectId" placeholder="请选择">
                <el-option
                    v-for="subject in subjectTwoList"
                    :key="subject.id"
                    :label="subject.title"
                    :value="subject.id"/>
            </el-select>
        </el-form-item>

        <!-- 标题 -->
        <el-form-item>
            <el-input v-model="courseQuery.title" placeholder="课程名称"/>
        </el-form-item>
        <!-- 讲师 -->
        <el-form-item>
            <el-select v-model="courseQuery.teacherId" placeholder="请选择讲师">
                <el-option
                    v-for="teacher in teacherList"
                    :key="teacher.id"
                    :label="teacher.name"
                    :value="teacher.id"/>
            </el-select>
        </el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
            <el-button type="default" @click="resetData()">清空</el-button>
        </el-form>

        <!-- 表格 -->
        <el-table
            v-loading="listLoading"
            :data="list"
            element-loading-text="数据加载中"
            border
            fit
            highlight-current-row
            row-class-name="myClassList">
            <el-table-column
                label="序号"
                width="70"
                align="center">
                <template slot-scope="scope">{{ (page - 1) * limit + scope.$index + 1 }}</template>
            </el-table-column>
            <el-table-column label="课程信息" width="470" align="center">
                <template slot-scope="scope">
                    <div class="info">
                        <div class="pic">
                            <!-- <p>{{scope.row.cover}}</p> -->
                            <img :src="scope.row.cover" alt="scope.row.title" width="150px">
                        </div>
                        <div class="title">
                            <a href="">{{ scope.row.title }}</a>
                            <p>{{ scope.row.lessonNum }} </p>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center">
                <template slot-scope="scope">
                    {{ scope.row.gmtCreate.substr(0, 10) }}
                </template>
            </el-table-column>
            <el-table-column label="发布时间" align="center">
                <template slot-scope="scope">
                    {{ scope.row.gmtModified.substr(0, 10) }}
                </template>
            </el-table-column>
            <el-table-column label="价格" width="100" align="center">
                <template slot-scope="scope">
                    {{ Number(scope.row.price) === 0 ? '免费' : '¥' + scope.row.price.toFixed(2) }}
                </template>
            </el-table-column>
            <el-table-column prop="buyCount" label="付费学员" width="100" align="center">
                <template slot-scope="scope">
                    {{ scope.row.buyCount }}人
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center">
                <template slot-scope="scope">
                    <router-link :to="'/course/info/'+scope.row.id">
                        <el-button type="text" size="mini" icon="el-icon-edit">编辑课程信息</el-button>
                    </router-link>
                    <router-link :to="'/course/chapter/'+scope.row.id">
                        <el-button type="text" size="mini" icon="el-icon-edit">编辑课程大纲</el-button>
                    </router-link>
                    <el-button type="text" size="mini" icon="el-icon-delete" @click="removeDataById(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>


        <!-- 分页 -->
        <el-pagination
            :current-page="page"
            :page-size="limit"
            :total="total"
            style="padding: 30px 0; text-align: center;"
            layout="total, prev, pager, next, jumper"
            @current-change="getList"
        />
    </div>
</template>

<script>
import course from '@/api/edu/course'
import teacher from '@/api/edu/teacher'
import subject from '@/api/edu/subject'

export default {
    data() { // 定义变量和初始值
        return {
            listLoading: true, // 是否显示loading信息
            list: null, // 数据列表
            total: 0, // 总记录数
            page: 1, // 页码
            limit: 5, // 每页记录数
            courseQuery: {
                subjectParentId: '',
                subjectId: '',
                title: '',
                teacherId: ''
            }, // 查询条件
            teacherList: [], // 讲师列表
            subjectOneList: [], // 一级分类列表
            subjectTwoList: [] // 二级分类列表,
        }
    },
    created() { // 页面渲染之前执行，一般调用methods定义的方法
        // 调用
        this.getList()
        // 初始化所有讲师
        this.getListTeacher()
        // 初始化一级分类
        this.getOneSubject()
    },
    methods:{ // 创建具体的方法，调用teacher.js定义的方法
        // 讲师列表的方法
        getList(page = 1) {
            this.page = page
            this.listLoading = true
            course.getListCourse(this.page, this.limit, this.courseQuery)
            // course.getListCourse()
                .then(response => { // 请求成功
                    //response接口返回的数据
                    // console.log(response)
                    this.list = response.data.rows
                    this.total = response.data.total
                    console.log(this.list)
                    this.listLoading = false
                    console.log(this.total)
                })
                .catch(error => { // 请求失败
                    console.log(error)
                })
        },
        resetData() { // 清除
            this.courseQuery = {} // 表单输入框数据清空
            this.subjectTwoList = [] // 二级分类列表
            this.getList() // 刷新页面重新查询
        },
        // 查询所有的一级分类
        getOneSubject() {
            subject.getSubjectList().then(response => {
                this.subjectOneList = response.data.list
            })
        },
        // 查询所有的讲师
        getListTeacher() {
            course.getListTeacher().then(response => {
                this.teacherList=response.data.items
            })
        },
        subjectLevelOneChanged(value) {
            // 遍历所有的分类，包含一级和二级
            for (let i = 0; i < this.subjectOneList.length; i++) {
                if (this.subjectOneList[i].id === value) {
                // 从一级分类获取里面所有的二级分类
                this.subjectTwoList = this.subjectOneList[i].children
                // 把二级分类的id值清空
                this.courseQuery.subjectId = ''
                }
            }
        },
        // 根据id删除
        removeDataById(id) {
            // alert(id)
            this.$confirm('此操作将永久删除该课程记录, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {// 点击确定，执行then方法
                course.deleteCourseById(id)
                    .then(response => { // 删除成功                    
                        // 提示信息
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        })
                        // 回到列表页面
                        this.getList()
                    })
            })// 点击确定，执行catch方法
            .catch((response) => { 
                if (response === 'cancel') {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    })
                } else {
                    this.$message({
                    type: 'error',
                    message: '删除失败'
                    })
                }
            })
        },
    }    
}
</script>
<style scoped>
    .myClassList .info {
        width: 450px;
        overflow: hidden;
    }
    .myClassList .info .pic {
        width: 150px;
        height: 90px;
        overflow: hidden;
        float: left;
    }
    .myClassList .info .pic a {
        display: block;
        width: 100%;
        height: 100%;
        margin: 0;
        padding: 0;
    }
    .myClassList .info .pic img {
        display: block;
        width: 100%;
    }
    .myClassList td .info .title {
        width: 280px;
        float: right;
        height: 90px;
    }
    .myClassList td .info .title a {
        display: block;
        height: 48px;
        line-height: 24px;
        overflow: hidden;
        color: #00baf2;
        margin-bottom: 12px;
    }
    .myClassList td .info .title p {
        line-height: 20px;
        margin-top: 5px;
        color: #818181;
    }
</style>
