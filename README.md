# How to run
1. Use eclipse maven import
2. Add tomcat service and add this project
3. `GET http://localhost:8080/simple-spring-mvc/Orders/query?name=fjd&age=1` You will get `{ name: fjd, age ： 1 }`

# Where is program entrance?
See `DispatcherServlet.java init()`