package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.HttpSession;

import java.util.Collection;
import java.util.Map;

public class ListUserController extends AbstractController{
  private static final Logger log = LoggerFactory.getLogger(ListUserController.class);
  @Override
  public void doGet(HttpRequest request, HttpResponse response) {
    if (!isLogin(request.getSession())) {
      response.sendRedirect("/user/login.html");
      return;
    }

    Collection<User> users = DataBase.findAll();
    StringBuilder sb = new StringBuilder();
    sb.append("<table border='1'>");
    for (User user : users) {
      sb.append("<tr>");
      sb.append("<td>").append(user.getUserId()).append("</td>");
      sb.append("<td>").append(user.getName()).append("</td>");
      sb.append("<td>").append(user.getEmail()).append("</td>");
      sb.append("</tr>");
    }
    sb.append("</table>");
    response.forwardBody(sb.toString());
  }

  private boolean isLogin(HttpSession session) {
    Object user = session.getAttribute("user");

    return user != null;
  }
}
