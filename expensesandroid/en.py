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
