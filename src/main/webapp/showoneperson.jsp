<%@ page language="java" contentType="text/html; charset=utf-8"
     pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>one person</title>
    </head>
    <body>
        <table>
            <tr>
                <th>姓名</th>
                <th>年龄</th>
            </tr>

                <tr>
                    <td>${person.name }</td>
                    <td>${person.age }</td>
                </tr>

        </table>
    </body>
</html>