# spring-access-rating-interception
spring boot 基于ip的访问频率限制

1、spring-boot使用方式：
  只需添加两个注解：
   1、在spring管理的configuration Bean中添加注解：EnableGlobalMethodRating
   2、在需要拦截的方式上添加注解：Rating

2、单纯的spring-mvc环境：
   1、在spring管理的configuration Bean中添加注解：EnableGlobalMethodRating
   2、在需要拦截的方式上添加注解：Rating
   不同点：
   3、手动添加Fliter:SpringRatingFilter
  

