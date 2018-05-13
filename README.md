# Playphrase.me

Playphrase.me website v. 2.0 (2018).

## Full Stack Clojure

По нашему мнению, на данный момент, Сlojure и СlojureScript являются лучшим выбором для создания систем для вэб c насыщенным UI. Старая версия сайта была реализована на angular.js/node.js (2014) и она работала хорошо для своего времени. Новая же версия отражает новые требования, проще по устройству и значительно выросла в качестве и быстродействии.

Server - Luminus (Clojure). Client - reagent/re-frame (ClojureScript).
  
Сервер на Clojure создает Rest API, и обеспечивает ограничения доступа к ресурсам. Посмотреть документаци к API можно по  
[адресу](https://www.playphrase.me/api-docs/index.html).

Клиентское приложение реализовано на re-frame (Reagent (React.js для ClojureScript), а так же MVC фреймворк (аналог Redux.js для ClojureScript)).
Клиентское приложение обеспечивает функционал по поиску - быстрое отображение результата в процессе ввода поискового запроса, клавиатурные сокращения, навигация по результату, доступ к RestAPI, идентификация пользователей. 

## Сервис

Сайт занимается поиском фраз в цитатах из  фильмов и сериалов.
Особенность сервиса в результате поиска - при поиске создается бесконечный "стрим" из видео-цитат, которые проигрываются друг за другом. 

## Челенджи 

При создании сайта были решены следующие проблемы - быстрый "inline" поиск фраз, "бесшовное" воспроизведение видео, "караоке" - подсветка произносимого в данный момент слова, переходы на отдельные слова, предложение исправление поиска, оптимизация для mobile, responsive interface. 

## Предзагрузка видео
 
Загрузка видео происходит в бэкграунде - одновременно на странице существует два видео проигрывателя - один соответсвенно проигрывает видео, а другой загружает следующее - такой конвейер загрузки. Таким образом добивается эффект "бесшовности", хотя в действительно видео-стрим состоит из множества видео-фрагментов, а не одного большого. Пользователи отмечают "хорошую скорость" работы сервиса - немалая часть этого ощущения основывается на быстрой загрузке видео.

## Inline поиск

Так же был реализован поиск фраз при вводе текста пользователя - запрос на сервер идет сразу, без задержек, обеспечивая отзывчивость сервиса. Данный быстрый поиск был бы не возможен без отдельного сервиса поиска фраз (код данного сервиса не доступен в дынных момент на github). С помощью данного сервиса (так же реализованного на Clojure) поиск по тексту производится в среднем за 50 наносекунд. 

## Оптимизация для мобильных устройств



## Старая версия

Old site was built on angular.js and node.js (2014)
Доступна в этом репозитории (ссылка).

## Лицензия

Copy rights © Playphrase.me