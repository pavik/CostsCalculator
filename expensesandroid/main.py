import jinja2
import os
import webapp2
import datetime
from google.appengine.api import mail
from google.appengine.ext import db

jinja_environment = jinja2.Environment(
  loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))

class MainPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'page_header': u'Расходы для Android - приложение для ведения семейных расходов',
      'page_content': u'<p>Приложение позволяет вести учет ежедневных семейных расходов.</p>'
    }
    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

class AboutPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'page_header': u'Приложение разработано для конкурса velcom android masters',
      'page_content': u'<p>Дополнительная информация о конкурсе доступна на странице <a href="http://android.velcom.by">http://android.velcom.by</a></p>'
    }
    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

class DownloadPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'page_header': u'Последняя версия приложения доступна для загрузки в Google Play',
      'page_content': u'<p><a href="https://play.google.com">Перейти в Google Play</a></p>'
    }
    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

class ContactPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'page_header': u'На этой странице можно отправить сообщение разработчикам',
      'page_content': u'<form action="/sendemail" method="POST">\
        <label>Адрес для ответа</label>\
        <input type="text" name="reply_email" placeholder="Напишите email">\
        <label>Текст сообщения</label>\
        <textarea rows="7" name="message" placeholder="Напишите текст сообщения"></textarea>\
        <br><button type="submit" class="btn btn-primary">Отправить</button>\
        </form>'
    }
    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

class SendEmail(webapp2.RequestHandler):
  def post(self):
    email = self.request.get('reply_email')
    message = self.request.get('message')
    page_header = u'Сообщение успешно отправлено'
    page_content = ''

    if not mail.is_email_valid(email):
      page_header = u'Неверный адрес для ответа'
    elif len(message) == 0:
      page_header = u'Напишите пожалуйста текст сообщения'
    else:
      subject = "Expenses for Android feedback"
      body = "Message from %s \n %s" %(email , message)
      mail.send_mail('aleksey.ploschanskiy@gmail.com', 'aleksey.ploschanskiy@gmail.com', subject, body)

    template_values = {
      'page_header': page_header,
      'page_content': page_content
    }
    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

class FeedbackPage(webapp2.RequestHandler):
  def get(self):
    feedbacklist = db.GqlQuery("SELECT * FROM FeedbackMessage")

    template_values = {
      'page_header': u'На этой странице можно оставить отзыв о приложении',
      'page_content': u'<form action="/sendfeedback" method="POST">\
        <label>Ваше имя</label>\
        <input type="text" name="author" placeholder="Напишите имя">\
        <label>Ваш отзыв</label>\
        <textarea rows="7" name="message" placeholder="Напишите отзыв"></textarea>\
        <br><button type="submit" class="btn btn-primary">Отправить</button>\
        </form>',
      'feedbacklist' : feedbacklist
    }

    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

class FeedbackMessage(db.Model):
  user = db.StringProperty(required=True)
  text = db.StringProperty(required=True)
  postdate = db.DateProperty()

class SendFeedback(webapp2.RequestHandler):
  def post(self):
    page_header = u'Спасибо за отзыв'
    page_content = u'<a href="http://expensesandroid.appspot.com/feedback">Назад</a>'

    author = self.request.get('author')
    message = self.request.get('message')

    if len(author) == 0:
      page_header = u'Напишите пожалуйста имя'
    elif len(message) == 0:
      page_header = u'Напишите пожалуйста текст отзыва'
    else:
      msg = FeedbackMessage(user = author, text = message)
      msg.postdate = datetime.datetime.now().date()
      msg.put()

    template_values = {
      'page_header': page_header,
      'page_content': page_content
    }

    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

class DeleteFeedback(webapp2.RequestHandler):
  def get(self):
    page_header = u'Отзыв удален'
    page_content = u'<a href="http://expensesandroid.appspot.com/feedback">Назад</a>'

    id = self.request.get('id')

    if len(id) == 0:
      page_header = u'Не указан идентификатор отзыва'
    else:
      msg = db.get(id)
      msg.delete()

    template_values = {
      'page_header': page_header,
      'page_content': page_content
    }

    template = jinja_environment.get_template('index.html')
    self.response.out.write(template.render(template_values))

app = webapp2.WSGIApplication([('/', MainPage), ('/main', MainPage),
                               ('/about', AboutPage), ('/download', DownloadPage),
                               ('/contact', ContactPage), ('/sendemail', SendEmail),
                               ('/feedback', FeedbackPage), ('/sendfeedback', SendFeedback),
                               ('/delete', DeleteFeedback)], debug=True)