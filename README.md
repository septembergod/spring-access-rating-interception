spring web环境 基于ip的访问频率限制
=
  1、使用方式 (spring mvc + spring boot 通用)
  --
    只需添加两个注解：
     1、在spring管理的configuration Bean中添加注解：EnableGlobalMethodRating
     2、在需要拦截的方式上添加注解：Rating
  

