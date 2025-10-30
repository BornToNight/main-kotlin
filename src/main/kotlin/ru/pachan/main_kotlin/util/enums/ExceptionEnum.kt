package ru.pachan.main_kotlin.util.enums

enum class ExceptionEnum(val message: String) {
    // auth
    SYSTEM_ERROR("Системная ошибка"),
    USER_IS_MISSING("Пользователь не найден"),
    ATTEMPT_TO_BYPASS_ACCESS("Попытка обойти доступ"),
    EXPIRED_OR_INVALID_TOKEN("Просроченный или неправильный токен"),
    INVALID_TOKEN("Неправильный токен"),
    DENIED_TOKEN_CREATE("Просроченный или неправильный токен"),
    INCORRECT_EXPIRATION_DATE("Неправильная дата токена"),
    INVALID_PROPERTY_TOKEN_ACCESS_TIME("Invalid property token.access.time"),
    INVALID_PROPERTY_TOKEN_REFRESH_TIME("Invalid property token.refresh.time"),
    INVALID_PATH("Неправильный путь"),
    EMPTY_TOKEN_FIELD("Не найдено поле в токене"),
    PERMISSION_DENIED("Недостаточно прав"),

    TOO_MUCH_SYMBOLS("Превышен лимит на количество символов"),
    WRONG_LOGIN_OR_PASSWORD("Неверное имя пользователя или пароль"),
    USER_IS_BLOCKED("Пользователь заблокирован"),
    REQUIRED_FIELDS_EMPTY("Не заполнены обязательные поля"),
    INVALID_DATA_FORMAT("Неверный формат данных"),
    OBJECT_NOT_FOUND("Объект не найден"),
    DUPLICATE_UNIQUE_FIELD("Объект с таким уникальным полем уже существует"), // + " - *введённые данные уникального поля*"
    NOT_FOUND_REFERENCE("Попытка связи с несуществующим объектом"),
    NOT_VALID_QUERY_PARAMETERS("Неверные параметры запроса"),
    SERVICE_UNAVAILABLE("Сервис временно недоступен");
}