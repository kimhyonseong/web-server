package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpSession;

public class LoginController implements Controller{
  private static final Logger log = LoggerFactory.getLogger(LoginController.class);

  @Override
  public void service(HttpRequest request, HttpResponse response) {
    User user = DataBase.findUserById(request.getParameter("userId"));
    if (user != null) {
      if (user.login(request.getParameter("password"))) {
        HttpSession session = request.getSession();
        session.setAttribute("user",user);
        response.sendRedirect("/user/index.html");
      } else {
        response.sendRedirect("/user/login_failed.html");
      }
    } else {
      response.sendRedirect("/user/login_failed.html");
    }
  }
}
