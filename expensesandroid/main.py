import jinja2
import os
import webapp2
import datetime
import importlib
import common
from google.appengine.api import mail
from google.appengine.ext import db

jinja_environment = jinja2.Environment(
  loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))

S = importlib.import_module('ru')

def loadlang(request):
  global S
  if request.get('lang') == 'en':
    S = importlib.import_module('en')
  else:
    S = importlib.import_module('ru')

def loadindexpagevars(dictvars):
  global S
  index_page_vars = {
  'lang' : S.lang,
  'apptitle' : S.apptitle,
  'headcontent' : S.headcontent,
  'nav_main' : S.nav_main,
  'nav_about' : S.nav_about,
  'nav_download' : S.nav_download,
  'nav_contact' : S.nav_contact,
  'nav_feedback' : S.nav_feedback,
  'nav_donate' : S.nav_donate,
  'headbrand' : S.headbrand
  }

  for k, v in dictvars.iteritems():
      index_page_vars[k] = v
  return index_page_vars

class MainPage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    page_content = u'<p>Приложение позволяет вести учет ежедневных семейных расходов по различным категориям, а также просматривать ежедневную и ежемесячную статистику расходов по этим категориям.</p>'
    page_content += u'<p><b>Доступные функции</b></p>'
    page_content += u'<ul>'
    page_content += u'<li>встроенные категории расходов, создание новых категорий расходов;</li>'
    page_content += u'<li>добавление расходов в доступных категориях (можно добавить сумму, дату, комментарий и тег);</li>'
    page_content += u'<li>просмотр полной истории расходов в каждой категории с возможностью вносить изменения;</li>'
    page_content += u'<li>внесение изменений доступно через контекстные меню, которые доступны по длинному нажатию на соответствующей позиции: категории или записи в истории расходов;</li>'
    page_content += u'<li>автозаполнение комментариев, тегов и валюты расходов на основе ранее введенных данных;</li>'
    page_content += u'<li>автоматическая подстановка последней использованной валюты расходов в рамках каждой категории расходов;</li>'
    page_content += u'<li>просмотр ежедневной и ежемесячной статистики расходов.</li>'
    page_content += u'</ul>'
    page_content += u'<p><b>Скриншоты программы</b></p>'
    page_content +=u'<img src="img/categories_list.png" class="img-polaroid" />'
    page_content +=u'<img src="img/category_menu.png" class="img-polaroid" />'
    page_content +=u'<img src="img/category_history.png" class="img-polaroid" />'
    page_content +=u'<img src="img/category_history_menu.png" class="img-polaroid" />'
    page_content +=u'<img src="img/daily_stat.png" class="img-polaroid" />'
    page_content +=u'<img src="img/monthly_stat.png" class="img-polaroid" />'
    page_content +=u'<img src="img/new_category.png" class="img-polaroid" />'

    template_values = {
      'class_active_main' : 'class="active"',
      'page_header': u'Расходы для Android - приложение для ведения семейных расходов',
      'page_content': page_content,
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class AboutPage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    page_content = u'<p></p>'
    page_content += u'<blockquote><p>'
    page_content += u'С 12 августа 2013 года новости и актуальная информация о развитии проекта доступна в twitter %s' %common.twitter_link
    page_content += u'</p></blockquote>'
    page_content += u'<blockquote><p>'
    page_content += u'Создание резервных копий данных в Dropbox аккаунте пользователя будет реализовано к середине апреля 2013 года в связи с работами по локализации приложения'
    page_content += u'</p></blockquote>'
    page_content += u'<blockquote><p>'
    page_content += u'Создание резервных копий данных в Dropbox аккаунте пользователя будет реализовано к середине марта 2013 года'
    page_content += u'</p></blockquote>'
    page_content += u'<blockquote><p>'
    page_content += u'24 февраля 2013 года вышел релиз очередной версии "Расходы" для Android, были реализованы следующие функции:</p>'
    page_content += u'<ul>'
    page_content += u'<li>Возможность удаления и изменения категорий расходов через контекстное меню доступное по длинному нажатию на папку соответствующей категории расходов</li>'
    page_content += u'<li>Возможность редактирования записей в истории расходов через контекстное меню доступное по длинному нажатию на соответствующую запись в истории расходов</li>'
    page_content += u'<li>Автозаполнение комментариев, тегов, валюты расходов на основе ранее введенных данных</li>'
    page_content += u'<li>Автоподстановка последней использованной валюты расходов в рамках каждой категории</li>'
    page_content += u'</ul></blockquote>'
    page_content += u'<blockquote><p>'
    page_content += u'В связи со стремительным ростом интереса пользователей к приложению, команда разработчиков постарается реализовать новые функции раньше 1 марта 2013 года'
    page_content += u'</p></blockquote>'
    page_content += u'<blockquote><p>В следующей версии будут доступны новые функции (дата релиза 1 марта 2013 года)</p>'
    page_content += u'<ul>'
    page_content += u'<li>Создание резервных копий данных в Dropbox аккаунте пользователя</li>'
    page_content += u'<li>Восстановление данных пользователя из резервных копий (удобно при переходе на другое устройство)</li>'
    page_content += u'<li>Возможность удаления и изменения категорий расходов</li>'
    page_content += u'<li>Возможность редактирования записей в истории расходов</li>'
    page_content += u'<li>Автозаполнение комментариев, тегов, валюты расходов на основе ранее введенных данных</li>'
    page_content += u'</ul></blockquote>'
    page_content += u'<blockquote><p>'
    page_content += u'Первая официальная версия приложеня 1.4 разработана для конкурса velcom android masters. '
    page_content += u'Дополнительная информация о конкурсе доступна на странице <a href="http://android.velcom.by">http://android.velcom.by</a>'
    page_content += u'</p></blockquote>'

    template_values = {
      'class_active_about' : 'class="active"',
      'page_header': u'Актуальная информация о развитии проекта',
      'page_content': page_content
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class DownloadPage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    template_values = {
      'class_active_download' : 'class="active"',
      'page_header': S.download_header,
      'page_content': u'<a href="%s">%s</a>' % (common.googleplay_link, S.download_gotogoogleplay),
      'page_content2': u'<a href="%s">%s</a>' % (common.dropbox_link, S.download_gotodropbox)
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class ContactPage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    form = u'<form action="/sendemail" method="POST">\
        <label>%s</label>\
        <input type="text" name="reply_email" placeholder="%s">\
        <label>%s</label>\
        <textarea rows="7" name="message" placeholder="%s"></textarea>\
        <br><button type="submit" class="btn btn-primary">%s</button>\
        </form>' % (S.contact_reply_label, S.contact_reply_hint, S.contact_message_label, S.contact_message_hint, S.contact_button_send)
    template_values = {
      'class_active_contact' : 'class="active"',
      'page_header': S.contact_header,
      'page_content': S.contact_content,
      'page_content2': form
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class SendEmail(webapp2.RequestHandler):
  def post(self):
    loadlang(self.request)
    email = self.request.get('reply_email')
    message = self.request.get('message')
    isdonation = self.request.get('donation')

    page_header = u'Сообщение успешно отправлено'
    page_content = ''

    if isdonation != 'true':
      if not mail.is_email_valid(email):
        page_header = u'Неверный адрес для ответа'
      elif len(message) == 0:
        page_header = u'Напишите пожалуйста текст сообщения'
      else:
        subject = "Expenses for Android feedback"
        body = "Message from %s \n %s" %(email , message)
        mail.send_mail('aleksey.ploschanskiy@gmail.com', 'aleksey.ploschanskiy@gmail.com', subject, body)
    else:
        subject = "Donation - Expenses for Android"
        body = "Message from %s \n %s" %(email , message)
        mail.send_mail('aleksey.ploschanskiy@gmail.com', 'aleksey.ploschanskiy@gmail.com', subject, body)

    template_values = {
      'page_header': page_header,
      'page_content': page_content
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class FeedbackPage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    template_values = {
      'class_active_feedback' : 'class="active"',
      'page_header': S.feedback_header,
      'page_content' : S.feedback_content,
      'page_content2': common.market_link
    }

    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class DonatePage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    template_values = {
      'class_active_donate' : 'class="active"',
      'page_header': S.donate_header,
      'page_content' : S.donate_content,
      'page_content2': S.donate_content2
    }

    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class DonateFinishPage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    template_values = {
      'class_active_donate' : 'class="active"',
      'page_header': u'Спасибо за Ваш вклад в развитие приложения!',
      'page_content': u'<form action="/sendemail?donation=true" method="POST">\
        <label>Ваше имя</label>\
        <input type="text" name="reply_email" placeholder="Напишите имя/email">\
        <label>Как по-вашему можно улучшить приложение Расходы</label>\
        <textarea rows="7" name="message"></textarea>\
        <br><button type="submit" class="btn btn-primary">Отправить</button>\
        </form>'
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

app = webapp2.WSGIApplication([('/', MainPage), ('/main', MainPage),
                               ('/about', AboutPage), ('/download', DownloadPage),
                               ('/contact', ContactPage), ('/sendemail', SendEmail),
                               ('/feedback', FeedbackPage), ('/donate', DonatePage),
                               ('/donatefinish', DonateFinishPage)], debug=True)