import request from '@/utils/request'

export default {
    //带条件课程分页查询
    getCourseList(page,limit,searchObj){
        return request({
            url: `/eduservice/coursefront/getFrontCourseList/${page}/${limit}`,
            method: 'post',
            data: searchObj
        })
    },
    //查询所有分类的方法
    getAllSubject(){
        return request({
            url: `/eduservice/subject/getAllSubject`,
            method: 'get'
        })
    },
    //查询课程详情
    getCourseInfo(courseId){
        return request({
            url: `/eduservice/coursefront/getCourseInfo/${courseId}`,
            method: 'get'
        })
    }
}