<template>
    <div class="app-container">
        <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />

    <el-tree
        ref="tree2"
        :data="subjectList"
        :props="defaultProps"
        :filter-node-method="filterNode"
        class="filter-tree"
        default-expand-all
    />

    </div>
</template>

<script>
import subject from '@/api/edu/subject'

export default {
    data() {
        return {
            filterText: '',
            subjectList: [], // 返回所有分类数据
            defaultProps: {
                children: 'children',
                label: 'title'
            }
        }
    },
    created() {
        this.getAllSubjectList()
    },
    watch: {
        filterText(val) {
            this.$refs.tree2.filter(val)
        }
    },

    methods: {
        getAllSubjectList() {
            subject.getSubjectList()
                .then(response => {
                if (response.success === true) {
                    this.subjectList = response.data.list
                }
            })
        },
        filterNode(value, data) {
            if (!value) return true
            return data.title.toLowerCase().indexOf(value.toLowerCase()) !== -1
        }
    }
}
</script>

