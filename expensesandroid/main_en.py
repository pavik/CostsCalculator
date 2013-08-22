import jinja2
import os
import webapp2
import datetime
from google.appengine.api import mail
from google.appengine.ext import db

jinja_environment = jinja2.Environment(
  loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))

index_page = 'index_en.html'
donation_link = u'<form class="form-inline" action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top"><input type="hidden" name="cmd" value="_s-xclick"><input type="hidden" name="hosted_button_id" value="E8XCQADY5X94Q">%s<input type="image" src="https://www.paypalobjects.com/en_US/PL/i/btn/btn_donateCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!"><img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1"></form>'

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
    template = jinja_environment.get_template(index_page)
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
    template = jinja_environment.get_template(index_page)
    self.response.out.write(template.render(template_values))

class DownloadPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'class_active_download' : 'class="active"',
      'page_header': u'Latest version of the application is avalaible in Google Play',
      'page_content': u'<p><a href="https://play.google.com/store/apps/details?id=net.costcalculator.activity">Go to Google Play</a></p><p><a href="https://www.dropbox.com/s/ssu6fq57ndhhe62/Expenses.apk">Download application from Dropbox</a></p>'
    }
    template = jinja_environment.get_template(index_page)
    self.response.out.write(template.render(template_values))

class ContactPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'class_active_contact' : 'class="active"',
      'page_header': u'Here you can send message to the application developers team/post problem',
      'page_content': u'<form action="/sendemail" method="POST">\
        <label>Reply email</label>\
        <input type="text" name="reply_email" placeholder="Type email here">\
        <label>Message</label>\
        <textarea rows="7" name="message" placeholder="Type message here"></textarea>\
        <br><button type="submit" class="btn btn-primary">Send</button>\
        </form>'
    }
    template = jinja_environment.get_template(index_page)
    self.response.out.write(template.render(template_values))

class SendEmail(webapp2.RequestHandler):
  def post(self):
    email = self.request.get('reply_email')
    message = self.request.get('message')
    page_header = u'Message has sent'
    page_content = ''

    if isdonation != 'true':
      if not mail.is_email_valid(email):
        page_header = u'Invalid reply email'
      elif len(message) == 0:
        page_header = u'Enter text of the message please'
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
    template = jinja_environment.get_template(index_page)
    self.response.out.write(template.render(template_values))

class FeedbackPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'class_active_feedback' : 'class="active"',
      'page_header': u'Feedback of users you can see and post in google play',
      'page_content': u'<a href="https://play.google.com/store/apps/details?id=net.costcalculator.activity"><img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_60.png" /></a>'
    }

    template = jinja_environment.get_template(index_page)
    self.response.out.write(template.render(template_values))

class DonatePage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'class_active_donate' : 'class="active"',
      'page_header': u'Voluntary donations - it is your contribution in the future of Expenses',
      'page_content': donation_link % u'<label>When you click the PayPal icon, you will be taken to the PayPal website where you will be able to donate any amount: </label>'
    }

    template = jinja_environment.get_template(index_page)
    self.response.out.write(template.render(template_values))

class DonateFinishPage(webapp2.RequestHandler):
  def get(self):
    template_values = {
      'class_active_donate' : 'class="active"',
      'page_header': u'Thank you for your contribution!',
      'page_content': u'<form action="/sendemail?donation=true" method="POST">\
        <label>Your name</label>\
        <input type="text" name="reply_email" placeholder="Type name/email">\
        <label>How we can improve application Expenses</label>\
        <textarea rows="7" name="message"></textarea>\
        <br><button type="submit" class="btn btn-primary">Send</button>\
        </form>'
    }
    template = jinja_environment.get_template(index_page)
    self.response.out.write(template.render(template_values))

app = webapp2.WSGIApplication([('/en/', MainPage), ('/en/main', MainPage),
                               ('/en/about', AboutPage), ('/en/download', DownloadPage),
                               ('/en/contact', ContactPage), ('/en/sendemail', SendEmail),
                               ('/en/feedback', FeedbackPage), ('/en/donate', DonatePage),
                               ('/en/donatefinish', DonateFinishPage)], debug=True)