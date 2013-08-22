﻿#-------------------------------------------------------------------------------
# Name:        strings
# Purpose:     russian translation
#
# Author:      Aliaksei Plashchanski
#
# Created:     21/08/2013
# Copyright:   (c) Aliaksei Plashchanski 2013
# Licence:     All rights reserved
#-------------------------------------------------------------------------------

import common

lang = 'ru'
appname = u'Расходы'
apptitle = u'Расходы - приложение для Android'
headcontent = u'Страница приложения Расходы для Android'
headbrand = u'Расходы для Android'

twitter_link = u'<a href="https://twitter.com/expenses_" class="twitter-follow-button" data-show-count="false" data-size="large">Follow @expenses_</a><script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?\'http\':\'https\';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\'://platform.twitter.com/widgets.js\';fjs.parentNode.insertBefore(js,fjs);}}(document, \'script\', \'twitter-wjs\');</script>'

nav_main = u'Главная'
nav_about = u'Новости'
nav_download = u'Скачать'
nav_contact = u'Контакты/Поддержка'
nav_feedback = u'Отзывы'
nav_donate = u'Donate'

download_header = u'Последняя версия приложения доступна для загрузки в Google Play'
download_gotogoogleplay = u'Перейти в Google Play'
download_gotodropbox = u'Загрузить приложение из Dropbox'

contact_header = u'Связаться с нами'
contact_content = u'Чтобы быть в курсе последних новостей читайте наш twitter %s. Также не стесняйтесь обращаться к нам с сообщениями об ошибках, выражением благодарности и предложениями по улучшению приложения Расходы.' %twitter_link
contact_reply_label = u'Адрес для ответа'
contact_reply_hint = u'Введите адрес email'
contact_message_label = u'Сообщение'
contact_message_hint = u'Введите текст сообщения'
contact_button_send = u'Отправить'

feedback_header = u'Отзывы пользователей можно прочитать на странице приложения в google play'
feedback_content = u'Ваши отзыва очень важны для нас. Мы регулярно читаем отзывы на google play и улучшаем наше приложение в соответствие с Вашими ожиданиями.'

donate_header = u'Пожертвования для развития приложения Расходы'
donate_content = u'Нам необходима Ваша финансовая помощь для дальнейшего развития приложения Расходы. Сделав пожертвование, вы получите возможность отправить нам сообщение с Вашими предложениями о дальнейшем развитии приложения Расходы.'
donate_content2 = common.donation_link % u'<label>Пожертвование можно сделать по ссылке: </label>'

def main():
    pass

if __name__ == '__main__':
    main()
