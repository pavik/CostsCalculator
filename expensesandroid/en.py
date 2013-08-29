#-------------------------------------------------------------------------------
# Name:        strings
# Purpose:     english translation
#
# Author:      Aliaksei Plashchanski
#
# Created:     21/08/2013
# Copyright:   (c) Aliaksei Plashchanski 2013
# Licence:     All rights reserved
#-------------------------------------------------------------------------------

import common

lang = 'en'
appname = "Expenses"
apptitle = "Expenses - Android Application"
headcontent = "Page of application Expenses for Android"
headbrand = "Expenses for Android"

nav_main = 'Main'
nav_about = 'News'
nav_download = 'Download'
nav_contact = 'Contact/Support'
nav_feedback = 'Feedback'
nav_donate = 'Donate'

main_header = u'Expenses for Android - great application for managing family expenses'
main_img_path = u'img/en/'
main_printscreen = u'Screenshots'
main_app_brief = u'The application allows you to keep track of daily household expenditure on various categories and tags, create and modify existing categories of expenditure, as well as view statistics of expense (per day, week, month, year and any period) by categories and tags. In each category, see the full story of expenditure with the possibility of making changes. The application allows you to configure data backup to Dropbox folder on the user\'s schedule.'
main_app_features = u'Available functions'
main_app_features_list = [
  u'built-in categories of expenses, unlimited number of new categories;',
  u'add expenses in available categories (you can enter amount, date, comment and tag);',
  u'view history of expense in every category, change records in history;',
  u'make any changes in your expenses using context menu that is available on long click on proper record (category or expense);',
  u'auto completion of comments, tags, currency of expense based on user experience;',
  u'auto completion of currency used in previous transaction in category;',
  u'view and filter statistic of expenses by categories and tags per year, month, week, day and any period of time;',
  u'preview of expenses in the statistic for period;',
  u'move expenses between categories;',
  u'backup your expenses in Dropbox account.'
]

news_header = u'What is new'
news_list = [
  u'31 of august 2013, released new version of "Expenses", what\'s new:\
  <ul>\
  <li>view and filter statistic of expenses by categories and tags per year, month, week, day and any period of time;</li>\
  <li>preview of expenses in the statistic for period;</li>\
  <li>installation on memory card;</li>\
  <li>optimization of Dropbox scheduled backup.</li>\
  </ul>',
  u'24 of february 2013 released next version of "Expenses" for Android, next functions are available:\
    <ul>\
    <li>Possibility of removing and editing categories of expenses from context menu available on long click on folder</li>\
    <li>Possibility of editing history of expenses from context menu available on long click on item in history of expenses</li>\
    <li>Autocompletion of comments, tags, currency of expenses based on user experience</li>\
    <li>Autocompletion of currency used in previous transaction in category</li>\
    </ul>',
  u'In the next release of Expenses for Android will be available: (planned date 1 of March 2013)\
    <ul>\
    <li>Dropbox backup of expenses into user account</li>\
    <li>Restore of expense from Dropbox account (very convenient when you buy new device)</li>\
    <li>Possibility of removing and editing categories of expense</li>\
    <li>Possibility of editing history of expense</li>\
    <li>Autocompletion of comments, tags, currency of expense based on user experience</li>\
    </ul>',
  ]

download_header = 'Latest version of the application there is in Google Play'
download_gotogoogleplay = 'Go to Google Play'
download_gotodropbox = 'Download application from Dropbox'

contact_header = 'Contact us'
contact_content = 'For ongoing information please read our news. Also, feel free to contact us with any problems with application, thanks and proposals.'
contact_reply_label = 'Reply email'
contact_reply_hint = 'Type email here'
contact_message_label = 'Message'
contact_message_hint = 'Type message here'
contact_button_send = 'Send'

feedback_header = 'Users\' reviews you can read on the application page in google play'
feedback_content = 'Your feedback is important to us. We continuously read your reviews and develop Expenses to meet your expectations.'

donate_header = 'Donate to Expenses'
donate_content = 'We urgently need your help to fund the continued development of Expenses. On donating, you\'ll be granted access, which allows you to send us message and propose what features we work on next.'
donate_content2 = common.donation_link % u'<label>Click here to donate: </label>'
donate_finish_header = u'Thank you for your contribution!'
donate_finish_name_label = u'Your name'
donate_finish_name_hint = u'Enter name/email'
donate_finish_msg_label = u'How we can improve application Expenses'
donate_finish_btn_send = u'Send'

mail_sent_ok = u'Message has been sent'
mail_invalid_address = u'Enter valid reply email'
mail_invalid_content = u'Enter text of the message'
mail_subject_contact = u'Expenses for Android (contact)'
mail_subject_donate = u'Expenses for Android (donation)'

def main():
    pass

if __name__ == '__main__':
    main()
