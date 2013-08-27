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
    page_content = u'<p>%s</p>' % S.main_app_brief
    page_content += u'<p><b>%s</b></p>' % S.main_app_features

    page_content += u'<ul>'
    for item in S.main_app_features_list:
      page_content += u'<li>%s</li>' % item
    page_content += u'</ul>'

    page_content += u'<p><b>%s</b></p>' % S.main_printscreen
    page_content += u'<ul class="thumbnails">'
    for img in common.main_images:
      page_content +=u'<li class="span4"><div class="thumbnail"><img data-src="holder.js/240x400" alt="" src="%s"></div></li>' % (S.main_img_path + img)
    page_content += u'</ul>'

    template_values = {
      'class_active_main' : 'class="active"',
      'page_header': S.main_header,
      'page_content': page_content,
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

class NewsPage(webapp2.RequestHandler):
  def get(self):
    loadlang(self.request)
    page_content = u'<p></p>'
    for s in S.news_list:
        page_content += u'<blockquote><p>%s</p></blockquote>' % s

    template_values = {
      'class_active_about' : 'class="active"',
      'page_header': S.news_header,
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
    form = u'<form action="/sendemail?lang=%s" method="POST">\
        <label>%s</label>\
        <input type="text" name="reply_email" placeholder="%s">\
        <label>%s</label>\
        <textarea rows="7" name="message" placeholder="%s"></textarea>\
        <br><button type="submit" class="btn btn-primary">%s</button>\
        </form>' % (S.lang, S.contact_reply_label, S.contact_reply_hint, S.contact_message_label, S.contact_message_hint, S.contact_button_send)
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

    page_header = S.mail_sent_ok
    page_content = ''

    if isdonation != 'true':
      if not mail.is_email_valid(email):
        page_header = S.mail_invalid_address
      elif len(message) == 0:
        page_header = S.mail_invalid_content
      else:
        body = "Message from %s \n %s" %(email , message)
        mail.send_mail('aleksey.ploschanskiy@gmail.com', 'aleksey.ploschanskiy@gmail.com', S.mail_subject_contact, body)
    else:
        body = "Message from %s \n %s" %(email , message)
        mail.send_mail('aleksey.ploschanskiy@gmail.com', 'aleksey.ploschanskiy@gmail.com', S.mail_subject_donate, body)

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
      'page_header': S.donate_finish_header,
      'page_content': u'<form action="/sendemail?lang=%s&donation=true" method="POST">\
        <label>%s</label>\
        <input type="text" name="reply_email" placeholder="%s">\
        <label>%s</label>\
        <textarea rows="7" name="message"></textarea>\
        <br><button type="submit" class="btn btn-primary">%s</button>\
        </form>' % (S.lang, S.donate_finish_name_label, S.donate_finish_name_hint, S.donate_finish_msg_label, S.donate_finish_btn_send)
    }
    template = jinja_environment.get_template(common.index_page)
    self.response.out.write(template.render(loadindexpagevars(template_values)))

app = webapp2.WSGIApplication([('/', MainPage), ('/main', MainPage),
                               ('/about', NewsPage), ('/download', DownloadPage),
                               ('/contact', ContactPage), ('/sendemail', SendEmail),
                               ('/feedback', FeedbackPage), ('/donate', DonatePage),
                               ('/donatefinish', DonateFinishPage)], debug=True)