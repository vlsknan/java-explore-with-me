# Дипломный проект курса Java-разработчик

## Explore-with-me
### Описание
Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. Сложнее всего в таком планировании поиск информации и переговоры. Какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться. Explore with me — афиша, где можно предложить какое-либо событие от выставки до похода в кино и набрать компанию для участия в нём.
### Спецификация API:

Для обоих сервисов разработана спецификация API:
* [Основной сервис](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json)
* [Сервис статистики](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json)

Просмотреть спецификацию можно на сайте https://editor-next.swagger.io/.

### В данном приложении используются следующие HTTP-коды:

* 400 - запрос составлен с ошибкой (класс InvalidRequestException)
* 403 - не выполнены условия для совершения операции (класс ConditionsNotMet)
* 404 - объект не найден (класс NotFoundException)
* 409 - запрос привод к нарушению целостности данных (класс ConflictException)
* 500 - внутренняя ошибка сервера

Все необходимые классы ошибок, а также обработчик ошибок расположены в пакете:  
main-service.src.main.java.ru.practicum.exception



