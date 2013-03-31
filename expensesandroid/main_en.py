﻿import jinja2
import os
import webapp2
import datetime
from google.appengine.api import mail
from google.appengine.ext import db

jinja_environment = jinja2.Environment(
  loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))

class MainPage(webapp2.RequestHandler):
  def get(self):
    page_content = u'<p>Application manages your daily expenses in different categories and provides daily and monthly expenses reports in these categories.</p>'
    page_content += u'<p><b>Available functions</b></p>'
    page_content += u'<ul>'
    page_content += u'<li>built-in categories of expenses, unlimited number of new categories;</li>'
    page_content += u'<li>add expenses in available categories (you can enter amount, date, comments and tag);</li>'
    page_content += u'<li>view history of expenses in every category, change items in history;</li>'
    page_content += u'<li>make any changes in your expenses using context menu that is available on long click on proper item (category or expense);</li>'
    page_content += u'<li>autocompletion of comments, tags, currency of expenses based on user experience;</li>'
    page_content += u'<li>autocompletion of currency used in previous transaction in category;</li>'
    page_content += u'<li>view daily and monthly expenses reports.</li>'
    page_content += u'</ul>'
    page_content += u'<p><b>Screenshots</b></p>'
    page_content +=u'<img src="img/categories_list.png" class="img-polaroid" />'
    page_content +=u'<img src="img/category_menu.png" class="img-polaroid" />'
    page_content +=u'<img src="img/category_history.png" class="img-polaroid" />'
    page_content +=u'<img src="img/category_history_menu.png" class="img-polaroid" />'
    page_content +=u'<img src="img/daily_stat.png" class="img-polaroid" />'
    page_content +=u'<img src="img/monthly_stat.png" class="img-polaroid" />'
    page_content +=u'<img src="img/new_category.png" class="img-polaroid" />'

    template_values = {
      'class_active_main' : 'class="active"',
      'page_header': u'Expenses for Android - good application for managing family expenses',
      'page_content': page_content,
      'lang_ref': u'<p><a href="main">English</a> | <a href="/../main">Русский</a></p>'
    }
    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

class AboutPage(webapp2.RequestHandler):
  def get(self):
    page_content = u'<p></p>'
    page_content += u'<blockquote><p>'
    page_content += u'Dropbox backup into user account will be implemented by the middle of April 2013 because translations to different languages are preparing for next version'
    page_content += u'</p></blockquote>'
    page_content += u'<blockquote><p>'
    page_content += u'Dropbox backup into user account will be implemented by the middle of March 2013'
    page_content += u'</p></blockquote>'
    page_content += u'<blockquote><p>'
    page_content += u'24 february 2013 released next version of "Expenses" for Android, next functions are available:'
    page_content += u'<ul>'
    page_content += u'<li>Possibility of removing and editing categories of expenses from context menu available on long click on folder</li>'
    page_content += u'<li>Possibility of editing history of expenses from context menu available on long click on item in history of expenses</li>'
    page_content += u'<li>Autocompletion of comments, tags, currency of expenses based on user experience</li>'
    page_content += u'<li>Autocompletion of currency used in previous transaction in category</li>'
    page_content += u'</ul></p></blockquote>'
    page_content += u'<blockquote><p>In the next release of Expenses for Android will be available: (planned date 1 of March 2013)'
    page_content += u'<ul>'
    page_content += u'<li>Dropbox backup of expenses into user account</li>'
    page_content += u'<li>Restore of expenses from Dropbox account (very convenient when you buy new device)</li>'
    page_content += u'<li>Possibility of removing and editing categories of expenses</li>'
    page_content += u'<li>Possibility of editing history of expenses</li>'
    page_content += u'<li>Autocompletion of comments, tags, currency of expenses based on user experience</li>'
    page_content += u'</ul></p></blockquote>'

    template_values = {
      'class_active_about' : 'class="active"',
      'page_header': u'What is new',
      'page_content': page_content
    }
    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

class DownloadPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'class_active_download' : 'class="active"',
      'page_header': u'Latest version of the application is avalaible in Google Play',
      'page_content': u'<p><a href="https://play.google.com/store/apps/details?id=net.costcalculator.activity">Go to Google Play</a></p><p><a href="https://www.dropbox.com/s/ssu6fq57ndhhe62/Expenses.apk">Download application from Dropbox</a></p>'
    }
    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

class ContactPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'class_active_contact' : 'class="active"',
      'page_header': u'Here you can send message to application developers team',
      'page_content': u'<form action="/sendemail" method="POST">\
        <label>Reply email</label>\
        <input type="text" name="reply_email" placeholder="Type email here">\
        <label>Message</label>\
        <textarea rows="7" name="message" placeholder="Type message here"></textarea>\
        <br><button type="submit" class="btn btn-primary">Send</button>\
        </form>'
    }
    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

class SendEmail(webapp2.RequestHandler):
  def post(self):
    email = self.request.get('reply_email')
    message = self.request.get('message')
    page_header = u'Message has sent'
    page_content = ''

    if not mail.is_email_valid(email):
      page_header = u'Invalid reply email'
    elif len(message) == 0:
      page_header = u'Enter text of the message please'
    else:
      subject = "Expenses for Android feedback"
      body = "Message from %s \n %s" %(email , message)
      mail.send_mail('aleksey.ploschanskiy@gmail.com', 'aleksey.ploschanskiy@gmail.com', subject, body)

    template_values = {
      'page_header': page_header,
      'page_content': page_content
    }
    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

class FeedbackPage(webapp2.RequestHandler):
  def get(self):
    feedbacklist = db.GqlQuery("SELECT * FROM FeedbackMessage")

    template_values = {
      'class_active_feedback' : 'class="active"',
      'page_header': u'Here you can post your feedback about application Expenses for Android',
      'page_content': u'<form action="/sendfeedback" method="POST">\
        <label>Name</label>\
        <input type="text" name="author" placeholder="Type name here">\
        <label>Message</label>\
        <textarea rows="7" name="message" placeholder="Type message here"></textarea>\
        <br><button type="submit" class="btn btn-primary">Send</button>\
        </form>',
      'feedbacklist' : feedbacklist
    }

    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

class FeedbackMessage(db.Model):
  user = db.StringProperty(required=True)
  text = db.StringProperty(required=True)
  postdate = db.DateProperty()

class SendFeedback(webapp2.RequestHandler):
  def post(self):
    page_header = u'Thank you'
    page_content = u'<a href="http://expensesandroid.appspot.com/feedback">Back</a>'

    author = self.request.get('author')
    message = self.request.get('message')

    if len(author) == 0:
      page_header = u'Enter your name please'
    elif len(message) == 0:
      page_header = u'Enter message please'
    else:
      msg = FeedbackMessage(user = author, text = message)
      msg.postdate = datetime.datetime.now().date()
      msg.put()

    template_values = {
      'page_header': page_header,
      'page_content': page_content
    }

    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

class DeleteFeedback(webapp2.RequestHandler):
  def get(self):
    page_header = u'Feedback has removed'
    page_content = u'<a href="http://expensesandroid.appspot.com/feedback">Back</a>'

    id = self.request.get('id')

    if len(id) == 0:
      page_header = u'Feedback identifier is not specified'
    else:
      msg = db.get(id)
      msg.delete()

    template_values = {
      'page_header': page_header,
      'page_content': page_content
    }

    template = jinja_environment.get_template('index_en.html')
    self.response.out.write(template.render(template_values))

app = webapp2.WSGIApplication([('/en/', MainPage), ('/en/main', MainPage),
                               ('/en/about', AboutPage), ('/en/download', DownloadPage),
                               ('/en/contact', ContactPage), ('/en/sendemail', SendEmail),
                               ('/en/feedback', FeedbackPage), ('/en/sendfeedback', SendFeedback),
                               ('/en/delete', DeleteFeedback)], debug=True)